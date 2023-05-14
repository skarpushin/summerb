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
package org.summerb.utils.exceptions.translator;

import java.util.Arrays;
import java.util.List;

public class ExceptionUnwindingStrategyImpl implements ExceptionUnwindingStrategy {
	// NOTE: I'm using String here because in some cases not all classes will be
	// available, i.e. excluded from classpath. Don
	public static final List<String> CLASS_NAMES_TO_SKIP = Arrays.asList(
			"org.springframework.web.util.NestedServletException", "java.lang.reflect.UndeclaredThrowableException",
			"java.lang.reflect.InvocationTargetException");

	@Override
	public Throwable getNextMeaningfulExc(Throwable current) {
		if (current == null) {
			return null;
		}
		Throwable cur = current;
		while (cur != null && isShouldSkipException(cur)) {
			if (cur == cur.getCause()) {
				break;
			}
			cur = cur.getCause();
		}
		return cur;
	}

	protected boolean isShouldSkipException(Throwable cur) {
		return CLASS_NAMES_TO_SKIP.contains(cur.getClass().getName());
	}
}
