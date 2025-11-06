package integr.org.summerb.easycrud.config;

import com.google.common.eventbus.EventBus;
import integr.org.summerb.easycrud.dtos.CommentRow;
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import integr.org.summerb.easycrud.testbeans.PostPerRowAuthImpl;
import integr.org.summerb.easycrud.testbeans.PostRowService;
import integr.org.summerb.easycrud.testbeans.PostRowServiceImpl;
import integr.org.summerb.easycrud.testbeans.UserRowService;
import integr.org.summerb.easycrud.utils.CurrentUserResolverTestImpl;
import integr.org.summerb.easycrud.utils.EasyCrudPerRowAuthStrategyTestImpl;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.auth.legacy.EasyCrudAuthorizationPerRowStrategy;
import org.summerb.easycrud.dao.EasyCrudDao;
import org.summerb.easycrud.dao.EasyCrudDaoSqlImpl;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.relations.EasyCrudM2MDaoSqlImpl;
import org.summerb.easycrud.relations.EasyCrudM2mServiceImpl;
import org.summerb.easycrud.relations.M2mAuthorizationWireTapImpl;
import org.summerb.easycrud.scaffold.EasyCrudScaffold;
import org.summerb.easycrud.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.security.api.CurrentUserUuidResolver;

@Configuration
public class EasyCrudServiceBeansConfig {
  @Bean
  EventBus eventBus() {
    return new EventBus();
  }

  @Bean
  CurrentUserUuidResolver currentUserResolver() {
    return new CurrentUserResolverTestImpl();
  }

  @Bean
  EasyCrudWireTapEventBusImpl<?> easyCrudWireTapEventBus(EventBus eventBus) {
    return new EasyCrudWireTapEventBusImpl<>(eventBus);
  }

  @Bean
  EasyCrudPerRowAuthStrategyTestImpl<UserRow> userAuth() {
    return new EasyCrudPerRowAuthStrategyTestImpl<>();
  }

  @Bean
  EasyCrudDao<String, UserRow> userDao(DataSource dataSource) {
    return new EasyCrudDaoSqlImpl<>(dataSource, "users_table", UserRow.class);
  }

  @Bean
  EasyCrudService<String, UserRow> userRowService(
      @Qualifier("userDao") EasyCrudDao<String, UserRow> userDao) {
    return new EasyCrudServiceImpl<>(userDao, UserRow.class);
  }

  @Bean
  EasyCrudService<String, UserRow> userRowServiceEb(
      @Qualifier("userDao") EasyCrudDao<String, UserRow> userDao, EventBus eventBus) {
    var ret = new EasyCrudServiceImpl<>(userDao, UserRow.class);
    ret.setWireTap(new EasyCrudWireTapEventBusImpl<>(eventBus));
    return ret;
  }

  @Bean
  EasyCrudService<String, UserRow> userBasicAuthEb(
      @Qualifier("userDao") EasyCrudDao<String, UserRow> userDao,
      EventBus eventBus,
      @Qualifier("userAuth") EasyCrudPerRowAuthStrategyTestImpl<UserRow> userAuth) {
    var ret = new EasyCrudServiceImpl<>(userDao, UserRow.class);
    ret.setWireTap(
        new EasyCrudWireTapDelegatingImpl<>(
            Arrays.asList(userAuth, new EasyCrudWireTapEventBusImpl<>(eventBus))));
    return ret;
  }

  @Bean
  EasyCrudService<String, UserRow> userRowServiceBasicAuth(
      @Qualifier("userDao") EasyCrudDao<String, UserRow> userDao,
      @Qualifier("userAuth") EasyCrudPerRowAuthStrategyTestImpl<UserRow> userAuth) {
    var ret = new EasyCrudServiceImpl<>(userDao, UserRow.class);
    ret.setWireTap(userAuth);
    return ret;
  }

  @Bean
  EasyCrudDao<Long, PostRow> postRowDao(DataSource dataSource) {
    return new EasyCrudDaoSqlImpl<>(dataSource, "posts", PostRow.class);
  }

  @Bean
  EasyCrudService<Long, PostRow> postRowService(
      @Qualifier("postRowDao") EasyCrudDao<Long, PostRow> postRowDao) {
    return new EasyCrudServiceImpl<>(postRowDao, PostRow.class);
  }

  @Bean
  EasyCrudAuthorizationPerRowStrategy<PostRow> postRowPerRowAuth() {
    return new PostPerRowAuthImpl();
  }

  @Bean
  EasyCrudService<Long, PostRow> postRowServiceBasicAuth(
      @Qualifier("postRowDao") EasyCrudDao<Long, PostRow> postRowDao,
      @Qualifier("postRowPerRowAuth") EasyCrudAuthorizationPerRowStrategy<PostRow> auth) {
    var ret = new EasyCrudServiceImpl<>(postRowDao, PostRow.class);
    ret.setWireTap(auth);
    return ret;
  }

  @Bean
  EasyCrudDaoSqlImpl<Long, CommentRow> commentRowDao(DataSource dataSource) {
    return new EasyCrudDaoSqlImpl<>(dataSource, "comments", CommentRow.class);
  }

  @Bean
  EasyCrudService<Long, CommentRow> commentRowService(EasyCrudDaoSqlImpl<Long, CommentRow> dao) {
    return new EasyCrudServiceImpl<>(dao, CommentRow.class);
  }

  @Bean
  EasyCrudM2MDaoSqlImpl<Long, PostRow, String, UserRow> m2mDao(DataSource dataSource) {
    return new EasyCrudM2MDaoSqlImpl<>(dataSource, "forms_mtom");
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Bean
  EasyCrudM2mServiceImpl<Long, PostRow, String, UserRow> m2mService(
      EasyCrudM2MDaoSqlImpl<Long, PostRow, String, UserRow> dao,
      @Qualifier("postRowServiceBasicAuth") EasyCrudService<Long, PostRow> serviceA,
      @Qualifier("userRowService") EasyCrudService<String, UserRow> serviceB,
      @Qualifier("postRowPerRowAuth") EasyCrudAuthorizationPerRowStrategy<PostRow> serviceAAuth) {

    var ret = new EasyCrudM2mServiceImpl<>(dao, serviceA, serviceB);
    M2mAuthorizationWireTapImpl wireTap = new M2mAuthorizationWireTapImpl(serviceA, serviceAAuth);
    wireTap.afterPropertiesSet();

    ret.setWireTap(wireTap);
    return ret;
  }

  @Bean
  UserRowService userRowServiceScaffolded(EasyCrudScaffold easyCrudScaffold) {
    return easyCrudScaffold.fromService(
        UserRowService.class, UserRow.class.getCanonicalName(), "users_table");
  }

  @Bean
  EasyCrudService<String, UserRow> userRowServiceEbScaffolded(
      EasyCrudScaffold easyCrudScaffold, EasyCrudWireTapEventBusImpl<?> easyCrudWireTapEventBus) {
    return easyCrudScaffold.fromRowClass(
        UserRow.class, UserRow.class.getCanonicalName(), "users_table", easyCrudWireTapEventBus);
  }

  @Bean
  EasyCrudService<Long, PostRow> postRowServiceScaffolded(EasyCrudScaffold easyCrudScaffold) {
    return easyCrudScaffold.fromRowClass(PostRow.class, PostRow.class.getCanonicalName(), "posts");
  }

  @Bean
  PostRowService postRowServiceCustom(EasyCrudScaffold easyCrudScaffold) {
    return easyCrudScaffold.fromService(PostRowService.class, new PostRowServiceImpl(), "posts");
  }
}
