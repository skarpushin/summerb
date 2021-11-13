/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.dataset.DataSetUpdaterOnEntityChangedEvent;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.datapackage.DataSet;
import org.summerb.easycrud.api.dto.datapackage.DataTable;

/**
 * This impl will simply update dataSet tables with updated entities
 * 
 * WARNING: It doesn't not update any back-refs. Only table rows and only if
 * table is created before this operation. Former is actually hard to implement
 * since DataSet is not carrying information regarding references and it's not
 * clear how to avoid n+1 problems.
 * 
 * @author sergeyk
 *
 */
public class DataSetUpdaterOnEntityChangedEventImpl implements DataSetUpdaterOnEntityChangedEvent {
	private EasyCrudServiceResolver easyCrudServiceResolver;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void updateDataSet(DataSet dataSet, EntityChangedEvent<?> e) {
		if (!e.isTypeOf(HasId.class)) {
			return;
		}

		EasyCrudService service = easyCrudServiceResolver.resolveByDtoClass(e.getValue().getClass());
		if (!dataSet.getTables().containsKey(service.getEntityTypeMessageCode())) {
			return;
		}

		DataTable table = dataSet.getTables().get(service.getEntityTypeMessageCode());
		HasId dto = (HasId) e.getValue();
		if (e.getChangeType() == ChangeType.REMOVED) {
			table.getRows().remove(dto.getId());
		} else {
			table.put(dto);
		}
	}

	public EasyCrudServiceResolver getEasyCrudServiceResolver() {
		return easyCrudServiceResolver;
	}

	@Autowired
	public void setEasyCrudServiceResolver(EasyCrudServiceResolver easyCrudServiceResolver) {
		this.easyCrudServiceResolver = easyCrudServiceResolver;
	}

}
