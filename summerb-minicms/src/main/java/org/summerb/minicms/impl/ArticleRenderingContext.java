/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
package org.summerb.minicms.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.encoder.Encode;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.minicms.api.ArticleAbsoluteUrlBuilder;
import org.summerb.minicms.api.ArticleService;
import org.summerb.minicms.api.AttachmentService;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.minicms.api.dto.consuming.RenderedArticle;
import org.summerb.utils.collection.DummyMapImpl;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class ArticleRenderingContext {
	protected Logger log = LogManager.getLogger(getClass());

	private final ArticleService articleService;
	private final AttachmentService attachmentService;
	private final ArticleAbsoluteUrlBuilder articleAbsoluteUrlBuilder;
	private final RenderedArticle renderedArticle;
	private final Locale locale;

	private Map<String, Article> referencedArticles = new HashMap<String, Article>();
	private Map<String, String> article;
	private Map<String, String> articleHref;
	private Map<String, String> pageUrl;
	private Map<String, String> img;
	private Map<String, String> attachment;

	private Map<String, Attachment> attachmentsCache;

	public ArticleRenderingContext(Locale locale, RenderedArticle renderedArticle, ArticleService articleService,
			AttachmentService attachmentService, ArticleAbsoluteUrlBuilder articleAbsoluteUrlBuilder) {
		Preconditions.checkArgument(articleService != null);
		Preconditions.checkArgument(attachmentService != null);
		Preconditions.checkArgument(locale != null);
		Preconditions.checkArgument(renderedArticle != null);

		this.renderedArticle = renderedArticle;
		this.attachmentService = attachmentService;
		this.articleService = articleService;
		this.articleAbsoluteUrlBuilder = articleAbsoluteUrlBuilder;
		this.locale = locale;
	}

	/**
	 * @return map partial impl for handy access to other article from article
	 *         template by key
	 */
	public Map<String, String> getArticle() {
		if (article == null) {
			article = new DummyMapImpl<String, String>() {
				@Override
				public String get(Object articleKey) {
					try {
						checkReferencedArticleKeyPreconditions(articleKey);

						Article referencedArticle = getReferencedArticle((String) articleKey);

						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append("<a href=\"");
						stringBuffer.append(articleAbsoluteUrlBuilder.buildUrlFroArticle(referencedArticle));
						stringBuffer.append("\" title=\"");
						stringBuffer.append(Encode.forHtmlAttribute(referencedArticle.getTitle()));
						stringBuffer.append("\">");
						stringBuffer.append(Encode.forHtmlContent(referencedArticle.getTitle()));
						stringBuffer.append("</a>");

						return stringBuffer.toString();
					} catch (GenericEntityNotFoundException nfe) {
						log.error("Render failure for " + renderedArticle.getArticleKey() + ", can't find dependency "
								+ nfe.getSubjectTypeMessageCode() + ":" + nfe.getIdentity());
					} catch (Throwable t) {
						log.error("Exception (when rendering " + renderedArticle.getArticleKey()
								+ ") getting article anchor: " + articleKey, t);
					}
					return "UNRESOLVED ARTICLE ANCHOR: " + articleKey;
				}
			};
		}
		return article;
	}

	public Map<String, String> getArticleHref() {
		if (articleHref == null) {
			articleHref = new DummyMapImpl<String, String>() {
				@Override
				public String get(Object articleKey) {
					try {
						checkReferencedArticleKeyPreconditions(articleKey);
						Article referencedArticle = getReferencedArticle((String) articleKey);
						return articleAbsoluteUrlBuilder.buildUrlFroArticle(referencedArticle);
					} catch (GenericEntityNotFoundException nfe) {
						log.error("Render failure for " + renderedArticle.getArticleKey() + ", can't find dependency "
								+ nfe.getSubjectTypeMessageCode() + ":" + nfe.getIdentity());
					} catch (Throwable t) {
						log.error("Exception (when rendering " + renderedArticle.getArticleKey()
								+ ") getting article href: " + articleKey, t);
					}
					return "UNRESOLVED ARTICLE HREF: " + articleKey;
				}
			};
		}
		return articleHref;
	}

	private void checkReferencedArticleKeyPreconditions(Object articleKey) {
		Preconditions.checkArgument(articleKey != null, "Article key must be specified");
		Preconditions.checkArgument(articleKey.toString().length() > 0, "Article key must be specified");
		Preconditions.checkArgument(!renderedArticle.getArticleKey().equals(articleKey),
				"Article can't reference itself");
	}

	protected Article getReferencedArticle(String articleKey) throws GenericEntityNotFoundException {
		if (referencedArticles.containsKey(articleKey)) {
			return referencedArticles.get(articleKey);
		}

		try {
			Article ret = articleService.findArticleByKeyAndLocale(articleKey, locale);
			if (ret == null) {
				throw new GenericEntityNotFoundException("article", articleKey);
			}
			referencedArticles.put(articleKey, ret);
			registerReferencedArticle(ret.getId());
			return ret;
		} catch (Throwable e) {
			Throwables.throwIfInstanceOf(e, GenericEntityNotFoundException.class);
			throw new RuntimeException("Failed to find effective referenced article permutation " + articleKey, e);
		}
	}

	private void registerReferencedArticle(Long id) {
		if (renderedArticle.getArticleReferences() == null) {
			renderedArticle.setArticleReferences(new LinkedList<Long>());
		}
		renderedArticle.getArticleReferences().add(id);
	}

	/**
	 * @return map partial impl for handy access to other article from article
	 *         template by key
	 */
	public Map<String, String> getImg() {
		if (img == null) {
			img = new DummyMapImpl<String, String>() {
				@Override
				public String get(Object name) {
					Preconditions.checkArgument(name != null, "Filename must be specified");
					Preconditions.checkArgument(name.toString().length() > 0, "Filename must be specified");

					Attachment att = findAttachmentByName(name.toString());
					if (att == null) {
						return "UNRESOLVED ARTICLE ATTACHMENT: " + name;
					}

					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("<img class=\"article-image\" src=\"");
					stringBuffer.append(articleAbsoluteUrlBuilder.buildUrlFroArticleAttachment(att));
					stringBuffer.append("\" alt=\"");
					stringBuffer.append(Encode.forXmlAttribute(att.getName()));
					stringBuffer.append("\" />");

					return stringBuffer.toString();
				}
			};
		}
		return img;
	}

	public Map<String, String> getAttachment() {
		if (attachment == null) {
			attachment = new DummyMapImpl<String, String>() {
				@Override
				public String get(Object attachmentFileName) {
					Preconditions.checkArgument(attachmentFileName != null, "Filename must be specified");
					Preconditions.checkArgument(attachmentFileName.toString().length() > 0,
							"Filename must be specified");

					Attachment articleAttachment = findAttachmentByName(attachmentFileName.toString());
					if (articleAttachment == null) {
						return "UNRESOLVED ARTICLE ATTACHMENT: " + attachmentFileName;
					}

					return articleAbsoluteUrlBuilder.buildUrlFroArticleAttachment(articleAttachment);
				}
			};
		}
		return attachment;
	}

	protected Attachment findAttachmentByName(String attachmentFileName) {
		if (attachmentsCache == null) {
			try {
				Map<String, Attachment> ret = new HashMap<String, Attachment>();
				Attachment[] arr = attachmentService.findArticleAttachments(renderedArticle.getId());
				for (int i = 0; i < arr.length; i++) {
					ret.put(arr[i].getName(), arr[i]);
				}
				attachmentsCache = ret;
			} catch (Throwable t) {
				log.error("Failed to resolve article attachments", t);
				attachmentsCache = new HashMap<String, Attachment>();
			}
		}

		return attachmentsCache.get(attachmentFileName);
	}

	public Map<String, String> getPageUrl() {
		if (pageUrl == null) {
			pageUrl = new DummyMapImpl<String, String>() {
				@Override
				public String get(Object relativeUrl) {
					Preconditions.checkArgument(relativeUrl != null, "Url must be specified");
					Preconditions.checkArgument(relativeUrl.toString().length() > 0, "Url must be specified");
					return articleAbsoluteUrlBuilder.buildUrlFroAppWebPage(relativeUrl.toString());
				}
			};
		}
		return pageUrl;
	}

}
