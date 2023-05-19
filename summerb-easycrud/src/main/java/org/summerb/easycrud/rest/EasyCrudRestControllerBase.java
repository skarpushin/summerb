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
package org.summerb.easycrud.rest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.GenericFilterBean;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.relations.DataSetLoader;
import org.summerb.easycrud.api.relations.ReferencesRegistry;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.datapackage.DataSet;
import org.summerb.easycrud.api.row.datapackage.DataTable;
import org.summerb.easycrud.api.row.relations.Ref;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.impl.auth.EascyCrudAuthorizationPerRowStrategy;
import org.summerb.easycrud.impl.auth.EascyCrudAuthorizationPerTableStrategy;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.mvc.filter.FilteringParamsToQueryConverter;
import org.summerb.easycrud.mvc.filter.FilteringParamsToQueryConverterImpl;
import org.summerb.easycrud.mvc.model.EasyCrudQueryParams;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.dto.CrudQueryResult;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;
import org.summerb.easycrud.rest.permissions.PermissionsResolverStrategy;
import org.summerb.easycrud.rest.permissions.PermissionsResolverStrategyPerRow;
import org.summerb.easycrud.rest.permissions.PermissionsResolverStrategyPerTable;
import org.summerb.easycrud.rest.querynarrower.QueryNarrowerStrategy;
import org.summerb.security.api.exceptions.NotAuthorizedException;

import com.google.common.base.Preconditions;

/**
 * Base class for EasyCrud REST API controllers which main responsibility is to serve CRUD requests
 * for entities managed by {@link EasyCrudService}.
 *
 * <p>It designed to be sub-classed. See
 * https://github.com/skarpushin/summerb/wiki/Easy-CRUD#rest-api-controller for details.
 *
 * <p>NOTE: Exception handling is not implemented here because we rely on RestExceptionTranslator,
 * which is subclass of {@link GenericFilterBean} (Spring approach on filtering requests).
 *
 * @param <TId> primary key type
 * @param <TRow> entity type
 * @param <TEasyCrudService> service type
 * @author sergeyk
 */
public class EasyCrudRestControllerBase<
        TId, TRow extends HasId<TId>, TEasyCrudService extends EasyCrudService<TId, TRow>>
    implements ApplicationContextAware, InitializingBean {
  protected static final String PERM_RESOLVER_REQ =
      "Cannot provide permissions since permissionsResolverStrategy is not set";

  protected TEasyCrudService service;
  protected DataSetLoader dataSetLoader;
  protected ReferencesRegistry referencesRegistry;

  protected ConvertBeforeReturnStrategy<TId, TRow> convertBeforeReturnStrategy =
      new ConvertBeforeReturnStrategy<TId, TRow>();

  protected QueryNarrowerStrategy queryNarrowerStrategy = new QueryNarrowerStrategy();
  protected PermissionsResolverStrategy<TId, TRow> permissionsResolverStrategy;
  protected FilteringParamsToQueryConverter filteringParamsToQueryConverter =
      new FilteringParamsToQueryConverterImpl();
  protected OrderBy defaultOrderBy;
  protected PagerParams defaultPagerParams = new PagerParams();

  protected ApplicationContext applicationContext;

  public EasyCrudRestControllerBase(TEasyCrudService service) {
    Preconditions.checkArgument(service != null, "Service required");
    this.service = service;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Preconditions.checkArgument(service != null);

    if (permissionsResolverStrategy == null) {
      permissionsResolverStrategy = tryDiscoverPermissionsResolverFromService();
    }
  }

  @SuppressWarnings("rawtypes")
  protected PermissionsResolverStrategy<TId, TRow> tryDiscoverPermissionsResolverFromService() {
    if (service instanceof EasyCrudServiceImpl) {
      EasyCrudWireTap wireTap = ((EasyCrudServiceImpl) service).getWireTap();
      return tryGetPermissionsResolverFromWireTap(wireTap);
    }
    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private PermissionsResolverStrategy<TId, TRow> tryGetPermissionsResolverFromWireTap(
      EasyCrudWireTap wireTap) {

    if (wireTap instanceof EascyCrudAuthorizationPerTableStrategy) {
      return new PermissionsResolverStrategyPerTable(
          (EascyCrudAuthorizationPerTableStrategy) wireTap);
    } else if (wireTap instanceof EascyCrudAuthorizationPerRowStrategy) {
      return new PermissionsResolverStrategyPerRow((EascyCrudAuthorizationPerRowStrategy) wireTap);
    } else if (wireTap instanceof EasyCrudWireTapDelegatingImpl) {
      List<EasyCrudWireTap> chain = ((EasyCrudWireTapDelegatingImpl) wireTap).getChain();

      for (EasyCrudWireTap wireTapFromDelegate : chain) {
        PermissionsResolverStrategy<TId, TRow> ret =
            tryGetPermissionsResolverFromWireTap(wireTapFromDelegate);
        if (ret != null) {
          return ret;
        }
      }
    }

    return null;
  }

  /**
   * Default action to get list of items in this collection with either non or simple query
   * parameters.
   *
   * <p>In order for this method to work properly (including orderBy and pagerParams) make sure to
   * register PojoFieldsArgumentResolver within spring mvc.
   *
   * @param pagerParams pagerParams
   * @param orderBy orderBy
   * @param needPerms provide true if needed to know permissions
   * @param referencesToResolve references to resolve
   * @param pathVariables path variables
   * @return list of items
   */
  @GetMapping
  public MultipleItemsResult<TId, TRow> getList(
      @RequestParam(value = "pagerParams", required = false) PagerParams pagerParams,
      @RequestParam(value = "orderBy", required = false) OrderBy orderBy,
      @RequestParam(value = "needPerms", required = false) boolean needPerms,
      @RequestParam(value = "referencesToResolve", required = false)
          List<String> referencesToResolve,
      /* @ApiIgnore */ PathVariablesMap pathVariables)
      throws Exception {
    if (orderBy != null && (orderBy.getDirection() == null || orderBy.getFieldName() == null)) {
      orderBy = defaultOrderBy;
    }
    if (pagerParams == null) {
      pagerParams = defaultPagerParams;
    }

    PaginatedList<TRow> rows;
    if (orderBy == null) {
      rows = service.find(pagerParams, queryNarrowerStrategy.narrow(null, pathVariables));
    } else {
      rows = service.find(pagerParams, queryNarrowerStrategy.narrow(null, pathVariables), orderBy);
    }
    MultipleItemsResult<TId, TRow> ret =
        new MultipleItemsResult<>(service.getRowMessageCode(), rows);

    if (needPerms) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, pathVariables);
    }

    if (rows.getHasItems() && !CollectionUtils.isEmpty(referencesToResolve)) {
      resolveReferences(referencesToResolve, ret, rows.getItems());
    }

    return convertBeforeReturnStrategy.convert(ret);
  }

  protected void resolveReferences(
      List<String> referencesToResolve, CrudQueryResult<TId, TRow> ret, List<TRow> items)
      throws EntityNotFoundException, NotAuthorizedException {
    Preconditions.checkState(
        dataSetLoader != null, "DataSetLoader is required to resolve references");
    Preconditions.checkState(
        referencesRegistry != null, "referencesRegistry is required to resolve references");
    DataSet ds = new DataSet();
    DataTable<TId, TRow> table = new DataTable<>(service.getRowMessageCode());
    table.putAll(items);
    ds.getTables().put(table.getName(), table);

    List<Ref> references =
        referencesToResolve.stream()
            .map(name -> referencesRegistry.getRefByName(name))
            .collect(Collectors.toList());
    Ref[] refsArr = (Ref[]) references.toArray(new Ref[references.size()]);
    dataSetLoader.loadReferencedObjects(ds, refsArr);

    // now remove initial table from dataset because we don't want to
    // duplicate this. It's already populated to rows
    ds.getTables().remove(table.getName());

    // x. ret
    ret.setRefsResolved(
        references.stream().collect(Collectors.toMap(Ref::getName, Function.identity())));
    ret.setRefs(ds);
  }

  @PostMapping(
      path = "/query",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public MultipleItemsResult<TId, TRow> getListWithQuery(
      @RequestBody EasyCrudQueryParams filteringParams,
      @RequestParam(value = "needPerms", required = false) boolean needPerms,
      @RequestParam(value = "referencesToResolve", required = false)
          List<String> referencesToResolve,
      /* @ApiIgnore */ PathVariablesMap pathVariables)
      throws Exception {

    if ((filteringParams.getOrderBy() == null || filteringParams.getOrderBy().length == 0)
        && defaultOrderBy != null) {
      filteringParams.setOrderBy(new OrderBy[] {defaultOrderBy});
    }
    if (filteringParams.getPagerParams() == null) {
      filteringParams.setPagerParams(defaultPagerParams);
    }

    Query query =
        filteringParamsToQueryConverter.convert(
            filteringParams.getFilterParams(), service.getRowClass());
    query = queryNarrowerStrategy.narrow(query, pathVariables);

    PaginatedList<TRow> rows =
        service.find(filteringParams.getPagerParams(), query, filteringParams.getOrderBy());
    MultipleItemsResult<TId, TRow> ret =
        new MultipleItemsResult<>(service.getRowMessageCode(), rows);

    if (needPerms) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, pathVariables);
    }

    if (rows.getHasItems() && !CollectionUtils.isEmpty(referencesToResolve)) {
      resolveReferences(referencesToResolve, ret, rows.getItems());
    }

    return convertBeforeReturnStrategy.convert(ret);
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public SingleItemResult<TId, TRow> getItem(
      @PathVariable("id") TId id,
      @RequestParam(value = "needPerms", required = false) boolean needPerms,
      @RequestParam(value = "referencesToResolve", required = false)
          List<String> referencesToResolve)
      throws Exception {

    TRow row = service.findById(id);
    SingleItemResult<TId, TRow> ret =
        new SingleItemResult<TId, TRow>(service.getRowMessageCode(), row);

    if (needPerms && row != null) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, null);
    }

    if (row != null && !CollectionUtils.isEmpty(referencesToResolve)) {
      resolveReferences(referencesToResolve, ret, Arrays.asList(row));
    }

    return convertBeforeReturnStrategy.convert(ret);
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public SingleItemResult<TId, TRow> createNewItem(
      @RequestBody TRow rowToCreate,
      @RequestParam(value = "needPerms", required = false) boolean needPerms)
      throws Exception {
    TRow row = service.create(rowToCreate);
    SingleItemResult<TId, TRow> ret = new SingleItemResult<>(service.getRowMessageCode(), row);
    if (needPerms) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, null);
    }
    return convertBeforeReturnStrategy.convert(ret);
  }

  @PutMapping(
      path = "/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public SingleItemResult<TId, TRow> updateItem(
      @PathVariable("id") TId id,
      @RequestBody TRow rowToUpdate,
      @RequestParam(value = "needPerms", required = false) boolean needPerms)
      throws Exception {
    TRow row = service.update(rowToUpdate);
    SingleItemResult<TId, TRow> ret = new SingleItemResult<>(service.getRowMessageCode(), row);
    if (needPerms) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, null);
    }
    return convertBeforeReturnStrategy.convert(ret);
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
