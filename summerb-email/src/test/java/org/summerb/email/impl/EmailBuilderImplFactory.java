/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import org.summerb.stringtemplate.api.StringTemplateFactory;
import org.summerb.stringtemplate.impl.StringTemplateFactorySpElImpl;

public class EmailBuilderImplFactory {
  static StringTemplateFactory stringTemplateFactory = new StringTemplateFactorySpElImpl();

  public static EmailMessageBuilderImpl createEmailBuilderImplUS() {
    EmailMessageBuilderImpl fixture = new EmailMessageBuilderImpl();

    fixture.setFromNameTemplate(stringTemplateFactory.build("From: ${from.name}"));
    fixture.setToNameTemplate(stringTemplateFactory.build("To: ${to.name}"));
    fixture.setSubjectTemplate(stringTemplateFactory.build("Subject: ${body.id}"));
    fixture.setBodyTemplate(stringTemplateFactory.build("Body: ${body.msg}"));
    fixture.setLocale(Locale.US);

    return fixture;
  }

  public static EmailMessageBuilderImpl createEmailBuilderImplRU() {
    EmailMessageBuilderImpl fixture = new EmailMessageBuilderImpl();

    fixture.setFromNameTemplate(stringTemplateFactory.build("От: ${from.name}"));
    fixture.setToNameTemplate(stringTemplateFactory.build("К: ${to.name}"));
    fixture.setSubjectTemplate(stringTemplateFactory.build("Тема: ${body.id}"));
    fixture.setBodyTemplate(stringTemplateFactory.build("Тело: ${body.msg}"));
    fixture.setLocale(new Locale("ru", "RU"));

    return fixture;
  }
}
