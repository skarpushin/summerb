/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.easycrud.query;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl;

/**
 * Describes Order By part of the query for a single field
 *
 * @author sergey.karpushin
 */
public class OrderBy {
  public static final String NULLS_LAST = "NULLS LAST";
  public static final String NULLS_FIRST = "NULLS FIRST";

  public static final String ORDER_DESC = "DESC";
  public static final String ORDER_ASC = "ASC";
  public static final List<String> ALLOWED_SORT_DIRECTIONS = List.of(ORDER_ASC, ORDER_DESC);

  /** This can be used to override default nulls handling for the whole application */
  public static Boolean defaultNullsLast = null;

  protected String fieldName;
  protected String direction;
  protected Boolean nullsLast = defaultNullsLast;
  protected String collate;

  /**
   * Originating query. Used only in conjunction with {@link
   * org.summerb.easycrud.join_query.JoinQuery}
   */
  protected final transient Query<?, ?> query;

  public OrderBy() {
    query = null;
  }

  public OrderBy(String fieldName, String direction, Query<?, ?> query) {
    setFieldName(fieldName);
    setDirection(direction);
    this.query = query;
  }

  /**
   * @param fieldName field name
   * @return OrderBy instance
   */
  public static OrderBy Asc(String fieldName) {
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "Field name must be provided");
    OrderBy ret = new OrderBy();
    ret.setFieldName(fieldName);
    ret.direction = ORDER_ASC;
    return ret;
  }

  /**
   * @param fieldName field name
   * @return OrderBy instance
   */
  public static OrderBy Desc(String fieldName) {
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "Field name must be provided");
    OrderBy ret = new OrderBy();
    ret.setFieldName(fieldName);
    ret.direction = ORDER_DESC;
    return ret;
  }

  public String format() {
    Preconditions.checkArgument(StringUtils.hasText(fieldName), "Field name must be provided");

    StringBuilder sb = new StringBuilder();
    sb.append(fieldName);

    if (direction != null) {
      sb.append(",").append(direction);
    }

    if (StringUtils.hasText(collate)) {
      sb.append(",collate ").append(collate);
    }

    if (nullsLast != null) {
      sb.append(nullsLast ? ",nulls last" : ",nulls first");
    }

    return sb.toString();
  }

  /**
   * Parse order by expression. Expecting comma-separated values, like "fieldName[,asc|desc][,nulls
   * first|last][,collate collation_name]"
   *
   * <p>NOTE: we expect fieldName here as it is called in java field. This will be translated to
   * column name using {@link QueryToSqlMySqlImpl#snakeCase(String)} when building SQL
   */
  public static OrderBy parse(String orderByStr) {
    String[] parts = orderByStr.split(",");
    if (parts.length < 1 || parts.length > 4) {
      throw new IllegalArgumentException("Invalid order by string: " + orderByStr);
    }

    OrderBy ret = new OrderBy();
    ret.setFieldName(parts[0].trim());

    if (parts.length > 1) {
      ret.direction = parts[1].trim().toUpperCase();
      if (!ALLOWED_SORT_DIRECTIONS.contains(ret.direction)) {
        throw new IllegalArgumentException(
            "Invalid order by direction: " + ret.direction + " for field: " + ret.fieldName);
      }
    }

    for (int i = 2; i < parts.length; i++) {
      String part = parts[i].toLowerCase().trim();
      if (part.startsWith("collate ")) {
        ret.setCollate(part.substring("collate ".length()).trim());
      } else if (part.startsWith("nulls ")) {
        ret.nullsLast =
            switch (part.toUpperCase().trim()) {
              case NULLS_LAST -> true;
              case NULLS_FIRST -> false;
              default ->
                  throw new IllegalArgumentException(
                      "Invalid order by nullsLast: " + parts[i] + " for field: " + ret.fieldName);
            };
      }
    }

    return ret;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    if (direction != null) {
      Preconditions.checkArgument(
          ALLOWED_SORT_DIRECTIONS.contains(direction.toUpperCase()),
          "Invalid sort direction: " + direction);
    }
    this.direction = direction;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    Preconditions.checkArgument(isValidFieldName(fieldName), "Invalid fieldName: " + fieldName);
    this.fieldName = fieldName;
  }

  public boolean isValidFieldName(String fieldName) {
    return fieldName != null
        && !fieldName.isEmpty()
        && fieldName
            .chars()
            .allMatch(ch -> Character.isLetterOrDigit(ch) || ch == '_' || ch == '.');
  }

  public String getCollate() {
    return collate;
  }

  /**
   * Specify collation to use with ordering.
   *
   * <p>WARNING: This might not work properly with Postgres
   */
  public void setCollate(String collate) {
    if (collate == null) {
      this.collate = null;
      return;
    }
    Preconditions.checkArgument(isValidCollation(collate), "Invalid collation: " + collate);
    this.collate = collate;
  }

  public boolean isValidCollation(String collation) {
    return collation != null
        && !collation.isEmpty()
        && collation.chars().allMatch(ch -> Character.isLetterOrDigit(ch) || ch == '_');
  }

  /**
   * Specify collation to use with ordering.
   *
   * <p>WARNING: This might not work properly with Postgres
   */
  public OrderBy withCollate(String collate) {
    setCollate(collate);
    return this;
  }

  /**
   * @return null means default (native) behavior; false means = nulls first; true means nulls last
   */
  public Boolean getNullsLast() {
    return nullsLast;
  }

  /**
   * Request DB to show nulls last (as specified by parameter)
   *
   * <p>WARNING MySQL does not support this
   */
  public void setNullsLast(Boolean nullsLast) {
    this.nullsLast = nullsLast;
  }

  public OrderBy withNullsLast(boolean nullsLast) {
    this.nullsLast = nullsLast;
    return this;
  }

  /**
   * Request DB to show nulls last
   *
   * <p>WARNING MySQL does not support this
   */
  public OrderBy nullsLast() {
    this.nullsLast = true;
    return this;
  }

  public OrderBy nullsDefault() {
    this.nullsLast = null;
    return this;
  }

  /**
   * Request DB to show nulls first
   *
   * <p>WARNING MySQL does not support this
   */
  public OrderBy nullsFirst() {
    this.nullsLast = false;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    OrderBy orderBy = (OrderBy) o;
    return Objects.equals(fieldName, orderBy.fieldName)
        && Objects.equals(direction, orderBy.direction)
        && Objects.equals(nullsLast, orderBy.nullsLast)
        && Objects.equals(collate, orderBy.collate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, direction, nullsLast, collate);
  }
}
