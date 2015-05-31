package org.summerb.springvmc.easycrud;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudSimpleAuthStrategy;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.impl.EasyCrudServiceSimpleAuthImpl;
import org.summerb.i18n.I18nUtils;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.springvmc.controllers.ControllerBase;
import org.summerb.springvmc.easycrud.model.EasyCrudQueryParams;
import org.summerb.springvmc.easycrud.model.FilteringParam;
import org.summerb.springvmc.utils.ValidationErrorsVm;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * 
 * @author sergey.karpushin
 *
 * @param <TId>
 * @param <TDto>
 * @param <TEasyCrudService>
 */
public class EasyCrudControllerBase<TId, TDto extends HasId<TId>, TEasyCrudService extends EasyCrudService<TId, TDto>>
		extends ControllerBase implements InitializingBean {

	protected static final String ATTR_AJAX_ERROR = "ajaxErrorMessage";
	protected static final String ATTR_AJAX_CREATED_OBJ = "ajaxCreatedObj";
	protected static final String ATTR_LIST = "easyCrudList";
	protected static final String ATTR_ONE = "easyCrudOne";
	protected static final String ATTR_ID = "id";
	protected static final String ATTR_PERMISSIONS = "easyCrudPermissions";
	protected static final String ATTR_PERM_CREATE = "create";
	protected static final String ATTR_PERM_UPDATE = "update";
	protected static final String ATTR_PERM_DELETE = "delete";

	private MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

	protected TEasyCrudService service;

	protected String viewNameForList;
	protected String viewNameForOne;
	protected String viewNameForJs = "easycrud/easy-crud-ctrl";
	protected long initialPageSize = 0;

	private Gson gson = new Gson();

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(service != null, "service Expected to be injected by IoC");
		Preconditions.checkState(StringUtils.hasText(viewNameForList),
				"viewNameForList Expected to be init by subclass");
		Preconditions.checkState(StringUtils.hasText(viewNameForJs), "viewNameForJs Expected to be init by subclass");
	}

	@RequestMapping(method = RequestMethod.GET, value = "ctrl.js")
	public String getClientSideControlsJs(WebRequest request, HttpServletResponse response) {
		response.setDateHeader("Last-Modified", new Date().getTime());
		response.setDateHeader("Expires", new Date().getTime() + 1000 * 60 * 60);
		response.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		response.setHeader("Cache-Control", "max-age: 84600");

		return viewNameForJs;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getList(Model model, HttpServletResponse response) throws Exception {
		if (initialPageSize > 0) {
			model.addAttribute(ATTR_LIST, service.query(new PagerParams(0, initialPageSize), null));
		}

		Map<String, Boolean> perms = getPermissionsMapForCurrentUser();
		model.addAttribute(ATTR_PERMISSIONS, gson.toJson(perms));
		return viewNameForList;
	}

	@SuppressWarnings("rawtypes")
	protected Map<String, Boolean> getPermissionsMapForCurrentUser() {
		Map<String, Boolean> ret = new HashMap<String, Boolean>();
		if (service instanceof EasyCrudServiceSimpleAuthImpl) {
			EasyCrudSimpleAuthStrategy authStrategy = ((EasyCrudServiceSimpleAuthImpl) service).getSimpleAuthStrategy();
			resolveEasyCrudSimpleAuthStrategyPermissions(authStrategy, ret);
		} else {
			throw new IllegalStateException("Not supported service type, can't resolve permissions");
		}

		return ret;
	}

	private void resolveEasyCrudSimpleAuthStrategyPermissions(EasyCrudSimpleAuthStrategy auth, Map<String, Boolean> ret) {
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
			HttpServletResponse response) {
		try {
			return Collections.singletonMap(
					ATTR_LIST,
					service.query(filteringParams.getPagerParams(),
							filterParamsToQuery(filteringParams.getFilterParams()), filteringParams.getOrderBy()));
		} catch (Exception t) {
			log.error("Failed to search for entities", t);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return Collections.singletonMap(ATTR_AJAX_ERROR, I18nUtils.buildMessagesChain(t, applicationContext));
		}
	}

	protected Query filterParamsToQuery(Map<String, FilteringParam> filterParams) {
		if (filterParams == null || filterParams.isEmpty()) {
			return null;
		}

		try {
			Query ret = Query.n();
			for (Entry<String, FilteringParam> entry : filterParams.entrySet()) {
				String fname = entry.getKey();
				String[] values = entry.getValue().getValues();
				Class<?> type = getFieldType(fname);
				boolean isStringType = String.class.equals(type);
				boolean isNumericType = int.class.equals(type) || long.class.equals(type) || Integer.class.equals(type)
						|| Long.class.equals(type);
				boolean isBooleanType = boolean.class.equals(type) || Boolean.class.equals(type);

				try {
					switch (entry.getValue().getCommand()) {
					case FilteringParam.CMD_BETWEEN:
						ret.between(fname, Long.parseLong(values[0]), Long.parseLong(values[1]));
						break;
					case FilteringParam.CMD_CONTAIN:
						ret.contains(fname, values[0]);
						break;
					case FilteringParam.CMD_EQUALS:
						if (isNumericType) {
							ret.eq(fname, Long.parseLong(values[0]));
						} else if (isStringType) {
							ret.eq(fname, values[0]);
						} else {
							throw new IllegalArgumentException("Field type " + type
									+ " is not supported for predicate " + entry.getValue().getCommand());
						}
						break;
					case FilteringParam.CMD_GREATER:
						ret.between(fname, Long.parseLong(values[0]) + 1, Long.MAX_VALUE);
						break;
					case FilteringParam.CMD_GREATER_OR_EQUAL:
						ret.between(fname, Long.parseLong(values[0]), Long.MAX_VALUE);
						break;
					case FilteringParam.CMD_IN:
						if (isNumericType) {
							ret.in(fname, convertToLongs(values));
						} else if (isStringType) {
							ret.in(fname, values);
						} else {
							throw new IllegalArgumentException("Field type " + type
									+ " is not supported for predicate " + entry.getValue().getCommand());
						}
						break;
					case FilteringParam.CMD_LESS:
						ret.between(fname, Long.MIN_VALUE, Long.parseLong(values[0]) + 1);
						break;
					case FilteringParam.CMD_LESS_OR_EQUAL:
						ret.between(fname, Long.MIN_VALUE, Long.parseLong(values[0]));
						break;
					case FilteringParam.CMD_NOT_BETWEEN:
						ret.notBetween(fname, Long.parseLong(values[0]), Long.parseLong(values[1]));
						break;
					case FilteringParam.CMD_NOT_CONTAIN:
						ret.notContains(fname, values[0]);
						break;
					case FilteringParam.CMD_NOT_EQUALS:
						if (isNumericType) {
							ret.ne(fname, Long.parseLong(values[0]));
						} else if (isStringType) {
							ret.ne(fname, values[0]);
						} else if (isBooleanType) {
							ret.ne(fname, Boolean.parseBoolean(values[0]));
						} else {
							throw new IllegalArgumentException("Field type " + type
									+ " is not supported for predicate " + entry.getValue().getCommand());
						}
						break;
					case FilteringParam.CMD_NOT_IN:
						if (isNumericType) {
							ret.notIn(fname, convertToLongs(values));
						} else if (isStringType) {
							ret.notIn(fname, values);
						} else {
							throw new IllegalArgumentException("Field type " + type
									+ " is not supported for predicate " + entry.getValue().getCommand());
						}
						break;
					default:
						throw new IllegalArgumentException("Unsupported filtering predicate: "
								+ entry.getValue().getCommand());
					}
				} catch (NumberFormatException nfe) {
					throw new RuntimeException("Can't parse Long-type value", nfe);
				}
			}
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to parse filtering params", t);
		}
	}

	private Long[] convertToLongs(String[] values) {
		Long[] ret = new Long[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = Long.parseLong(values[i]);
		}
		return ret;
	}

	private Class<?> getFieldType(String fname) {
		try {
			return service.getDtoClass().getMethod("get" + fname.substring(0, 1).toUpperCase() + fname.substring(1))
					.getReturnType();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to resolve field type", t);
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, ? extends Object> ajaxCreate(@RequestBody TDto dto, HttpServletResponse response) {
		try {
			TDto result = service.create(dto);
			return Collections.singletonMap(ATTR_AJAX_CREATED_OBJ, result);
		} catch (FieldValidationException fve) {
			log.debug("Failed to create entity", fve);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ValidationErrorsVm vepm = new ValidationErrorsVm(fve.getErrors());
			return Collections.singletonMap(ATTR_VALIDATION_ERRORS, vepm.getMsg());
		} catch (Exception t) {
			log.error("Failed to create", t);
			response.setStatus(t instanceof FieldValidationException ? HttpServletResponse.SC_BAD_REQUEST
					: HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return Collections.singletonMap(ATTR_AJAX_ERROR, I18nUtils.buildMessagesChain(t, applicationContext));
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "ajaxDelete/{entityId}")
	public @ResponseBody Map<String, ? extends Object> ajaxDelete(@PathVariable("entityId") TId id,
			HttpServletResponse response) {
		try {
			service.deleteById(id);
			return Collections.singletonMap(ATTR_ID, id);
		} catch (Exception t) {
			log.error("Failed to delete", t);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return Collections.singletonMap(ATTR_AJAX_ERROR, I18nUtils.buildMessagesChain(t, applicationContext));
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "{entityId}")
	public ModelAndView getOne(@PathVariable("entityId") TId id, HttpServletResponse response) throws Exception {
		Preconditions.checkState(StringUtils.hasText(viewNameForOne), "viewNameForOne Expected to be init by subclass");
		ModelAndView ret = new ModelAndView(viewNameForOne);
		ret.addObject(ATTR_ONE, service.findById(id));
		ret.addObject(ATTR_ID, id);
		return ret;
	}

	@RequestMapping(method = RequestMethod.POST, value = "{entityId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, ? extends Object> ajaxUpdate(@RequestBody TDto dto, HttpServletResponse response) {
		try {
			TDto result = service.update(dto);
			return Collections.singletonMap(ATTR_ONE, result);
		} catch (FieldValidationException fve) {
			log.debug("Failed to update entity", fve);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ValidationErrorsVm vepm = new ValidationErrorsVm(fve.getErrors());
			return Collections.singletonMap(ATTR_VALIDATION_ERRORS, vepm.getMsg());
		} catch (Exception t) {
			log.error("Failed to update", t);
			response.setStatus(t instanceof FieldValidationException ? HttpServletResponse.SC_BAD_REQUEST
					: HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return Collections.singletonMap(ATTR_AJAX_ERROR, I18nUtils.buildMessagesChain(t, applicationContext));
		}
	}

	@Override
	@ExceptionHandler(Throwable.class)
	public ModelAndView handleUnexpectedControllerException(Throwable ex, HttpServletRequest req,
			HttpServletResponse res) {
		try {
			if (!isJsonHeader(req.getHeader("Accept")) && !isJsonHeader(req.getContentType())) {
				return super.handleUnexpectedControllerException(ex, req, res);
			}

			Map<String, Object> model = new HashMap<String, Object>();
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			model.put(ATTR_AJAX_ERROR, I18nUtils.buildMessagesChain(ex, applicationContext));
			return new ModelAndView(jsonView, model);
		} catch (Throwable t) {
			log.warn("Failed to handle easy crud controller exception", t);
			return super.handleUnexpectedControllerException(ex, req, res);
		}
	}

	private boolean isJsonHeader(String header) {
		return header != null && header.startsWith("application/json");
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

}
