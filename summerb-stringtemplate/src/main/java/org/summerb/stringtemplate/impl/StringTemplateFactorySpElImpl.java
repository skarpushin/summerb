/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.stringtemplate.impl;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.summerb.stringtemplate.api.StringTemplate;
import org.summerb.stringtemplate.api.StringTemplateFactory;

public class StringTemplateFactorySpElImpl implements StringTemplateFactory {
  private ExpressionParser expressionParser = new SpelExpressionParser();
  private ParserContext templateParserContext =
      new ParserContext() {
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
  public StringTemplate build(String template) {
    try {
      if (!StringUtils.hasText(template)) {
        return new StringTemplateStaticImpl("");
      }

      if (template.contains(templateParserContext.getExpressionPrefix())) {
        Expression compiledTemplate =
            expressionParser.parseExpression(template, templateParserContext);
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
