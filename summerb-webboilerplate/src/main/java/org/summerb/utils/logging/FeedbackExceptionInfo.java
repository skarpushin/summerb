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
package org.summerb.utils.logging;

import java.io.Serializable;
import java.util.Set;

public class FeedbackExceptionInfo implements Serializable {
	private static final long serialVersionUID = 283896704292263083L;

	/**
	 * That field contains id of exception location, helps to group many exception
	 * to one cause
	 */
	private String id;
	private int count;
	private String msgs;
	private Set<String> affectedUsers;

	public String getId() {
		return id;
	}

	public void setId(String identification) {
		this.id = identification;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int timesSeen) {
		this.count = timesSeen;
	}

	public String getMsgs() {
		return msgs;
	}

	public void setMsgs(String messagesChain) {
		this.msgs = messagesChain;
	}

	public Set<String> getAffectedUsers() {
		return affectedUsers;
	}

	public void setAffectedUsers(Set<String> affectedUsers) {
		this.affectedUsers = affectedUsers;
	}
}
