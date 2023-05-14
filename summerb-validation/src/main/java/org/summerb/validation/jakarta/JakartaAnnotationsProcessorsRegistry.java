package org.summerb.validation.jakarta;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Registry of {@link AnnotationProcessor}.
 *
 * <p>p.s. It does both - identification and instantiation which seem at first as OOD:ISP/SRP
 * violation, but identification is closely related to checking if correct constructor is present,
 * which leads us to instantiation, hence I've decided to live with this for some time
 */
public interface JakartaAnnotationsProcessorsRegistry {

  @Nonnull
  Set<Class<? extends Annotation>> getSupportedAnnotations();

  @Nonnull
  <T extends Annotation> AnnotationProcessor<T> buildAnnotationProcessor(
      @Nonnull T annotation, @Nonnull String propertyName);
}
