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
package org.summerb.easycrud.rest.commonpathvars;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;

import com.google.common.base.Preconditions;

public class PathVariablesMapArgumentResolver implements HandlerMethodArgumentResolver {
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(PathVariablesMap.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory)
      throws Exception {
    PathVariablesMap ret = new PathVariablesMap();

    Class<?> containingClass = parameter.getContainingClass();
    HasCommonPathVariables annPlural = containingClass.getAnnotation(HasCommonPathVariables.class);
    HasCommonPathVariable annSingle = containingClass.getAnnotation(HasCommonPathVariable.class);
    if (annPlural == null && annSingle == null) {
      return ret;
    }

    if (annSingle != null) {
      processCommonPathVariable(parameter, annSingle, webRequest, binderFactory, ret);
    }

    if (annPlural != null && annPlural.value() != null) {
      for (HasCommonPathVariable ann : annPlural.value()) {
        processCommonPathVariable(parameter, ann, webRequest, binderFactory, ret);
      }
    }

    return ret;
  }

  protected void processCommonPathVariable(
      MethodParameter parameter,
      HasCommonPathVariable ann,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory,
      PathVariablesMap ret)
      throws Exception {
    String paramName = ann.name();
    Object value = findValue(paramName, webRequest, ann);
    value = convertValueIfNeeded(paramName, value, parameter, webRequest, binderFactory, ann);
    addToPathVariables(webRequest, paramName, value);
    ret.put(paramName, value);
  }

  @SuppressWarnings("unchecked")
  protected Object findValue(
      String paramName, NativeWebRequest webRequest, HasCommonPathVariable ann) {
    Map<String, String> uriTemplateVars =
        (Map<String, String>)
            webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    Object value = (uriTemplateVars != null ? uriTemplateVars.get(paramName) : null);
    if (value == null) {
      value = ann.defaultValue();
    }
    Preconditions.checkArgument(
        value != null, "Value must be provided for path variable: " + paramName);
    return value;
  }

  protected Object convertValueIfNeeded(
      String paramName,
      Object value,
      MethodParameter parameter,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory,
      HasCommonPathVariable ann)
      throws Exception {
    WebDataBinder binder = binderFactory.createBinder(webRequest, null, paramName);
    try {
      return binder.convertIfNecessary(value, ann.type(), parameter);
    } catch (ConversionNotSupportedException ex) {
      throw new MethodArgumentConversionNotSupportedException(
          value, ex.getRequiredType(), paramName, parameter, ex.getCause());
    } catch (TypeMismatchException ex) {
      throw new MethodArgumentTypeMismatchException(
          value, ex.getRequiredType(), paramName, parameter, ex.getCause());
    }
  }

  @SuppressWarnings("unchecked")
  protected void addToPathVariables(NativeWebRequest webRequest, String paramName, Object value) {
    String key = View.PATH_VARIABLES;
    int scope = RequestAttributes.SCOPE_REQUEST;
    Map<String, Object> pathVars = (Map<String, Object>) webRequest.getAttribute(key, scope);
    if (pathVars == null) {
      pathVars = new HashMap<>();
      webRequest.setAttribute(key, pathVars, scope);
    }
    pathVars.put(paramName, value);
  }
}
