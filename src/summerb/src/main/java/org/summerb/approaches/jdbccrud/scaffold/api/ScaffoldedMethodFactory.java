package org.summerb.approaches.jdbccrud.scaffold.api;

import java.lang.reflect.Method;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

/**
 * Impl of this interface will be responsible for creating impls for methods of
 * sub-interfaces of {@link EasyCrudService} marked with {@link ScaffoldedQuery}
 * annotation and instantiated using
 * {@link EasyCrudScaffold#fromService(Class, String, String, Object...)}
 * 
 * @author sergeyk
 *
 */
public interface ScaffoldedMethodFactory {

	CallableMethod create(Method key);

}
