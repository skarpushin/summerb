/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.summerb.easycrud.impl.dao.DaoBase;
import org.summerb.easycrud.scaffold.api.CallableMethod;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;
import org.summerb.easycrud.scaffold.api.ScaffoldedQuery;

/**
 * Impl of {@link ScaffoldedMethodFactory} assuming underlying DB is MySQL
 *
 * @author sergeyk
 */
public class ScaffoldedMethodFactoryMySqlImpl extends DaoBase implements ScaffoldedMethodFactory {
  @Autowired
  public ScaffoldedMethodFactoryMySqlImpl(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public CallableMethod create(Method key) {
    return new CallableMethodImpl(key);
  }

  public class CallableMethodImpl implements CallableMethod {
    protected Method method;
    protected ScaffoldedQuery annotation;

    @SuppressWarnings("rawtypes")
    protected RowMapper rowMapper;

    public CallableMethodImpl(Method key) {
      annotation = key.getAnnotation(ScaffoldedQuery.class);
      this.method = key;
      initRowMapper(key);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
    protected void initRowMapper(Method key) {
      try {
        rowMapper = annotation.rowMapper().newInstance();

        if (rowMapper instanceof BeanPropertyRowMapper) {
          Class<?> returnType = method.getReturnType();
          if (isCollectionType(returnType) && Collection.class.isAssignableFrom(returnType)) {
            Type collectionType =
                ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            Class collectionClass = (Class) collectionType;
            ((BeanPropertyRowMapper) rowMapper).setMappedClass(collectionClass);
          } else if (!isPrimitive(returnType)) {
            ((BeanPropertyRowMapper) rowMapper).setMappedClass(returnType);
          } else {
            throw new RuntimeException(
                "CallableMethodImpl doesn't support other cases of return value: "
                    + method.getName());
          }
        }
      } catch (Throwable e) {
        throw new RuntimeException("Failed to instantiate row mapper for method " + key, e);
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object call(Object[] args) throws Exception {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
      Class<?> returnType = method.getReturnType();
      if (isCollectionType(returnType)) {
        return jdbcTemplate.query(annotation.value(), rowMapper, args);
      } else if (!isPrimitive(returnType)) {
        return jdbcTemplate.queryForObject(annotation.value(), rowMapper, args);
      } else {
        throw new RuntimeException(
            "StoredProceduresImpl. This case is not supported yet. Method name: "
                + method.getName());
      }
    }

    protected boolean isPrimitive(Class<?> clazz) {
      return clazz.isPrimitive()
          || clazz.getName().startsWith("java.lang.")
          || clazz.getName().equals("java.util.Date");
    }

    protected boolean isCollectionType(Class<?> clazz) {
      return Collection.class.isAssignableFrom(clazz);
    }
  }
}
