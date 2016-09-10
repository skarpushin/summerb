package org.summerb.approaches.jdbccrud.api.dto.datapackage;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

/**
 * DataSet represents "data package" several rows from several tables most
 * likely related to each other somehow
 * 
 * @author sergeyk
 *
 */
@SuppressWarnings("rawtypes")
public class DataSet {
	private Map<String, DataTable> tables;

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
