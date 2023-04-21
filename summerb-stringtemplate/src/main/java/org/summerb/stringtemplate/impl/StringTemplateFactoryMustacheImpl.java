package org.summerb.stringtemplate.impl;

import java.io.StringReader;
import java.io.StringWriter;

import org.springframework.util.StringUtils;
import org.summerb.stringtemplate.api.StringTemplate;
import org.summerb.stringtemplate.api.StringTemplateFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

/**
 * Mustache-based impl of the template.
 * 
 * IMPORTANT: You'll need to include mustache in your pom manually.
 * 
 * <pre>
 *    <groupId>com.github.spullara.mustache.java</groupId>
 *    <artifactId>compiler</artifactId>
 * </pre>
 * 
 * @author Sergey Karpushin
 *
 */
public class StringTemplateFactoryMustacheImpl implements StringTemplateFactory {
	@Override
	public StringTemplate build(String template) {
		try {
			if (!StringUtils.hasText(template)) {
				return new StringTemplateStaticImpl("");
			}

			MustacheFactory factory = new DefaultMustacheFactory();
			Mustache compiled = factory.compile(new StringReader(template), "template");

			return new StringTemplateImpl(compiled);
		} catch (Exception t) {
			throw new IllegalArgumentException("Failed to compile template", t);
		}
	}

	private static class StringTemplateImpl implements StringTemplate {
		private Mustache compiled;

		public StringTemplateImpl(Mustache compiled) {
			this.compiled = compiled;
		}

		@Override
		public String applyTo(Object templateParamsObjects) {
			StringWriter writer = new StringWriter();
			compiled.execute(writer, templateParamsObjects);
			return writer.toString();
		}
	}
}
