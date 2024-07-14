package org.summerb.properties;

import com.google.common.eventbus.EventBus;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.properties.api.PropertyService;
import org.summerb.properties.api.SimplePropertyService;
import org.summerb.properties.impl.PropertyServiceImpl;
import org.summerb.properties.impl.SimplePropertyServiceImpl;
import org.summerb.properties.impl.StringIdAliasServiceCachedImpl;
import org.summerb.properties.impl.dao.PropertyDao;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.properties.impl.dao.impl.PropertyDaoImpl;
import org.summerb.properties.impl.dao.impl.StringIdAliasDaoImpl;
import org.summerb.properties.internal.StringIdAliasService;
import org.summerb.spring.security.SecurityConstants;

/**
 * Template configuration for Properties Service beans
 *
 * <p>NOTE: This class by purpose does not have {@link Configuration} to avoid automatic
 * instantiation by Spring
 *
 * @author Sergey Karpushin
 */
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
  protected StringIdAliasDao appAliasDao() {
    return new StringIdAliasDaoImpl(dataSource, "props_alias_app");
  }

  @Bean
  protected StringIdAliasDao domainAliasDao() {
    return new StringIdAliasDaoImpl(dataSource, "props_alias_domain");
  }

  @Bean
  protected StringIdAliasDao propertyNameAliasDao() {
    return new StringIdAliasDaoImpl(dataSource, "props_alias_name");
  }

  @Bean
  StringIdAliasService appAliasService(@Qualifier("appAliasDao") StringIdAliasDao appAliasDao) {
    return new StringIdAliasServiceCachedImpl(appAliasDao);
  }

  @Bean
  StringIdAliasService domainAliasService(
      @Qualifier("domainAliasDao") StringIdAliasDao domainAliasDao) {
    return new StringIdAliasServiceCachedImpl(domainAliasDao);
  }

  @Bean
  StringIdAliasService propertyNameAliasService(
      @Qualifier("propertyNameAliasDao") StringIdAliasDao propertyNameAliasDao) {
    return new StringIdAliasServiceCachedImpl(propertyNameAliasDao);
  }

  @Bean
  PropertyDao propertyDao() {
    return new PropertyDaoImpl(dataSource, "props_values");
  }

  @Bean
  PropertyService propertyService(
      PropertyDao propertyDao,
      @Qualifier("domainAliasService") StringIdAliasService domainAliasService,
      @Qualifier("appAliasService") StringIdAliasService appAliasService,
      @Qualifier("propertyNameAliasService") StringIdAliasService propertyNameAliasService) {
    return new PropertyServiceImpl(
        propertyDao, domainAliasService, appAliasService, propertyNameAliasService);
  }
}
