package org.summerb.approaches.jdbccrud.impl.postgres;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.approaches.jdbccrud.api.QueryToNativeSqlCompiler;
import org.summerb.approaches.jdbccrud.api.query.restrictions.StringContainsRestriction;
import org.summerb.approaches.jdbccrud.api.query.restrictions.StringEqRestriction;
import org.summerb.approaches.jdbccrud.api.query.restrictions.StringOneOfRestriction;
import org.summerb.approaches.jdbccrud.impl.mysql.ConditionConverter;
import org.summerb.approaches.jdbccrud.impl.mysql.QueryToNativeSqlCompilerMySqlImpl;

/**
 * MySQL specific impl of {@link QueryToNativeSqlCompiler}
 * 
 * WARNING!!! It is suggested to have an index "lower(string_field)" for text
 * fields which will be searched
 * 
 * @author sergey.karpushin
 *
 */
public class QueryToNativeSqlCompilerPostgresImpl extends QueryToNativeSqlCompilerMySqlImpl {
	public QueryToNativeSqlCompilerPostgresImpl() {
		super();
		converters.put(StringEqRestriction.class, stringEqRestriction);
		converters.put(StringContainsRestriction.class, stringContainsRestriction);
		converters.put(StringOneOfRestriction.class, stringOneOfRestriction);
	}

	private ConditionConverter<StringEqRestriction> stringEqRestriction = new ConditionConverter<StringEqRestriction>() {
		@Override
		public String convert(StringEqRestriction r, MapSqlParameterSource params, AtomicInteger paramIdx,
				String underscoredFieldName) {
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValue().toLowerCase());
			return String.format("lower(%s) %s :%s", underscoredFieldName, r.isNegative() ? " != " : " = ", pn);
		}
	};

	private ConditionConverter<StringContainsRestriction> stringContainsRestriction = new ConditionConverter<StringContainsRestriction>() {
		@Override
		public String convert(StringContainsRestriction r, MapSqlParameterSource params, AtomicInteger paramIdx,
				String underscoredFieldName) {
			String pn = pname(paramIdx);
			params.addValue(pn, "%" + r.getValue() + "%");
			String ret = underscoredFieldName + (r.isNegative() ? " NOT ILIKE :" : " ILIKE :") + pn;
			return ret;
		}
	};

	private ConditionConverter<StringOneOfRestriction> stringOneOfRestriction = new ConditionConverter<StringOneOfRestriction>() {
		@Override
		public String convert(StringOneOfRestriction r, MapSqlParameterSource params, AtomicInteger paramIdx,
				String underscoredFieldName) {
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValues().stream().map(x -> x.toLowerCase()).collect(Collectors.toList()));
			return String.format("lower(%s) %s (:%s)", underscoredFieldName, r.isNegative() ? " NOT IN " : " IN ", pn);
		}
	};

}
