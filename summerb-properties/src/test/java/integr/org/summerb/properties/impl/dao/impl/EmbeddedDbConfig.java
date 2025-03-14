package integr.org.summerb.properties.impl.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmbeddedDbConfig {
  @Bean
  PlatformTransactionManager transactionManager(DataSource dataSource) {
    // return new DataSourceTransactionManager(dataSource);
    return new JdbcTransactionManager(dataSource);
  }

  @Bean
  String runDbInitScripts(DataSource dataSource) {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("embedded-db/init_db.sql"));
      return "done";
    } catch (SQLException e) {
      throw new RuntimeException("Failed to run DB init scripts", e);
    }
  }
}
