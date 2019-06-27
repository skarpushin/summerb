package org.summerb.approaches.jdbccrud.common;

import java.sql.DataTruncation;

import org.springframework.jdbc.support.JdbcUtils;
import org.summerb.approaches.jdbccrud.api.DaoExceptionToFveTranslator;
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
