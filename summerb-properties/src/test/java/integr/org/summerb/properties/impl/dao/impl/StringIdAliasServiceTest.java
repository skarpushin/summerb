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
package integr.org.summerb.properties.impl.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.properties.PropertiesConfig;
import org.summerb.properties.impl.StringIdAliasServiceEagerImpl;
import org.summerb.properties.internal.StringIdAliasService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, PropertiesConfig.class})
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.BEFORE_CLASS)
public class StringIdAliasServiceTest {
  @Autowired protected StringIdAliasService appAliasService;

  @Test
  public void testFindAlias_expectALiasWillBeFound() throws Exception {
    Map<String, Long> map = new HashMap<String, Long>();

    for (int i = 0; i < 10; i++) {
      String name = "anyA" + i;
      long alias = appAliasService.getAliasFor(name);
      assertTrue(alias > 0);
      map.put(name, alias);
    }

    // test for smae instance
    for (int i = 0; i < 10; i++) {
      String name = "anyA" + i;
      long alias = appAliasService.getAliasFor(name);
      assertEquals(map.get(name).longValue(), alias);
    }

    // test for new instance
    StringIdAliasServiceEagerImpl newAppAliasService =
        new StringIdAliasServiceEagerImpl(
            ((StringIdAliasServiceEagerImpl) appAliasService).getStringIdAliasDao());
    newAppAliasService.afterPropertiesSet();

    for (long aliasId : map.values()) {
      assertNotNull(newAppAliasService.getNameByAlias(aliasId));
    }
  }
}
