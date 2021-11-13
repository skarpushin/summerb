/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.scaffold.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudTableAuthStrategy;
import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.easycrud.impl.mysql.EasyCrudDaoMySqlImpl;
import org.summerb.easycrud.impl.mysql.QueryToNativeSqlCompilerMySqlImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapTableAuthImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.easycrud.scaffold.api.EasyCrudScaffold;
import org.summerb.easycrud.scaffold.api.EasyCrudServiceProxyFactory;
import org.summerb.utils.DtoBase;

import com.google.common.base.Preconditions;

/**
 * Default impl of {@link EasyCrudScaffold}
 * 
 * @author sergeyk
 *
 */
public class EasyCrudScaffoldImpl implements EasyCrudScaffold {
	private DataSource dataSource;

	private EasyCrudServiceProxyFactory easyCrudServiceProxyFactory = new EasyCrudServiceProxyFactoryImpl();
	private AutowireCapableBeanFactory beanFactory;

	@Override
	public <TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromDto(Class<TDto> dtoClass) {
		String messageCode = dtoClass.getSimpleName();
		String tableName = QueryToNativeSqlCompilerMySqlImpl.underscore(messageCode);
		return fromDto(dtoClass, messageCode, tableName);
	}

	@Override
	public <TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromDto(Class<TDto> dtoClass, String messageCode,
			String tableName, Object... injections) {
		try {
			EasyCrudDao<TId, TDto> dao = buildDao(dtoClass, tableName);
			EasyCrudService<TId, TDto> service = buildService(dtoClass, messageCode, tableName, dao, injections);
			return service;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to scaffold EasyCrudService for " + dtoClass, t);
		}
	}

	protected <TDto extends HasId<TId>, TId> EasyCrudServicePluggableImpl<TId, TDto, EasyCrudDao<TId, TDto>> buildService(
			Class<TDto> dtoClass, String messageCode, String tableName, EasyCrudDao<TId, TDto> dao,
			Object... injections) throws Exception {
		EasyCrudServicePluggableImpl<TId, TDto, EasyCrudDao<TId, TDto>> service = new EasyCrudServicePluggableImpl<>();
		initService(service, dtoClass, messageCode, tableName, dao, injections);
		return service;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <TId, TDto extends HasId<TId>> void initService(
			EasyCrudServicePluggableImpl<TId, TDto, EasyCrudDao<TId, TDto>> service, Class<TDto> dtoClass,
			String messageCode, String tableName, EasyCrudDao<TId, TDto> dao, Object... injections) throws Exception {
		service.setDtoClass(dtoClass);
		service.setDao(dao);
		service.setEntityTypeMessageCode(messageCode);
		beanFactory.autowireBean(service);

		if (injections == null || injections.length == 0) {
			service.afterPropertiesSet();
			return;
		}

		// NOTE: Ok. This thing around EasyCrudExceptionStrategy tells me this code is
		// begging to be refactored. Screamign about OCP... I'll do it later.
		List<EasyCrudWireTap<TId, TDto>> wireTaps = Arrays.stream(injections)
				.filter(x -> !(x instanceof EasyCrudExceptionStrategy))
				.map(injectionToWireTapMapper(dtoClass, messageCode, tableName)).collect(Collectors.toList());
		if (wireTaps.size() > 0) {
			service.setWireTap(new EasyCrudWireTapDelegatingImpl<>(wireTaps));
		}

		EasyCrudExceptionStrategy exceptionStrategy = Arrays.stream(injections)
				.filter(x -> x instanceof EasyCrudExceptionStrategy).map(x -> (EasyCrudExceptionStrategy) x).findFirst()
				.orElse(null);
		if (exceptionStrategy != null) {
			service.setGenericExceptionStrategy(exceptionStrategy);
		}

		// x.
		service.afterPropertiesSet();
	}

	@SuppressWarnings("unchecked")
	protected <TId, TDto extends HasId<TId>> Function<Object, EasyCrudWireTap<TId, TDto>> injectionToWireTapMapper(
			Class<TDto> dtoClass, String messageCode, String tableName) {
		return inj -> {
			// Note: I know, OCP smell
			if (inj instanceof EasyCrudValidationStrategy) {
				return new EasyCrudWireTapValidationImpl<>((EasyCrudValidationStrategy<TDto>) inj);
			} else if (inj instanceof EasyCrudPerRowAuthStrategy) {
				return new EasyCrudWireTapPerRowAuthImpl<>((EasyCrudPerRowAuthStrategy<TDto>) inj);
			} else if (inj instanceof EasyCrudTableAuthStrategy) {
				return new EasyCrudWireTapTableAuthImpl<>((EasyCrudTableAuthStrategy) inj);
			} else if (inj instanceof EasyCrudWireTap) {
				return (EasyCrudWireTap<TId, TDto>) inj;
			} else {
				throw new IllegalStateException(
						"Can't convert injection " + inj + " into wireTap for service for " + dtoClass);
			}
		};
	}

	protected <TId, TDto extends HasId<TId>> EasyCrudDao<TId, TDto> buildDao(Class<TDto> dtoClass, String tableName)
			throws Exception {
		EasyCrudDaoMySqlImpl<TId, TDto> dao = new EasyCrudDaoMySqlImpl<>();
		dao.setDataSource(dataSource);
		dao.setDtoClass(dtoClass);
		dao.setTableName(tableName);
		beanFactory.autowireBean(dao);
		dao.afterPropertiesSet();
		return dao;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>> TService fromService(
			Class<TService> serviceInterface, String messageCode, String tableName, Object... injections) {
		try {
			Class<TDto> dtoClass = discoverDtoClassFromServiceInterface(serviceInterface);
			EasyCrudDao<TId, TDto> dao = buildDao(dtoClass, tableName);

			TService proxy = easyCrudServiceProxyFactory.createImpl(serviceInterface);
			EasyCrudServicePluggableImpl service = (EasyCrudServicePluggableImpl) java.lang.reflect.Proxy
					.getInvocationHandler(proxy);
			initService(service, dtoClass, messageCode, tableName, dao, injections);

			return proxy;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to scaffold EasyCrudService for " + serviceInterface, t);
		}
	}

	@SuppressWarnings("unchecked")
	protected <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>> Class<TDto> discoverDtoClassFromServiceInterface(
			Class<TService> serviceInterface) {
		Preconditions.checkArgument(EasyCrudService.class.isAssignableFrom(serviceInterface),
				"Service interface is supposed to be a subclass of EasyCrudService");

		ParameterizedType easyCrudServiceType = null;
		for (int i = 0; i < serviceInterface.getGenericInterfaces().length; i++) {
			Type candidate = serviceInterface.getGenericInterfaces()[i];
			if (!(candidate instanceof ParameterizedType)) {
				continue;
			}

			ParameterizedType candidatePt = (ParameterizedType) candidate;

			if (EasyCrudService.class.equals(candidatePt.getRawType())) {
				easyCrudServiceType = candidatePt;
				break;
			}
		}
		Preconditions.checkState(easyCrudServiceType != null,
				"Wasn't able to located parent interface EasyCrudService");
		Type ret = easyCrudServiceType.getActualTypeArguments()[1];
		Preconditions.checkArgument(DtoBase.class.isAssignableFrom((Class<?>) ret),
				"DTO class supposed to impl DtoBase interface");
		Preconditions.checkArgument(HasId.class.isAssignableFrom((Class<?>) ret),
				"DTO class supposed to impl HasId interface");

		return (Class<TDto>) ret;
	}

	public EasyCrudServiceProxyFactory getEasyCrudServiceProxyFactory() {
		return easyCrudServiceProxyFactory;
	}

	@Autowired(required = false)
	public void setEasyCrudServiceProxyFactory(EasyCrudServiceProxyFactory easyCrudServiceProxyFactory) {
		this.easyCrudServiceProxyFactory = easyCrudServiceProxyFactory;
	}

	public AutowireCapableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Autowired
	public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
