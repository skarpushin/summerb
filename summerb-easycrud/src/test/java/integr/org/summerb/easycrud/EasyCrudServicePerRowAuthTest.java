/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package integr.org.summerb.easycrud;

import static org.junit.Assert.assertThrows;

import integr.org.summerb.easycrud.config.EasyCrudConfig;
import integr.org.summerb.easycrud.config.EasyCrudServiceBeansConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.security.api.exceptions.NotAuthorizedException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {EmbeddedDbConfig.class, EasyCrudConfig.class, EasyCrudServiceBeansConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class EasyCrudServicePerRowAuthTest extends GenericCrudServiceTestTemplate {

  @Autowired
  @Qualifier("userRowServiceBasicAuth")
  private EasyCrudService<String, UserRow> userRowService;

  @Autowired
  @Qualifier("postRowServiceBasicAuth")
  private EasyCrudService<Long, PostRow> postRowServiceBasicAuth;

  @Autowired
  @Qualifier("userBasicAuthEb")
  private EasyCrudService<String, UserRow> userRowServiceEb;

  @Override
  public EasyCrudService<String, UserRow> getUserRowService() {
    return userRowService;
  }

  @Override
  public EasyCrudService<Long, PostRow> getPostRowServiceBasicAuth() {
    return postRowServiceBasicAuth;
  }

  @Override
  public EasyCrudService<String, UserRow> getUserRowServiceEb() {
    return userRowServiceEb;
  }

  @Test
  public void testCreateDto2ExpectNae() {
    PostRow dto = new PostRow();
    dto.setTitle("throwNaeOnCreate");
    dto.setBody("link-to-full-download1");
    dto.setLikes(5);
    dto.setDislikes(6);
    dto.setAuthorId("someid");
    assertThrows(NotAuthorizedException.class, () -> postRowServiceBasicAuth.create(dto));
  }
}
