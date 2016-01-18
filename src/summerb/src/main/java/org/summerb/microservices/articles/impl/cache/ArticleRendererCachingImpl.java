package org.summerb.microservices.articles.impl.cache;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.microservices.articles.api.ArticleRenderer;
import org.summerb.microservices.articles.api.dto.Article;
import org.summerb.microservices.articles.api.dto.Attachment;
import org.summerb.microservices.articles.api.dto.consuming.RenderedArticle;
import org.summerb.utils.cache.CachesInvalidationNeeded;
import org.summerb.utils.cache.TransactionBoundCache;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ArticleRendererCachingImpl implements ArticleRenderer, InitializingBean {
	private ArticleRenderer articleRenderer;
	private EventBus eventBus;

	private LoadingCache<ArticleCacheKey, RenderedArticle> cache;
	private ConcurrentHashMap<Long, ArticleCacheKey> articleIdToKeyCache;

	@Override
	public void afterPropertiesSet() throws Exception {
		eventBus.register(this);

		cache = new TransactionBoundCache<>("ArticleRendererCachingImpl", CacheBuilder.newBuilder()
				.maximumWeight(2000000).removalListener(removalListener).weigher(weigher).recordStats(), loader);

		articleIdToKeyCache = new ConcurrentHashMap<Long, ArticleCacheKey>();
	}

	private Weigher<ArticleCacheKey, RenderedArticle> weigher = new Weigher<ArticleCacheKey, RenderedArticle>() {
		@Override
		public int weigh(ArticleCacheKey k, RenderedArticle g) {
			return g.getContent().length() * 2;
		}
	};

	private CacheLoader<ArticleCacheKey, RenderedArticle> loader = new CacheLoader<ArticleCacheKey, RenderedArticle>() {
		@Override
		public RenderedArticle load(ArticleCacheKey key) {
			RenderedArticle ret = articleRenderer.renderArticle(key.getArticleKey(), new Locale(key.getLang()));
			if (ret != null) {
				articleIdToKeyCache.put(ret.getId(), key);
			}
			return ret;
		}
	};

	private RemovalListener<ArticleCacheKey, RenderedArticle> removalListener = new RemovalListener<ArticleCacheKey, RenderedArticle>() {
		@Override
		public void onRemoval(RemovalNotification<ArticleCacheKey, RenderedArticle> evt) {
			long id = evt.getValue().getId();
			articleIdToKeyCache.remove(id);

			for (Entry<ArticleCacheKey, RenderedArticle> e : cache.asMap().entrySet()) {
				RenderedArticle dep = e.getValue();
				List<Long> refs = dep.getArticleReferences();
				if (refs != null && refs.contains(id)) {
					cache.invalidate(e.getKey());
				}
			}
		}
	};

	@Subscribe
	public void onArticleChanged(EntityChangedEvent<Article> evt) {
		if (!evt.isTypeOf(Article.class)) {
			return;
		}
		if (evt.getChangeType() != ChangeType.UPDATED && evt.getChangeType() != ChangeType.REMOVED) {
			return;
		}

		ArticleCacheKey cacheKey = new ArticleCacheKey(evt.getValue());
		cache.invalidate(cacheKey);
	}

	@Subscribe
	public void onArticleAttachmentChanged(EntityChangedEvent<Attachment> evt) {
		if (!evt.isTypeOf(Attachment.class)) {
			return;
		}

		ArticleCacheKey articleKey = articleIdToKeyCache.get(evt.getValue().getArticleId());
		if (articleKey != null) {
			cache.invalidate(articleKey);
		}
	}

	@Subscribe
	public void onCacheInvalidationRequest(CachesInvalidationNeeded evt) {
		cache.invalidateAll();
	}

	@Override
	public RenderedArticle renderArticle(String articleKey, Locale locale) {
		Preconditions.checkArgument(StringUtils.hasText(articleKey));
		Preconditions.checkArgument(locale != null);

		ArticleCacheKey cacheKey = new ArticleCacheKey(articleKey, locale);
		RenderedArticle ret = cache.getUnchecked(cacheKey);
		return ret; // DeepCopy.copy(ret);
	}

	public ArticleRenderer getArticleRenderer() {
		return articleRenderer;
	}

	@Required
	public void setArticleRenderer(ArticleRenderer articleRenderer) {
		this.articleRenderer = articleRenderer;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	@Autowired
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
