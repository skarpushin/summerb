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

/**
 * Narrow case of pagination params when we're interested only in top N records. In this case we
 * improve performance by skipping retrieval of totalResults
 *
 * @author sergeyk
 */
public class Top extends PagerParams {
  @Serial private static final long serialVersionUID = 5201858426665248240L;

  public Top() {}

  public Top(long max) {
    super(0, max);
  }

  public static boolean is(PagerParams pagerParams) {
    return pagerParams instanceof Top;
  }

  @Override
  public void setOffset(long offset) {
    Preconditions.checkArgument(offset == 0, "For top-based queries only 0 offset is allowed");
  }
}
