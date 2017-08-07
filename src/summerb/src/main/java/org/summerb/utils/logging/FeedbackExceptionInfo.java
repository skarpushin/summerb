package org.summerb.utils.logging;

import java.io.Serializable;
import java.util.Set;

public class FeedbackExceptionInfo implements Serializable {
	private static final long serialVersionUID = 283896704292263083L;

	/**
	 * That field contains id of exception location, helps to group many
	 * exception to one cause
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
