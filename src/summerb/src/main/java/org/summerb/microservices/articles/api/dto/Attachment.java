package org.summerb.microservices.articles.api.dto;

import java.io.InputStream;

import org.summerb.approaches.jdbccrud.api.dto.HasAutoincrementId;
import org.summerb.approaches.jdbccrud.common.DtoBase;
import org.summerb.utils.Clonnable;

public class Attachment implements DtoBase, HasAutoincrementId, Clonnable<Attachment> {
	private static final long serialVersionUID = 6611992873465286245L;

	public static final String FN_NAME = "name";
	public static final int FN_NAME_MAXSIZE = 200;
	public static final String FN_ARTICLE_ID = "articleId";
	public static final String FN_CONTENTS = "contents";
	public static final String FN_SIZE = "size";

	private Long id;
	private long articleId;
	private String name;
	private long size;
	private InputStream contents;

	@Override
	public Attachment clone() {
		Attachment ret = new Attachment();
		ret.setId(id);
		ret.setArticleId(articleId);
		ret.setName(name);
		ret.setSize(size);
		ret.setContents(contents);
		return ret;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public InputStream getContents() {
		return contents;
	}

	public void setContents(InputStream content) {
		this.contents = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
