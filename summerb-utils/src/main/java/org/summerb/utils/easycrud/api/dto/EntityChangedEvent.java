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
package org.summerb.utils.easycrud.api.dto;

import com.google.common.base.Preconditions;
import java.io.Serial;
import java.io.Serializable;
import org.summerb.utils.DtoBase;

/**
 * Data structure used to contain and transmit entities changes within the application as well as
 * across the process/machine border
 *
 * @author sergeyk
 * @param <T> type of dto. Note: T type is limited to {@link DtoBase} for security reasons
 */
public class EntityChangedEvent<T extends DtoBase> implements Serializable {
  @Serial private static final long serialVersionUID = 8920065013673943648L;

  public enum ChangeType {
    ADDED,
    UPDATED,
    REMOVED
  }

  private T value;
  private ChangeType changeType;

  /**
   * @deprecated fo IO purposes only
   */
  @Deprecated
  public EntityChangedEvent() {}

  public EntityChangedEvent(T value, ChangeType changeType) {
    Preconditions.checkArgument(value != null, "value required");
    Preconditions.checkArgument(changeType != null, "changeType required");
    this.value = value;
    this.changeType = changeType;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T extends DtoBase> EntityChangedEvent<T> added(T notNullObject) {
    Preconditions.checkArgument(notNullObject != null);
    return new EntityChangedEvent(notNullObject, ChangeType.ADDED);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T extends DtoBase> EntityChangedEvent<T> updated(T notNullObject) {
    Preconditions.checkArgument(notNullObject != null);
    return new EntityChangedEvent(notNullObject, ChangeType.UPDATED);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T extends DtoBase> EntityChangedEvent<T> removedObject(T notNullObject) {
    return new EntityChangedEvent(notNullObject, ChangeType.REMOVED);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public boolean isTypeOf(Class clazz) {
    Preconditions.checkArgument(clazz != null, "Clazz required");
    return clazz.isAssignableFrom(value.getClass());
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public ChangeType getChangeType() {
    return changeType;
  }

  public void setChangeType(ChangeType changeType) {
    this.changeType = changeType;
  }

  @Override
  public String toString() {
    return "EntityChangedEvent [changeType=" + changeType + ", value=" + value + "]";
  }
}
