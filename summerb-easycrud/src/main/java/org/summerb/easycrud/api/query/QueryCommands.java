package org.summerb.easycrud.api.query;

import com.google.common.base.Preconditions;
import java.util.List;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceQueryApi;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

/**
 * A lightweight and simple way for building queries for {@link EasyCrudService}. It provides usual
 * conditions, nothing fancy (no aggregation, etc.). If you need to build complex queries please
 * consider other options, i.e. use {@link org.summerb.easycrud.scaffold.api.Query} if your service
 * is scaffolded or implement them yourself in Dao. Usually Query will provide sufficient facilities
 * for querying rows.
 *
 * <p>It provides you with the ability to specify field names two ways: (a) Method references
 * (recommended, it uses ByteBuddy under the hood to extract field names) and (b) using string
 * literals.
 *
 * <p>It is not recommended to specify field names as string literals because then you lose all
 * power of static code analysis, compiler defense against typos and IDE features like call
 * hierarchy analysis.
 *
 * <p>Instance of this class supposed to be retrieved via {@link EasyCrudService#query()}
 *
 * @author Sergey Karpushin
 * @param <TId> type of Row id
 * @param <TRow> type of Row for which this query is being built
 */
public class QueryCommands<TId, TRow extends HasId<TId>>
    extends QueryShortcuts<TRow, QueryCommands<TId, TRow>> {

  protected final EasyCrudServiceQueryApi<TId, TRow> service;

  public QueryCommands(
      PropertyNameResolver<TRow> propertyNameResolver, EasyCrudServiceQueryApi<TId, TRow> service) {
    super(propertyNameResolver);
    Preconditions.checkArgument(service != null, "service required");

    this.service = service;
  }

  public TRow findOne() {
    return service.findOneByQuery(this);
  }

  public TRow getOne() {
    return service.getOneByQuery(this);
  }

  public TRow getFirst(OrderBy... orderBy) {
    return service.getFirstByQuery(this, orderBy);
  }

  public TRow findFirst(OrderBy... orderBy) {
    return service.findFirstByQuery(this, orderBy);
  }

  public PaginatedList<TRow> find(PagerParams pagerParams, OrderBy... orderBy) {
    return service.find(pagerParams, this, orderBy);
  }

  public List<TRow> findAll(OrderBy... orderBy) {
    return service.findAll(this, orderBy);
  }

  public List<TRow> getAll(OrderBy... orderBy) {
    return service.getAll(this, orderBy);
  }
}
