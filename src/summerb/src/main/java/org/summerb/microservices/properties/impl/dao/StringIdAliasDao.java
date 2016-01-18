package org.summerb.microservices.properties.impl.dao;

import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;

public interface StringIdAliasDao {

	PaginatedList<AliasEntry> loadAllAliases(PagerParams pagerParams);

	long createAliasFor(String str);

	Long findAliasFor(String str);

	String findAliasName(long alias);

}
