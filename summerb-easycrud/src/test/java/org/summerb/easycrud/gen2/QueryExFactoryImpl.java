package org.summerb.easycrud.gen2;

import org.summerb.methodCapturers.PropertyNameObtainerFactory;

import com.google.common.base.Preconditions;

public class QueryExFactoryImpl implements QueryExFactory {

  protected PropertyNameObtainerFactory propertyNameObtainerFactory;

  public QueryExFactoryImpl(PropertyNameObtainerFactory propertyNameObtainerFactory) {
    Preconditions.checkArgument(
        propertyNameObtainerFactory != null, "propertyNameObtainerFactory required");
    this.propertyNameObtainerFactory = propertyNameObtainerFactory;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public <T, F extends QueryEx<T>> F buildFor(Class<T> clazz) {
    try {
      return (F) new QueryEx<T>(propertyNameObtainerFactory.getObtainer(clazz));
    } catch (Exception e) {
      throw new RuntimeException("Failed to build QueryEx for class " + clazz, e);
    }
  }

  @Override
  public QueryEx<?> build() {
    return new QueryEx<>(null);
  }
}
