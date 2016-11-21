package org.summerb.approaches.jdbccrud.common;

import java.util.LinkedList;

import javax.sql.DataSource;

/**
 * Simple base class for all DAOs
 * 
 * @author sergey.karpushin
 * 
 */
public abstract class DaoBase {
	private DataSource dataSource;
	protected NamedParameterJdbcTemplateEx jdbc;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbc = new NamedParameterJdbcTemplateEx(dataSource);
	}

	public static LinkedList<Long> convertArrayOfLongsToListOfLongs(long[] longs) {
		LinkedList<Long> ids = new LinkedList<Long>();
		for (long l : longs) {
			ids.add(l);
		}
		return ids;
	}

}
