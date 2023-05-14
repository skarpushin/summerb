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
package org.summerb.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactory;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameObtainerFactory;
import org.summerb.methodCapturers.PropertyNameObtainerFactoryImpl;
import org.summerb.validation.jakarta.JakartaAnnotationsProcessorsRegistry;
import org.summerb.validation.jakarta.JakartaAnnotationsProcessorsRegistryPackageScanImpl;
import org.summerb.validation.jakarta.JakartaValidationBeanProcessor;
import org.summerb.validation.jakarta.JakartaValidationBeanProcessorCachedImpl;
import org.summerb.validation.jakarta.JakartaValidationBeanProcessorImpl;
import org.summerb.validation.jakarta.JakartaValidator;
import org.summerb.validation.jakarta.JakartaValidatorImpl;

/**
 * Validations beans config template.
 *
 * @author Sergey Karpushin
 */
@Configuration
public class ValidationBeansConfig {

  @Bean
  public MethodCapturerProxyClassFactory methodCapturerProxyClassFactory() {
    // NOTE: Pitest will incorrectly report that this is not properly covered. But if you replace
    // return value with null tests will fail. I guess this is because this is Spring Config that is
    // not being mutated properly by pitest
    return new MethodCapturerProxyClassFactoryImpl();
  }

  @Bean
  public JakartaAnnotationsProcessorsRegistry jakartaAnnotationsProcessorsRegistry() {
    return new JakartaAnnotationsProcessorsRegistryPackageScanImpl();
  }

  @Bean
  public JakartaValidationBeanProcessor jakartaValidationBeanProcessor(
      JakartaAnnotationsProcessorsRegistry jakartaAnnotationsProcessorsRegistry) {
    return new JakartaValidationBeanProcessorCachedImpl(
        new JakartaValidationBeanProcessorImpl(jakartaAnnotationsProcessorsRegistry));
  }

  @Bean
  public JakartaValidator jakartaValidator(
      JakartaValidationBeanProcessor jakartaValidationBeanProcessor) {
    return new JakartaValidatorImpl(jakartaValidationBeanProcessor);
  }

  @Bean
  public PropertyNameObtainerFactory propertyNameObtainerFactory(
      MethodCapturerProxyClassFactory methodCapturerProxyClassFactory) {
    return new PropertyNameObtainerFactoryImpl(methodCapturerProxyClassFactory);
  }

  @Bean
  public ValidationContextFactory validationContextFactory(
      JakartaValidator jakartaValidator, PropertyNameObtainerFactory propertyNameObtainerFactory) {
    return new ValidationContextFactoryImpl(propertyNameObtainerFactory, jakartaValidator);
  }
}
