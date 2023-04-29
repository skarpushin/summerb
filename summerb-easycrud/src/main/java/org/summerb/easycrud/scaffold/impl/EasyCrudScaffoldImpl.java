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
import java.util.Objects;
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
import org.summerb.easycrud.api.ParameterSourceBuilder;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.easycrud.impl.mysql.EasyCrudDaoMySqlImpl;
import org.summerb.easycrud.impl.mysql.QueryToNativeSqlCompilerMySqlImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapTableAuthImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.easycrud.scaffold.api.EasyCrudScaffold;
import org.summerb.easycrud.scaffold.api.EasyCrudServiceProxyFactory;
import org.summerb.utils.DtoBase;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * Default impl of {@link EasyCrudScaffold}
 * 
 * @author sergeyk
 *
 */
public class EasyCrudScaffoldImpl implements EasyCrudScaffold {
	protected DataSource dataSource;

	protected EasyCrudServiceProxyFactory easyCrudServiceProxyFactory = new EasyCrudServiceProxyFactoryImpl();
	protected AutowireCapableBeanFactory beanFactory;

	@Override
	public <TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromRowClass(Class<TDto> rowClass) {
		String messageCode = rowClass.getSimpleName();
		String tableName = QueryToNativeSqlCompilerMySqlImpl.underscore(messageCode);
		return fromRowClass(rowClass, messageCode, tableName);
	}

	@Override
	public <TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromRowClass(Class<TDto> rowClass, String messageCode,
			String tableName, Object... injections) {
		try {
			EasyCrudDao<TId, TDto> dao = buildDao(rowClass, tableName);
			EasyCrudService<TId, TDto> service = buildService(rowClass, messageCode, tableName, dao, injections);
			return service;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to scaffold EasyCrudService for " + rowClass, t);
		}
	}

	protected <TRow extends HasId<TId>, TId> EasyCrudServicePluggableImpl<TId, TRow, EasyCrudDao<TId, TRow>> buildService(
			Class<TRow> rowClass, String messageCode, String tableName, EasyCrudDao<TId, TRow> dao,
			Object... injections) throws Exception {
		EasyCrudServicePluggableImpl<TId, TRow, EasyCrudDao<TId, TRow>> service = buildServiceImpl();
		initService(service, rowClass, messageCode, tableName, dao, injections);
		return service;
	}

	protected <TDto extends HasId<TId>, TId> EasyCrudServicePluggableImpl<TId, TDto, EasyCrudDao<TId, TDto>> buildServiceImpl() {
		return new EasyCrudServicePluggableImpl<>();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <TId, TDto extends HasId<TId>> void initService(
			EasyCrudServicePluggableImpl<TId, TDto, EasyCrudDao<TId, TDto>> service, Class<TDto> rowClass,
			String messageCode, String tableName, EasyCrudDao<TId, TDto> dao, Object... injections) throws Exception {
		service.setRowClass(rowClass);
		service.setDao(dao);
		service.setRowMessageCode(messageCode);
		getBeanFactory().autowireBean(service);

		if (injections == null || injections.length == 0) {
			service.afterPropertiesSet();
			return;
		}

		// NOTE: Ok. This thing around EasyCrudExceptionStrategy tells me this code is
		// begging to be refactoried. Screaming about OCP... I'll do it later.
		List<EasyCrudWireTap<TId, TDto>> wireTaps = Arrays.stream(injections)
				.map(injectionToWireTapMapper(rowClass, messageCode, tableName)).filter(Objects::nonNull)
				.collect(Collectors.toList());
		if (wireTaps.size() > 0) {
			service.setWireTap(new EasyCrudWireTapDelegatingImpl<>(wireTaps));
		}

		EasyCrudExceptionStrategy exceptionStrategy = Arrays.stream(injections)
				.filter(x -> x instanceof EasyCrudExceptionStrategy).map(x -> (EasyCrudExceptionStrategy) x).findFirst()
				.orElse(null);
		if (exceptionStrategy != null) {
			service.setGenericExceptionStrategy(exceptionStrategy);
		}

		// TZD: Give a warning or maybe even a failure if some of the injections were
		// not used

		// x.
		service.afterPropertiesSet();
	}

	@SuppressWarnings("unchecked")
	protected <TId, TDto extends HasId<TId>> Function<Object, EasyCrudWireTap<TId, TDto>> injectionToWireTapMapper(
			Class<TDto> rowClass, String messageCode, String tableName) {
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
			} else if (inj instanceof EventBus) {
				return new EasyCrudWireTapEventBusImpl<>((EventBus) inj);
			} else if (inj instanceof EasyCrudExceptionStrategy) {
				// do nothing - it was injected above
				return null;
			} else if (inj instanceof ParameterSourceBuilder) {
				// do nothing - it was injected above
				return null;
			} else {
				throw new IllegalStateException("Failed to automatically inject " + inj);
			}
		};
	}

	protected <TId, TDto extends HasId<TId>> EasyCrudDao<TId, TDto> buildDao(Class<TDto> rowClass, String tableName)
			throws Exception {
		EasyCrudDaoMySqlImpl<TId, TDto> dao = buildInstance();
		dao.setDataSource(getDataSource());
		dao.setRowClass(rowClass);
		dao.setTableName(tableName);
		beanFactory.autowireBean(dao);
		dao.afterPropertiesSet();
		return dao;
	}

	protected <TDto extends HasId<TId>, TId> EasyCrudDaoMySqlImpl<TId, TDto> buildInstance() {
		return new EasyCrudDaoMySqlImpl<>();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>> TService fromService(
			Class<TService> serviceInterface, String messageCode, String tableName, Object... injections) {

		try {
			Class<TDto> rowClass = discoverRowClassFromServiceInterface(serviceInterface);
			EasyCrudDao<TId, TDto> dao = buildDao(rowClass, tableName, injections);

			TService proxy = getEasyCrudServiceProxyFactory().createImpl(serviceInterface);
			EasyCrudServicePluggableImpl service = (EasyCrudServicePluggableImpl) java.lang.reflect.Proxy
					.getInvocationHandler(proxy);
			initService(service, rowClass, messageCode, tableName, dao, injections);

			return proxy;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to scaffold EasyCrudService for " + serviceInterface, t);
		}
	}

	protected <TId, TDto extends HasId<TId>> EasyCrudDao<TId, TDto> buildDao(Class<TDto> rowClass, String tableName,
			Object... injections) throws Exception {
		EasyCrudDaoMySqlImpl<TId, TDto> dao = buildInstance();
		dao.setDataSource(getDataSource());
		dao.setRowClass(rowClass);
		dao.setTableName(tableName);
		beanFactory.autowireBean(dao);
		propagateInjectionsIntoDaoIfAny(dao, injections);
		dao.afterPropertiesSet();
		return dao;
	}

	@SuppressWarnings("unchecked")
	protected <TDto extends HasId<TId>, TId> void propagateInjectionsIntoDaoIfAny(EasyCrudDaoMySqlImpl<TId, TDto> dao,
			Object... injections) {
		if (injections != null) {
			for (Object inj : injections) {
				if (inj instanceof ParameterSourceBuilder) {
					dao.setParameterSourceBuilder((ParameterSourceBuilder<TDto>) inj);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>> Class<TDto> discoverRowClassFromServiceInterface(
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
