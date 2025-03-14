package org.summerb.easycrud.impl.dao;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.util.CollectionUtils;

public class SqlTypeOverridesDelegatingImpl extends SqlTypeOverridesDefaultImpl {
  protected Collection<SqlTypeOverride> overrides;

  public SqlTypeOverridesDelegatingImpl(SqlTypeOverride override) {
    super();
    Preconditions.checkArgument(override != null, "override required");
    this.overrides = Arrays.asList(override);
  }

  public SqlTypeOverridesDelegatingImpl(Collection<SqlTypeOverride> overrides) {
    super();
    Preconditions.checkArgument(!CollectionUtils.isEmpty(overrides), "overrides required");
    this.overrides = overrides;
  }

  protected SqlTypeOverride findDelegateOverrideForClass(Class<?> valueClass) {
    return overrides.stream().filter(x -> x.supportsType(valueClass)).findFirst().orElse(null);
  }

  @Override
  public SqlTypeOverride findOverrideForValue(Object value) {
    if (value == null) {
      return null;
    }

    return findOverrideForClass(value.getClass());
  }

  @Override
  public SqlTypeOverride findOverrideForClass(Class<?> valueClass) {
    SqlTypeOverride override = findDelegateOverrideForClass(valueClass);
    if (override != null) {
      return override;
    }

    return super.findOverrideForClass(valueClass);
  }
}
