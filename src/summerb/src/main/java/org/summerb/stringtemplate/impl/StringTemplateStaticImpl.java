package org.summerb.stringtemplate.impl;

import org.summerb.stringtemplate.api.StringTemplate;

import com.google.common.base.Preconditions;

public class StringTemplateStaticImpl implements StringTemplate {
	private final String content;

	public StringTemplateStaticImpl(String content) {
		Preconditions.checkArgument(content != null, "String template content must not be null");
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String applyTo(Object rootObject) {
		return content;
	}
}
