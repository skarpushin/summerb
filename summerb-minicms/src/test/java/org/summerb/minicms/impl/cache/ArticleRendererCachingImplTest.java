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
package org.summerb.minicms.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.minicms.api.ArticleRenderer;
import org.summerb.minicms.api.dto.Article;
import org.summerb.minicms.api.dto.Attachment;
import org.summerb.minicms.api.dto.consuming.RenderedArticle;

import com.google.common.eventbus.EventBus;

public class ArticleRendererCachingImplTest {

	@Test
	public void testRenderArticle_expectNewVersionWillBeReturnedAfterEvent() throws Exception {
		ArticleRendererCachingImpl fixture = new ArticleRendererCachingImpl();
		fixture.setEventBus(mock(EventBus.class));
		ArticleRenderer renderer = mock(ArticleRenderer.class);
		fixture.setArticleRenderer(renderer);
		fixture.afterPropertiesSet();

		Locale locale = new Locale("ru");

		mockTest(renderer, locale);

		// force cache
		RenderedArticle ra3v1 = fixture.renderArticle("a3", locale);
		assertEquals("t3v1", ra3v1.getTitle());

		RenderedArticle ra3v2 = new RenderedArticle();
		ra3v2.setId(3L);
		ra3v2.setLang("ru");
		ra3v2.setTitle("t3v2");
		ra3v2.setArticleKey("a3");
		ra3v2.setContent("c");
		when(renderer.renderArticle("a3", locale)).thenReturn(ra3v2);

		ra3v1 = fixture.renderArticle("a3", locale);
		assertEquals("t3v1", ra3v1.getTitle());

		Article a3 = new Article();
		a3.setArticleKey("a3");
		a3.setLang("ru");
		fixture.onArticleChanged(EntityChangedEvent.updated(a3));

		ra3v1 = fixture.renderArticle("a3", locale);
		assertEquals("t3v2", ra3v1.getTitle());
	}

	@Test
	public void testRenderArticle_expectDependentWillBeEvictedAsWell() throws Exception {
		ArticleRendererCachingImpl fixture = new ArticleRendererCachingImpl();
		fixture.setEventBus(mock(EventBus.class));
		ArticleRenderer renderer = mock(ArticleRenderer.class);
		fixture.setArticleRenderer(renderer);
		fixture.afterPropertiesSet();

		Locale locale = new Locale("ru");

		mockTest(renderer, locale);

		// force cache
		fixture.renderArticle("a1", locale);
		fixture.renderArticle("a2", locale);
		fixture.renderArticle("a3", locale);

		RenderedArticle ra1v2 = new RenderedArticle();
		ra1v2.setId(1L);
		ra1v2.setLang("ru");
		ra1v2.setTitle("t1v2");
		ra1v2.setArticleKey("a1");
		ra1v2.setContent("c");
		ra1v2.setArticleReferences(Arrays.asList(2L, 3L));
		when(renderer.renderArticle("a1", locale)).thenReturn(ra1v2);

		Article a3 = new Article();
		a3.setArticleKey("a3");
		a3.setLang("ru");
		fixture.onArticleChanged(EntityChangedEvent.updated(a3));

		ra1v2 = fixture.renderArticle("a1", locale);
		assertEquals("t1v2", ra1v2.getTitle());
	}

	private void mockTest(ArticleRenderer renderer, Locale locale) {
		RenderedArticle ra1 = new RenderedArticle();
		ra1.setId(1L);
		ra1.setLang("ru");
		ra1.setTitle("t1v1");
		ra1.setArticleKey("a1");
		ra1.setContent("c");
		ra1.setArticleReferences(Arrays.asList(2L, 3L));
		when(renderer.renderArticle("a1", locale)).thenReturn(ra1);

		RenderedArticle ra2 = new RenderedArticle();
		ra2.setId(2L);
		ra2.setLang("ru");
		ra2.setTitle("t2v1");
		ra2.setArticleKey("a2");
		ra2.setContent("c");
		ra2.setArticleReferences(Arrays.asList(3L));
		when(renderer.renderArticle("a2", locale)).thenReturn(ra2);

		RenderedArticle ra3 = new RenderedArticle();
		ra3.setId(3L);
		ra3.setLang("ru");
		ra3.setTitle("t3v1");
		ra3.setArticleKey("a3");
		ra3.setContent("c");
		when(renderer.renderArticle("a3", locale)).thenReturn(ra3);
	}

	@Test
	public void testRenderArticle_expectArticleCacheInvalidatedOnAttachmentChange() throws Exception {
		ArticleRendererCachingImpl fixture = new ArticleRendererCachingImpl();
		fixture.setEventBus(mock(EventBus.class));
		ArticleRenderer renderer = mock(ArticleRenderer.class);
		fixture.setArticleRenderer(renderer);
		fixture.afterPropertiesSet();

		Locale locale = new Locale("ru");

		mockTest(renderer, locale);

		// force cache
		fixture.renderArticle("a1", locale);
		fixture.renderArticle("a2", locale);
		fixture.renderArticle("a3", locale);

		RenderedArticle ra2v2 = new RenderedArticle();
		ra2v2.setId(1L);
		ra2v2.setLang("ru");
		ra2v2.setTitle("t2v2");
		ra2v2.setArticleKey("a2");
		ra2v2.setContent("c");
		ra2v2.setArticleReferences(Arrays.asList(3L));
		when(renderer.renderArticle("a2", locale)).thenReturn(ra2v2);

		Attachment att3 = new Attachment();
		att3.setArticleId(3L);
		fixture.onArticleAttachmentChanged(EntityChangedEvent.removedObject(att3));

		ra2v2 = fixture.renderArticle("a2", locale);
		assertEquals("t2v2", ra2v2.getTitle());
	}

}
