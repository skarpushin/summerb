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
package org.summerb.easycrud.rest.commonpathvars;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.PathVariable;
import org.summerb.easycrud.rest.EasyCrudRestControllerBase;

/**
 * It's similar to {@link PathVariable}, but to be applied at the whole controller level.
 *
 * <p>Designed to be used to annotate controllers, like subclasses of {@link
 * EasyCrudRestControllerBase} and then use variable of type {@link PathVariablesMap} for each
 * request method where you need this. This variable will be resolved by the {@link
 * PathVariablesMapArgumentResolver}, which is expected to be registered in site servlet, i.e.
 *
 * <p><code>
 * &lt;mvc:annotation-driven&gt;
 * &lt;mvc:argument-resolvers&gt;
 * &lt;bean class=&quot;org.summerb.easycrud.rest.PathVariablesMapArgumentResolver&quot; /&gt;
 * &lt;/mvc:argument-resolvers&gt;
 * &lt;/mvc:annotation-driven&gt;
 * </code> In case Swagger is being used then CommonPathVariableOperationBuilderPlugin (see
 * easycrud-swagger2 artifact) needs to be registered as a Spring bean in the context (Swagger will
 * locate it automatically)
 *
 * @author sergeyk
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasCommonPathVariable {
  /** @return The URI template variable to bind to. */
  String name();

  Class<?> type();

  String defaultValue() default "";
}
