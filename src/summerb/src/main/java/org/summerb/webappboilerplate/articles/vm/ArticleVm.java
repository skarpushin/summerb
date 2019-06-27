package org.summerb.webappboilerplate.articles.vm;

import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;
import org.summerb.webappboilerplate.model.ListPm;

import com.google.gson.Gson;

public class ArticleVm {
	private static final Gson cachedGson = new Gson();

	private Article dto;
	private ListPm<Attachment> attachments;
	private String dataAsJson;

	public String getDataAsJson() {
		if (dataAsJson == null) {
			dataAsJson = cachedGson.toJson(dto);
		}
		return dataAsJson;
	}

	public Article getDto() {
		return dto;
	}

	public void setDto(Article dto) {
		this.dto = dto;
		this.dataAsJson = null;
	}

	public ListPm<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(ListPm<Attachment> attachments) {
		this.attachments = attachments;
	}

}
