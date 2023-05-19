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
  SimplePropertyService appProps(PropertyService propertyService, Optional<EventBus> eventBus) {
    SimplePropertyServiceImpl ret =
        new SimplePropertyServiceImpl(
            propertyService, SecurityConstants.DOMAIN, SecurityConstants.DOMAIN);
    if (eventBus.isPresent()) {
      ret.setEventBus(eventBus.get());
    }
    return ret;
  }

  // ================= Under-th-hood impl
  @Bean
  StringIdAliasDao appAliasDao() {
    return new StringIdAliasDaoImpl(dataSource, "props_alias_app");
  }

  @Bean
  StringIdAliasDao domainAliasDao() {
    return new StringIdAliasDaoImpl(dataSource, "props_alias_domain");
  }

  @Bean
  StringIdAliasDao propertyNameAliasDao() {
    return new StringIdAliasDaoImpl(dataSource, "props_alias_name");
  }

  @Bean
  StringIdAliasService appAliasService() {
    return new StringIdAliasServiceEagerImpl(appAliasDao());
  }

  @Bean
  StringIdAliasService domainAliasService() {
    return new StringIdAliasServiceEagerImpl(domainAliasDao());
  }

  @Bean
  StringIdAliasService propertyNameAliasService() {
    return new StringIdAliasServiceEagerImpl(propertyNameAliasDao());
  }

  @Bean
  PropertyDao propertyDao() {
    return new PropertyDaoImpl(dataSource, "props_values");
  }

  @Bean
  PropertyService propertyService(PropertyDao propertyDao) {
    return new PropertyServiceImpl(
        propertyDao, domainAliasService(), appAliasService(), propertyNameAliasService());
  }
}
