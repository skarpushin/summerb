package org.summerb.approaches.jdbccrud.rest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataTable;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.api.relations.DataSetLoader;
import org.summerb.approaches.jdbccrud.api.relations.ReferencesRegistry;
import org.summerb.approaches.jdbccrud.mvc.filter.FilteringParamsToQueryConverter;
import org.summerb.approaches.jdbccrud.mvc.filter.FilteringParamsToQueryConverterImpl;
import org.summerb.approaches.jdbccrud.mvc.model.EasyCrudQueryParams;
import org.summerb.approaches.jdbccrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.approaches.jdbccrud.rest.dto.CrudQueryResult;
import org.summerb.approaches.jdbccrud.rest.dto.MultipleItemsResult;
import org.summerb.approaches.jdbccrud.rest.dto.SingleItemResult;
import org.summerb.approaches.jdbccrud.rest.permissions.PermissionsResolverStrategy;
import org.summerb.approaches.jdbccrud.rest.querynarrower.QueryNarrowerStrategy;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.springmvc.controllers.ControllerBase;

import com.google.common.base.Preconditions;

import springfox.documentation.annotations.ApiIgnore;

public class EasyCrudRestControllerBase<TId, TDto extends HasId<TId>, TEasyCrudService extends EasyCrudService<TId, TDto>>
		extends ControllerBase {
	private static final String PERM_RESOLVER_REQ = "Cannot provide permissions since permissionsResolverStrategy is not set";

	protected TEasyCrudService service;
	protected DataSetLoader dataSetLoader;
	protected ReferencesRegistry referencesRegistry;

	protected ConvertBeforeReturnStrategy<TId, TDto> convertBeforeReturnStrategy = new ConvertBeforeReturnStrategy<TId, TDto>();;
	protected QueryNarrowerStrategy queryNarrowerStrategy = new QueryNarrowerStrategy();
	protected PermissionsResolverStrategy<TId, TDto> permissionsResolverStrategy;
	protected FilteringParamsToQueryConverter filteringParamsToQueryConverter = new FilteringParamsToQueryConverterImpl();

	public EasyCrudRestControllerBase(TEasyCrudService service) {
		Preconditions.checkArgument(service != null, "Service required");
		this.service = service;
	}

	/**
	 * Default action to get list of items in this collection with either non or
	 * simple query parameters
	 * 
	 * @param pagerParams
	 * @param orderBy
	 * @param needPerms
	 *            provide true if needed to know permissions
	 * @return list of items
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public MultipleItemsResult<TId, TDto> getList(@ModelAttribute("pagerParams") PagerParams pagerParams,
			@ModelAttribute("orderBy") OrderBy orderBy, @ApiIgnore PathVariablesMap pathVariables,
			@RequestParam(value = "needPerms", required = false) boolean needPerms,
			@RequestParam(value = "referencesToResolve", required = false) List<String> referencesToResolve)
			throws Exception {
		if (orderBy.getDirection() == null || orderBy.getFieldName() == null) {
			orderBy = null;
		}

		PaginatedList<TDto> rows;
		if (orderBy == null) {
			rows = service.query(pagerParams, queryNarrowerStrategy.narrow(null, pathVariables));
		} else {
			rows = service.query(pagerParams, queryNarrowerStrategy.narrow(null, pathVariables), orderBy);
		}
		MultipleItemsResult<TId, TDto> ret = new MultipleItemsResult<>(service.getEntityTypeMessageCode(), rows);

		if (needPerms) {
			Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
			permissionsResolverStrategy.resolvePermissions(ret, pathVariables);
		}

		if (rows.getHasItems() && !CollectionUtils.isEmpty(referencesToResolve)) {
			resolveReferences(referencesToResolve, ret, rows.getItems());
		}

		return convertBeforeReturnStrategy.convert(ret);
	}

	private void resolveReferences(List<String> referencesToResolve, CrudQueryResult<TId, TDto> ret, List<TDto> items)
			throws EntityNotFoundException, NotAuthorizedException {
		Preconditions.checkState(dataSetLoader != null, "DataSetLoader is required to resolve references");
		Preconditions.checkState(referencesRegistry != null, "referencesRegistry is required to resolve references");
		DataSet ds = new DataSet();
		DataTable<TId, TDto> table = new DataTable<>(service.getEntityTypeMessageCode());
		table.putAll(items);
		ds.getTables().put(table.getName(), table);

		List<Ref> references = referencesToResolve.stream().map(name -> referencesRegistry.getRefByName(name))
				.collect(Collectors.toList());
		Ref[] refsArr = (Ref[]) references.toArray(new Ref[references.size()]);
		dataSetLoader.resolveReferencedObjects(ds, refsArr);

		// now remove initial table from dataset because we don't want to
		// duplicate this. It's already populated to rows
		ds.getTables().remove(table.getName());

		// x. ret
		ret.setRefsResolved(references.stream().collect(Collectors.toMap(Ref::getName, Function.identity())));
		ret.setRefs(ds);
	}

	@PostMapping(path = "/query", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public MultipleItemsResult<TId, TDto> getListWithQuery(@RequestBody EasyCrudQueryParams filteringParams,
			@RequestParam(value = "needPerms", required = false) boolean needPerms,
			@RequestParam(value = "referencesToResolve", required = false) List<String> referencesToResolve,
			@ApiIgnore PathVariablesMap pathVariables) throws Exception {

		Query query = filteringParamsToQueryConverter.convert(filteringParams.getFilterParams(), service.getDtoClass());
		query = queryNarrowerStrategy.narrow(query, pathVariables);

		PaginatedList<TDto> rows = service.query(filteringParams.getPagerParams(), query, filteringParams.getOrderBy());
		MultipleItemsResult<TId, TDto> ret = new MultipleItemsResult<>(service.getEntityTypeMessageCode(), rows);

		if (needPerms) {
			Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
			permissionsResolverStrategy.resolvePermissions(ret, pathVariables);
		}

		if (rows.getHasItems() && !CollectionUtils.isEmpty(referencesToResolve)) {
			resolveReferences(referencesToResolve, ret, rows.getItems());
		}

		return convertBeforeReturnStrategy.convert(ret);
	}

	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public SingleItemResult<TId, TDto> getItem(@PathVariable("id") TId id,
			@RequestParam(value = "needPerms", required = false) boolean needPerms,
			@RequestParam(value = "referencesToResolve", required = false) List<String> referencesToResolve)
			throws Exception {

		TDto row = service.findById(id);
		SingleItemResult<TId, TDto> ret = new SingleItemResult<TId, TDto>(service.getEntityTypeMessageCode(), row);

		if (needPerms && row != null) {
			Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
			permissionsResolverStrategy.resolvePermissions(ret);
		}

		if (row != null && !CollectionUtils.isEmpty(referencesToResolve)) {
			resolveReferences(referencesToResolve, ret, Arrays.asList(row));
		}

		return convertBeforeReturnStrategy.convert(ret);
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public SingleItemResult<TId, TDto> createNewItem(@RequestBody TDto dto,
			@RequestParam(value = "needPerms", required = false) boolean needPerms) throws Exception {
		TDto row = service.create(dto);
		SingleItemResult<TId, TDto> ret = new SingleItemResult<>(service.getEntityTypeMessageCode(), row);
		if (needPerms) {
			Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
			permissionsResolverStrategy.resolvePermissions(ret);
		}
		return convertBeforeReturnStrategy.convert(ret);
	}

	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public SingleItemResult<TId, TDto> updateItem(@PathVariable("id") TId id, @RequestBody TDto rowToUpdate,
			@RequestParam(value = "needPerms", required = false) boolean needPerms) throws Exception {
		TDto row = service.update(rowToUpdate);
		SingleItemResult<TId, TDto> ret = new SingleItemResult<>(service.getEntityTypeMessageCode(), row);
		if (needPerms) {
			Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
			permissionsResolverStrategy.resolvePermissions(ret);
		}
		return convertBeforeReturnStrategy.convert(ret);
	}

	@DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void deleteItem(@PathVariable("id") TId id) throws Exception {
		service.deleteById(id);
	}

	public DataSetLoader getDataSetLoader() {
		return dataSetLoader;
	}

	@Autowired(required = false)
	public void setDataSetLoader(DataSetLoader dataSetLoader) {
		this.dataSetLoader = dataSetLoader;
	}

	public ReferencesRegistry getReferencesRegistry() {
		return referencesRegistry;
	}

	@Autowired(required = false)
	public void setReferencesRegistry(ReferencesRegistry referencesRegistry) {
		this.referencesRegistry = referencesRegistry;
	}
}
