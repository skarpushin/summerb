package org.summerb.utils.stringtemplate.api;

import org.summerb.utils.stringtemplate.impl.StringTemplateStaticImpl;

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
