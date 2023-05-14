package org.summerb.validation.jakarta.processors;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.summerb.validation.ValidationContext;
import org.summerb.validation.errors.MustMatchPattern;
import org.summerb.validation.jakarta.processors.abstracts.AnnotationProcessorNullableAbstract;

import com.google.common.base.Preconditions;

import jakarta.validation.constraints.Email;

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
