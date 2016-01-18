package org.summerb.microservices.emailsender.api.dto;

import java.util.HashMap;
import java.util.Map;

import org.summerb.microservices.emailsender.api.EmailMessageBuilder;

import com.google.common.base.Preconditions;

/**
 * Contains email message params (!), not body or template of the body, but
 * their params.
 * 
 * {@link #getFrom()}, {@link #getTo()}, {@link #getBody()} used when evaluating
 * pre-compiled email template {@link EmailMessageBuilder} and so contains
 * template arguments/parameters, not content itself.
 * 
 * Instance of this class is exposed as root object to templates evaluation
 * context
 * 
 * @author skarpushin
 * 
 */
public class EmailTemplateParams {
	private Object from;
	private Object to;
	private Object body;
	private Map<String, Object> extension;

	public EmailTemplateParams() {
	}

	public EmailTemplateParams(Object sender, Object recipient, Object body) {
		Preconditions.checkArgument(sender != null);
		Preconditions.checkArgument(recipient != null);
		Preconditions.checkArgument(body != null);

		this.from = sender;
		this.to = recipient;
		this.body = body;
	}

	public Map<String, Object> getExtension() {
		if (extension == null) {
			extension = new HashMap<String, Object>();
		}
		return extension;
	}

	public void setExtension(Map<String, Object> extension) {
		this.extension = extension;
	}

	public Object getFrom() {
		if (from == null) {
			from = new Object();
		}
		return from;
	}

	public void setFrom(Object sender) {
		this.from = sender;
	}

	public Object getTo() {
		if (to == null) {
			to = new Object();
		}
		return to;
	}

	public void setTo(Object recipient) {
		this.to = recipient;
	}

	public Object getBody() {
		if (body == null) {
			body = new Object();
		}
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
}
