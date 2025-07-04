/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.DaoExceptionTranslator;
import org.summerb.easycrud.api.EasyCrudAuthorizationPerRow;
import org.summerb.easycrud.api.EasyCrudAuthorizationPerTable;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.ParameterSourceBuilder;
import org.summerb.easycrud.api.QueryToSql;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerRowWireTapAdapter;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerTableWireTapAdapter;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoInjections;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoSqlImpl;
import org.summerb.easycrud.impl.dao.mysql.QueryToSqlMySqlImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.easycrud.scaffold.api.EasyCrudScaffold;
import org.summerb.easycrud.scaffold.api.EasyCrudServiceProxyFactory;
import org.summerb.easycrud.scaffold.api.Query;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.utils.DtoBase;

/**
 * Default impl of {@link EasyCrudScaffold}
 *
 * @author sergeyk
 */
public class EasyCrudScaffoldImpl implements EasyCrudScaffold, InitializingBean {
  protected DataSource dataSource;
  protected AutowireCapableBeanFactory beanFactory;
  protected ScaffoldedMethodFactory scaffoldedMethodFactory;

  protected EasyCrudServiceProxyFactory easyCrudServiceProxyFactory;

  @Autowired(required = false)
  protected CurrentUserUuidResolver currentUserUuidResolver;

  public EasyCrudScaffoldImpl(
      DataSource dataSource,
      AutowireCapableBeanFactory beanFactory,
      ScaffoldedMethodFactory scaffoldedMethodFactory) {
    this.beanFactory = beanFactory;
    this.dataSource = dataSource;
    this.scaffoldedMethodFactory = scaffoldedMethodFactory;
  }

  @Override
  public void afterPropertiesSet() {
    Preconditions.checkArgument(dataSource != null, "dataSource required");
    Preconditions.checkArgument(beanFactory != null, "beanFactory required");
    Preconditions.checkArgument(
        scaffoldedMethodFactory != null, "scaffoldedMethodFactory required");

    if (easyCrudServiceProxyFactory == null) {
      easyCrudServiceProxyFactory =
          buildDefaultEasyCrudServiceProxyFactory(scaffoldedMethodFactory);
    }
  }

  public AutowireCapableBeanFactory getBeanFactory() {
    return beanFactory;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public EasyCrudServiceProxyFactory getEasyCrudServiceProxyFactory() {
    return easyCrudServiceProxyFactory;
  }

  /**
   * Set {@link EasyCrudServiceProxyFactory}. Optional. If nothing set, then {@link
   * EasyCrudServiceProxyFactoryImpl} will be used
   *
   * @param easyCrudServiceProxyFactory easyCrudServiceProxyFactory
   */
  public void setEasyCrudServiceProxyFactory(
      EasyCrudServiceProxyFactory easyCrudServiceProxyFactory) {
    Preconditions.checkArgument(
        easyCrudServiceProxyFactory != null, "easyCrudServiceProxyFactory required");
    this.easyCrudServiceProxyFactory = easyCrudServiceProxyFactory;
  }

  public ScaffoldedMethodFactory getScaffoldedMethodFactory() {
    return scaffoldedMethodFactory;
  }

  /**
   * Set {@link ScaffoldedMethodFactory}. Optional. Required only if your interface has methods
   * annotated with {@link Query}
   *
   * @param scaffoldedMethodFactory scaffoldedMethodFactory
   */
  public void setScaffoldedMethodFactory(ScaffoldedMethodFactory scaffoldedMethodFactory) {
    Preconditions.checkArgument(
        scaffoldedMethodFactory != null, "scaffoldedMethodFactory required");
    this.scaffoldedMethodFactory = scaffoldedMethodFactory;
  }

  protected EasyCrudServiceProxyFactory buildDefaultEasyCrudServiceProxyFactory(
      ScaffoldedMethodFactory scaffoldedMethodFactory) {
    return new EasyCrudServiceProxyFactoryImpl(scaffoldedMethodFactory);
  }

  @Override
  public <TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromRowClass(
      Class<TDto> rowClass) {
    String messageCode = rowClass.getSimpleName();
    String tableName = QueryToSqlMySqlImpl.underscore(messageCode);
    return fromRowClass(rowClass, messageCode, tableName);
  }

  @Override
  public <TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromRowClass(
      Class<TDto> rowClass, String messageCode, String tableName, Object... injections) {
    try {
      EasyCrudDao<TId, TDto> dao = buildDao(rowClass, tableName);
      var ret = instantiateAndAutowireService(dao, rowClass);
      ret.setRowMessageCode(messageCode);
      autowireInjections(injections);
      setServiceInjectionsIfAny(ret, messageCode, injections);
      ret.afterPropertiesSet();
      return ret;
    } catch (Throwable t) {
      throw new RuntimeException("Failed to scaffold EasyCrudService for " + rowClass, t);
    }
  }

  /**
   * Autowire injections here so that we do not have to create separate beans and then inject them
   * in configuration
   *
   * @param injections injections to autowire
   */
  protected void autowireInjections(Object[] injections) {
    if (injections == null) {
      return;
    }
    for (Object inj : injections) {
      if (inj == null) {
        continue;
      }
      beanFactory.autowireBean(inj);
      if (inj instanceof InitializingBean) {
        try {
          ((InitializingBean) inj).afterPropertiesSet();
        } catch (Exception e) {
          throw new RuntimeException("Calling afterPropertiesSet failed on " + inj, e);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected <TId, TRow extends HasId<TId>> void setServiceInjectionsIfAny(
      EasyCrudServiceImpl<TId, TRow, EasyCrudDao<TId, TRow>> ret,
      String messageCode,
      Object... injections) {
    var currentUserResolver = find(injections, CurrentUserUuidResolver.class);
    if (currentUserResolver != null) {
      ret.setCurrentUserResolver(currentUserResolver);
    }

    var stringIdGenerator = find(injections, StringIdGenerator.class);
    if (stringIdGenerator != null) {
      ret.setStringIdGenerator(stringIdGenerator);
    }

    var exceptionStrategy = find(injections, EasyCrudExceptionStrategy.class);
    if (exceptionStrategy != null) {
      ret.setExceptionStrategy(exceptionStrategy);
    }

    var easyCrudWireTap = buildWireTap(messageCode, injections);
    if (easyCrudWireTap != null) {
      ret.setWireTap((EasyCrudWireTap<TRow>) easyCrudWireTap);
    }
  }

  protected <TDto extends HasId<TId>, TId>
      EasyCrudServiceImpl<TId, TDto, EasyCrudDao<TId, TDto>> instantiateAndAutowireService(
          EasyCrudDao<TId, TDto> dao, Class<TDto> rowClass) {

    var ret = new EasyCrudServiceImpl<>(dao, rowClass);
    beanFactory.autowireBean(ret);
    return ret;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected <TId, TRow extends HasId<TId>> EasyCrudWireTap<TRow> buildWireTap(
      String messageCode, Object... injections) {
    List<EasyCrudWireTap> wireTaps =
        Arrays.stream(injections)
            .map(x -> tryMapServiceInjectionToWireTap(x, messageCode))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    if (!wireTaps.isEmpty()) {
      return (EasyCrudWireTap<TRow>) new EasyCrudWireTapDelegatingImpl(wireTaps);
    }

    return null;
  }

  @Override
  public <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>>
      TService fromService(
          Class<TService> serviceInterface,
          String messageCode,
          String tableName,
          Object... injections) {

    try {
      Class<TDto> rowClass = discoverRowClassFromServiceInterface(serviceInterface);
      EasyCrudDao<TId, TDto> dao = buildDao(rowClass, tableName, injections);

      EasyCrudServiceImpl<TId, TDto, EasyCrudDao<TId, TDto>> actualService =
          instantiateAndAutowireService(dao, rowClass);
      actualService.setRowMessageCode(messageCode);
      setServiceInjectionsIfAny(actualService, messageCode, injections);
      actualService.afterPropertiesSet();

      //noinspection unchecked
      EasyCrudDaoInjections<TId, TDto> daoInjections = (EasyCrudDaoInjections<TId, TDto>) dao;

      return getEasyCrudServiceProxyFactory()
          .createProxy(serviceInterface, actualService, daoInjections);
    } catch (Throwable t) {
      throw new RuntimeException("Failed to scaffold EasyCrudService for " + serviceInterface, t);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <
          TId,
          TRow extends HasId<TId>,
          TService extends EasyCrudService<TId, TRow>,
          TServiceImpl extends EasyCrudServiceImpl<TId, TRow, EasyCrudDao<TId, TRow>>>
      TService fromService(
          Class<TService> serviceInterface,
          TServiceImpl serviceImpl,
          String tableName,
          Object... injections) {
    try {
      Preconditions.checkNotNull(serviceInterface, "serviceInterface required");
      Preconditions.checkNotNull(serviceImpl, "serviceImpl must not be null");
      Preconditions.checkNotNull(serviceImpl.getRowClass(), "serviceImplRowClass must not be null");
      Preconditions.checkArgument(StringUtils.hasText(tableName), "tableName required");

      EasyCrudDao<TId, TRow> dao = buildDao(serviceImpl.getRowClass(), tableName, injections);

      serviceImpl.setDao(dao);
      beanFactory.autowireBean(serviceImpl);
      setServiceInjectionsIfAny(serviceImpl, serviceImpl.getRowMessageCode(), injections);
      serviceImpl.afterPropertiesSet();

      return (TService) serviceImpl;
    } catch (Throwable t) {
      throw new RuntimeException("Failed to scaffold EasyCrudService for " + serviceInterface, t);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected EasyCrudWireTap tryMapServiceInjectionToWireTap(Object inj, String messageCode) {
    // Note: I know, OCP smell
    if (inj instanceof EasyCrudWireTap) {
      return (EasyCrudWireTap) inj;
    }

    if (inj instanceof EasyCrudValidationStrategy) {
      return new EasyCrudWireTapValidationImpl((EasyCrudValidationStrategy) inj);
    }

    if (inj instanceof EventBus) {
      return new EasyCrudWireTapEventBusImpl<>((EventBus) inj);
    }

    if (inj instanceof EasyCrudAuthorizationPerRow<?>) {
      return new EasyCrudAuthorizationPerRowWireTapAdapter(
          messageCode, currentUserUuidResolver, (EasyCrudAuthorizationPerRow) inj);
    }

    if (inj instanceof EasyCrudAuthorizationPerTable) {
      return new EasyCrudAuthorizationPerTableWireTapAdapter(
          messageCode, currentUserUuidResolver, (EasyCrudAuthorizationPerTable) inj);
    }

    return null;
  }

  protected <TId, TDto extends HasId<TId>> EasyCrudDao<TId, TDto> buildDao(
      Class<TDto> rowClass, String tableName, Object... injections) throws Exception {

    EasyCrudDao<?, ?> daoFromInjections = find(injections, EasyCrudDao.class);
    if (daoFromInjections != null) {
      //noinspection unchecked,CastCanBeRemovedNarrowingVariableType
      return (EasyCrudDao<TId, TDto>) daoFromInjections;
    }

    EasyCrudDaoSqlImpl<TId, TDto> ret = instantiateAndAutowireDao(dataSource, tableName, rowClass);
    autowireInjections(injections);
    setDaoInjectionsIfAny(ret, injections);
    ret.afterPropertiesSet();
    return ret;
  }

  @SuppressWarnings("unchecked")
  protected <TId, TDto extends HasId<TId>> void setDaoInjectionsIfAny(
      EasyCrudDaoSqlImpl<TId, TDto> ret, Object... injections) {

    var conversionService = find(injections, ConversionService.class);
    if (conversionService != null) {
      ret.setConversionService(conversionService);
    }

    var sqlTypeOverrides = find(injections, SqlTypeOverrides.class);
    if (sqlTypeOverrides != null) {
      ret.setSqlTypeOverrides(sqlTypeOverrides);
    }

    var stringIdGenerator = find(injections, StringIdGenerator.class);
    if (stringIdGenerator != null) {
      ret.setStringIdGenerator(stringIdGenerator);
    }

    var rowMapper = find(injections, RowMapper.class);
    if (rowMapper != null) {
      ret.setRowMapper(rowMapper);
    }

    var parameterSourceBuilder = find(injections, ParameterSourceBuilder.class);
    if (parameterSourceBuilder != null) {
      ret.setParameterSourceBuilder(parameterSourceBuilder);
    }

    var queryToNativeSqlCompiler = find(injections, QueryToSql.class);
    if (queryToNativeSqlCompiler != null) {
      ret.setQueryToSql(queryToNativeSqlCompiler);
    }

    var daoExceptionTranslator = find(injections, DaoExceptionTranslator.class);
    if (daoExceptionTranslator != null) {
      ret.setDaoExceptionTranslator(daoExceptionTranslator);
    }
  }

  @SuppressWarnings({"unchecked"})
  protected <T> T find(Object[] injections, Class<T> clazz) {
    if (injections == null) {
      return null;
    }
    for (Object i : injections) {
      if (clazz.isAssignableFrom(i.getClass())) {
        return (T) i;
      }
    }
    return null;
  }

  protected <TDto extends HasId<TId>, TId> EasyCrudDaoSqlImpl<TId, TDto> instantiateAndAutowireDao(
      DataSource dataSource, String tableName, Class<TDto> rowClass) {
    EasyCrudDaoSqlImpl<TId, TDto> ret = new EasyCrudDaoSqlImpl<>(dataSource, tableName, rowClass);
    beanFactory.autowireBean(ret);
    return ret;
  }

  @SuppressWarnings("unchecked")
  protected <TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>>
      Class<TDto> discoverRowClassFromServiceInterface(Class<TService> serviceInterface) {
    Preconditions.checkArgument(
        EasyCrudService.class.isAssignableFrom(serviceInterface),
        "Service interface is supposed to be a subclass of EasyCrudService");

    ParameterizedType easyCrudServiceType = null;
    for (int i = 0; i < serviceInterface.getGenericInterfaces().length; i++) {
      Type candidate = serviceInterface.getGenericInterfaces()[i];
      if (!(candidate instanceof ParameterizedType candidatePt)) {
        continue;
      }

      if (EasyCrudService.class.equals(candidatePt.getRawType())) {
        easyCrudServiceType = candidatePt;
        break;
      }
    }
    Preconditions.checkState(
        easyCrudServiceType != null, "Wasn't able to located parent interface EasyCrudService");
    Type ret = easyCrudServiceType.getActualTypeArguments()[1];
    Preconditions.checkArgument(
        DtoBase.class.isAssignableFrom((Class<?>) ret),
        "DTO class supposed to impl DtoBase interface");
    Preconditions.checkArgument(
        HasId.class.isAssignableFrom((Class<?>) ret), "DTO class supposed to impl HasId interface");

    return (Class<TDto>) ret;
  }
}
