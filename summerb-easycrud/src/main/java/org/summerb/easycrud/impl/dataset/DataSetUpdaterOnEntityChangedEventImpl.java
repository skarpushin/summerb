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
package org.summerb.easycrud.impl.dataset;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.dataset.DataSetUpdaterOnEntityChangedEvent;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.datapackage.DataSet;
import org.summerb.easycrud.api.row.datapackage.DataTable;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent.ChangeType;

import com.google.common.base.Preconditions;

/**
 * This impl will simply update dataSet tables with updated entities
 *
 * <p>WARNING: It doesn't not update any back-refs. Only table rows and only if table is created
 * before this operation. Former is actually hard to implement since DataSet is not carrying
 * information regarding references and it's not clear how to avoid n+1 problems.
 *
 * @author sergeyk
 */
public class DataSetUpdaterOnEntityChangedEventImpl implements DataSetUpdaterOnEntityChangedEvent {
  protected EasyCrudServiceResolver easyCrudServiceResolver;

  public DataSetUpdaterOnEntityChangedEventImpl(EasyCrudServiceResolver easyCrudServiceResolver) {
    Preconditions.checkArgument(
        easyCrudServiceResolver != null, "easyCrudServiceResolver required");
    this.easyCrudServiceResolver = easyCrudServiceResolver;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void updateDataSet(DataSet dataSet, EntityChangedEvent<?> e) {
    if (!e.isTypeOf(HasId.class)) {
      return;
    }

    EasyCrudService service = easyCrudServiceResolver.resolveByRowClass(e.getValue().getClass());
    if (!dataSet.getTables().containsKey(service.getRowMessageCode())) {
      return;
    }

    DataTable table = dataSet.getTables().get(service.getRowMessageCode());
    HasId rowto = (HasId) e.getValue();
    if (e.getChangeType() == ChangeType.REMOVED) {
      table.getRows().remove(rowto.getId());
    } else {
      table.put(rowto);
    }
  }
}
