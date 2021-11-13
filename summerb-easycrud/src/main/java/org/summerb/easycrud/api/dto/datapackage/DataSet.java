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
package org.summerb.easycrud.api.dto.datapackage;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

/**
 * DataSet represents "data package" that contains several rows from several
 * tables.
 * 
 * @author sergeyk
 *
 */
@SuppressWarnings("rawtypes")
public class DataSet {
	private Map<String, DataTable> tables;

	/**
	 * Get tanle by name
	 * 
	 * @param name
	 *            table name
	 * @return never null instance of {@link DataTable}
	 */
	public DataTable get(String name) {
		Preconditions.checkArgument(StringUtils.hasText(name));
		DataTable ret = getTables().get(name);
		if (ret == null) {
			ret = new DataTable(name);
			tables.put(name, ret);
		}
		return ret;
	}

	public Map<String, DataTable> getTables() {
		if (tables == null) {
			tables = new HashMap<>();
		}
		return tables;
	}

	public void setTables(Map<String, DataTable> tables) {
		this.tables = tables;
	}

	// @Transient
	// @JsonIgnore
	public boolean isEmpty() {
		if (CollectionUtils.isEmpty(tables)) {
			return true;
		}
		for (DataTable t : tables.values()) {
			if (t.getRows().isEmpty()) {
				return true;
			}
		}

		return false;
	}
}
