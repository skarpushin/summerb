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
package org.summerb.easycrud.api.exceptions;

import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageCode;

/**
 * Base class for exceptions for case when something wasn't found by its identity
 *
 * @author sergey.karpushin
 */
public abstract class EntityNotFoundException extends RuntimeException
    implements HasMessageCode, HasMessageArgs {
  private static final long serialVersionUID = 3254284449960233351L;

  protected Object identity;

  /**
   * @deprecated Used only for io
   */
  @Deprecated
  public EntityNotFoundException() {}

  public EntityNotFoundException(Object identity) {
    this(identity, null);
  }

  public EntityNotFoundException(Object identity, Throwable cause) {
    this("Entity identified by '" + identity + "' not found", identity, cause);
  }

  public EntityNotFoundException(String techMessage, Object identity, Throwable cause) {
    super(techMessage, cause);
    this.identity = identity;
  }

  @Override
  public Object[] getMessageArgs() {
    return new Object[] {identity};
  }

  public Object getIdentity() {
    return identity;
  }

  public void setIdentity(Object identity) {
    this.identity = identity;
  }
}
