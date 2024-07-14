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

import static org.junit.jupiter.api.Assertions.*;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.properties.PropertiesConfig;
import org.summerb.properties.internal.StringIdAliasService;
import org.summerb.properties.internal.StringIdAliasServiceVisibleForTesting;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, PropertiesConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.BEFORE_CLASS)
public class StringIdAliasServiceTest {
  @Autowired protected StringIdAliasService appAliasService;

  @BeforeEach
  public void beforeEachTest() {
    ((StringIdAliasServiceVisibleForTesting) appAliasService).clearCache();
  }

  @Test
  public void testFindAlias_expectAliasWillBeFound() throws Exception {
    // now let's create values
    Map<String, Long> map = new HashMap<>();
    for (int i = 0; i < 10; i++) {
      String name = "anyA" + i;
      long alias = appAliasService.getAliasFor(name);
      assertTrue(alias > 0);
      map.put(name, alias);
    }

    // Make sure aliases can be fetched from cached state
    for (int i = 0; i < 10; i++) {
      String name = "anyA" + i;
      long alias = appAliasService.getAliasFor(name);
      assertEquals(map.get(name).longValue(), alias);
    }

    // Now let's clear it's cache
    ((StringIdAliasServiceVisibleForTesting) appAliasService).clearCache();
    for (int i = 0; i < 10; i++) {
      String name = "anyA" + i;
      long alias = appAliasService.getAliasFor(name);
      assertEquals(map.get(name).longValue(), alias);
    }
  }
}
