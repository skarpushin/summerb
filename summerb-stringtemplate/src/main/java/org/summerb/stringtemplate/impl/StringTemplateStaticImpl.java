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
package org.summerb.stringtemplate.impl;

import org.summerb.stringtemplate.api.StringTemplate;

import com.google.common.base.Preconditions;

public class StringTemplateStaticImpl implements StringTemplate {
	private final String content;

	public StringTemplateStaticImpl(String content) {
		Preconditions.checkArgument(content != null, "String template content must not be null");
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String applyTo(Object rootObject) {
		return content;
	}
}
