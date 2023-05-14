package org.summerb.validation.jakarta;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.summerb.validation.ValidationContext;

import jakarta.validation.constraints.AssertTrue;

/**
 * Impl of this interface is supposed to be statefull (instance created for each annotation on the
 * bean)
 *
 * @author Sergey Karpushin
 * @param <T> type of annotation, one of jakarta validation constraints. I.e. {@link AssertTrue}
 */
public interface AnnotationProcessor<T extends Annotation> {
  public static final String CTX_REQUIRED = "ctx required";

  void validate(@Nullable Object value, @Nonnull ValidationContext<?> ctx);
}
