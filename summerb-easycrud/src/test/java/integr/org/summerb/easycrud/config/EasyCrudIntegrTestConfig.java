package integr.org.summerb.easycrud.config;

import com.google.common.eventbus.EventBus;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestDto2;
import integr.org.summerb.easycrud.dtos.TestDto3;
import integr.org.summerb.easycrud.testbeans.TestDto1Service;
import integr.org.summerb.easycrud.testbeans.TestDto2PerRowAuthImpl;
import integr.org.summerb.easycrud.testbeans.TestDto2Service;
import integr.org.summerb.easycrud.testbeans.TestDto2ServiceImpl;
import integr.org.summerb.easycrud.utils.CurrentUserResolverTestImpl;
import integr.org.summerb.easycrud.utils.EasyCrudPerRowAuthStrategyTestImpl;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.summerb.easycrud.api.DaoExceptionTranslator;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.QueryToSql;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.impl.EasyCrudServiceResolverSpringImpl;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerRowStrategy;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoSqlImpl;
import org.summerb.easycrud.impl.dao.postgres.DaoExceptionTranslatorPostgresImpl;
import org.summerb.easycrud.impl.dao.postgres.QueryToSqlPostgresImpl;
import org.summerb.easycrud.impl.query.QueryFactoryImpl;
import org.summerb.easycrud.impl.relations.EasyCrudM2MDaoSqlImpl;
import org.summerb.easycrud.impl.relations.EasyCrudM2mServiceImpl;
import org.summerb.easycrud.impl.relations.M2mAuthorizationWireTapImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.easycrud.scaffold.api.EasyCrudScaffold;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;
import org.summerb.easycrud.scaffold.impl.EasyCrudScaffoldImpl;
import org.summerb.easycrud.scaffold.impl.ScaffoldedMethodFactoryMySqlImpl;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameResolverFactory;
import org.summerb.methodCapturers.PropertyNameResolverFactoryImpl;
import org.summerb.security.api.CurrentUserUuidResolver;

@Configuration
public class EasyCrudIntegrTestConfig {

  @Bean
  @Profile("postgres")
  QueryToSql queryToSql() {
    return new QueryToSqlPostgresImpl();
  }

  @Bean
  @Profile("postgres")
  DaoExceptionTranslator daoExceptionTranslator() {
    return new DaoExceptionTranslatorPostgresImpl();
  }

  @Bean
  EventBus eventBus() {
    return new EventBus();
  }

  @Bean
  EasyCrudWireTapEventBusImpl<?> easyCrudWireTapEventBus(EventBus eventBus) {
    return new EasyCrudWireTapEventBusImpl<>(eventBus);
  }

  @Bean
  PropertyNameResolverFactory propertyNameResolverFactory() {
    PropertyNameResolverFactoryImpl ret =
        new PropertyNameResolverFactoryImpl(new MethodCapturerProxyClassFactoryImpl());
    Query.FACTORY = new QueryFactoryImpl(ret);
    return ret;
  }

  @Bean
  ScaffoldedMethodFactory scaffoldedMethodFactory() {
    return new ScaffoldedMethodFactoryMySqlImpl();
  }

  @Bean
  EasyCrudScaffold easyCrudScaffold(
      DataSource dataSource,
      AutowireCapableBeanFactory beanFactory,
      ScaffoldedMethodFactory scaffoldedMethodFactory) {
    return new EasyCrudScaffoldImpl(dataSource, beanFactory, scaffoldedMethodFactory);
  }

  @Bean
  CurrentUserUuidResolver currentUserResolver() {
    return new CurrentUserResolverTestImpl();
  }

  @Bean
  EasyCrudServiceResolver easyCrudServiceResolver() {
    return new EasyCrudServiceResolverSpringImpl();
  }

  @Bean
  EasyCrudPerRowAuthStrategyTestImpl<TestDto1> testDto1Auth() {
    return new EasyCrudPerRowAuthStrategyTestImpl<>();
  }

  @Bean
  EasyCrudDao<String, TestDto1> testDto1Dao(DataSource dataSource) {
    return new EasyCrudDaoSqlImpl<>(dataSource, "forms_test_1", TestDto1.class);
  }

  @Bean
  EasyCrudService<String, TestDto1> testDto1Service(
      @Qualifier("testDto1Dao") EasyCrudDao<String, TestDto1> testDto1Dao) {
    return new EasyCrudServiceImpl<>(testDto1Dao, TestDto1.class);
  }

  @Bean
  EasyCrudService<String, TestDto1> testDto1ServiceEb(
      @Qualifier("testDto1Dao") EasyCrudDao<String, TestDto1> testDto1Dao, EventBus eventBus) {
    var ret = new EasyCrudServiceImpl<>(testDto1Dao, TestDto1.class);
    ret.setWireTap(new EasyCrudWireTapEventBusImpl<>(eventBus));
    return ret;
  }

  @Bean
  EasyCrudService<String, TestDto1> testDto1ServiceBasicAuthEb(
      @Qualifier("testDto1Dao") EasyCrudDao<String, TestDto1> testDto1Dao,
      EventBus eventBus,
      @Qualifier("testDto1Auth") EasyCrudPerRowAuthStrategyTestImpl<TestDto1> testDto1Auth) {
    var ret = new EasyCrudServiceImpl<>(testDto1Dao, TestDto1.class);
    ret.setWireTap(
        new EasyCrudWireTapDelegatingImpl<>(
            Arrays.asList(testDto1Auth, new EasyCrudWireTapEventBusImpl<>(eventBus))));
    return ret;
  }

  @Bean
  EasyCrudService<String, TestDto1> testDto1ServiceBasicAuth(
      @Qualifier("testDto1Dao") EasyCrudDao<String, TestDto1> testDto1Dao,
      @Qualifier("testDto1Auth") EasyCrudPerRowAuthStrategyTestImpl<TestDto1> testDto1Auth) {
    var ret = new EasyCrudServiceImpl<>(testDto1Dao, TestDto1.class);
    ret.setWireTap(testDto1Auth);
    return ret;
  }

  @Bean
  EasyCrudDao<Long, TestDto2> testDto2Dao(DataSource dataSource) {
    return new EasyCrudDaoSqlImpl<>(dataSource, "forms_test_2", TestDto2.class);
  }

  @Bean
  EasyCrudService<Long, TestDto2> testDto2Service(
      @Qualifier("testDto2Dao") EasyCrudDao<Long, TestDto2> testDto2Dao) {
    return new EasyCrudServiceImpl<>(testDto2Dao, TestDto2.class);
  }

  @Bean
  EasyCrudAuthorizationPerRowStrategy<TestDto2> testDto2PerRowAuth() {
    return new TestDto2PerRowAuthImpl();
  }

  @Bean
  EasyCrudService<Long, TestDto2> testDto2ServiceBasicAuth(
      @Qualifier("testDto2Dao") EasyCrudDao<Long, TestDto2> testDto2Dao,
      @Qualifier("testDto2PerRowAuth") EasyCrudAuthorizationPerRowStrategy<TestDto2> auth) {
    var ret = new EasyCrudServiceImpl<>(testDto2Dao, TestDto2.class);
    ret.setWireTap(auth);
    return ret;
  }

  @Bean
  EasyCrudDaoSqlImpl<String, TestDto3> daoTestDto3(DataSource dataSource) {
    return new EasyCrudDaoSqlImpl<>(dataSource, "forms_test_3", TestDto3.class);
  }

  @Bean
  EasyCrudService<String, TestDto3> testDto3Service(EasyCrudDaoSqlImpl<String, TestDto3> dao) {
    return new EasyCrudServiceImpl<>(dao, TestDto3.class);
  }

  @Bean
  EasyCrudM2MDaoSqlImpl<Long, TestDto2, String, TestDto1> m2mDao(DataSource dataSource) {
    return new EasyCrudM2MDaoSqlImpl<>(dataSource, "forms_mtom");
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Bean
  EasyCrudM2mServiceImpl<Long, TestDto2, String, TestDto1> m2mService(
      EasyCrudM2MDaoSqlImpl<Long, TestDto2, String, TestDto1> dao,
      @Qualifier("testDto2ServiceBasicAuth") EasyCrudService<Long, TestDto2> serviceA,
      @Qualifier("testDto1Service") EasyCrudService<String, TestDto1> serviceB,
      @Qualifier("testDto2PerRowAuth") EasyCrudAuthorizationPerRowStrategy<TestDto2> serviceAAuth) {

    var ret = new EasyCrudM2mServiceImpl<>(dao, serviceA, serviceB);
    M2mAuthorizationWireTapImpl wireTap = new M2mAuthorizationWireTapImpl(serviceA, serviceAAuth);
    wireTap.afterPropertiesSet();

    ret.setWireTap(wireTap);
    return ret;
  }

  @Bean
  TestDto1Service testDto1ServiceScaffolded(EasyCrudScaffold easyCrudScaffold) {
    return easyCrudScaffold.fromService(
        TestDto1Service.class, TestDto1.class.getCanonicalName(), "forms_test_1");
  }

  @Bean
  EasyCrudService<String, TestDto1> testDto1ServiceEbScaffolded(
      EasyCrudScaffold easyCrudScaffold, EasyCrudWireTapEventBusImpl<?> easyCrudWireTapEventBus) {
    return easyCrudScaffold.fromRowClass(
        TestDto1.class, TestDto1.class.getCanonicalName(), "forms_test_1", easyCrudWireTapEventBus);
  }

  @Bean
  EasyCrudService<Long, TestDto2> testDto2ServiceScaffolded(EasyCrudScaffold easyCrudScaffold) {
    return easyCrudScaffold.fromRowClass(
        TestDto2.class, TestDto2.class.getCanonicalName(), "forms_test_2");
  }

  @Bean
  TestDto2Service testDto2ServiceCustom(EasyCrudScaffold easyCrudScaffold) {
    return easyCrudScaffold.fromService(
        TestDto2Service.class, new TestDto2ServiceImpl(), "forms_test_2");
  }
}
