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
package org.summerb.webappboilerplate.model;

import org.springframework.context.i18n.LocaleContextHolder;
import org.summerb.webappboilerplate.utils.CurrentRequestUtils;

public class PageMessage {
	private String text;
	private MessageSeverity messageSeverity;

	public PageMessage() {
	}

	public PageMessage(String text) {
		this.text = text;
		this.messageSeverity = MessageSeverity.Info;
	}

	public PageMessage(String text, MessageSeverity messageSeverity) {
		this(text);
		this.messageSeverity = messageSeverity;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setSeverity(MessageSeverity messageSeverity) {
		this.messageSeverity = messageSeverity;
	}

	public MessageSeverity getSeverity() {
		return messageSeverity;
	}

	public String getCssClass() {
		return messageSeverity.toString().toLowerCase();
	}

	public String getIntroWord() {
		return CurrentRequestUtils.getWac().getMessage("message.severity." + getCssClass(), null,
				LocaleContextHolder.getLocale());
	}
}
