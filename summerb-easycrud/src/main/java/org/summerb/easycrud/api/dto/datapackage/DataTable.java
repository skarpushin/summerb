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
import java.util.Set;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.relations.Ref;
import org.summerb.easycrud.api.relations.DataSetLoader;

/**
 * Data structure that holds rows of certain table
 * 
 * @author sergeyk
 *
 * @param <TId> type of primary key
 * @param <T>   DTO type
 */
public class DataTable<TId, T extends HasId<TId>> {
	private String name;
	private Map<TId, T> rows;

	private RowIdToBackReferencesMap backRefs;

	/**
	 * 
	 * @param name most often this will match value of {@link EasyCrudService}'s
	 *             getEntityTypeMessageCode() method
	 */
	public DataTable(String name) {
		this.name = name;
		rows = new HashMap<>();
	}

	/**
	 * 
	 * @return name most often this will match value of {@link EasyCrudService}'s
	 *         getEntityTypeMessageCode() method
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param row to add
	 */
	public void put(T row) {
		rows.put(row.getId(), row);
	}

	/**
	 * @param rows rows to add
	 */
	public void putAll(Iterable<T> rows) {
		for (T row : rows) {
			put(row);
		}
	}

	public Map<TId, T> getRows() {
		return rows;
	}

	public void setRows(Map<TId, T> rows) {
		this.rows = rows;
	}

	public T find(TId id) {
		return rows.get(id);
	}

	/**
	 * @return data structure that contains back references. Filled automatically by
	 *         {@link DataSetLoader} if there are other {@link DataTable} which has
	 *         rows referencing rows in this table
	 * 
	 * @deprecated avoid using this -- in future versions it might be removed.
	 *             Initially I thought it's a good idea, but after some time it
	 *             doesn't appear as one
	 */
	@Deprecated
	public RowIdToBackReferencesMap getBackRefs() {
		if (backRefs == null) {
			backRefs = new RowIdToBackReferencesMap();
		}
		return backRefs;
	}

	public void setBackRefs(RowIdToBackReferencesMap backReferences) {
		this.backRefs = backReferences;
	}

	/**
	 * MAPS {@link Ref} TO set of ids of matching objects from other table.
	 * Presumably fromTable field in {@link Ref} object will always point to this
	 * table.
	 * 
	 * @author sergeyk
	 *
	 */
	public static class RefToReferencedObjectsIdsMap extends HashMap<String, Set<Object>> {
		private static final long serialVersionUID = 6272759788167550514L;

		public Set<Object> getForRef(Ref ref) {
			return get(ref.getName());
		}
	}

	/**
	 * This data structure is used to contain all back refs from specific row in
	 * this table to all references loaded by {@link DataSetLoader}
	 * 
	 * @author sergeyk
	 *
	 */
	public static class RowIdToBackReferencesMap extends HashMap<Object, RefToReferencedObjectsIdsMap> {
		private static final long serialVersionUID = -4000441053721948805L;

		@SuppressWarnings("rawtypes")
		public RefToReferencedObjectsIdsMap getForRow(HasId row) {
			return get(row.getId());
		}
	}
}
