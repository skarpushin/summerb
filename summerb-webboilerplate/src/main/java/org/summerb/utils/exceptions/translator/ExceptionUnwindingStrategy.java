/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.web.util.NestedServletException;

public interface ExceptionUnwindingStrategy {
	/**
	 * Get first exception that actually might mean something. Impl supposed to skip
	 * meaningless exceptions like {@link NestedServletException},
	 * {@link UndeclaredThrowableException}, etc..
	 * 
	 * @param current
	 *            current exception at hand
	 * @return either the same exception or more meaningful.
	 */
	Throwable getNextMeaningfulExc(Throwable current);
}
