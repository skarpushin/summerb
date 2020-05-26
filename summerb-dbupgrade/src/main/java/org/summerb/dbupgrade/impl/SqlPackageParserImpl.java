package org.summerb.dbupgrade.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradeStatement;
import org.summerb.dbupgrade.utils.StringTokenizer;

/**
 * This is rather crude impl of parser which is mindful of comments though but
 * is not guaranteed to support all possible lexuical constructs
 *
 */
public class SqlPackageParserImpl implements SqlPackageParser {
	@Override
	public Stream<UpgradeStatement> getUpgradeScriptsStream(InputStream is) throws Exception {
		List<UpgradeStatement> ret = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(read(is), "'", "--", "/*", "*/", "\n", "\\'", ";");

		StringBuilder sb = new StringBuilder();
		String t;
		boolean isWithinString = false;
		boolean isWithinSingleLineComment = false;
		boolean isWithinMultilineComment = false;
		while ((t = tokenizer.next()) != null) {
			// Tokens ignoring mode
			if ("\n".equals(t)) {
				if (isWithinSingleLineComment) {
					isWithinSingleLineComment = false;
				}
				continue;
			}

			if (isWithinSingleLineComment) {
				continue;
			}

			if (isWithinMultilineComment) {
				if ("*/".equals(t)) {
					isWithinMultilineComment = false;
				}
				continue;
			}

			// String capturing mode
			if (isWithinString) {
				if ("'".equals(t)) {
					isWithinString = false;
				}
				sb.append(t);
				continue;
			}

			// Entering ignoring mode
			if ("--".equals(t)) {
				isWithinSingleLineComment = true;
				continue;
			}

			if ("/*".equals(t)) {
				isWithinMultilineComment = true;
				continue;
			}

			if ("'".equals(t)) {
				isWithinString = true;
				sb.append(t);
				continue;
			}

			// Statement termination
			if (";".equals(t)) {
				sb.append(t);
				ret.add(new UpgradeStatement(sb.toString()));
				sb = new StringBuilder();
				continue;
			}

			// Regular capturing
			sb.append(t);
		}
		return ret.stream();
	}

	private String read(InputStream is) throws Exception {
		StringWriter writer = new StringWriter();
		String encoding = StandardCharsets.UTF_8.name();
		IOUtils.copy(is, writer, encoding);
		return writer.toString();
	}

}
