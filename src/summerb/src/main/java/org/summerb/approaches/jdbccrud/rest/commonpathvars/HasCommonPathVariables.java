package org.summerb.approaches.jdbccrud.rest.commonpathvars;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to contain multiple {@link HasCommonPathVariable}
 * annotations
 * 
 * @see HasCommonPathVariable
 * 
 * @author sergeyk
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasCommonPathVariables {
	HasCommonPathVariable[] value();
}