/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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
package org.summerb.easycrud.api.dto;

// TBD: Add support for NULLS FIRST, LAST

/**
 * Describes Order By part of the query for a single field
 *
 * @author sergey.karpushin
 */
public class OrderBy {
  private static final String ORDER_DESC = "DESC";
  private static final String ORDER_ASC = "ASC";

  protected String direction;
  protected String fieldName;

  /**
   * @param fieldName field name
   * @return OrderBy instance
   */
  public static OrderBy Asc(String fieldName) {
    OrderBy ret = new OrderBy();
    ret.fieldName = fieldName;
    ret.direction = ORDER_ASC;
    return ret;
  }

  /**
   * @param fieldName field name
   * @return OrderBy instance
   */
  public static OrderBy Desc(String fieldName) {
    OrderBy ret = new OrderBy();
    ret.fieldName = fieldName;
    ret.direction = ORDER_DESC;
    return ret;
  }

  public String getDirection() {
    return direction;
  }

  public boolean isAsc() {
    return ORDER_ASC.equals(direction);
  }

  public String getFieldName() {
    return fieldName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((direction == null) ? 0 : direction.hashCode());
    result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    OrderBy other = (OrderBy) obj;
    if (direction == null) {
      if (other.direction != null) return false;
    } else if (!direction.equals(other.direction)) return false;
    if (fieldName == null) {
      if (other.fieldName != null) return false;
    } else if (!fieldName.equals(other.fieldName)) return false;
    return true;
  }
}
