package org.summerb.springvmc.controllers.model;

import org.springframework.context.i18n.LocaleContextHolder;
import org.summerb.springvmc.utils.CurrentRequestUtils;

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
