package integr.org.summerb.easycrud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.summerb.easycrud.config.EasyCrudConfigAbstract;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.exceptions.DaoExceptionTranslator;
import org.summerb.easycrud.exceptions.DaoExceptionTranslatorMySqlImpl;
import org.summerb.easycrud.exceptions.DaoExceptionTranslatorPostgresImpl;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.mysql.OrderByToSqlMySqlImpl;
import org.summerb.easycrud.sql_builder.mysql.QueryToSqlMySqlImpl;
import org.summerb.easycrud.sql_builder.mysql.SqlBuilderMySqlImpl;
import org.summerb.easycrud.sql_builder.mysql.SqlBuilderPostgresImpl;
import org.summerb.easycrud.sql_builder.postgres.OrderByToSqlPostgresImpl;
import org.summerb.easycrud.sql_builder.postgres.QueryToSqlPostgresImpl;
import org.summerb.validation.ValidationContextConfig;

@Configuration
@Import({ValidationContextConfig.class})
public class EasyCrudConfig extends EasyCrudConfigAbstract {
  @Autowired Environment environment;

  protected boolean isPostgres() {
    return environment.matchesProfiles("postgres");
  }

  @Override
  protected QueryToSql queryToNativeSqlCompiler(SqlTypeOverrides sqlTypeOverrides) {
    return isPostgres()
        ? new QueryToSqlPostgresImpl(sqlTypeOverrides)
        : new QueryToSqlMySqlImpl(sqlTypeOverrides);
  }

  @Override
  protected OrderByToSql orderByToSql() {
    return isPostgres() ? new OrderByToSqlPostgresImpl() : new OrderByToSqlMySqlImpl();
  }

  @Override
  protected SqlBuilder sqlBuilder(
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister,
      QueryToSql queryToSql,
      OrderByToSql orderByToSql) {
    return isPostgres()
        ? new SqlBuilderPostgresImpl(
            querySpecificsResolver, fieldsEnlister, queryToSql, orderByToSql)
        : new SqlBuilderMySqlImpl(querySpecificsResolver, fieldsEnlister, queryToSql, orderByToSql);
  }

  @Override
  protected DaoExceptionTranslator daoExceptionTranslator() {
    return isPostgres()
        ? new DaoExceptionTranslatorPostgresImpl()
        : new DaoExceptionTranslatorMySqlImpl();
  }
}
