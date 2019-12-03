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
package org.summerb.email.api;

import java.util.Locale;

import org.summerb.email.api.dto.EmailMessage;
import org.summerb.email.api.dto.EmailTemplateParams;

/**
 * Interface for interacting with specific SINGLE email template.
 * 
 * For example you need to periodicaly send some email. This builder will have
 * precompiled version of email tempalte and specific email instance might be
 * acquired by calling {@link #buildEmail(String, String, EmailTemplateParams)}
 * 
 * @author skarpushin
 * 
 */
public interface EmailMessageBuilder {

	/**
	 * Get locale this builder was created for
	 */
	Locale getLocale();

	/**
	 * Get specific email message
	 * 
	 * @param fromAddress
	 * @param toAddress
	 * @param emailTemplateParams
	 * @return
	 */
	EmailMessage buildEmail(String fromAddress, String toAddress, EmailTemplateParams emailTemplateParams);
}
