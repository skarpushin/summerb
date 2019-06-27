package org.summerb.approaches.jdbccrud.impl.postgres;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.DaoExceptionToFveTranslator;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationContext;
import org.summerb.approaches.validation.errors.DuplicateRecordValidationError;
import org.summerb.utils.exceptions.ExceptionUtils;

/**
 * Postgres-specific impl
 * 
 * @author sergeyk
 *
 */
public class DaoExceptionToFveTranslatorPostgresImpl implements DaoExceptionToFveTranslator {
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

		PSQLException dkep = ExceptionUtils.findExceptionOfType(t, PSQLException.class);
		if (dkep == null) {
			return;
		}

		String detail = dkep.getServerErrorMessage().getDetail();
		if (!StringUtils.hasText(detail)) {
			return;
		}

		if (!detail.startsWith("Key (")) {
			return;
		}

		String fieldsStr = detail.substring(5, detail.indexOf(")"));
		String[] fields = fieldsStr.split(",");

		ValidationContext ctx = new ValidationContext();
		for (String field : fields) {
			ctx.add(new DuplicateRecordValidationError(JdbcUtils.convertUnderscoreNameToPropertyName(field.trim())));
		}
		ctx.throwIfHasErrors();
	}

}
