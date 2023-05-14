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

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;

public class PatternProcessor extends AnnotationProcessorNullableAbstract<Pattern> {

  private Predicate<String> predicate;

  public PatternProcessor(@Nonnull Pattern annotation, @Nonnull String propertyName) {
    super(annotation, propertyName);
    predicate = buildPredicate(annotation.regexp(), annotation.flags());
  }

  @Override
  protected void internalValidate(Object value, ValidationContext<?> ctx) {
    Preconditions.checkArgument(
        value instanceof CharSequence, "Argument must be of CharSequence type");

    String valueStr = value instanceof String ? (String) value : ((CharSequence) value).toString();
    ctx.matches(valueStr, predicate, MustMatchPattern.MESSAGE_CODE, propertyName);
  }

  public static @Nonnull Predicate<String> buildPredicate(
      @Nonnull String regexp, @Nonnull Flag[] flags) {
    return java.util.regex.Pattern.compile(regexp, combineFlags(flags)).asMatchPredicate();
  }

  public static int combineFlags(@Nonnull Flag[] flags) {
    int ret = 0;
    for (Flag flag : flags) {
      ret |= flag.getValue();
    }
    return ret;
  }
}
