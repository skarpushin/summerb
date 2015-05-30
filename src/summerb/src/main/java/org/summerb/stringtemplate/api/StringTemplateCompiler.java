package org.summerb.stringtemplate.api;

/**
 * Interface for interacting with string template compiler. Compiler able to
 * parse template and provide compiled result.
 * 
 * @author skarpushin
 * 
 */
public interface StringTemplateCompiler {
	StringTemplate compile(String template);
}
