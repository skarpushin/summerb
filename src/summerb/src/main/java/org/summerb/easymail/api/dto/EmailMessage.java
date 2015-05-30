package org.summerb.easymail.api.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmailMessage {
	private String subject;
	private String body;

	public String getSubject() {
		return subject;
	}

	@XmlElement
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	@XmlElement
	public void setBody(String body) {
		this.body = body;
	}
}
