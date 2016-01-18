package org.summerb.utils.collection;

public interface ParametrizedItemMatcher<TItemType, TParamType> {
	boolean isMatch(TItemType item, TParamType param);
}
