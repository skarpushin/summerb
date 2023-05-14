/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.dbupgrade;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;
import org.summerb.dbupgrade.impl.UpgradePackageMetaResolverClasspathImpl;

@Configuration
@PropertySource("test-dbconnection.properties")
public class TestMySqlConfig extends TestConfigBase {
  @Override
  protected UpgradePackageMetaResolver upgradePackageMetaResolver() throws Exception {
    return new UpgradePackageMetaResolverClasspathImpl(
        resourcePatternResolver, "classpath:/db_mysql/*");
  }

  @Bean
  DataSource dataSource(
      @Value("${mysql.driverClassName}") String driver,
      @Value("${mysql.databaseurl}") String url,
      @Value("${mysql.username}") String username,
      @Value("${mysql.password}") String password)
      throws Exception {
    return new SimpleDriverDataSource(
        (Driver) Class.forName(driver).newInstance(), url, username, password);
  }
}
