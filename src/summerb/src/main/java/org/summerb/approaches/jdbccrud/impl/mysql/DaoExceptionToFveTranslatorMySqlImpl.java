package org.summerb.approaches.jdbccrud.impl.mysql;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.JdbcUtils;
import org.summerb.approaches.jdbccrud.api.DaoExceptionToFveTranslator;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.errors.DuplicateRecordValidationError;
import org.summerb.utils.exceptions.ExceptionUtils;

/**
 * MySQL-specific impl
 * 
 * @author sergeyk
 *
 */
public class DaoExceptionToFveTranslatorMySqlImpl implements DaoExceptionToFveTranslator {
	public static final String MYSQL_CONSTRAINT_PRIMARY = "PRIMARY";
	public static final String MYSQL_CONSTRAINT_UNIQUE = "_UNIQUE";

	@Override
	public void translateAndThtowIfApplicable(Throwable t) throws FieldValidationException {
		throwIfDuplicate(t);
		
		/**
		 * TODO: We should also be able to translate "data too long" exception. See
		 * DaoExceptionUtils#findTruncatedFieldNameIfAny
		 */
	}

	private void throwIfDuplicate(Throwable t) throws FieldValidationException {
		DuplicateKeyException dke = ExceptionUtils.findExceptionOfType(t, DuplicateKeyException.class);
		if (dke == null) {
			return;
		}

		String constraint = findViolatedConstraintName(dke);
		if (constraint == null) {
			// NOTE: This means that this handler is not capable of parsing such error. We
			// should log it
			return;
		}

		// Handle case when uuid is duplicated
		if (DaoExceptionToFveTranslatorMySqlImpl.MYSQL_CONSTRAINT_PRIMARY.equals(constraint)) {
			throw new FieldValidationException(new DuplicateRecordValidationError(HasId.FN_ID));
		}

		if (!constraint.contains(DaoExceptionToFveTranslatorMySqlImpl.MYSQL_CONSTRAINT_UNIQUE)) {
			throw new IllegalArgumentException("Constraint violation " + constraint, dke);
		}

		String fieldName = constraint.substring(0,
				constraint.indexOf(DaoExceptionToFveTranslatorMySqlImpl.MYSQL_CONSTRAINT_UNIQUE));
		if (fieldName.contains("_")) {
			fieldName = JdbcUtils.convertUnderscoreNameToPropertyName(fieldName);
		}

		throw new FieldValidationException(new DuplicateRecordValidationError(fieldName));
	}

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

}
