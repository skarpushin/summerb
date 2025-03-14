/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.validation.jakarta;

import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an "auto-magical" implementation of {@link JakartaAnnotationsProcessorsRegistry}. It
 * scans package for impls of {@link AnnotationProcessor} and uses Generic parameter to also
 * identify which Annotations are supported.
 *
 * <p>WARNING: It does not support all cases - i.e. if you define generic interface or class that
 * extends {@link AnnotationProcessor} this impl will not be able to process it. In such case you
 * have 2 options:
 *
 * <ol>
 *   <li>Option 1: Extend this class and handle such class in {@link
 *       #getAnnotationClassHandledByAnnotationProcessor(Class)}, while deferring standard cases to
 *       super impl
 *   <li>Option 2: Create your own impl of {@link JakartaAnnotationsProcessorsRegistry}, most likely
 *       hard-coded as it is much faster than impl and testing reflection-based logic, trust me :-)
 * </ol>
 *
 * @author Sergey Karpushin
 */
public class JakartaAnnotationsProcessorsRegistryPackageScanImpl
    implements JakartaAnnotationsProcessorsRegistry {

  protected Logger log = LoggerFactory.getLogger(getClass());

  protected final Map<
          Class<? extends Annotation>, Class<? extends AnnotationProcessor<? extends Annotation>>>
      mapping;

  public JakartaAnnotationsProcessorsRegistryPackageScanImpl() {
    this("org.summerb.validation.jakarta.processors");
  }

  public JakartaAnnotationsProcessorsRegistryPackageScanImpl(String packageName) {
    try {
      Set<Class<? extends AnnotationProcessor<? extends Annotation>>> annotationProcessorClasses =
          Collections.unmodifiableSet(findAnnotationProcessorsClasses(packageName));
      mapping =
          Collections.unmodifiableMap(mapAnnotationClassesToProcessors(annotationProcessorClasses));
    } catch (Exception e) {
      throw new RuntimeException(
          "JakartaAnnotationsProcessorsRegistryPackageScanImpl constructor failed", e);
    }
  }

  @SuppressWarnings("unchecked")
  protected Set<Class<? extends AnnotationProcessor<? extends Annotation>>>
      findAnnotationProcessorsClasses(String packageName) throws IOException {
    return ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
        .filter(clazz1 -> clazz1.getPackageName().equalsIgnoreCase(packageName))
        .map(ClassPath.ClassInfo::load)
        .filter(AnnotationProcessor.class::isAssignableFrom)
        .map(x -> (Class<? extends AnnotationProcessor<? extends Annotation>>) x)
        .filter(x -> findEligibleConstructor(x) != null)
        .collect(Collectors.toSet());
  }

  protected Map<
          Class<? extends Annotation>, Class<? extends AnnotationProcessor<? extends Annotation>>>
      mapAnnotationClassesToProcessors(
          Set<Class<? extends AnnotationProcessor<? extends Annotation>>>
              annotationProcessorClasses) {

    Map<Class<? extends Annotation>, Class<? extends AnnotationProcessor<? extends Annotation>>>
        ret = new HashMap<>();

    for (Class<? extends AnnotationProcessor<? extends Annotation>> annotationProcessorClass :
        annotationProcessorClasses) {
      ret.put(
          getAnnotationClassHandledByAnnotationProcessor(annotationProcessorClass),
          annotationProcessorClass);
    }

    return ret;
  }

  @SuppressWarnings("unchecked")
  protected Constructor<? extends AnnotationProcessor<? extends Annotation>>
      findEligibleConstructor(Class<? extends AnnotationProcessor<? extends Annotation>> subject) {
    for (Constructor<?> ctor : subject.getConstructors()) {
      if (ctor.getParameterCount() != 2) {
        continue;
      }

      if (!Annotation.class.isAssignableFrom(ctor.getParameterTypes()[0])) {
        continue;
      }

      if (!String.class.equals(ctor.getParameterTypes()[1])) {
        continue;
      }

      return (Constructor<? extends AnnotationProcessor<? extends Annotation>>) ctor;
    }

    log.warn("{} does not have eligible constructor", subject);
    return null;
  }

  @SuppressWarnings("unchecked")
  @SuppressFBWarnings(
      value = "NP_NULL_ON_SOME_PATH",
      justification = "checked by preceeding Preconditions.checkState")
  protected <T extends Annotation> Class<T> getAnnotationClassHandledByAnnotationProcessor(
      Class<? extends AnnotationProcessor<? extends Annotation>> annotationProcessorClass) {

    Set<Type> typesToCheck = new HashSet<>();
    findParentTypesToCheck(annotationProcessorClass, typesToCheck);

    ParameterizedType ourParametrizedType = null;
    for (Type candidate : typesToCheck) {
      if (!(candidate instanceof ParameterizedType)) {
        continue;
      }
      ParameterizedType candidatePt = (ParameterizedType) candidate;

      if (AnnotationProcessor.class.isAssignableFrom((Class<?>) candidatePt.getRawType())) {
        ourParametrizedType = candidatePt;
        break;
      }
    }

    // NOTE: ourParametrizedType will not be null here, thats why we cannot kill pitest mutation
    // here. But we still put a check here, just in case
    Preconditions.checkState(
        ourParametrizedType != null,
        "Cannot find ParametrizedType of AnnotationProcessor of class: %s",
        annotationProcessorClass);

    Type ret = ourParametrizedType.getActualTypeArguments()[0];
    Preconditions.checkArgument(
        Annotation.class.isAssignableFrom((Class<?>) ret),
        "This impl can correctly handle only cases when Annotation is a first parameter of a first encountered parametrized type. %s cannot be correctly processed",
        annotationProcessorClass);

    return (Class<T>) ret;
  }

  protected void findParentTypesToCheck(Class<?> clazz, Set<Type> ret) {
    Set<Type> founds = new HashSet<>();
    founds.addAll(Arrays.asList(clazz.getGenericInterfaces()));
    if (clazz.getGenericSuperclass() != null
        && !Object.class.equals(clazz.getGenericSuperclass())) {
      founds.add(clazz.getGenericSuperclass());
    }
    // found.removeIf(ret::contains); // too paranoid
    ret.addAll(founds);

    for (Type found : founds) {
      if (found instanceof Class) {
        findParentTypesToCheck((Class<?>) found, ret);
        // } else if (found instanceof ParameterizedType) { // case not supported as we cannot get
        // parent ParameterizedType
      }
    }
  }

  @Override
  public Set<Class<? extends Annotation>> getSupportedAnnotations() {
    return mapping.keySet();
  }

  @SuppressWarnings("unchecked")
  @SuppressFBWarnings(
      value = {"NP_NULL_ON_SOME_PATH", "NP_NULL_PARAM_DEREF"},
      justification = "Preconditions.checkArgument checks for null")
  @Override
  public <T extends Annotation> AnnotationProcessor<T> buildAnnotationProcessor(
      T annotation, String propertyName) {

    Class<? extends AnnotationProcessor<? extends Annotation>> processorClass =
        mapping.get(annotation.annotationType());

    Preconditions.checkArgument(
        processorClass != null,
        "processorClass not found for annotation: %s",
        annotation.getClass());

    Constructor<? extends AnnotationProcessor<? extends Annotation>> constructor =
        findEligibleConstructor(processorClass);

    // NOTE: We're not checking if `constructor == null` because if class made it to our mapping, it
    // means that presence of eligible constructor was already checked

    try {
      return (AnnotationProcessor<T>) constructor.newInstance(annotation, propertyName);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to buildAnnotationProcessor for annotation "
              + annotation
              + ", propertyName "
              + propertyName,
          e);
    }
  }
}
