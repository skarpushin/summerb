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
package org.summerb.validation.jakarta.processors;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustMatchPattern;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import com.google.common.base.Preconditions;

import javax.validation.constraints.Email;

public class EmailProcessor extends AnnotationProcessorNullableAbstract<Email> {
  private Predicate<String> predicate;

  public EmailProcessor(@Nonnull Email annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
    predicate = PatternProcessor.buildPredicate(annotation.regexp(), annotation.flags());
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(
        value instanceof CharSequence, "Argument must be of CharSequence type");

    String email = value instanceof String ? (String) value : ((CharSequence) value).toString();
    ctx.validEmail(email, propertyName);
    ctx.matches(email, predicate, MustMatchPattern.MESSAGE_CODE, propertyName);
  }
}
