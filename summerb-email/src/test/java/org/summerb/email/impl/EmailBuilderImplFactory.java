/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.email.impl;

import java.util.Locale;

import org.summerb.stringtemplate.api.StringTemplateCompiler;
import org.summerb.stringtemplate.impl.StringTemplateCompilerlImpl;

public class EmailBuilderImplFactory {
	static StringTemplateCompiler stringTemplateCompiler = new StringTemplateCompilerlImpl();

	public static EmailMessageBuilderImpl createEmailBuilderImplUS() {
		EmailMessageBuilderImpl fixture = new EmailMessageBuilderImpl();

		fixture.setFromNameTemplate(stringTemplateCompiler.compile("From: ${from.name}"));
		fixture.setToNameTemplate(stringTemplateCompiler.compile("To: ${to.name}"));
		fixture.setSubjectTemplate(stringTemplateCompiler.compile("Subject: ${body.id}"));
		fixture.setBodyTemplate(stringTemplateCompiler.compile("Body: ${body.msg}"));
		fixture.setLocale(Locale.US);

		return fixture;
	}

	public static EmailMessageBuilderImpl createEmailBuilderImplRU() {
		EmailMessageBuilderImpl fixture = new EmailMessageBuilderImpl();

		fixture.setFromNameTemplate(stringTemplateCompiler.compile("От: ${from.name}"));
		fixture.setToNameTemplate(stringTemplateCompiler.compile("К: ${to.name}"));
		fixture.setSubjectTemplate(stringTemplateCompiler.compile("Тема: ${body.id}"));
		fixture.setBodyTemplate(stringTemplateCompiler.compile("Тело: ${body.msg}"));
		fixture.setLocale(new Locale("ru", "RU"));

		return fixture;
	}

}
