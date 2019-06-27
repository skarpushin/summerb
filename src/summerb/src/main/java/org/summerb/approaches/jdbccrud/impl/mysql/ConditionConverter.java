package org.summerb.approaches.jdbccrud.impl.mysql;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.approaches.jdbccrud.api.query.Restriction;

public interface ConditionConverter<T extends Restriction<?>> {

	String convert(T r, MapSqlParameterSource params, AtomicInteger paramIdx, String underscoredFieldName);

}