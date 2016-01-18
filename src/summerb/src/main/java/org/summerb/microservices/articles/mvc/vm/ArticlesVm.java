package org.summerb.microservices.articles.mvc.vm;

import java.util.List;
import java.util.Map;

import org.summerb.approaches.springmvc.utils.DummyMapImpl;

public class ArticlesVm {
	private final List<ArticleVm> contents;

	public ArticlesVm(List<ArticleVm> contents) {
		this.contents = contents;
	}

	private final Map<String, ArticleVm> map = new DummyMapImpl<String, ArticleVm>() {
		@Override
		public ArticleVm get(Object key) {
			for (ArticleVm localization : contents) {
				if (localization.getDto().getLang().equals(key)) {
					return localization;
				}
			}
			throw new RuntimeException("Article localization for " + key + " not found");
		}
	};

	public Map<String, ArticleVm> getMap() {
		return map;
	}

}
