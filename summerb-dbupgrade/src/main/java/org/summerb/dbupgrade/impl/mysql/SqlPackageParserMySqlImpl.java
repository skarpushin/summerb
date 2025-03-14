/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.dbupgrade.impl.mysql;

import java.io.InputStream;
import org.summerb.dbupgrade.impl.SqlPackageParserAbstract;
import org.summerb.dbupgrade.utils.StringTokenizer;
import org.summerb.dbupgrade.utils.StringTokenizer.SubString;

public class SqlPackageParserMySqlImpl extends SqlPackageParserAbstract {

  // we're not really processing, but we still have it here so that tokenizer will
  // recognize this, and we don't count it as a string region modifier
  private static final SubString ESCAPED_STRING_MARKER = new SubString("\\'");

  @Override
  protected StringTokenizer buildTokenizer(InputStream is) throws Exception {
    return new StringTokenizer(
        read(is),
        STRING_MARKER,
        SINGLE_LINE_COMMENT,
        MULTI_LINE_COMMENT_OPEN,
        MULTI_LINE_COMMENT_CLOSE,
        NEW_LINE,
        ESCAPED_STRING_MARKER,
        STATEMENT_END);
  }
}
