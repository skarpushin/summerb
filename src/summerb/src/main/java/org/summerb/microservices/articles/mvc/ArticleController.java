package org.summerb.microservices.articles.mvc;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.mvc.EasyCrudControllerBase;
import org.summerb.approaches.jdbccrud.mvc.filter.FilteringParamsToQueryConverter;
import org.summerb.approaches.jdbccrud.mvc.filter.FilteringParamsToQueryConverterImpl;
import org.summerb.approaches.jdbccrud.mvc.model.EasyCrudQueryParams;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.springmvc.controllers.ControllerBase;
import org.summerb.approaches.springmvc.model.MessageSeverity;
import org.summerb.approaches.springmvc.model.PageMessage;
import org.summerb.approaches.springmvc.utils.CurrentRequestUtils;
import org.summerb.approaches.springmvc.utils.MimeTypeResolver;
import org.summerb.microservices.articles.api.ArticleRenderer;
import org.summerb.microservices.articles.api.AttachmentService;
import org.summerb.microservices.articles.api.dto.Attachment;
import org.summerb.microservices.articles.api.dto.consuming.RenderedArticle;
import org.summerb.utils.exceptions.translator.ExceptionTranslator;

@Controller
public class ArticleController extends ControllerBase {
	public static final long HOUR = 1000L * 60L * 60L;
	public static final long DAY = HOUR * 24L;

	private Logger log = Logger.getLogger(getClass());

	private static final String ATTR_ARTICLE = "article";

	@Autowired
	private ArticleRenderer articleRenderer;

	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private MimeTypeResolver mimeTypeResolver;

	private String viewNameArticle = "articles/article";

	private FilteringParamsToQueryConverter filteringParamsToQueryConverter = new FilteringParamsToQueryConverterImpl();

	@SuppressWarnings("serial")
	public static class AttachmentNotFoundException extends Exception {
		public AttachmentNotFoundException(long id) {
			super("article attachment " + id + " not found");
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ArticleAbsoluteUrlBuilderImpl.DEFAULT_PATH_ARTICLES_ATTACHMENTS
			+ "/{id}/{proposedName}")
	public ResponseEntity<InputStreamResource> getAttachment(Model model, @PathVariable("id") long id,
			HttpServletResponse response) throws AttachmentNotFoundException {
		try {
			Attachment attachment = attachmentService.findById(id);
			if (attachment == null) {
				throw new AttachmentNotFoundException(id);
			}
			long now = new Date().getTime();

			HttpHeaders headers = new HttpHeaders();
			headers.setCacheControl("private");
			headers.setExpires(now + DAY);
			headers.setContentType(
					MediaType.parseMediaType(mimeTypeResolver.resolveContentTypeByFileName(attachment.getName())));
			headers.setContentLength((int) attachment.getSize());

			response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getName() + "\"");
			// NOTE: Looks like there is a bug in the spring - it will add
			// Last-Modified twice to the response
			// responseHeaders.setLastModified(maxLastModified);
			response.setDateHeader("Last-Modified", now);

			InputStreamResource ret = new InputStreamResource(attachmentService.getContentInputStream(id));
			return new ResponseEntity<InputStreamResource>(ret, headers, HttpStatus.OK);
		} catch (AttachmentNotFoundException t) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setHeader("Error", "File not found");
			return null;
		} catch (Throwable t) {
			log.warn("Failed to get article attachment", t);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			response.setHeader("Error", "Failed to get article attachment -> " + msg);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ArticleAbsoluteUrlBuilderImpl.DEFAULT_PATH_ARTICLES_ATTACHMENTS
			+ "/{id}")
	public ModelAndView getAttachment2(Model model, @PathVariable("id") long id, HttpServletResponse response) {
		try {
			Attachment attachment = attachmentService.findById(id);

			response.setContentType(mimeTypeResolver.resolveContentTypeByFileName(attachment.getName()));
			response.setContentLength((int) attachment.getSize());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getName() + "\"");

			FileCopyUtils.copy(attachmentService.getContentInputStream(id), response.getOutputStream());

			return null;
		} catch (Throwable t) {
			log.debug("Failed to get article attachment", t);

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			response.setHeader("Error", "Failed to get article attachment -> " + msg);
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ArticleAbsoluteUrlBuilderImpl.DEFAULT_PATH_ARTICLES
			+ "/{articleKey}")
	public String get(Model model, @PathVariable("articleKey") String articleKey, HttpServletResponse respons,
			Locale locale) {

		try {
			RenderedArticle article = articleRenderer.renderArticle(articleKey, locale);
			model.addAttribute(ATTR_ARTICLE, article);
		} catch (Throwable t) {
			log.debug("Failed to get article", t);
			String msg = exceptionTranslator.buildUserMessage(t, CurrentRequestUtils.getLocale());
			addPageMessage(model.asMap(), new PageMessage(msg, MessageSeverity.Danger));
		}

		return viewNameArticle;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/rest/articles-attachments/ajaxList")
	public @ResponseBody Map<String, ? extends Object> ajaxList(@RequestBody EasyCrudQueryParams filteringParams,
			HttpServletResponse response) throws NotAuthorizedException {
		Query query = filteringParamsToQueryConverter.convert(filteringParams.getFilterParams(),
				attachmentService.getDtoClass());
		PaginatedList<Attachment> ret = attachmentService.query(filteringParams.getPagerParams(), query,
				filteringParams.getOrderBy());
		return Collections.singletonMap(EasyCrudControllerBase.ATTR_LIST, ret);
	}

	public String getViewNameArticle() {
		return viewNameArticle;
	}

	public void setViewNameArticle(String viewNameArticle) {
		this.viewNameArticle = viewNameArticle;
	}
}
