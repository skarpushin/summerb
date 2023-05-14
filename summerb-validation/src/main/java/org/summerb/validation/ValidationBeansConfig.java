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
 * <p>It is not marked with {@link Configuration} annotation intentionally to avoid automatic
 * registration in case you don't need it, or you'd like to setup {@link ValidationContextFactory}
 * without {@link JakartaValidator}
 *
 * @author Sergey Karpushin
 */
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
