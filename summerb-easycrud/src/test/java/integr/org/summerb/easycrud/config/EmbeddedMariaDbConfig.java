package integr.org.summerb.easycrud.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.summerb.utils.exceptions.ExceptionUtils;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;

/**
 * "Embedded" MariaDB server. It is not actually embedded -- it is more like portabe because {@link
 * MariaDB4jSpringService} spawns real MariaDN server but it helps to make tests self-sufficient and
 * run them without tie to local env
 *
 * @author Sergey Karpushin
 */
public class EmbeddedMariaDbConfig {
  public static final String DB_NAME = "summerb_tests";

  protected final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * IMPORTANT: Sometimes if test fails abruptly, DB server will not exit -- you'll have to kill it
   * manually
   */
  @Bean
  public MariaDB4jSpringService inMemoryMariaDb() throws ManagedProcessException {
    try {
      MariaDB4jSpringService ret = new MariaDB4jSpringService();
      ret.setDefaultPort(-1); // TZD: next time you want to change it -- externalize into properties
      ret.start();
      ret.getDB().createDB(DB_NAME);
      invokeInitScripts(ret.getDB(), DB_NAME);
      return ret;
    } catch (Exception e) {
      ManagedProcessException mpe =
          ExceptionUtils.findExceptionOfType(e, ManagedProcessException.class);
      if (mpe != null && mpe.getMessage().contains("libncurses")) {
        log.error(
            "Your system is missing library 'libncurses'.\r\n"
                + "You can create a link if you have compatible version or you'll need to install"
                + " it.\r\n"
                + "Step 1: Do `ls -l /lib/ | grep libncurses` and see what version you have on your"
                + " local systemStep 2: Link existing version to libncurses.so.5, i.e. `sudo link"
                + " /lib/libncursesw.so /lib/libncurses.so.5`");
      }
      throw e;
    }
  }

  /**
   * subclasses can add their own additional scripts
   *
   * @throws ManagedProcessException
   */
  protected void invokeInitScripts(DB db, String dbName) throws ManagedProcessException {
    db.source("embedded-db/mysql_init.sql", DB_NAME);
  }

  @Bean
  public DataSource dataSource(
      MariaDB4jSpringService inMemoryMariaDb,
      @Value("${spring.datasource.url}") String url,
      @Value("${spring.datasource.username}") String username,
      @Value("${spring.datasource.password}") String password) {
    return DataSourceBuilder.create()
        .username(username)
        .password(password)
        .url(inMemoryMariaDb.getConfiguration().getURL(DB_NAME))
        .build();
  }

  @Bean
  TransactionManager transactionManager(DataSource dataSource) {
    return new JdbcTransactionManager(dataSource);
  }
}
