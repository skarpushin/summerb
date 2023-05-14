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
package org.summerb.easycrud.api.exceptions;

import org.summerb.easycrud.api.dto.HasTimestamps;
import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageArgsConverters;
import org.summerb.i18n.HasMessageCode;
import org.summerb.i18n.MessageArgConverter;
import org.summerb.i18n.MessageCodeMessageArgConverter;

/**
 * Exception is thrown if record was already updated by someone else. Normally
 * {@link HasTimestamps#getModifiedAt()} is used for optimistic locking logic
 * 
 * @author sergey.karpushin
 *
 */
public class ConcurrentModificationException extends Exception
		implements HasMessageCode, HasMessageArgs, HasMessageArgsConverters {
	private static final long serialVersionUID = -8553908925129274626L;
	private static final MessageArgConverter[] MESSAGE_ARG_CONVERTERS = new MessageArgConverter[] {
			MessageCodeMessageArgConverter.INSTANCE, null };

	private String objectTypeName;
	private String objectIdentifier;

	/**
	 * @deprecated only for io
	 */
	@Deprecated
	public ConcurrentModificationException() {
	}

	public ConcurrentModificationException(String objectTypeName, String objectIdentifier) {
		this.objectTypeName = objectTypeName;
		this.objectIdentifier = objectIdentifier;
	}

	@Override
	public String getMessageCode() {
		return "exception.dao.concurrentModification";
	}

	@Override
	public Object[] getMessageArgs() {
		return new Object[] { objectTypeName, objectIdentifier };
	}

	@Override
	public MessageArgConverter[] getMessageArgsConverters() {
		return MESSAGE_ARG_CONVERTERS;
	}

	public String getObjectTypeName() {
		return objectTypeName;
	}

	public String getObjectIdentifier() {
		return objectIdentifier;
	}

}
