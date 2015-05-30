package org.summerb.easycrud.impl;

import java.util.LinkedList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Simple base class for all DAOs
 * 
 * @author sergey.karpushin
 * 
 */
public abstract class DaoBase {
	private DataSource dataSource;
	protected NamedParameterJdbcTemplate jdbc;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbc = new NamedParameterJdbcTemplate(dataSource);
	}

	public static LinkedList<Long> convertArrayOfLongsToListOfLongs(long[] longs) {
		LinkedList<Long> ids = new LinkedList<Long>();
		for (long l : longs) {
			ids.add(l);
		}
		return ids;
	}

}
