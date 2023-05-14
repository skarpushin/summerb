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

import com.google.common.base.Preconditions;

public class BooleanEqRestriction extends NegatableRestrictionBase<Boolean> {
  private static final long serialVersionUID = 1809030822888453382L;

  private boolean value;

  public BooleanEqRestriction() {}

  public BooleanEqRestriction(boolean value) {
    this.value = value;
  }

  public boolean getValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  @Override
  public boolean isMeet(Boolean subjectValue) {
    Preconditions.checkArgument(subjectValue != null);
    return isNegative() ? value != subjectValue : value == subjectValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (value ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    BooleanEqRestriction other = (BooleanEqRestriction) obj;
    if (value != other.value) return false;
    return true;
  }
}
