package org.summerb.approaches.jdbccrud.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This ArgumentResolver will map request parameters to a given method
 * parameter. Only parameters with matching names will be mapped
 * 
 * This impl is brutally simple and a little "hack-ish". It basically relies on
 * {@link Gson} as deserializer/mapper from request parameters.
 * 
 * If at some point this approach will be deemed wrong then we'll need to impl
 * something more sophisticated, similar to {@link BeanPropertyRowMapper}
 * 
 * @author sergeyk
 *
 */
public class PojoFieldsArgumentResolver implements HandlerMethodArgumentResolver {
	private static final String ATTR_NAME = "PojoFieldsArgumentResolver.cachedJson";

	private Set<Class<?>> supportedClasses;
	private boolean acceptSubClasses = true;
	private Gson gson = new GsonBuilder().create();

	public PojoFieldsArgumentResolver() {
		this(Arrays.asList(PagerParams.class.getName(), OrderBy.class.getName()));
	}

	public PojoFieldsArgumentResolver(List<String> supportedClassNames) {
		this(supportedClassNames.stream().map(x -> {
			try {
				return Class.forName(x);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to load class: " + x, e);
			}
		}).collect(Collectors.toSet()));
	}

	public PojoFieldsArgumentResolver(Set<Class<?>> supportedClassNames) {
		this.supportedClasses = supportedClassNames;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		if (supportedClasses.contains(type)) {
			return true;
		}

		if (acceptSubClasses) {
			return supportedClasses.stream().anyMatch(x -> x.isAssignableFrom(type));
		}

		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		int scope = RequestAttributes.SCOPE_REQUEST;
		String paramsJson = (String) webRequest.getAttribute(ATTR_NAME, scope);
		if (paramsJson == null) {
			Map<Object, Object> map = webRequest.getParameterMap().entrySet().stream()
					.collect(Collectors.toMap(x -> x.getKey(), y -> y.getValue()[0]));
			String json = gson.toJson(map);
			webRequest.setAttribute(ATTR_NAME, json, scope);
		}

		return gson.fromJson(paramsJson, parameter.getParameterType());
	}

	public boolean isAcceptSubClasses() {
		return acceptSubClasses;
	}

	public void setAcceptSubClasses(boolean acceptSubClasses) {
		this.acceptSubClasses = acceptSubClasses;
	}
}
