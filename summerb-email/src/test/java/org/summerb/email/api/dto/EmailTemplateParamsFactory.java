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
package org.summerb.email.api.dto;

public class EmailTemplateParamsFactory {
	private EmailTemplateParamsFactory() {
	}

	public static class SimpleUser {
		private String name;

		public SimpleUser(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class SimpleMsgBody {
		private final String msg;
		private final String id;

		public SimpleMsgBody(String msg, String id) {
			this.msg = msg;
			this.id = id;
		}

		public String getMsg() {
			return msg;
		}

		public String getId() {
			return id;
		}
	}

	public static EmailTemplateParams createEmailTemplateParams() {
		return new EmailTemplateParams(new SimpleUser("actualSender"), new SimpleUser("actualRecipient"),
				new SimpleMsgBody("bodyMsg", "bodyId"));
	}

}
