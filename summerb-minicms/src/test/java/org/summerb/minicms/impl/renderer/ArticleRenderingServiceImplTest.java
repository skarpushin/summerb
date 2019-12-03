/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
package org.summerb.minicms.impl.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.summerb.minicms.api.ArticleAbsoluteUrlBuilder;
import org.summerb.minicms.api.ArticleService;
import org.summerb.minicms.api.AttachmentService;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.minicms.api.dto.consuming.RenderedArticle;
import org.summerb.minicms.impl.ArticleRendererImpl;
import org.summerb.stringtemplate.impl.StringTemplateCompilerlImpl;

import integr.org.summerb.minicms.impl.UrlBuilderTestImpl;

public class ArticleRenderingServiceImplTest {
	@Test
	public void testRenderArticle_smokeTest() throws Exception {
		ArticleAbsoluteUrlBuilder articleAbsoluteUrlBuilder = new UrlBuilderTestImpl();
		ArticleService articleService = Mockito.mock(ArticleService.class);
		AttachmentService attachmentService = Mockito.mock(AttachmentService.class);

		Article a1 = new Article();
		a1.setId(1L);
		a1.setArticleKey("a1");
		a1.setAnnotation("Article annotation. Read other article first: ${article['a2']}");
		a1.setContent(
				"Article text. See: ${article['a2']}. And now - here is the screenshot: ${img['screenshot1.jpg']}");
		when(articleService.findArticleByKeyAndLocale(eq("a1"), any(Locale.class))).thenReturn(a1);

		Attachment att1 = new Attachment();
		att1.setId(1L);
		att1.setArticleId(1L);
		att1.setName("screenshot1.jpg");
		when(attachmentService.findArticleAttachments(1L)).thenReturn(new Attachment[] { att1 });

		Article a2 = new Article();
		a2.setId(1L);
		a2.setArticleKey("a2");
		a2.setTitle("Title of article 2");
		a2.setAnnotation("Article annotation. Read other article first: ${article['a2']}");
		a2.setContent(
				"Article text. See: ${article['a2']}. And now - here is the screenshot: ${img['screenshot1.jpg']}");
		when(articleService.findArticleByKeyAndLocale(eq("a2"), any(Locale.class))).thenReturn(a2);

		ArticleRendererImpl fixture = new ArticleRendererImpl();
		fixture.setArticleAbsoluteUrlBuilder(articleAbsoluteUrlBuilder);
		fixture.setArticleService(articleService);
		fixture.setAttachmentService(attachmentService);
		fixture.setStringTemplateCompiler(new StringTemplateCompilerlImpl());

		RenderedArticle result = fixture.renderArticle("a1", Locale.ENGLISH);
		assertNotNull(result);
		String a2href = "<a href=\"url-article:a2\" title=\"Title of article 2\">Title of article 2</a>";
		assertEquals("Article annotation. Read other article first: " + a2href, result.getAnnotation());
		assertEquals("Article text. See: " + a2href
				+ ". And now - here is the screenshot: <img class=\"article-image\" src=\"url-att:screenshot1.jpg\" alt=\"screenshot1.jpg\" />",
				result.getContent());
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}
