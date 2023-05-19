package integr.org.summerb.users.impl.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.summerb.users.api.AuthTokenService;
import org.summerb.users.api.PasswordService;
import org.summerb.users.api.PermissionService;
import org.summerb.users.api.UserService;
import org.summerb.users.impl.AuthTokenServiceImpl;
import org.summerb.users.impl.PasswordServiceImpl;
import org.summerb.users.impl.PermissionServiceImpl;
import org.summerb.users.impl.UserServiceCachedImpl;
import org.summerb.users.impl.UserServiceImpl;
import org.summerb.users.impl.dao.AuthTokenDao;
import org.summerb.users.impl.dao.PasswordDao;
import org.summerb.users.impl.dao.PermissionDao;
import org.summerb.users.impl.dao.UserDao;
import org.summerb.users.impl.dao.impl.AuthTokenDaoImpl;
import org.summerb.users.impl.dao.impl.PasswordDaoImpl;
import org.summerb.users.impl.dao.impl.PermissionDaoImpl;
import org.summerb.users.impl.dao.impl.UserDaoImpl;
import org.summerb.validation.ValidationContextConfig;
import org.summerb.validation.ValidationContextFactory;

import com.google.common.eventbus.EventBus;

@SuppressWarnings("deprecation")
@Configuration
@Import(ValidationContextConfig.class)
public class UserServicesTestConfig {

  @Bean
  EventBus eventBus() {
    return new EventBus();
  }

  @Bean
  public UserDao userDao(DataSource dataSource) {
    return new UserDaoImpl(dataSource, "users");
  }

  @Bean
  protected UserService userServiceNoncached(
      UserDao userDao, EventBus eventBus, ValidationContextFactory validationContextFactory) {
    return new UserServiceImpl(userDao, eventBus, validationContextFactory);
  }

  @Bean
  public UserService userService(
      DataSource dataSource,
      EventBus eventBus,
      UserDao userDao,
      @Qualifier("userServiceNoncached") UserService userServiceNoncached) {
    return new UserServiceCachedImpl(userServiceNoncached, eventBus);
  }

  @Bean
  public PasswordDao passwordDao(DataSource dataSource) {
    return new PasswordDaoImpl(dataSource, "users_passwords");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new StandardPasswordEncoder("test");
  }

  @Bean
  public PasswordService passwordService(
      PasswordDao passwordDao, PasswordEncoder passwordEncoder, UserService userService) {
    return new PasswordServiceImpl(passwordDao, passwordEncoder, userService);
  }

  @Bean
  public AuthTokenDao authTokenDao(DataSource dataSource) {
    return new AuthTokenDaoImpl(dataSource, "users_auth_tokens");
  }

  @Bean
  public AuthTokenService authTokenService(
      AuthTokenDao authTokenDao, UserService userService, PasswordService passwordService) {
    return new AuthTokenServiceImpl(authTokenDao, userService, passwordService);
  }

  @Bean
  public PermissionDao permissionDao(DataSource dataSource) {
    return new PermissionDaoImpl(dataSource, "users_permissions");
  }

  @Bean
  public PermissionService permissionService(PermissionDao permissionDao) {
    return new PermissionServiceImpl(permissionDao);
  }
}
