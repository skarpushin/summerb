package org.summerb.easycrud.dao;

import javax.sql.DataSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.summerb.easycrud.exceptions.DaoExceptionTranslator;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.sql_builder.SqlBuilder;
import org.summerb.easycrud.tools.StringIdGenerator;
import org.summerb.utils.clock.ClockResolver;

public interface EasyCrudDaoInjections<TId, TRow extends HasId<TId>> {
  String getTableName();

  Class<TRow> getRowClass();

  RowMapper<TRow> getRowMapper();

  ParameterSourceBuilder<TRow> getParameterSourceBuilder();

  SqlTypeOverrides getSqlTypeOverrides();

  ConversionService getConversionService();

  StringIdGenerator getStringIdGenerator();

  DaoExceptionTranslator getDaoExceptionTranslator();

  ClockResolver getClockResolver();

  NamedParameterJdbcTemplate getJdbc();

  DataSource getDataSource();

  SqlBuilder getSqlBuilder();
}
