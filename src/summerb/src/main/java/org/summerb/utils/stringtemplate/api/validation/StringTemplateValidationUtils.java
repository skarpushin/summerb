package org.summerb.utils.stringtemplate.api.validation;

import org.summerb.approaches.validation.ValidationContext;
import org.summerb.utils.stringtemplate.api.StringTemplateCompiler;

import com.google.common.base.Preconditions;

/**
 * Simple utility class which provides validation function(s) for string
 * templates
 * 
 * @author skarpushin
 * 
 */
public class StringTemplateValidationUtils {

	/**
	 * Validate string template basically using compiler. If compile successful,
	 * that template validates fine
	 * 
	 * @param stringTemplateCompiler
	 *            compiler to validate against
	 * @param stringTemplate
	 *            template to validate
	 * @param ctx
	 *            validation context
	 * @param fieldToken
	 *            field token
	 */
	public static void validateStringTemplate(StringTemplateCompiler stringTemplateCompiler, String stringTemplate,
			ValidationContext ctx, String fieldToken) {

		Preconditions.checkArgument(stringTemplateCompiler != null);
		Preconditions.checkArgument(ctx != null);

		try {
			stringTemplateCompiler.compile(stringTemplate);
		} catch (Throwable t) {
			ctx.getErrors().add(new StringTemplateValidationError(fieldToken, t));
		}
	}
}
