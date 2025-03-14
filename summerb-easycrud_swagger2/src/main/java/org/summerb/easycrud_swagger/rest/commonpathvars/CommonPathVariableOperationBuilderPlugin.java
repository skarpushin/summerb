/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.easycrud_swagger.rest.commonpathvars;

import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.summerb.easycrud.rest.commonpathvars.HasCommonPathVariable;
import org.summerb.easycrud.rest.commonpathvars.HasCommonPathVariables;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * This bean will help Swagger to discovered path variables described using {@link
 * HasCommonPathVariable} and {@link HasCommonPathVariables} annotations
 *
 * @author sergeyk
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class CommonPathVariableOperationBuilderPlugin implements OperationBuilderPlugin {
  protected Logger log = LoggerFactory.getLogger(getClass());

  @Autowired protected TypeResolver typeResolver;

  public CommonPathVariableOperationBuilderPlugin() {}

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @Override
  public void apply(OperationContext opCtx) {
    List<Parameter> ret = new ArrayList<>();
    Optional<HasCommonPathVariable> annSingle =
        opCtx.findControllerAnnotation(HasCommonPathVariable.class);
    if (annSingle.isPresent()) {
      ret.add(addParameter(annSingle.get()));
    }

    Optional<HasCommonPathVariables> annPlural =
        opCtx.findControllerAnnotation(HasCommonPathVariables.class);
    if (annPlural.isPresent()) {
      for (HasCommonPathVariable ann : annPlural.get().value()) {
        ret.add(addParameter(ann));
      }
    }
    opCtx.operationBuilder().parameters(ret);
  }

  protected Parameter addParameter(HasCommonPathVariable ann) {
    ParameterBuilder pb = new ParameterBuilder();
    pb.parameterType("path").name(ann.name()).type(typeResolver.resolve(ann.type()));
    pb.modelRef(new ModelRef("string"));
    pb.required(true);
    if (!"".equals(ann.defaultValue())) {
      pb.defaultValue(ann.defaultValue());
    }
    return pb.build();
  }
}
