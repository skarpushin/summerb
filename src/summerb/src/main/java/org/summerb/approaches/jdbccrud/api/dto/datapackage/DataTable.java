package org.summerb.approaches.jdbccrud.api.dto.datapackage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;

public class DataTable<TId, T extends HasId<TId>> {
	private String name;
	private Map<TId, T> rows;

	// TODO: We need some data structure to track one2many & many2many
	// references. It's like list of other objects that are referencing objects
	// in this table
	private RowIdToBackReferencesMap backRefs;

	/**
	 * 
	 * @param name
	 *            that could be a entity name returned by Service, not a table
	 *            name. Main idea is that it must be consistent with references
	 *            configuration
	 */
	public DataTable(String name) {
		this.name = name;
		rows = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	/**
	 * Just a shortCut method to put row into table
	 * 
	 * @param id
	 * @param row
	 */
	public void put(T row) {
		rows.put(row.getId(), row);
	}

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

	public RowIdToBackReferencesMap getBackRefs() {
		if (backRefs == null) {
			backRefs = new RowIdToBackReferencesMap();
		}
		return backRefs;
	}

	public void setBackRefs(RowIdToBackReferencesMap backReferences) {
		this.backRefs = backReferences;
	}

	public static class RefToReferencedObjectsIdsMap extends HashMap<String, Set<Object>> {
		private static final long serialVersionUID = 6272759788167550514L;

		public Set<Object> getForRef(Ref ref) {
			return get(ref.getName());
		}
	}

	public static class RowIdToBackReferencesMap extends HashMap<Object, RefToReferencedObjectsIdsMap> {
		private static final long serialVersionUID = -4000441053721948805L;

		public RefToReferencedObjectsIdsMap getForRow(HasId row) {
			return get(row.getId());
		}
	}

}
