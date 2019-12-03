/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package org.summerb.easycrud.common;

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
