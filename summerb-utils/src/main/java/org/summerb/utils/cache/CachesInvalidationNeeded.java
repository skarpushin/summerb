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
package org.summerb.utils.cache;

/**
 * That event is reserved for cases when we want to invalidate all caches in the app
 *
 * @author sergey.karpushin
 */
public class CachesInvalidationNeeded {
  private String cause;

  public CachesInvalidationNeeded() {}

  public CachesInvalidationNeeded(String cause) {
    this.cause = cause;
  }

  public String getCause() {
    return cause;
  }

  public void setCause(String cause) {
    this.cause = cause;
  }

  @Override
  public String toString() {
    return "CachesInvalidationNeeded [cause=" + cause + "]";
  }
}
