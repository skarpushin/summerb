package org.summerb.easycrud_swagger.swagger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/** This extension will retain information on roles specified in {@link Secured} annotation */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class OperationBuilderPluginSecuredAware implements OperationBuilderPlugin {
  @Override
  public void apply(OperationContext context) {
    Set<String> roles = new HashSet<>();
    Secured controllerAnnotation = context.findControllerAnnotation(Secured.class).orElse(null);
    if (controllerAnnotation != null) {
      roles.addAll(Arrays.asList(controllerAnnotation.value()));
    }

    Secured methodAnnotation = context.findAnnotation(Secured.class).orElse(null);
    if (methodAnnotation != null) {
      roles.addAll(Arrays.asList(methodAnnotation.value()));
    }

    if (!roles.isEmpty()) {
      context.operationBuilder().extensions(List.of(new TrimToRoles(roles.toArray(new String[0]))));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
