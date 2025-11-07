package org.summerb.easycrud.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.summerb.easycrud.EasyCrudServiceResolver;
import org.summerb.easycrud.dao.NamedParameterJdbcTemplateEx;
import org.summerb.easycrud.dao.SqlTypeOverrides;
import org.summerb.easycrud.dao.SqlTypeOverridesDefaultImpl;
import org.summerb.easycrud.exceptions.DaoExceptionTranslator;
import org.summerb.easycrud.impl.EasyCrudServiceResolverSpringImpl;
import org.summerb.easycrud.join_query.JoinQueryFactory;
import org.summerb.easycrud.join_query.QuerySpecificsResolver;
import org.summerb.easycrud.join_query.ReferringToFieldsFinder;
import org.summerb.easycrud.join_query.SelectFactory;
import org.summerb.easycrud.join_query.impl.JoinQueryFactoryImpl;
import org.summerb.easycrud.join_query.impl.QuerySpecificsResolverImpl;
import org.summerb.easycrud.join_query.impl.ReferringToFieldsFinderCachingImpl;
import org.summerb.easycrud.join_query.impl.ReferringToFieldsFinderImpl;
import org.summerb.easycrud.join_query.impl.SelectFactoryImpl;
import org.summerb.easycrud.scaffold.EasyCrudScaffold;
import org.summerb.easycrud.scaffold.EasyCrudServiceProxyFactory;
import org.summerb.easycrud.scaffold.ScaffoldedMethodFactory;
import org.summerb.easycrud.scaffold.impl.EasyCrudScaffoldImpl;
import org.summerb.easycrud.scaffold.impl.EasyCrudServiceProxyFactoryImpl;
import org.summerb.easycrud.scaffold.impl.ScaffoldedMethodFactoryMySqlImpl;
import org.summerb.easycrud.sql_builder.FieldsEnlister;
import org.summerb.easycrud.sql_builder.OrderByToSql;
import org.summerb.easycrud.sql_builder.QueryToSql;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.sql_builder.impl.FieldsEnlisterCachingImpl;
import org.summerb.easycrud.sql_builder.impl.FieldsEnlisterImpl;
import org.summerb.easycrud.tools.RowCloner;
import org.summerb.easycrud.tools.RowClonerReflectionImpl;
import org.summerb.easycrud.tools.StringIdGenerator;
import org.summerb.easycrud.tools.StringIdGeneratorUuidImpl;

public abstract class EasyCrudConfigAbstract {
  @Bean
  protected abstract QueryToSql queryToNativeSqlCompiler(SqlTypeOverrides sqlTypeOverrides);

  @Bean
  protected abstract OrderByToSql orderByToSql();

  @Bean
  protected abstract SqlBuilder sqlBuilder(
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister,
      QueryToSql queryToSql,
      OrderByToSql orderByToSql);

  @Bean
  protected abstract DaoExceptionTranslator daoExceptionTranslator();

  @Bean
  protected StringIdGenerator stringIdGenerator() {
    return new StringIdGeneratorUuidImpl();
  }

  @Bean
  protected EasyCrudServiceProxyFactory easyCrudServiceProxyFactory(
      ScaffoldedMethodFactory scaffoldedMethodFactory) {
    return new EasyCrudServiceProxyFactoryImpl(scaffoldedMethodFactory);
  }

  @Bean
  protected SqlTypeOverrides sqlTypeOverrides() {
    return new SqlTypeOverridesDefaultImpl();
  }

  @Bean
  protected ScaffoldedMethodFactory scaffoldedMethodFactory() {
    // NOTE: Works just as fine for Postgresql, no MySQL specifics there
    return new ScaffoldedMethodFactoryMySqlImpl();
  }

  @Bean
  protected EasyCrudScaffold easyCrudScaffold(
      DataSource dataSource,
      AutowireCapableBeanFactory beanFactory,
      ScaffoldedMethodFactory scaffoldedMethodFactory) {
    return new EasyCrudScaffoldImpl(dataSource, beanFactory, scaffoldedMethodFactory);
  }

  @Bean
  protected ReferringToFieldsFinder referringToFieldsFinder() {
    return new ReferringToFieldsFinderCachingImpl(new ReferringToFieldsFinderImpl());
  }

  @Bean
  protected NamedParameterJdbcTemplateEx namedParameterJdbcTemplateEx(DataSource dataSource) {
    return new NamedParameterJdbcTemplateEx(dataSource);
  }

  @Bean
  protected QuerySpecificsResolver querySpecificsResolver() {
    return new QuerySpecificsResolverImpl();
  }

  @Bean
  protected FieldsEnlister fieldsEnlister() {
    return new FieldsEnlisterCachingImpl(new FieldsEnlisterImpl());
  }

  @Bean
  protected SelectFactory selectFactory(
      QuerySpecificsResolver querySpecificsResolver,
      SqlBuilder sqlBuilder,
      NamedParameterJdbcTemplateEx jdbc,
      FieldsEnlister fieldsEnlister) {
    return new SelectFactoryImpl(querySpecificsResolver, sqlBuilder, jdbc, fieldsEnlister);
  }

  @Bean
  protected JoinQueryFactory joinQueryFactory(
      ReferringToFieldsFinder referringToFieldsFinder,
      SelectFactory selectFactory,
      QuerySpecificsResolver querySpecificsResolver,
      FieldsEnlister fieldsEnlister) {
    return new JoinQueryFactoryImpl(
        referringToFieldsFinder, selectFactory, querySpecificsResolver, fieldsEnlister);
  }

  @Bean
  protected EasyCrudServiceResolver easyCrudServiceResolver() {
    return new EasyCrudServiceResolverSpringImpl();
  }

  @Bean
  protected RowCloner rowCloner() {
    return new RowClonerReflectionImpl();
  }
}
