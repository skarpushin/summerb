/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

public abstract class SqlPackageParserAbstract implements SqlPackageParser {
  public static final SubString STRING_MARKER = new SubString("'");
  public static final SubString SINGLE_LINE_COMMENT = new SubString("--");
  public static final SubString MULTI_LINE_COMMENT_OPEN = new SubString("/*");
  public static final SubString MULTI_LINE_COMMENT_CLOSE = new SubString("*/");
  public static final SubString NEW_LINE = new SubString("\n");
  public static final SubString STATEMENT_END = new SubString(";");

  @Override
  public Stream<UpgradeStatement> getUpgradeScriptsStream(InputStream is) throws Exception {
    return StreamSupport.stream(new UpgradeStatementSpliterator(is), false);
  }

  protected abstract StringTokenizer buildTokenizer(InputStream is) throws Exception;

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
  }

  protected String read(InputStream is) throws Exception {
    StringWriter writer = new StringWriter();
    String encoding = StandardCharsets.UTF_8.name();
    IOUtils.copy(is, writer, encoding);
    return writer.toString();
  }
}
