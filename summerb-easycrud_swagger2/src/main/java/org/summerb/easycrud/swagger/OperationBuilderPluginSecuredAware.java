package org.summerb.easycrud.swagger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * This extension will retain information on roles specified in {@link Secured}
 * annotation
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class OperationBuilderPluginSecuredAware implements OperationBuilderPlugin {
	@Override
	public void apply(OperationContext context) {
		Set<String> roles = new HashSet<>();
		Secured controllerAnnotation = context.findControllerAnnotation(Secured.class).orNull();
		if (controllerAnnotation != null) {
			roles.addAll(Arrays.asList(controllerAnnotation.value()));
		}

		Secured methodAnnotation = context.findAnnotation(Secured.class).orNull();
		if (methodAnnotation != null) {
			roles.addAll(Arrays.asList(methodAnnotation.value()));
		}

		if (!roles.isEmpty()) {
			context.operationBuilder().extensions(Arrays.asList(new TrimToRoles(roles.toArray(new String[0]))));
		}
	}

	@Override
	public boolean supports(DocumentationType delimiter) {
		return SwaggerPluginSupport.pluginDoesApply(delimiter);
	}
}
