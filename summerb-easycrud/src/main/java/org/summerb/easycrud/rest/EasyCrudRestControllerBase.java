/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
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
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerRowStrategy;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerTableStrategy;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.mvc.filter.FilteringParamsToQueryConverter;
import org.summerb.easycrud.mvc.filter.FilteringParamsToQueryConverterImpl;
import org.summerb.easycrud.mvc.model.EasyCrudQueryParams;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;
import org.summerb.easycrud.rest.permissions.PermissionsResolverStrategy;
import org.summerb.easycrud.rest.permissions.PermissionsResolverStrategyPerRow;
import org.summerb.easycrud.rest.permissions.PermissionsResolverStrategyPerTable;
import org.summerb.easycrud.rest.querynarrower.QueryNarrowerStrategy;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/**
 * Base class for EasyCrud REST API controllers which main responsibility is to serve CRUD requests
 * for entities managed by {@link EasyCrudService}.
 *
 * <p>It designed to be subclassed. See <a
 * href="https://github.com/skarpushin/summerb/wiki/Easy-CRUD#rest-api-controller">for details</a> .
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

  protected ConvertBeforeReturnStrategy<TId, TRow> convertBeforeReturnStrategy =
      new ConvertBeforeReturnStrategy<>();

  protected QueryNarrowerStrategy<TId, TRow> queryNarrowerStrategy = new QueryNarrowerStrategy<>();
  protected PermissionsResolverStrategy<TId, TRow> permissionsResolverStrategy;
  protected FilteringParamsToQueryConverter<TId, TRow> filteringParamsToQueryConverter;
  protected OrderBy[] defaultOrderBy;
  protected PagerParams defaultPagerParams = new PagerParams();

  protected ApplicationContext applicationContext;

  public EasyCrudRestControllerBase(TEasyCrudService service) {
    Preconditions.checkArgument(service != null, "Service required");
    this.service = service;
    this.filteringParamsToQueryConverter = new FilteringParamsToQueryConverterImpl<>(service);
  }

  @Override
  public void afterPropertiesSet() {
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
  protected PermissionsResolverStrategy<TId, TRow> tryGetPermissionsResolverFromWireTap(
      EasyCrudWireTap wireTap) {

    if (wireTap instanceof EasyCrudAuthorizationPerTableStrategy) {
      return new PermissionsResolverStrategyPerTable(
          (EasyCrudAuthorizationPerTableStrategy) wireTap);
    } else if (wireTap instanceof EasyCrudAuthorizationPerRowStrategy) {
      return new PermissionsResolverStrategyPerRow((EasyCrudAuthorizationPerRowStrategy) wireTap);
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
   * @param optionalPagerParams pagerParams
   * @param optionalOrderBy orderBy, might be empty
   * @param needPerms provide true if needed to know permissions
   * @param referencesToResolve references to resolve
   * @param pathVariables path variables
   * @return list of items
   */
  @GetMapping
  public MultipleItemsResult<TId, TRow> getList(
      @RequestParam(value = "pagerParams", required = false) PagerParams optionalPagerParams,
      @RequestParam(value = "orderBy", required = false) OrderBy optionalOrderBy,
      @RequestParam(value = "needPerms", required = false) boolean needPerms,
      @RequestParam(value = "referencesToResolve", required = false)
          List<String> referencesToResolve,
      @Parameter(hidden = true) PathVariablesMap pathVariables) {

    OrderBy[] orderBy = clarifyOrderBy(optionalOrderBy);
    PagerParams pagerParams = clarifyPagerParams(optionalPagerParams);

    Query<TId, TRow> query = narrowQuery(null, pathVariables);
    PaginatedList<TRow> rows = queryRows(orderBy, pagerParams, query);

    MultipleItemsResult<TId, TRow> ret = buildMultipleItemsResult(rows);
    fillMultipleItemsResultWithPermissionsInfo(ret, needPerms, pathVariables);

    return convert(ret);
  }

  protected MultipleItemsResult<TId, TRow> convert(MultipleItemsResult<TId, TRow> ret) {
    return convertBeforeReturnStrategy.convert(ret);
  }

  protected void fillMultipleItemsResultWithPermissionsInfo(
      MultipleItemsResult<TId, TRow> ret, boolean needPerms, PathVariablesMap pathVariables) {
    if (needPerms) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, pathVariables);
    }
  }

  protected MultipleItemsResult<TId, TRow> buildMultipleItemsResult(PaginatedList<TRow> rows) {
    return new MultipleItemsResult<>(service.getRowMessageCode(), rows);
  }

  protected PagerParams clarifyPagerParams(PagerParams optionalPagerParams) {
    if (optionalPagerParams == null || optionalPagerParams.getMax() == 0) {
      return defaultPagerParams;
    }
    return optionalPagerParams;
  }

  protected OrderBy[] clarifyOrderBy(OrderBy... optionalOrderBy) {
    if (optionalOrderBy == null || optionalOrderBy.length == 0) {
      return getDefaultOrderBy();
    }

    List<OrderBy> list =
        Arrays.stream(optionalOrderBy)
            .filter(Objects::nonNull)
            .filter(
                x -> StringUtils.hasText(x.getDirection()) && StringUtils.hasText(x.getFieldName()))
            .toList();
    if (list.isEmpty()) {
      return getDefaultOrderBy();
    }

    return optionalOrderBy;
  }

  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  protected PaginatedList<TRow> queryRows(
      OrderBy[] orderBy, PagerParams pagerParams, Query<TId, TRow> query)
      throws NotAuthorizedException {
    return service.find(pagerParams, query, orderBy);
  }

  protected Query<TId, TRow> buildQuery(
      EasyCrudQueryParams filteringParams, PathVariablesMap pathVariables) {
    Query<TId, TRow> query =
        filteringParamsToQueryConverter.convert(filteringParams.getFilterParams());
    query = narrowQuery(query, pathVariables);
    return query;
  }

  protected Query<TId, TRow> narrowQuery(Query<TId, TRow> query, PathVariablesMap pathVariables) {
    return queryNarrowerStrategy.narrow(query, pathVariables);
  }

  @PostMapping(path = "/query")
  public MultipleItemsResult<TId, TRow> getListWithQuery(
      @RequestBody EasyCrudQueryParams filteringParams,
      @RequestParam(value = "needPerms", required = false) boolean needPerms,
      @RequestParam(value = "referencesToResolve", required = false)
          List<String> referencesToResolve,
      @Parameter(hidden = true) PathVariablesMap pathVariables) {

    OrderBy[] orderBy = clarifyOrderBy(filteringParams.getOrderBy());
    PagerParams pagerParams = clarifyPagerParams(filteringParams.getPagerParams());

    Query<TId, TRow> query = buildQuery(filteringParams, pathVariables);
    PaginatedList<TRow> rows = queryRows(orderBy, pagerParams, query);

    MultipleItemsResult<TId, TRow> ret = buildMultipleItemsResult(rows);
    fillMultipleItemsResultWithPermissionsInfo(ret, needPerms, pathVariables);

    return convert(ret);
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public SingleItemResult<TId, TRow> getItem(
      @PathVariable("id") TId id,
      @RequestParam(value = "needPerms", required = false) boolean needPerms,
      @RequestParam(value = "referencesToResolve", required = false)
          List<String> referencesToResolve) {

    TRow row = service.findById(id);
    SingleItemResult<TId, TRow> ret = new SingleItemResult<>(service.getRowMessageCode(), row);

    if (needPerms && row != null) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, null);
    }

    return convertBeforeReturnStrategy.convert(ret);
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public SingleItemResult<TId, TRow> createNewItem(
      @RequestBody TRow rowToCreate,
      @RequestParam(value = "needPerms", required = false) boolean needPerms) {
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
      @RequestParam(value = "needPerms", required = false) boolean needPerms) {
    TRow row = service.update(rowToUpdate);
    SingleItemResult<TId, TRow> ret = new SingleItemResult<>(service.getRowMessageCode(), row);
    if (needPerms) {
      Preconditions.checkArgument(permissionsResolverStrategy != null, PERM_RESOLVER_REQ);
      permissionsResolverStrategy.resolvePermissions(ret, null);
    }
    return convertBeforeReturnStrategy.convert(ret);
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public void deleteItem(@PathVariable("id") TId id) {
    service.deleteById(id);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
