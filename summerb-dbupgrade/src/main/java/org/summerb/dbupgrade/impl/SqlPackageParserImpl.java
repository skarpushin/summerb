package org.summerb.dbupgrade.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradeStatement;
import org.summerb.dbupgrade.utils.StringTokenizer;
import org.summerb.dbupgrade.utils.StringTokenizer.SubString;

public class SqlPackageParserImpl implements SqlPackageParser {
	private static final SubString STRING_MARKER = new SubString("'");
	private static final SubString SINGLE_LINE_COMMENT = new SubString("--");
	private static final SubString MULTI_LINE_COMMENT_OPEN = new SubString("/*");
	private static final SubString MULTI_LINE_COMMENT_CLOSE = new SubString("*/");
	private static final SubString NEW_LINE = new SubString("\n");
	// we're not really processing, but we still have it here so that tokenizer will
	// recognize this and we don't count it as a string region modifier
	private static final SubString ESCAPED_STRING_MARKER = new SubString("\\'");
	private static final SubString STATEMENT_END = new SubString(";");

	@Override
	public Stream<UpgradeStatement> getUpgradeScriptsStream(InputStream is) throws Exception {
		return StreamSupport.stream(new UpgradeStatementSpliterator(is), false);
	}

	protected class UpgradeStatementSpliterator implements Spliterator<UpgradeStatement> {
		StringTokenizer tokenizer;
		StringBuilder sb = new StringBuilder();
		SubString t;
		boolean isWithinString = false;
		boolean isWithinSingleLineComment = false;
		boolean isWithinMultilineComment = false;

		public UpgradeStatementSpliterator(InputStream is) throws Exception {
			tokenizer = buildTokenizer(is);
		}

		@Override
		public boolean tryAdvance(Consumer<? super UpgradeStatement> action) {
			if (sb == null) {
				return false;
			}

			while ((t = tokenizer.next()) != null) {
				// Tokens ignoring mode
				if (NEW_LINE == t) {
					if (isWithinSingleLineComment) {
						isWithinSingleLineComment = false;
					}
					continue;
				}

				if (isWithinSingleLineComment) {
					continue;
				}

				if (isWithinMultilineComment) {
					if (MULTI_LINE_COMMENT_CLOSE == t) {
						isWithinMultilineComment = false;
					}
					continue;
				}

				// String capturing mode
				if (isWithinString) {
					if (STRING_MARKER == t) {
						isWithinString = false;
					}
					sb.append(t);
					continue;
				}

				// Entering ignoring mode
				if (SINGLE_LINE_COMMENT == t) {
					isWithinSingleLineComment = true;
					continue;
				}

				if (MULTI_LINE_COMMENT_OPEN == t) {
					isWithinMultilineComment = true;
					continue;
				}

				if (STRING_MARKER == t) {
					isWithinString = true;
					sb.append(t);
					continue;
				}

				// Statement termination
				if (STATEMENT_END == t) {
					sb.append(t);
					String statement = sb.toString();
					if (StringUtils.hasText(statement)) {
						action.accept(new UpgradeStatement(statement));
					}
					sb = new StringBuilder();
					return true;
				}

				// Regular capturing
				sb.append(t);
			}

			// last part of script not closed by semi-colon ";"
			if (sb.length() > 0) {
				String statement = sb.toString();
				if (StringUtils.hasText(statement)) {
					action.accept(new UpgradeStatement(statement));
				}
				sb = null;
				return true;
			}

			return false;
		}

		@Override
		public long estimateSize() {
			return Long.MAX_VALUE;
		}

		@Override
		public int characteristics() {
			return Spliterator.NONNULL | Spliterator.IMMUTABLE;
		}

		@Override
		public Spliterator<UpgradeStatement> trySplit() {
			return null;
		}
	};

	protected StringTokenizer buildTokenizer(InputStream is) throws Exception {
		StringTokenizer tokenizer = new StringTokenizer(read(is), STRING_MARKER, SINGLE_LINE_COMMENT,
				MULTI_LINE_COMMENT_OPEN, MULTI_LINE_COMMENT_CLOSE, NEW_LINE, ESCAPED_STRING_MARKER, STATEMENT_END);
		return tokenizer;
	}

	protected String read(InputStream is) throws Exception {
		StringWriter writer = new StringWriter();
		String encoding = StandardCharsets.UTF_8.name();
		IOUtils.copy(is, writer, encoding);
		return writer.toString();
	}
}
