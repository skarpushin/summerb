package org.summerb.properties;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.properties.api.PropertyService;
import org.summerb.properties.api.SimplePropertyService;
import org.summerb.properties.impl.PropertyServiceImpl;
import org.summerb.properties.impl.SimplePropertyServiceImpl;
import org.summerb.properties.impl.StringIdAliasServiceEagerImpl;
import org.summerb.properties.impl.dao.PropertyDao;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.properties.impl.dao.impl.PropertyDaoImpl;
import org.summerb.properties.impl.dao.impl.StringIdAliasDaoImpl;
import org.summerb.properties.internal.StringIdAliasService;
import org.summerb.spring.security.SecurityConstants;

import com.google.common.eventbus.EventBus;

/**
 * Template configuration for Properties Service beans
 *
 * @author Sergey Karpushin
 */
@Configuration
public class PropertiesConfig {

  public @Autowired DataSource dataSource;

  @Bean
  public SimplePropertyService appProps(Optional<EventBus> eventBus) {
    SimplePropertyServiceImpl ret = new SimplePropertyServiceImpl();
    ret.setAppName(SecurityConstants.DOMAIN);
    ret.setDomainName(SecurityConstants.DOMAIN);
    if (eventBus.isPresent()) {
      ret.setEventBus(eventBus.get());
    }
    ret.setPropertyService(propertyService());
    return ret;
  }

  // ================= Under-th-hood impl
  @Bean
  public StringIdAliasDao appAliasDao() {
    return buildStringIdAliasDao(dataSource, "props_alias_app");
  }

  @Bean
  public StringIdAliasDao domainAliasDao() {
    return buildStringIdAliasDao(dataSource, "props_alias_domain");
  }

  @Bean
  public StringIdAliasDao propertyNameAliasDao() {
    return buildStringIdAliasDao(dataSource, "props_alias_name");
  }

  private StringIdAliasDao buildStringIdAliasDao(DataSource dataSource, String tableName) {
    StringIdAliasDaoImpl ret = new StringIdAliasDaoImpl();
    ret.setDataSource(dataSource);
    ret.setTableName(tableName);
    return ret;
  }

  @Bean
  public StringIdAliasService appAliasService() {
    StringIdAliasServiceEagerImpl ret = new StringIdAliasServiceEagerImpl();
    ret.setStringIdAliasDao(appAliasDao());
    return ret;
  }

  @Bean
  public StringIdAliasService domainAliasService() {
    StringIdAliasServiceEagerImpl ret = new StringIdAliasServiceEagerImpl();
    ret.setStringIdAliasDao(domainAliasDao());
    return ret;
  }

  @Bean
  public StringIdAliasService propertyNameAliasService() {
    StringIdAliasServiceEagerImpl ret = new StringIdAliasServiceEagerImpl();
    ret.setStringIdAliasDao(propertyNameAliasDao());
    return ret;
  }

  @Bean
  public PropertyDao propertyDao() {
    PropertyDaoImpl ret = new PropertyDaoImpl();
    ret.setDataSource(dataSource);
    return ret;
  }

  @Bean
  public PropertyService propertyService() {
    PropertyServiceImpl ret = new PropertyServiceImpl();
    ret.setPropertyDao(propertyDao());
    ret.setAppNameAlias(appAliasService());
    ret.setDomainNameAlias(domainAliasService());
    ret.setPropertyNameAlias(propertyNameAliasService());
    return ret;
  }
}
