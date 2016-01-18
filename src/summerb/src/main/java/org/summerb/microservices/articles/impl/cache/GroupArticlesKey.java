package org.summerb.microservices.articles.impl.cache;

import java.util.Locale;

class GroupArticlesKey {
	String group;
	String lang;

	public GroupArticlesKey(String group, Locale locale) {
		this.group = group;
		lang = locale.getLanguage();
	}

	public GroupArticlesKey(String articleGroup, String lang) {
		group = articleGroup;
		this.lang = lang;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupArticlesKey other = (GroupArticlesKey) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		return true;
	}
}