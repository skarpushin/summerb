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
package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.Locale;

public class HackToGetCacheSeconds extends AbstractResourceBasedMessageSource {
	public static long getFrom(AbstractResourceBasedMessageSource other) {
		return other.getCacheMillis() / 1000;
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		throw new IllegalStateException("This is not a real impl");
	}
}
