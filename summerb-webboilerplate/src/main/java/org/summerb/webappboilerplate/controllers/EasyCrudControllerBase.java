/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.webappboilerplate.controllers;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudTableAuthStrategy;
import org.summerb.easycrud.api.HasEasyCrudTableAuthStrategy;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.mvc.filter.FilteringParamsToQueryConverter;
import org.summerb.easycrud.mvc.filter.FilteringParamsToQueryConverterImpl;
import org.summerb.easycrud.mvc.model.EasyCrudQueryParams;
import org.summerb.easycrud.rest.EasyCrudRestControllerBase;
import org.summerb.security.api.exceptions.NotAuthorizedException;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * 
 * @author sergey.karpushin
 *
 * @param <TId>
 * @param <TDto>
 * @param <TEasyCrudService>
 * 
 * @deprecated use {@link EasyCrudRestControllerBase} instead, it's better than
 *             this. This controller happened to be very clumsy. Will remove it
 *             in the near future. Most of it's methods are REST API, but naming
 *             is discouraged and 3 regular MVC actions brings entropy
 */
public class EasyCrudControllerBase<TId, TDto extends HasId<TId>, TEasyCrudService extends EasyCrudService<TId, TDto>>
		extends ControllerBase implements InitializingBean {

	protected static final String ATTR_AJAX_CREATED_OBJ = "ajaxCreatedObj";
	public static final String ATTR_LIST = "easyCrudList";
	protected static final String ATTR_ONE = "easyCrudOne";
	protected static final String ATTR_ID = "id";
	protected static final String ATTR_PERMISSIONS = "easyCrudPermissions";
	protected static final String ATTR_PERM_CREATE = "create";
	protected static final String ATTR_PERM_UPDATE = "update";
	protected static final String ATTR_PERM_DELETE = "delete";

	protected TEasyCrudService service;

	protected String viewNameForList;
	protected String viewNameForOne;
	protected String viewNameForJs = "easycrud/easy-crud-ctrl";
	protected long initialPageSize = 0;

	protected Gson gson = new Gson();
	private FilteringParamsToQueryConverter filteringParamsToQueryConverter = new FilteringParamsToQueryConverterImpl();

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		Preconditions.checkState(service != null, "service Expected to be injected by IoC");
		Preconditions.checkState(StringUtils.hasText(viewNameForList),
				"viewNameForList Expected to be init by subclass");
		Preconditions.checkState(StringUtils.hasText(viewNameForOne), "viewNameForOne Expected to be init by subclass");
		Preconditions.checkState(StringUtils.hasText(viewNameForJs), "viewNameForJs Expected to be init by subclass");
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getList(HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView(viewNameForList);
		if (initialPageSize > 0) {
			ret.addObject(ATTR_LIST, service.find(new PagerParams(0, initialPageSize), null));
		}

		// TBD: Shouldn't we cache such things?
		Map<String, Boolean> perms = getPermissionsMapForCurrentUser();
		ret.addObject(ATTR_PERMISSIONS, gson.toJson(perms));
		return ret;
	}

	protected Map<String, Boolean> getPermissionsMapForCurrentUser() {
		Map<String, Boolean> ret = new HashMap<String, Boolean>();
		if (service instanceof HasEasyCrudTableAuthStrategy) {
			resolveEasyCrudTableAuthStrategyPermissions(((HasEasyCrudTableAuthStrategy) service).getTableAuthStrategy(),
					ret);
		} else {
			throw new IllegalStateException("Not supported service type, can't resolve permissions for: " + service);
		}

		return ret;
	}

	private void resolveEasyCrudTableAuthStrategyPermissions(EasyCrudTableAuthStrategy auth, Map<String, Boolean> ret) {
		try {
			auth.assertAuthorizedToCreate();
			ret.put(ATTR_PERM_CREATE, true);
		} catch (NotAuthorizedException nae) {
			ret.put(ATTR_PERM_CREATE, false);
		}
		try {
			auth.assertAuthorizedToUpdate();
			ret.put(ATTR_PERM_UPDATE, true);
		} catch (NotAuthorizedException nae) {
			ret.put(ATTR_PERM_UPDATE, false);
		}
		try {
			auth.assertAuthorizedToDelete();
			ret.put(ATTR_PERM_DELETE, true);
		} catch (NotAuthorizedException nae) {
			ret.put(ATTR_PERM_DELETE, false);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "ajaxList")
	public @ResponseBody Map<String, ? extends Object> ajaxList(@RequestBody EasyCrudQueryParams filteringParams,
			HttpServletResponse response) throws Exception {
		Query query = filteringParamsToQueryConverter.convert(filteringParams.getFilterParams(), service.getRowClass());
		PaginatedList<TDto> results = service.find(filteringParams.getPagerParams(), query,
				filteringParams.getOrderBy());
		return Collections.singletonMap(ATTR_LIST, results);
	}

	@RequestMapping(method = RequestMethod.GET, value = "ctrl.js")
	public String getClientSideControlsJs(WebRequest request, HttpServletResponse response) {
		response.setDateHeader("Last-Modified", new Date().getTime());
		response.setDateHeader("Expires", new Date().getTime() + 1000 * 60 * 60);
		response.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		response.setHeader("Cache-Control", "max-age: 84600");

		return viewNameForJs;
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> ajaxCreate(@RequestBody TDto dto, HttpServletResponse response)
			throws Exception {
		TDto result = service.create(dto);
		return Collections.singletonMap(ATTR_AJAX_CREATED_OBJ, result);
	}

	@RequestMapping(method = RequestMethod.GET, value = "ajaxDelete/{entityId}")
	public @ResponseBody Map<String, ? extends Object> ajaxDelete(@PathVariable("entityId") TId id,
			HttpServletResponse response) throws Exception {
		service.deleteById(id);
		return Collections.singletonMap(ATTR_ID, id);
	}

	@RequestMapping(method = RequestMethod.GET, value = "{entityId}")
	public ModelAndView getOne(@PathVariable("entityId") TId id, HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView(viewNameForOne);
		ret.addObject(ATTR_ONE, service.findById(id));
		return ret;
	}

	@RequestMapping(method = RequestMethod.POST, value = "{entityId}")
	public @ResponseBody Map<String, ? extends Object> ajaxUpdate(@RequestBody TDto dto, HttpServletResponse response)
			throws Exception {
		TDto result = service.update(dto);
		return Collections.singletonMap(ATTR_ONE, result);
	}

	public TEasyCrudService getService() {
		return service;
	}

	public void setService(TEasyCrudService service) {
		this.service = service;
	}

	public long getInitialPageSize() {
		return initialPageSize;
	}

	public void setInitialPageSize(long defaultSizeForList) {
		this.initialPageSize = defaultSizeForList;
	}

	public FilteringParamsToQueryConverter getFilteringParamsToQueryConverter() {
		return filteringParamsToQueryConverter;
	}

	public void setFilteringParamsToQueryConverter(FilteringParamsToQueryConverter filteringParamsToQueryConverter) {
		this.filteringParamsToQueryConverter = filteringParamsToQueryConverter;
	}

}
