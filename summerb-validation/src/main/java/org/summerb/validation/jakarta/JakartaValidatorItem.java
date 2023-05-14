package org.summerb.validation.jakarta;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.springframework.util.StringUtils;
import org.summerb.validation.ValidationContext;

import com.google.common.base.Preconditions;

public class JakartaValidatorItem {
  private final String propertyName;
  private final Annotation annotation;
  private final AnnotationProcessor<Annotation> processor;
  private final Function<Object, Object> valueGetter;

  public JakartaValidatorItem(
      @Nonnull String propertyName,
      @Nonnull Annotation annotation,
      @Nonnull AnnotationProcessor<Annotation> processor,
      @Nonnull Function<Object, Object> valueGetter) {
    super();

    Preconditions.checkArgument(StringUtils.hasText(propertyName));
    Preconditions.checkArgument(annotation != null);
    Preconditions.checkArgument(processor != null);
    Preconditions.checkArgument(valueGetter != null);

    this.propertyName = propertyName;
    this.annotation = annotation;
    this.processor = processor;
    this.valueGetter = valueGetter;
  }

  public void process(@Nonnull Object bean, @Nonnull ValidationContext<?> ctx) {
    processor.validate(valueGetter.apply(bean), ctx);
  }

  public @Nonnull AnnotationProcessor<Annotation> getProcessor() {
    return processor;
  }

  public @Nonnull Function<Object, Object> getValueGetter() {
    return valueGetter;
  }

  public @Nonnull String getPropertyName() {
    return propertyName;
  }

  public @Nonnull Annotation getAnnotation() {
    return annotation;
  }
}
