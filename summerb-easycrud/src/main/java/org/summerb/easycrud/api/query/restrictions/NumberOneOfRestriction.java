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
package org.summerb.easycrud.api.query.restrictions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NumberOneOfRestriction extends NegatableRestrictionBase<Long> {
  private static final long serialVersionUID = 4538148232283195229L;

  private Set<Long> values;

  public NumberOneOfRestriction() {}

  public NumberOneOfRestriction(Set<Long> values) {
    this.values = values;
  }

  public NumberOneOfRestriction(Long... values) {
    this.values = new HashSet<>(Arrays.asList(values));
  }

  public Set<Long> getValues() {
    return values;
  }

  public void setValues(Set<Long> values) {
    this.values = values;
  }

  @Override
  public boolean isMeet(Long subjectValue) {
    if (values == null) {
      return !isNegative();
    }
    return isNegative() ? !values.contains(subjectValue) : values.contains(subjectValue);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((values == null) ? 0 : values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    NumberOneOfRestriction other = (NumberOneOfRestriction) obj;
    if (values == null) {
      if (other.values != null) return false;
    } else if (!values.equals(other.values)) return false;
    return true;
  }
}
