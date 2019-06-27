package org.summerb.webappboilerplate.articles.vm;

import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.summerb.microservices.articles.api.dto.Attachment;

public class ArticleAttachmentVm {
	private final Attachment articleAttachment;
	private CommonsMultipartFile file;

	public ArticleAttachmentVm(Attachment articleAttachment) {
		this.articleAttachment = articleAttachment;
	}

	public ArticleAttachmentVm() {
		this.articleAttachment = new Attachment();
	}

	public String getName() {
		return articleAttachment.getName();
	}

	public void setName(String fileName) {
		articleAttachment.setName(fileName);
	}

	public CommonsMultipartFile getFile() {
		return file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	public Attachment getAttachment() {
		return articleAttachment;
	}
}
