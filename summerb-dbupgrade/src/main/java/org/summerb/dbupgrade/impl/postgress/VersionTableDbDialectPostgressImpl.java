package org.summerb.dbupgrade.impl.postgress;

import org.postgresql.util.PSQLException;
import org.summerb.dbupgrade.impl.mysql.VersionTableDbDialectMySqlImpl;
import org.summerb.utils.exceptions.ExceptionUtils;

public class VersionTableDbDialectPostgressImpl extends VersionTableDbDialectMySqlImpl {
	@Override
	public boolean isTableMissingException(Exception exc) {
		PSQLException grammarException = ExceptionUtils.findExceptionOfType(exc,
				PSQLException.class);
		return grammarException != null && "42P01".equals(grammarException.getSQLState());
	}
}
