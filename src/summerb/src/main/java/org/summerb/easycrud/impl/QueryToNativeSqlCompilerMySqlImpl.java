package org.summerb.easycrud.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.PropertyAccessor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.summerb.easycrud.api.QueryToNativeSqlCompiler;
import org.summerb.easycrud.api.query.DisjunctionCondition;
import org.summerb.easycrud.api.query.FieldCondition;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.query.Restriction;
import org.summerb.easycrud.api.query.restrictions.BooleanEqRestriction;
import org.summerb.easycrud.api.query.restrictions.IsNullRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberEqRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberGreaterOrEqualRestriction;
import org.summerb.easycrud.api.query.restrictions.NumberOneOfRestriction;
import org.summerb.easycrud.api.query.restrictions.StringContainsRestriction;
import org.summerb.easycrud.api.query.restrictions.StringEqRestriction;
import org.summerb.easycrud.api.query.restrictions.StringLengthBetweenRestriction;
import org.summerb.easycrud.api.query.restrictions.StringOneOfRestriction;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class QueryToNativeSqlCompilerMySqlImpl implements QueryToNativeSqlCompiler {

	@Override
	public String buildWhereClauseAndPopulateParams(Query query, MapSqlParameterSource params) {
		StringBuilder sb = new StringBuilder();
		AtomicInteger paramIdx = new AtomicInteger(0);
		buildWhereClauseAndPopulateParams(query, params, paramIdx, sb);
		return sb.toString();
	}

	private void buildWhereClauseAndPopulateParams(Query query, MapSqlParameterSource params, AtomicInteger paramIdx,
			StringBuilder sb) {
		sb.append("(");
		List<Restriction<PropertyAccessor>> rr = query.getRestrictions();
		for (int i = 0; i < rr.size(); i++) {
			Restriction<PropertyAccessor> r = rr.get(i);
			if (i > 0) {
				sb.append(" AND ");
			}

			if (r instanceof FieldCondition) {
				sb.append(buildCondition((FieldCondition) r, params, paramIdx));
			} else if (r instanceof DisjunctionCondition) {
				DisjunctionCondition dc = (DisjunctionCondition) r;
				sb.append("(");
				for (int j = 0; j < dc.getQueries().length; j++) {
					if (j > 0) {
						sb.append(" OR ");
					}
					buildWhereClauseAndPopulateParams(dc.getQueries()[j], params, paramIdx, sb);
				}
				sb.append(")");
			} else {
				throw new IllegalStateException("Unsupported condition: " + r);
			}
		}
		sb.append(")");
	}

	private String buildCondition(FieldCondition c, MapSqlParameterSource params, AtomicInteger paramIdx) {
		String fn = c.getFieldName();
		String cn = underscore(fn);
		if (c.getRestriction() instanceof BooleanEqRestriction) {
			BooleanEqRestriction r = (BooleanEqRestriction) c.getRestriction();
			String pn = pname(paramIdx);
			params.addValue(pn, r.isNegative() ? !r.getValue() : r.getValue());
			return cn + " = :" + pn;
		} else if (c.getRestriction() instanceof IsNullRestriction) {
			IsNullRestriction r = (IsNullRestriction) c.getRestriction();
			return cn + (r.isNegative() ? " IS NOT NULL" : " IS NULL");
		} else if (c.getRestriction() instanceof NumberBetweenRestriction) {
			NumberBetweenRestriction r = (NumberBetweenRestriction) c.getRestriction();
			String pnLower = pname(paramIdx);
			String pnUpper = pname(paramIdx);
			params.addValue(pnLower, r.getLowerBound());
			params.addValue(pnUpper, r.getUpperBound());
			if (!r.isNegative()) {
				return String.format("(%s BETWEEN :%s AND :%s)", cn, pnLower, pnUpper);
			} else {
				return String.format("(%s < :%s OR :%s < %s)", cn, pnLower, pnUpper, cn);
			}
		} else if (c.getRestriction() instanceof StringLengthBetweenRestriction) {
			StringLengthBetweenRestriction r = (StringLengthBetweenRestriction) c.getRestriction();
			String pnLower = pname(paramIdx);
			String pnUpper = pname(paramIdx);
			params.addValue(pnLower, r.getLowerBound());
			params.addValue(pnUpper, r.getUpperBound());
			return String.format("(CHAR_LENGTH(%s) BETWEEN :%s AND :%s)", cn, pnLower, pnUpper);
		} else if (c.getRestriction() instanceof NumberEqRestriction) {
			NumberEqRestriction r = (NumberEqRestriction) c.getRestriction();
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValue());
			return cn + (r.isNegative() ? " != :" : " = :") + pn;
		} else if (c.getRestriction() instanceof NumberGreaterOrEqualRestriction) {
			NumberGreaterOrEqualRestriction r = (NumberGreaterOrEqualRestriction) c.getRestriction();
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValue());
			return cn + (r.isNegative() ? " < :" : " >= :") + pn;
		} else if (c.getRestriction() instanceof NumberOneOfRestriction) {
			NumberOneOfRestriction r = (NumberOneOfRestriction) c.getRestriction();
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValues());
			return cn + (r.isNegative() ? " NOT IN (:" : " IN (:") + pn + ")";
		} else if (c.getRestriction() instanceof StringContainsRestriction) {
			StringContainsRestriction r = (StringContainsRestriction) c.getRestriction();
			String pn = pname(paramIdx);
			params.addValue(pn, "%" + r.getValue() + "%");
			String ret = cn + (r.isNegative() ? " NOT LIKE :" : " LIKE :") + pn;
			return ret;
		} else if (c.getRestriction() instanceof StringEqRestriction) {
			StringEqRestriction r = (StringEqRestriction) c.getRestriction();
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValue());
			String ret = cn + (r.isNegative() ? " != :" : " = :") + pn;
			return ret;
		} else if (c.getRestriction() instanceof StringOneOfRestriction) {
			StringOneOfRestriction r = (StringOneOfRestriction) c.getRestriction();
			String pn = pname(paramIdx);
			params.addValue(pn, r.getValues());
			String ret = cn + (r.isNegative() ? " NOT IN (:" : " IN (:") + pn + ")";
			return ret;
		} else {
			throw new IllegalStateException("Unsupported restriction: " + c);
		}
	}

	private String pname(AtomicInteger paramIdx) {
		return "arg" + paramIdx.incrementAndGet();
	}

	public static String underscore(String name) {
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			result.append(name.substring(0, 1).toLowerCase());
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				if (s.equals(s.toUpperCase())) {
					result.append("_");
					result.append(s.toLowerCase());
				} else {
					result.append(s);
				}
			}
		}
		return result.toString();
	}
}
