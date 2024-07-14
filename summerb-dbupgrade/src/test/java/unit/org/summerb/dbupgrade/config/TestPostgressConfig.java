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
package unit.org.summerb.dbupgrade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.dbupgrade.api.SqlPackageParser;
import org.summerb.dbupgrade.api.UpgradePackageMetaResolver;
import org.summerb.dbupgrade.impl.UpgradePackageMetaResolverClasspathImpl;
import org.summerb.dbupgrade.impl.VersionTableDbDialect;
import org.summerb.dbupgrade.impl.postgress.SqlPackageParserPostgressImpl;
import org.summerb.dbupgrade.impl.postgress.VersionTableDbDialectPostgressImpl;

@Configuration
public class TestPostgressConfig extends TestConfigBase {
  @Override
  protected UpgradePackageMetaResolver upgradePackageMetaResolver() {
    return new UpgradePackageMetaResolverClasspathImpl(
        resourcePatternResolver, "classpath:/db_postgres/*");
  }

  @Override
  @Bean
  protected SqlPackageParser sqlPackageParser() {
    return new SqlPackageParserPostgressImpl();
  }

  @Bean
  @Override
  protected VersionTableDbDialect versionTableDbDialect() {
    return new VersionTableDbDialectPostgressImpl();
  }
}
