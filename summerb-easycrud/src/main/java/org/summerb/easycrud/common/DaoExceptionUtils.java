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

import java.sql.DataTruncation;

import org.springframework.jdbc.support.JdbcUtils;
import org.summerb.easycrud.api.DaoExceptionToFveTranslator;
import org.summerb.utils.exceptions.ExceptionUtils;

public class DaoExceptionUtils {

	/**
	 * Find a name of the field which was truncated
	 * 
	 * @param t
	 *            exception received from Spring JDBC
	 * @return field name if any, otherwize null
	 * @deprecated this is mysql-specific impl, should be OCP'ed and moved to
	 *             {@link DaoExceptionToFveTranslator}
	 */
	@Deprecated
	public static String findTruncatedFieldNameIfAny(Throwable t) {
		DataTruncation exc = ExceptionUtils.findExceptionOfType(t, DataTruncation.class);
		if (exc == null) {
			return null;
		}

		String msg = exc.getMessage();
		if (!msg.contains("too long")) {
			return null;
		}

		String[] params = msg.split("\'");
		if (params.length < 2) {
			return null;
		}

		String fieldName = params[1];

		// Ok now convert it to camel case if needed
		if (fieldName.contains("_")) {
			fieldName = JdbcUtils.convertUnderscoreNameToPropertyName(fieldName);
		}

		return fieldName;
	}

}
