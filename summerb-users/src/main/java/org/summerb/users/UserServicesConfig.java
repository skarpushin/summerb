package org.summerb.users;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.UserService;
import org.summerb.users.impl.PermissionServiceImpl;
import org.summerb.users.impl.UserServiceCachedImpl;
import org.summerb.users.impl.UserServiceImpl;
import org.summerb.users.impl.dao.PermissionDao;
import org.summerb.users.impl.dao.UserDao;
import org.summerb.users.impl.dao.impl.PermissionDaoImpl;
import org.summerb.users.impl.dao.impl.UserDaoImpl;
import org.summerb.validation.ValidationContextFactory;

import com.google.common.eventbus.EventBus;

@Configuration
public class UserServicesConfig {

  @Bean
  UserDao userDao(DataSource dataSource) {
    return new UserDaoImpl(dataSource, "users");
  }

  @Bean
  UserService userServiceNoncached(
      UserDao userDao, EventBus eventBus, ValidationContextFactory validationContextFactory) {
    return new UserServiceImpl(userDao, eventBus, validationContextFactory);
  }

  @Bean
  UserService userService(
      DataSource dataSource,
      EventBus eventBus,
      UserDao userDao,
      @Qualifier("userServiceNoncached") UserService userServiceNoncached) {
    return new UserServiceCachedImpl(userServiceNoncached, eventBus);
  }

  @Bean
  PermissionDao permissionDao(DataSource dataSource) {
    return new PermissionDaoImpl(dataSource, "users_permissions");
  }

  @Bean
  PermissionService permissionService(PermissionDao permissionDao) {
    return new PermissionServiceImpl(permissionDao);
  }
}
