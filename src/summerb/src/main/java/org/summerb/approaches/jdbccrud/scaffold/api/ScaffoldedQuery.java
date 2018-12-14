package org.summerb.approaches.jdbccrud.scaffold.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.scaffold.impl.EasyCrudServiceProxyFactoryImpl;
import org.summerb.approaches.jdbccrud.scaffold.impl.ScaffoldedMethodFactoryMySqlImpl;

/**
 * Use it when you don't want to impl the whole stack of interfaces and classes
 * of Easy CRUD, when you just need to execute one custom query.
 * 
 * Add this attribute to the method of custom sub-interface of
 * {@link EasyCrudService}, impl of which is instantiated by @link
 * EasyCrudScaffold} (instead of defining class that implements this interface)
 * 
 * Then you can easily define new query to the underlying database.
 * 
 * This is normally implemented by {@link ScaffoldedMethodFactoryMySqlImpl}, but
 * could be easily overridden, by injecting custom
 * {@link ScaffoldedMethodFactory} into {@link EasyCrudServiceProxyFactoryImpl}
 * 
 * NOTE: Now, although service-layer supposed to know nothing about particulars
 * of the storage layer, I still believe that this "convenience shortcut" worth
 * a shot.
 * 
 * @author sergeyk
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScaffoldedQuery {
	/**
	 * Query
	 */
	String value();

	/**
	 * Customize row mapper if needed. Default will be used
	 * {@link BeanPropertyRowMapper}
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends RowMapper> rowMapper() default BeanPropertyRowMapper.class;
}
