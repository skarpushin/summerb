package org.summerb.validation.jakarta.processors.abstracts;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

import org.springframework.util.StringUtils;
import org.summerb.validation.jakarta.AnnotationProcessor;

import com.google.common.base.Preconditions;

public abstract class AnnotationProcessorAbstract<T extends Annotation>
    implements AnnotationProcessor<T> {

  protected final T annotation;
  protected final String propertyName;

  public AnnotationProcessorAbstract(@Nonnull T annotation, @Nonnull String propertyName) {
    Preconditions.checkArgument(annotation != null, "annotation required");
    Preconditions.checkArgument(StringUtils.hasText(propertyName), "propertyName required");

    this.annotation = annotation;
    this.propertyName = propertyName;
  }
}
