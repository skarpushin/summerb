package org.summerb.stringtemplate.api;

/**
 * Interface for evaluating string template and getting "rendered" result.
 * 
 * Instance of this interface must be thread-safe, because it might be cached
 * and used concurrently in several threads
 * 
 * @author skarpushin
 * 
 * @see StringTemplateStaticImpl
 * @see StringTemplateCompiler
 */
public interface StringTemplate {
	String applyTo(Object rootObject);
}
