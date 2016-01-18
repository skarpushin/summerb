package org.summerb.approaches.jdbccrud.common;

import java.sql.DataTruncation;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.JdbcUtils;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.errors.DataTooLongValidationError;
import org.summerb.utils.exceptions.ExceptionUtils;

public class DaoExceptionUtils {

	public static final String CONSTRAINT_PRIMARY = "PRIMARY";

	/**
	 * Parse exception and find constraint name.
	 * 
	 * Current impl will parse text string which format is expected to be like:
	 * "Duplicate entry 'sameUuid' for key 'PRIMARY'"
	 * 
	 * @param duplicateKeyException
	 *            exception received from DAO
	 * @return null if failed to parse, constraint name otherwise
	 */
	public static String findViolatedConstraintName(DuplicateKeyException duplicateKeyException) {
		if (duplicateKeyException == null) {
			return null;
		}

		String message = duplicateKeyException.getMessage();
		int pos = message.indexOf("for key");
		if (pos < 0) {
			return null;
		}

		message = message.substring(pos);
		if (message.indexOf("'") < 0) {
			return null;
		}

		String[] parts = message.split("'");
		if (parts.length < 2) {
			return null;
		}

		return parts[1];
	}

	/**
	 * @deprecated This feature will not actually work at least with mysql.
	 *             MySql doesn't send the numbers - it sends only field name
	 *             which were truncated.
	 */
	@Deprecated
	public static void propagateIfTruncationError(Throwable t) throws FieldValidationException {
		DataTruncation exc = ExceptionUtils.findExceptionOfType(t, DataTruncation.class);
		if (exc == null) {
			return;
		}

		String fieldName = findTruncatedFieldNameIfAny(t);
		if (fieldName == null) {
			return;
		}

		DataTooLongValidationError dataTooLongValidationError = new DataTooLongValidationError(exc.getDataSize(),
				exc.getTransferSize(), fieldName);

		throw new FieldValidationException(dataTooLongValidationError);
	}

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
