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
package org.summerb.easycrud.impl.SimpleJdbcUpdate;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Interface specifying the API for a Simple JDBC Update implemented by
 * {@link SimpleJdbcUpdate}. This interface is not often used directly, but
 * provides the option to enhance testability, as it can easily be mocked or
 * stubbed.
 */
public interface SimpleJdbcUpdateOperations {

	/**
	 * Specify the table name to be used for the update.
	 * 
	 * @param tableName the name of the stored table
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdateOperations withTableName(String tableName);

	/**
	 * Specify the shema name, if any, to be used for the update.
	 * 
	 * @param schemaName the name of the schema
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdateOperations withSchemaName(String schemaName);

	/**
	 * Specify the catalog name, if any, to be used for the update.
	 * 
	 * @param catalogName the name of the catalog
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdateOperations withCatalogName(String catalogName);

	/**
	 * Specify the column names that the update statement should be limited to use.
	 * 
	 * @param columnNames one or more column names
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdateOperations updatingColumns(String... columnNames);

	/**
	 * Specify the names of any columns that is a primary key.
	 * 
	 * @param columnNames one or more column names
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdate restrictingColumns(String... columnNames);

	/**
	 * Specify the names of any columns that are to be part of the where clause.
	 * 
	 * @param columnsToOperators one or more column names
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdate restrictingColumns(Map<String, Operator> columnsToOperators);

	/**
	 * Turn off any processing of column meta data information obtained via JDBC.
	 * 
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdateOperations withoutTableColumnMetaDataAccess();

	/**
	 * Include synonyms for the column meta data lookups via JDBC. Note: this is
	 * only necessary to include for Oracle since other databases supporting
	 * synonyms seems to include the synonyms automatically.
	 * 
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdateOperations includeSynonymsForTableColumnMetaData();

	/**
	 * @param flag Use a the provided NativeJdbcExtractor during the column meta
	 *             data lookups via JDBC. Note: this is only necessary to include
	 *             when running with a connection pool that wraps the meta data
	 *             connection and when using a database like Oracle where it is
	 *             necessary to access the native connection to include synonyms.
	 * 
	 * @return the instance of this SimpleJdbcUpdate
	 */
	SimpleJdbcUpdateOperations useNativeJdbcExtractorForMetaData(boolean flag);

	/**
	 * Execute the update using the values passed in.
	 * 
	 * @param updatingValues    Map containing column names and corresponding value
	 * @param restrictingValues List containing PK column values
	 * @return the number of rows affected as returned by the JDBC driver
	 */
	int execute(Map<String, Object> updatingValues, Map<String, Object> restrictingValues);

	/**
	 * Execute the update using the values passed in.
	 * 
	 * @param updatingValues    SqlParameterSource containing values to use for
	 *                          update
	 * @param restrictingValues List containing PK column values
	 * @return the number of rows affected as returned by the JDBC driver
	 */
	int execute(SqlParameterSource updatingValues, SqlParameterSource restrictingValues);

}
