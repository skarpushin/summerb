package org.summerb.dbupgrade.impl.postgress;

import java.io.InputStream;

import org.summerb.dbupgrade.impl.SqlPackageParserAbstract;
import org.summerb.dbupgrade.utils.StringTokenizer;
import org.summerb.dbupgrade.utils.StringTokenizer.SubString;

public class SqlPackageParserPostgressImpl extends SqlPackageParserAbstract {

	// we're not really processing, but we still have it here so that tokenizer will
	// recognize this and we don't count it as a string region modifier
	private static final SubString ESCAPED_STRING_MARKER = new SubString("''");

	@Override
	protected StringTokenizer buildTokenizer(InputStream is) throws Exception {
		StringTokenizer tokenizer = new StringTokenizer(read(is), STRING_MARKER, SINGLE_LINE_COMMENT,
				MULTI_LINE_COMMENT_OPEN, MULTI_LINE_COMMENT_CLOSE, NEW_LINE, ESCAPED_STRING_MARKER, STATEMENT_END);
		return tokenizer;
	}

}
