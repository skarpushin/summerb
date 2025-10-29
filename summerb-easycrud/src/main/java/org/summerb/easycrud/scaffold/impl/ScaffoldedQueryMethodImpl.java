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
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.dao.SqlTypeOverride;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoInjections;
import org.summerb.easycrud.scaffold.api.CallableMethod;
import org.summerb.easycrud.scaffold.api.Query;

/**
 * This clas provides default implementation for interface methods marked with {@link Query}
 * annotations
 *
 * @param <TMethodParameter> type of the MethodParameter meta information. This is needed only for
 *     easy of possible extension -- cases when this class needs to be subclassed and subclass will
 *     prefer to carry some additional information related to each method parameter
 */
public class ScaffoldedQueryMethodImpl<TMethodParameter extends ScaffoldedMethodParameter>
    implements CallableMethod {
  private final Logger log = LoggerFactory.getLogger(getClass());

  protected final Method method;
  protected final EasyCrudService<?, HasId<?>> service;
  protected final EasyCrudDaoInjections<?, ?> dao;
  protected final Query annotation;

  @SuppressWarnings("rawtypes")
  protected final RowMapper rowMapper;

  protected final String query;

  protected final TMethodParameter[] methodParameters;

  public ScaffoldedQueryMethodImpl(
      EasyCrudService<?, HasId<?>> service, EasyCrudDaoInjections<?, HasId<?>> dao, Method method) {
    Preconditions.checkNotNull(service, "service required");
    Preconditions.checkNotNull(dao, "dao required");
    Preconditions.checkNotNull(method, "method required");

    this.annotation = method.getAnnotation(Query.class);
    this.query = annotation.value();
    this.method = method;
    this.service = service;
    this.dao = dao;
    this.rowMapper = initRowMapper();
    this.methodParameters = buildMethodParameters(this.method);
  }

  protected TMethodParameter[] buildMethodParameters(Method method) {
    if (method.getParameterCount() == 0) {
      return null;
    }

    TMethodParameter[] ret = newMethodParametersArray(method.getParameterCount());
    for (int i = 0; i < method.getParameterCount(); i++) {
      ret[i] = buildMethodParameter(method, method.getParameters()[i], i);
    }
    return ret;
  }

  protected TMethodParameter buildMethodParameter(Method method, Parameter parameter, int i) {
    TMethodParameter ret = newMethodParameter();
    ret.idx = i;
    ret.name = resolveParameterName(parameter, i);

    Class<?> parameterType = parameter.getType();
    ret.array = parameter.isVarArgs() || parameterType.isArray();
    ret.iterable = Iterable.class.isAssignableFrom(parameterType);

    if (ret.array) {
      ret.sqlTypeOverride =
          dao.getSqlTypeOverrides().findOverrideForClass(parameterType.getComponentType());
      ret.sqlTypeOverrideResolved = true;
    } else if (ret.iterable) {
      ret.sqlTypeOverrideResolved = false;
    } else {
      ret.sqlTypeOverride = dao.getSqlTypeOverrides().findOverrideForClass(parameterType);
      ret.sqlTypeOverrideResolved = true;
    }
    return ret;
  }

  protected String resolveParameterName(Parameter parameter, int idx) {
    String ret = parameter.getName();
    Preconditions.checkArgument(
        !("arg" + idx).equals(ret),
        "Parameter names are not included in bytecode. Make sure to configure compiler with '-parameters' so that "
            + "parameter names will be included into bytecode and will be available via reflection");
    return ret;
  }

  @SuppressWarnings("unchecked")
  protected TMethodParameter newMethodParameter() {
    return (TMethodParameter) new ScaffoldedMethodParameter();
  }

  @SuppressWarnings("unchecked")
  protected TMethodParameter[] newMethodParametersArray(int count) {
    return (TMethodParameter[]) new ScaffoldedMethodParameter[count];
  }

  protected RowMapper<?> initRowMapper() {
    try {
      if (!annotation.rowMapper().equals(RowMapper.class)) {
        // this means that specific RowMapper is provided, so we just instantiate it and return
        return annotation.rowMapper().getDeclaredConstructor().newInstance();
      }

      return guessRowMapper();
    } catch (Throwable e) {
      throw new RuntimeException("Failed to instantiate row mapper for method " + methodName(), e);
    }
  }

  protected String methodName() {
    return method.getDeclaringClass().getName() + "::" + method.getName();
  }

  protected RowMapper<?> guessRowMapper() {
    Class<?> returnType = method.getReturnType();

    return guessRowMapper(returnType, null);
  }

  protected RowMapper<?> guessRowMapper(Class<?> returnType, Class<?> enclosingType) {
    if (isPrimitive(returnType)) {
      return new SingleColumnRowMapper<>(returnType);
    }

    if (isCollectionType(returnType)) {
      Preconditions.checkArgument(
          enclosingType == null,
          "Collection of collections is not supported. Method: %s",
          methodName());
      Type elementType =
          ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
      return guessRowMapper((Class<?>) elementType, returnType);
    }

    return buildBeanPropertyRowMapper(returnType);
  }

  protected RowMapper<?> buildBeanPropertyRowMapper(Class<?> returnType) {
    if (returnType.equals(dao.getRowClass())) {
      return dao.getRowMapper();
    }

    BeanPropertyRowMapper<?> ret = new BeanPropertyRowMapper<>(returnType);
    ret.setConversionService(dao.getConversionService());
    return ret;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Object call(Object[] methodArgs) {
    try {
      Class<?> returnType = method.getReturnType();
      if (annotation.modifying()) {
        assertReturnTypeForModifyingQuery(returnType);
      }

      MapSqlParameterSource params = buildQueryParams(methodArgs);
      log.debug(
          "Executing scaffolded query {}\nQuery: {}\nParams: {}", methodName(), query, params);

      if (annotation.modifying()) {
        int affectedRows = dao.getJdbc().update(query, params);
        return returnType.equals(Void.TYPE) ? null : affectedRows;
      }

      EasyCrudWireTap wireTap = service.getWireTap();
      boolean isTriggerWireTap =
          rowMapper == dao.getRowMapper() && wireTap != null && wireTap.requiresOnRead();
      if (isTriggerWireTap) {
        wireTap.beforeRead();
      }

      // Case: Collection
      if (isCollectionType(returnType)) {
        List<?> ret = dao.getJdbc().query(query, params, rowMapper);
        boolean isTriggerWireTapMultiple =
            rowMapper == dao.getRowMapper() && wireTap != null && wireTap.requiresOnReadMultiple();
        if (!CollectionUtils.isEmpty(ret) && isTriggerWireTapMultiple) {
          wireTap.afterRead(ret);
        }
        return adjustToReturnType(ret, returnType);
      }

      // Case: Single object
      Object ret;
      try {
        ret = dao.getJdbc().queryForObject(query, params, rowMapper);
      } catch (EmptyResultDataAccessException e) {
        return null;
      }
      if (ret != null && isTriggerWireTap) {
        wireTap.afterRead(ret);
      }
      return ret;
    } catch (Throwable t) {
      throw service.getExceptionStrategy().handleExceptionAtFind(t);
    }
  }

  protected Object adjustToReturnType(List<?> results, Class<?> returnType) {
    if (results.getClass().equals(returnType)
        || List.class.equals(returnType)
        || Collection.class.equals(returnType)
        || Iterable.class.equals(returnType)) {
      return results;
    }
    if (Set.class.equals(returnType)) {
      return new HashSet<>(results);
    }
    throw new RuntimeException("Adjusting to return type is not supported for " + returnType);
  }

  protected void assertReturnTypeForModifyingQuery(Class<?> returnType) {
    if (returnType.equals(Void.TYPE)
        || returnType.equals(Integer.TYPE)
        || returnType.equals(Integer.class)) {
      return;
    }

    throw new IllegalArgumentException(
        "Method "
            + methodName()
            + " is annotated with @Query(modifying = true), but it returns "
            + returnType.getName()
            + ". Only void or int are allowed for modifying methods");
  }

  protected MapSqlParameterSource buildQueryParams(Object[] methodArgs) {
    if (methodArgs == null || methodArgs.length == 0) {
      return ScaffoldedMethodFactoryMySqlImpl.EMPTY_PARAMETER_SOURCE;
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    for (int i = 0; i < methodArgs.length; i++) {
      Object arg = methodArgs[i];
      TMethodParameter param = methodParameters[i];
      addValue(params, param, arg);
    }
    return params;
  }

  protected void addValue(MapSqlParameterSource params, TMethodParameter param, Object arg) {
    if (!param.iterable && !param.array) {
      addScalarValue(params, param, arg, param.sqlTypeOverride);
      return;
    }

    if (param.sqlTypeOverrideResolved) {
      addNonScalarValue(params, param, arg, param.sqlTypeOverride);
      return;
    }

    addNonScalarValueForUnresolvedOverride(params, param, arg);
  }

  protected void addScalarValue(
      MapSqlParameterSource params,
      TMethodParameter param,
      Object arg,
      SqlTypeOverride sqlTypeOverride) {
    if (sqlTypeOverride == null) {
      params.addValue(param.name, arg);
    } else {
      params.addValue(param.name, sqlTypeOverride.convert(arg), sqlTypeOverride.getSqlType());
    }
  }

  protected void addNonScalarValue(
      MapSqlParameterSource params,
      TMethodParameter param,
      Object arg,
      SqlTypeOverride sqlTypeOverride) {
    if (sqlTypeOverride == null) {
      params.addValue(param.name, !param.array ? arg : map(arg, null));
    } else {
      if (param.array || sqlTypeOverride.isConversionRequired()) {
        params.addValue(param.name, map(arg, sqlTypeOverride), sqlTypeOverride.getSqlType());
      } else {
        params.addValue(param.name, arg, sqlTypeOverride.getSqlType());
      }
    }
  }

  protected void addNonScalarValueForUnresolvedOverride(
      MapSqlParameterSource params, TMethodParameter param, Object arg) {
    List<Object> values = map(arg, null);
    if (values == null) {
      params.addValue(param.name, null);
      return;
    }

    Object firstNonNull = findFirstNonNullValue(values, param);
    if (firstNonNull == null) {
      params.addValue(param.name, null);
      return;
    }

    SqlTypeOverride sqlTypeOverride = dao.getSqlTypeOverrides().findOverrideForValue(firstNonNull);
    param.sqlTypeOverride = sqlTypeOverride;
    param.sqlTypeOverrideResolved = true;

    if (sqlTypeOverride == null) {
      params.addValue(param.name, !param.array ? arg : map(arg, null));
    } else {
      if (param.array || sqlTypeOverride.isConversionRequired()) {
        params.addValue(
            param.name,
            values.stream().map(sqlTypeOverride::convert).collect(Collectors.toList()),
            sqlTypeOverride.getSqlType());
      } else {
        params.addValue(param.name, arg, sqlTypeOverride.getSqlType());
      }
    }
  }

  protected List<Object> map(Object arg, SqlTypeOverride sqlTypeOverride) {
    if (arg == null) {
      return null;
    }

    List<Object> ret;
    if (arg instanceof Collection) {
      if (sqlTypeOverride == null) {
        ret = new ArrayList<>(((Collection<?>) arg));
      } else {
        ret = new ArrayList<>(((Collection<?>) arg).size());
        for (Object next : (Collection<?>) arg) {
          ret.add(sqlTypeOverride.convert(next));
        }
      }
    } else if (arg.getClass().isArray()) {
      ret = new ArrayList<>(Array.getLength(arg));
      for (int i = 0; i < Array.getLength(arg); i++) {
        Object value =
            sqlTypeOverride == null
                ? Array.get(arg, i)
                : sqlTypeOverride.convert(Array.get(arg, i));
        ret.add(value);
      }
    } else if (arg instanceof Iterable) {
      ret = new LinkedList<>();
      for (Object next : (Iterable<?>) arg) {
        Object value = sqlTypeOverride == null ? next : sqlTypeOverride.convert(next);
        ret.add(value);
      }
    } else {
      throw new IllegalArgumentException(
          "Argument is not a supported collection type: " + arg.getClass().getName());
    }

    return ret;
  }

  protected Object findFirstNonNullValue(List<Object> values, TMethodParameter param) {
    return values.stream().filter(Objects::nonNull).findFirst().orElse(null);
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  protected boolean isPrimitive(Class<?> clazz) {
    return clazz.isPrimitive()
        || clazz.getName().startsWith("java.lang.")
        || clazz.getName().equals("java.util.Date");
  }

  protected boolean isCollectionType(Class<?> clazz) {
    return Collection.class.isAssignableFrom(clazz);
  }
}
