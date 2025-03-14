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
package org.summerb.easycrud.rest;

import java.util.List;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;

/** Use this to alter response right before it gets returned to consumer */
public class ConvertBeforeReturnStrategy<TId, TDto extends HasId<TId>> {
  protected boolean isConvertionRequired() {
    return false;
  }

  public MultipleItemsResult<TId, TDto> convert(MultipleItemsResult<TId, TDto> ret) {
    if (!isConvertionRequired()) {
      return ret;
    }
    List<TDto> rows = ret.getRows();
    for (int i = 0; i < rows.size(); i++) {
      rows.set(i, convert(rows.get(i)));
    }
    return ret;
  }

  public SingleItemResult<TId, TDto> convert(SingleItemResult<TId, TDto> ret) {
    if (!isConvertionRequired()) {
      return ret;
    }
    ret.setRow(convert(ret.getRow()));
    return ret;
  }

  protected TDto convert(TDto row) {
    return row;
  }
}
