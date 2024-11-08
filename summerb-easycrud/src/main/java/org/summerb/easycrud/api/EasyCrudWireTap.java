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
package org.summerb.easycrud.api;

import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;

import com.google.common.eventbus.EventBus;

/**
 * This interface defines "wire tap" for all common CRUD service methods implemented by {@link
 * EasyCrudServiceImpl}.
 *
 * <p>You can easily tap into the workflow by implementing methods of this interface and injecting
 * it into {@link EasyCrudServiceImpl}.
 *
 * <p>Consider extending {@link EasyCrudWireTapNoOpImpl} class if you don't need to implement all
 * methods.
 *
 * <p>In most cases you'd want to inject multiple wireTaps, in such case use {@link
 * EasyCrudWireTapDelegatingImpl} which will delegate to list of wireTaps each time operation
 * invoked.
 *
 * <p>Also note that EasyCrud contains default implementations for common tasks:
 *
 * <p>Validation impl of {@link EasyCrudValidationStrategy} can be injected using {@link
 * EasyCrudWireTapValidationImpl}.
 *
 * <p>TODO: Add doc for auth wire taps
 *
 * <p>{@link EventBus}-based events can be injected using {@link EasyCrudWireTapEventBusImpl}.
 *
 * @author sergeyk
 */
public interface EasyCrudWireTap<T> {

  /**
   * Impl must respond whether or not {@link #beforeCreate(Object)} and {@link #afterCreate(Object)}
   * must be called
   *
   * @return true if must be called, false otherwise.
   */
  boolean requiresOnCreate();

  void beforeCreate(T row);

  void afterCreate(T row);

  /**
   * Impl must respond whether or not {@link #beforeRead()} and {@link #afterRead(Object)} must be
   * called
   *
   * @return true if must be called, false otherwise.
   */
  boolean requiresOnRead();

  void beforeRead();

  void afterRead(T row);

  /**
   * Impl must respond whether or not {@link #beforeUpdate(Object, Object)} and {@link
   * #afterUpdate(Object, Object)} must be called and what amount of information is required
   *
   * @return level of information needed for "Update" WireTap
   */
  EasyCrudWireTapMode requiresOnUpdate();

  void beforeUpdate(T from, T to);

  void afterUpdate(T from, T to);

  /**
   * Impl must respond whether or not {@link #beforeDelete(Object)} and {@link #afterDelete(Object)}
   * must be called
   *
   * @return level of information needed for "Delete" WireTap
   */
  EasyCrudWireTapMode requiresOnDelete();

  void beforeDelete(T row);

  void afterDelete(T row);
}
