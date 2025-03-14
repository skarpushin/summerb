package org.summerb.easycrud.api.query;

import com.google.common.base.Preconditions;
import java.util.List;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.methodCapturers.PropertyNameResolver;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

public class QueryCommands<TId, TRow extends HasId<TId>>
    extends QueryShortcuts<TRow, QueryCommands<TId, TRow>> {

  protected final EasyCrudService<TId, TRow> service;

  public QueryCommands(
      PropertyNameResolver<TRow> propertyNameResolver, EasyCrudService<TId, TRow> service) {
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
