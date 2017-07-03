package org.summerb.microservices.articles.api.dto;

import org.summerb.approaches.jdbccrud.api.dto.HasAuthor;
import org.summerb.approaches.jdbccrud.api.dto.HasAutoincrementId;
import org.summerb.approaches.jdbccrud.api.dto.HasTimestamps;
import org.summerb.approaches.jdbccrud.common.DtoBase;

public class Article implements DtoBase, HasAutoincrementId, HasAuthor, HasTimestamps {
	private static final long serialVersionUID = -3559789221976911911L;

	public static final String FN_KEY = "articleKey";
	public static final int FN_KEY_SIZE = 255;
	public static final String FN_LANG = "lang";
	public static final int FN_LANG_SIZE = 2;
	public static final String FN_TITLE = "title";
	public static final int FN_TITLE_SIZE = 255;
	public static final String FN_ANNOTATION = "annotation";
	public static final int FN_ANNOTATION_SIZE = 32767;
	public static final String FN_CONTENT = "content";
	public static final int FN_CONTENT_SIZE = 8388607;
	public static final String FN_GROUP = "articleGroup";
	public static final int FN_GROUP_SIZE = 255;

	private Long id;
	private long createdAt;
	private long modifiedAt;
	private String createdBy;
	private String modifiedBy;

	private String articleKey;
	private String lang;

	private String title;
	private String annotation;
	private String content;

	private String articleGroup;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public long getModifiedAt() {
		return modifiedAt;
	}

	@Override
	public void setModifiedAt(long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String getModifiedBy() {
		return modifiedBy;
	}

	@Override
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getArticleKey() {
		return articleKey;
	}

	public void setArticleKey(String key) {
		this.articleKey = key;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getArticleGroup() {
		return articleGroup;
	}

	public void setArticleGroup(String group) {
		this.articleGroup = group;
	}
}
