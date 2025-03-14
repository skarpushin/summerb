package org.summerb.easycrud.impl.query;

import com.google.common.base.Preconditions;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.QueryFactory;
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
  public <T, F extends Query<T>> F buildFor(Class<T> clazz) {
    try {
      return (F) new Query<>(propertyNameResolverFactory.getResolver(clazz));
    } catch (Exception e) {
      throw new RuntimeException("Failed to build Query for class " + clazz, e);
    }
  }

  @Override
  public Query<?> build() {
    return new Query<>();
  }
}
