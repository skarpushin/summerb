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
package org.summerb.validation.jakarta;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.summerb.methodCapturers.PropertyNameResolverImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class JakartaValidationBeanProcessorImpl implements JakartaValidationBeanProcessor {
  protected Logger log = LoggerFactory.getLogger(getClass());

  protected JakartaAnnotationsProcessorsRegistry jakartaAnnotationsProcessorsRegistry;

  public JakartaValidationBeanProcessorImpl(
      JakartaAnnotationsProcessorsRegistry jakartaAnnotationsProcessorsRegistry) {

    Preconditions.checkArgument(
        jakartaAnnotationsProcessorsRegistry != null,
        "jakartaAnnotationsProcessorsRegistry required");

    this.jakartaAnnotationsProcessorsRegistry = jakartaAnnotationsProcessorsRegistry;
  }

  @Override
  public List<JakartaValidatorItem> getValidationsFor(Class<?> clazz) {
    Set<Class<? extends Annotation>> supportedAnnotations =
        jakartaAnnotationsProcessorsRegistry.getSupportedAnnotations();

    List<JakartaValidatorItem> ret = new ArrayList<>();
    findValidatorsOnGettersAndSetters(clazz, supportedAnnotations, ret);
    findValidatorsFields(clazz, supportedAnnotations, ret);
    return ret;
  }

  protected void findValidatorsFields(
      Class<?> clazz,
      Set<Class<? extends Annotation>> supportedAnnotations,
      List<JakartaValidatorItem> out) {

    List<Field> fields = getAllFields(new LinkedList<>(), clazz);
    for (Field field : fields) {
      if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
        continue;
      }

      Annotation[] annotations = field.getAnnotations();
      if (annotations.length == 0
          || Arrays.stream(annotations)
              .noneMatch(x -> supportedAnnotations.contains(x.annotationType()))) {
        continue;
      }

      // simple case - if there is a property with same name
      PropertyDescriptor property = findPropertyDescriptorForField(clazz, field);
      if (property != null) {
        createValidators(annotations, property.getReadMethod(), supportedAnnotations, out);
        continue;
      }

      // complicated case -- seems like a discrepancy that we should not support
      throw new IllegalArgumentException(
          "Class "
              + clazz
              + " has annotated field "
              + field.getName()
              + " but it does not have matching property getter/setter. Please rectify -- add matching getter/setter.");
    }
  }

  @VisibleForTesting
  protected PropertyDescriptor findPropertyDescriptorForField(Class<?> clazz, Field field) {
    return BeanUtils.getPropertyDescriptor(clazz, field.getName());
  }

  @VisibleForTesting
  protected PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
    return BeanUtils.getPropertyDescriptors(clazz);
  }

  public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
    fields.addAll(Arrays.asList(type.getDeclaredFields()));
    Class<?> superclass = type.getSuperclass();
    if (!Object.class.equals(superclass)) {
      getAllFields(fields, superclass);
    }
    return fields;
  }

  protected void findValidatorsOnGettersAndSetters(
      Class<?> clazz,
      Set<Class<? extends Annotation>> supportedAnnotations,
      List<JakartaValidatorItem> out) {

    PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      Method readMethod = propertyDescriptor.getReadMethod();
      if (readMethod != null && Object.class.equals(readMethod.getDeclaringClass())) {
        continue;
      }

      Method writeMethod = propertyDescriptor.getWriteMethod();

      if (readMethod == null || writeMethod == null) {
        log.warn(
            "Property \"{}\" doesn't have both methods. Getter: \"{}\", Setter: \"{}\"",
            propertyDescriptor.getName(),
            readMethod,
            writeMethod);
        continue;
      }

      createValidators(readMethod.getAnnotations(), readMethod, supportedAnnotations, out);
      createValidators(writeMethod.getAnnotations(), readMethod, supportedAnnotations, out);
    }
  }

  protected void createValidators(
      Annotation[] annotations,
      Method getter,
      Set<Class<? extends Annotation>> supportedAnnotations,
      List<JakartaValidatorItem> out) {

    if (annotations.length == 0) {
      return;
    }

    String propertyName = PropertyNameResolverImpl.getPropertyNameFromGetterName(getter);
    Function<Object, Object> valueGetter = null;

    for (Annotation annotation : annotations) {
      if (!supportedAnnotations.contains(annotation.annotationType())) {
        continue;
      }

      boolean hasSameAnnotationForSameProperty =
          out.stream()
              .anyMatch(
                  x ->
                      propertyName.equals(x.getPropertyName())
                          && annotation
                              .annotationType()
                              .equals(x.getAnnotation().annotationType()));
      Preconditions.checkArgument(
          !hasSameAnnotationForSameProperty,
          "Duplicate annotation %s on property %s (check your class hierarchy)",
          annotation,
          propertyName);

      if (valueGetter == null) {
        valueGetter =
            x -> {
              try {
                return getter.invoke(x);
              } catch (Exception e) {
                throw new RuntimeException(
                    "Failed to get value for property " + propertyName + " using method " + getter,
                    e);
              }
            };
      }

      AnnotationProcessor<Annotation> processor =
          jakartaAnnotationsProcessorsRegistry.buildAnnotationProcessor(annotation, propertyName);
      out.add(new JakartaValidatorItem(propertyName, annotation, processor, valueGetter));
    }
  }
}
