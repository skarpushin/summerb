package org.summerb.easycrud.impl.dao.mysql;

import javax.sql.DataSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.summerb.easycrud.api.DaoExceptionTranslator;
import org.summerb.easycrud.api.ParameterSourceBuilder;
import org.summerb.easycrud.api.QueryToSql;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.dao.SqlTypeOverrides;
import org.summerb.utils.clock.NowResolver;

public interface EasyCrudDaoInjections<TId, TRow extends HasId<TId>> {
  String getTableName();

  Class<TRow> getRowClass();

  RowMapper<TRow> getRowMapper();

  ParameterSourceBuilder<TRow> getParameterSourceBuilder();

  SqlTypeOverrides getSqlTypeOverrides();

  QueryToSql getQueryToSql();

  ConversionService getConversionService();

  StringIdGenerator getStringIdGenerator();

  DaoExceptionTranslator getDaoExceptionTranslator();

  NowResolver getNowResolver();

  NamedParameterJdbcTemplate getJdbc();

  DataSource getDataSource();
}
