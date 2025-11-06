package org.summerb.easycrud.config;

import org.springframework.context.annotation.Bean;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.exceptions.DaoExceptionTranslator;
import org.summerb.easycrud.exceptions.DaoExceptionTranslatorMySqlImpl;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.mysql.OrderByToSqlMySqlImpl;
import org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl;
import org.summerb.easycrud.sql_builder.mysql.SqlBuilderMySqlImpl;
import org.summerb.validation.ValidationContextConfig;

/**
 * This is a baseline configuration class that you can import into your context if you're using
 * MySQL. Or you can extend it, or you can build your own.
 *
 * <p>NOTE: This configuration also expects that summerb validation beans are also registered. You
 * can import their baseline configuration too {@link ValidationContextConfig}
 */
public class EasyCrudConfigMySql extends EasyCrudConfigAbstract {
  @Bean
  @Override
  protected QueryToSql queryToNativeSqlCompiler(SqlTypeOverrides sqlTypeOverrides) {
    return new QueryToSqlMySqlImpl(sqlTypeOverrides);
  }

  @Bean
  @Override
  protected OrderByToSql orderByToSql() {
    return new OrderByToSqlMySqlImpl();
  }

  @Bean
  @Override
  protected DaoExceptionTranslator daoExceptionTranslator() {
    return new DaoExceptionTranslatorMySqlImpl();
  }

  @Bean
  @Override
  protected SqlBuilder sqlBuilder(
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister,
      QueryToSql queryToSql,
      OrderByToSql orderByToSql) {
    return new SqlBuilderMySqlImpl(
        querySpecificsResolver, fieldsEnlister, queryToSql, orderByToSql);
  }
}
