package org.summerb.easycrud.impl.query;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.QueryFactory;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.methodCapturers.PropertyNameResolverFactory;

public class QueryFactoryImpl implements QueryFactory {

  protected PropertyNameResolverFactory propertyNameResolverFactory;

  public QueryFactoryImpl(PropertyNameResolverFactory propertyNameResolverFactory) {
    Preconditions.checkArgument(
        propertyNameResolverFactory != null, "propertyNameResolverFactory required");
    this.propertyNameResolverFactory = propertyNameResolverFactory;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public <TId, TRow extends HasId<TId>, F extends Query<TId, TRow>> F buildFor(
      EasyCrudService<TId, TRow> service) {
    try {
      return (F)
          new Query<>(propertyNameResolverFactory.getResolver(service.getRowClass()), service);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to build Query for Row type "
              + service.getRowMessageCode()
              + " class "
              + service.getRowClass(),
          e);
    }
  }
}
