package org.summerb.easycrud.impl.query;

import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.QueryFactory;
import org.summerb.methodCapturers.PropertyNameObtainerFactory;

import com.google.common.base.Preconditions;

public class QueryExFactoryImpl implements QueryFactory {

  protected PropertyNameObtainerFactory propertyNameObtainerFactory;

  public QueryExFactoryImpl(PropertyNameObtainerFactory propertyNameObtainerFactory) {
    Preconditions.checkArgument(
        propertyNameObtainerFactory != null, "propertyNameObtainerFactory required");
    this.propertyNameObtainerFactory = propertyNameObtainerFactory;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public <T, F extends Query<T>> F buildFor(Class<T> clazz) {
    try {
      return (F) new Query<T>(propertyNameObtainerFactory.getObtainer(clazz));
    } catch (Exception e) {
      throw new RuntimeException("Failed to build Query for class " + clazz, e);
    }
  }

  @Override
  public Query<?> build() {
    return new Query<>();
  }
}
