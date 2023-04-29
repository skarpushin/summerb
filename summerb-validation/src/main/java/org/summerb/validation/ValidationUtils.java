/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.validation;

import java.io.File;
import java.io.IOException;

import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

public class ValidationUtils {

  public static boolean isValidFilename(String fileName) {
    Preconditions.checkArgument(StringUtils.hasText(fileName), "File name must be provided");

    // Check if name contains path separator
    if (fileName.contains("\\") || fileName.contains("/")) {
      return false;
    }

    File f = new File(fileName);
    try {
      f.getCanonicalPath();
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
