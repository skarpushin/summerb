package org.summerb.easycrud.config;

import org.springframework.context.annotation.Bean;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.exceptions.DaoExceptionTranslator;
import org.summerb.easycrud.exceptions.DaoExceptionTranslatorPostgresImpl;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.mysql.SqlBuilderPostgresImpl;
import org.summerb.easycrud.sql_builder.postgres.OrderByToSqlPostgresImpl;
import org.summerb.easycrud.sql_builder.postgres.QueryToSqlPostgresImpl;
import org.summerb.validation.ValidationContextConfig;

/**
 * This is a baseline configuration class that you can import into your context if you're using
 * Postgres. Or you can extend it, or you can build your own.
 *
 * <p>NOTE: This configuration also expects that summerb validation beans are also registered. You
 * can import their baseline configuration too {@link ValidationContextConfig}
 */
public class EasyCrudConfigPostgres extends EasyCrudConfigAbstract {
  @Bean
  @Override
  protected QueryToSql queryToNativeSqlCompiler(SqlTypeOverrides sqlTypeOverrides) {
    return new QueryToSqlPostgresImpl(sqlTypeOverrides);
  }

  @Bean
  @Override
  protected OrderByToSql orderByToSql() {
    return new OrderByToSqlPostgresImpl();
  }

  @Bean
  @Override
  protected DaoExceptionTranslator daoExceptionTranslator() {
    return new DaoExceptionTranslatorPostgresImpl();
  }

  @Bean
  @Override
  protected SqlBuilder sqlBuilder(
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister,
      QueryToSql queryToSql,
      OrderByToSql orderByToSql) {
    return new SqlBuilderPostgresImpl(
        querySpecificsResolver, fieldsEnlister, queryToSql, orderByToSql);
  }
}
