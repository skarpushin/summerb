package org.summerb.utils.stringtemplate.impl;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.summerb.utils.stringtemplate.api.StringTemplate;
import org.summerb.utils.stringtemplate.api.StringTemplateCompiler;

public class StringTemplateCompilerlImpl implements StringTemplateCompiler {
	private ExpressionParser expressionParser = new SpelExpressionParser();
	private ParserContext templateParserContext = new ParserContext() {
		@Override
		public boolean isTemplate() {
			return true;
		}

		@Override
		public String getExpressionSuffix() {
			return "}";
		}

		@Override
		public String getExpressionPrefix() {
			return "${";
		}
	};

	@Override
	public StringTemplate compile(String template) {
		try {
			if (!StringUtils.hasText(template)) {
				return new StringTemplateStaticImpl("");
			}

			if (template.contains(templateParserContext.getExpressionPrefix())) {
				Expression compiledTemplate = expressionParser.parseExpression(template, templateParserContext);
				return new StringTemplateImpl(compiledTemplate);
			} else {
				// Looks like a static text without template stuff, so don't
				// waste resources on it
				return new StringTemplateStaticImpl(template);
			}
		} catch (Throwable t) {
			throw new RuntimeException("Failed to compile template", t);
		}
	}

	private static class StringTemplateImpl implements StringTemplate {
		private final Expression compiledTemplate;

		StringTemplateImpl(Expression compiledTemplate) {
			this.compiledTemplate = compiledTemplate;
		}

		@Override
		public String applyTo(Object rootObject) {
			try {
				EvaluationContext context = new StandardEvaluationContext(rootObject);
				return String.valueOf(compiledTemplate.getValue(context));
			} catch (Throwable t) {
				throw new RuntimeException("Failed to execute string template", t);
			}
		}
	}

}
