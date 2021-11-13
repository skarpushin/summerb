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
package org.summerb.validation.errors;

import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;
import org.summerb.validation.ValidationError;

public class MustBeEqualsValidationError extends ValidationError implements HasMessageArgsConverters {
	private static final long serialVersionUID = 1269537536170054395L;

	/**
	 * @deprecated used only for serialization
	 */
	@Deprecated
	public MustBeEqualsValidationError() {

	}

	public MustBeEqualsValidationError(String aMessageCode, String bMessageCode, String fieldToken) {
		super("validation.valuesMustBeEquals", fieldToken, aMessageCode, bMessageCode);
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return new MessageArgConverter[] { MessageCodeMessageArgConverter.INSTANCE,
				MessageCodeMessageArgConverter.INSTANCE };
	}
}
