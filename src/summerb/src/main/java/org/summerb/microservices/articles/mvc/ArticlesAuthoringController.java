package org.summerb.microservices.articles.mvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.springmvc.Views;
import org.summerb.approaches.springmvc.controllers.ControllerBase;
import org.summerb.approaches.springmvc.model.ListPm;
import org.summerb.approaches.springmvc.model.MessageSeverity;
import org.summerb.approaches.springmvc.model.PageMessage;
import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.microservices.articles.api.ArticleService;
import org.summerb.microservices.articles.api.AttachmentService;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;
import org.summerb.microservices.articles.mvc.vm.ArticleAttachmentVm;
import org.summerb.microservices.articles.mvc.vm.ArticleVm;
import org.summerb.microservices.articles.mvc.vm.ArticlesVm;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;

import com.google.common.base.Preconditions;

@Controller
@RequestMapping(value = "/article-authoring")
@Secured({ "ROLE_ADMIN" })
public class ArticlesAuthoringController extends ControllerBase {
	private static final String ATTR_ARTICLE = "article";
	private static final String ATTR_ARTICLE_KEY = "articleKey";
	private static final String ATTR_ARTICLES = "articles";
	private static final String ATTR_ARTICLE_ATTACHMENT = "articleAttachment";

	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private ArticleService articleService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private ExceptionTranslator exceptionTranslator;

	private String viewNameArticlesAuthoringList = "article-authoring/articles";
	private String viewNameArticleAuthoring = "article-authoring/article";

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String listArticles(Model model, Locale locale) throws NotAuthorizedException {
		PaginatedList<Article> articles = articleService.findArticles(PagerParams.ALL, locale);
		model.addAttribute(ATTR_ARTICLES, articles);
		model.addAttribute(ATTR_ARTICLE, new Article());
		return viewNameArticlesAuthoringList;
	}

	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public @ResponseBody Map<String, ? extends Object> createArticle(@RequestBody Article dto,
			HttpServletResponse response) {
		try {
			if (!StringUtils.hasText(dto.getArticleGroup())) {
				dto.setArticleGroup(null);
			}

			putTbds(dto, new Locale("ru"));
			articleService.create(dto);

			putTbds(dto, new Locale("en"));
			articleService.create(dto);

			return Collections.singletonMap(ATTR_ARTICLE_KEY, dto.getArticleKey());
		} catch (Exception t) {
			log.error("Failed to create article", t);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			return Collections.singletonMap(ATTR_ERROR, msg);
		}
	}

	private void putTbds(Article dto, Locale locale) throws FieldValidationException {
		// NOTE: That is BAD !!! Strategy to convert LOcal to string suppose to
		// be incapsulated into service!
		dto.setLang(locale.getLanguage());
		dto.setTitle("TBD: title: " + dto.getArticleKey() + ", " + locale.toString());
		dto.setAnnotation("TBD: Annotation text, " + locale.toString());
		dto.setContent("TBD: Content, " + locale.toString());
	}

	@Transactional
	@RequestMapping(method = RequestMethod.DELETE, value = "/{articleKey}")
	public @ResponseBody Map<String, ? extends Object> deleteArticle(Model model,
			@PathVariable("articleKey") String articleKey, HttpServletResponse response) {

		try {
			Map<Locale, Article> options = articleService.findArticleLocalizations(articleKey);
			for (Article a : options.values()) {
				articleService.deleteByIdOptimistic(a.getId(), a.getModifiedAt());
			}

			return Collections.singletonMap(ATTR_ARTICLE_KEY, articleKey);
		} catch (Throwable t) {
			log.error("Failed to delete article", t);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			return Collections.singletonMap(ATTR_ERROR, msg);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{articleKey}")
	public String getArticle(Model model, @PathVariable("articleKey") String articleKey, Locale locale)
			throws NotAuthorizedException {
		model.addAttribute(ATTR_ARTICLE_KEY, articleKey);

		Map<Locale, Article> options = articleService.findArticleLocalizations(articleKey);
		if (options.size() == 0) {
			throw new RuntimeException("Article not found: " + articleKey);
		}

		List<ArticleVm> contents = new LinkedList<ArticleVm>();

		for (Entry<Locale, Article> entry : options.entrySet()) {
			ArticleVm contentPm = new ArticleVm();
			contentPm.setDto(entry.getValue());
			contentPm.setAttachments(new ListPm<Attachment>(
					Arrays.asList(articleService.findArticleAttachments(entry.getValue().getId()))));
			contents.add(contentPm);
		}

		ArticlesVm articlesVm = new ArticlesVm(contents);
		model.addAttribute(ATTR_ARTICLES, articlesVm.getMap());
		model.addAttribute(ATTR_ARTICLE, contents.get(0));

		return viewNameArticleAuthoring;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}")
	public @ResponseBody Map<String, ? extends Object> updateArticle(@RequestBody Article dto,
			@PathVariable("id") long id, HttpServletResponse response) {
		try {
			Article currentVersion = articleService.findById(id);
			currentVersion.setTitle(dto.getTitle());
			currentVersion.setAnnotation(dto.getAnnotation());
			currentVersion.setContent(dto.getContent());
			Article newVersion = articleService.update(currentVersion);

			// ret
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put(ATTR_ARTICLE, newVersion);
			return ret;
		} catch (Throwable t) {
			log.error("Failed to update article content", t);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			return Collections.singletonMap(ATTR_ERROR, msg);
		}
	}

	// NOTE: File upload example:
	// http://www.ioncannon.net/programming/975/spring-3-file-upload-example/
	@RequestMapping(method = RequestMethod.POST, value = "/{articleId}/addAttachment")
	public String createAttachment(@ModelAttribute(ATTR_ARTICLE_ATTACHMENT) ArticleAttachmentVm articleAttachmentVm,
			Model model, @PathVariable("articleId") long articleId) throws Exception {
		try {
			Preconditions.checkArgument(
					articleAttachmentVm.getFile() != null && articleAttachmentVm.getFile().getFileItem() != null,
					"File required");

			Article article = articleService.findById(articleId);
			Preconditions.checkArgument(article != null, "Article not found");

			Attachment att = articleAttachmentVm.getAttachment();
			att.setArticleId(articleId);

			FileItem fileItem = articleAttachmentVm.getFile().getFileItem();
			att.setSize(fileItem.getSize());
			String fileName = fileItem.getName();
			att.setName(fileName);
			att.setContents(articleAttachmentVm.getFile().getInputStream());
			articleService.addArticleAttachment(att);
			return Views.redirect(String.format("article-authoring/%s", article.getArticleKey()));
		} catch (Throwable t) {
			log.error("Failed to create attachment", t);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			addPageMessage(model.asMap(), new PageMessage(msg, MessageSeverity.Danger));
			// TBD: Navigate to article authoring instead!
			return viewNameArticleAuthoring;
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{articleId}/attachments/{attachmentId}")
	public @ResponseBody Map<String, ? extends Object> deleteAttachment(Model model,
			@PathVariable("articleId") long articleId, @PathVariable("attachmentId") long attachmentId,
			HttpServletResponse response) {
		try {
			attachmentService.deleteById(attachmentId);
			return new HashMap<String, Object>();
		} catch (Throwable t) {
			log.error("Failed to delete attachment", t);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			return Collections.singletonMap(ATTR_ERROR, msg);
		}
	}

}
