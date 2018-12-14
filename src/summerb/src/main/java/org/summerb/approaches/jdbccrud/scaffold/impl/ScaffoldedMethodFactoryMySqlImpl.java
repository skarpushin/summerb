package org.summerb.approaches.jdbccrud.scaffold.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.summerb.approaches.jdbccrud.common.DaoBase;
import org.summerb.approaches.jdbccrud.scaffold.api.CallableMethod;
import org.summerb.approaches.jdbccrud.scaffold.api.ScaffoldedMethodFactory;
import org.summerb.approaches.jdbccrud.scaffold.api.ScaffoldedQuery;

/**
 * Impl of {@link ScaffoldedMethodFactory} assuming underlying DB is MySQL
 * 
 * @author sergeyk
 *
 */
public class ScaffoldedMethodFactoryMySqlImpl extends DaoBase implements ScaffoldedMethodFactory {
	@Autowired
	public ScaffoldedMethodFactoryMySqlImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	public CallableMethod create(Method key) {
		return new CallableMethodImpl(key);
	}

	public class CallableMethodImpl implements CallableMethod {
		private Method method;
		private ScaffoldedQuery annotation;
		@SuppressWarnings("rawtypes")
		private RowMapper rowMapper;

		public CallableMethodImpl(Method key) {
			annotation = key.getAnnotation(ScaffoldedQuery.class);
			this.method = key;
			initRowMapper(key);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void initRowMapper(Method key) {
			try {
				rowMapper = annotation.rowMapper().newInstance();
				
				if (rowMapper instanceof BeanPropertyRowMapper) {
					Class<?> returnType = method.getReturnType();
					if (isCollectionType(returnType) && Collection.class.isAssignableFrom(returnType)) {
						Type collectionType = ((ParameterizedType) method.getGenericReturnType())
								.getActualTypeArguments()[0];
						Class collectionClass = (Class) collectionType;
						((BeanPropertyRowMapper) rowMapper).setMappedClass(collectionClass);
					} else if (!isPrimitive(returnType)) {
						((BeanPropertyRowMapper) rowMapper).setMappedClass(returnType);
					} else {
						throw new RuntimeException(
								"CallableMethodImpl doesn't support other cases of return value: " + method.getName());
					}
				}
			} catch (Throwable e) {
				throw new RuntimeException("Failed to instantiate row mapper for method " + key, e);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object call(Object[] args) throws Exception {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
			Class<?> returnType = method.getReturnType();
			if (isCollectionType(returnType)) {
				return jdbcTemplate.query(annotation.value(), args, rowMapper);
			} else if (!isPrimitive(returnType)) {
				return jdbcTemplate.queryForObject(annotation.value(), rowMapper, args);
			} else {
				throw new RuntimeException(
						"StoredProceduresImpl. This case is not supported yet. Method name: " + method.getName());
			}
		}

		private boolean isPrimitive(Class<?> clazz) {
			return clazz.isPrimitive() || clazz.getName().startsWith("java.lang.")
					|| clazz.getName().equals("java.util.Date");
		}

		private boolean isCollectionType(Class<?> clazz) {
			return Collection.class.isAssignableFrom(clazz);
		}
	}
}
