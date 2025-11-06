package integr.org.summerb.easycrud.config;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmbeddedDbConfig {
  @Bean
  PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Profile("!postgres")
  @Bean
  String runDbInitScriptsMysql(DataSource dataSource) {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("embedded-db/mysql_init.sql"));
      return "done";
    } catch (SQLException e) {
      throw new RuntimeException("Failed to run DB init scripts", e);
    }
  }

  @Profile("postgres")
  @Bean
  String runDbInitScriptsPostgres(DataSource dataSource) {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("embedded-db/postgres_init.sql"));
      return "done";
    } catch (SQLException e) {
      throw new RuntimeException("Failed to run DB init scripts", e);
    }
  }
}
