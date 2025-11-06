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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import integr.org.summerb.easycrud.config.EasyCrudConfig;
import integr.org.summerb.easycrud.config.EasyCrudServiceBeansConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.relations.EasyCrudM2mService;
import org.summerb.easycrud.tools.EasyCrudDtoUtils;
import org.summerb.security.api.exceptions.NotAuthorizedException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {EmbeddedDbConfig.class, EasyCrudConfig.class, EasyCrudServiceBeansConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class ManyToManyServiceTest {

  @Autowired
  @Qualifier("postRowServiceBasicAuth")
  private EasyCrudService<Long, PostRow> postRowServiceBasicAuth;

  @Autowired
  @Qualifier("userRowService")
  private EasyCrudService<String, UserRow> userRowService;

  @Autowired
  @Qualifier("m2mService")
  private EasyCrudM2mService<Long, PostRow, String, UserRow> m2mService;

  @Test
  public void testAddReferenceeExpectFoundAfterAddition() {
    PostRow d2i1 = new PostRow();
    d2i1.setTitle("required");
    d2i1.setBody("required");
    d2i1.setAuthorId("someid");
    d2i1 = postRowServiceBasicAuth.create(d2i1);

    UserRow d3i1 = new UserRow();
    d3i1.setName("required");
    d3i1.setAbout("required");
    d3i1 = userRowService.create(d3i1);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());

    List<UserRow> ree = m2mService.findReferenceeByReferencer(d2i1.getId());

    assertNotNull(ree);
    assertEquals(1, ree.size());

    // add another
    UserRow d3i2 = new UserRow();
    d3i2.setName("required");
    d3i2.setAbout("required");
    d3i2 = userRowService.create(d3i2);
    m2mService.addReferencee(d2i1.getId(), d3i2.getId());
    ree = m2mService.findReferenceeByReferencer(d2i1.getId());
    assertNotNull(ree);
    assertEquals(2, ree.size());
  }

  @Test
  public void testAddReferenceeExpectNaeIfNotAllowedToUpdateSrc() {
    PostRow d2i1n = new PostRow();
    d2i1n.setTitle("throwNaeOnUpdate");
    d2i1n.setBody("required");
    d2i1n.setAuthorId("someid");
    PostRow d2i1 = postRowServiceBasicAuth.create(d2i1n);

    UserRow d3i1n = new UserRow();
    d3i1n.setName("required");
    d3i1n.setAbout("required");
    UserRow d3i1 = userRowService.create(d3i1n);

    assertThrows(
        NotAuthorizedException.class, () -> m2mService.addReferencee(d2i1.getId(), d3i1.getId()));
  }

  @Test
  public void testAddReferenceeExpectMultipleFoundAfterAddition() {
    PostRow d2i1 = new PostRow();
    d2i1.setTitle("required");
    d2i1.setBody("required");
    d2i1.setAuthorId("someid");
    d2i1 = postRowServiceBasicAuth.create(d2i1);
    PostRow d2i2 = new PostRow();
    d2i2.setTitle("required");
    d2i2.setBody("required");
    d2i2.setAuthorId("someid");
    d2i2 = postRowServiceBasicAuth.create(d2i2);

    UserRow d3i1 = new UserRow();
    d3i1.setName("required");
    d3i1.setAbout("required");
    d3i1 = userRowService.create(d3i1);
    UserRow d3i2 = new UserRow();
    d3i2.setName("required");
    d3i2.setAbout("required");
    d3i2 = userRowService.create(d3i2);
    UserRow d3i3 = new UserRow();
    d3i3.setName("required");
    d3i3.setAbout("required");
    d3i3 = userRowService.create(d3i3);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());
    m2mService.addReferencee(d2i1.getId(), d3i2.getId());
    m2mService.addReferencee(d2i2.getId(), d3i2.getId());
    m2mService.addReferencee(d2i2.getId(), d3i3.getId());

    Map<Long, List<UserRow>> result =
        m2mService.findReferenceeByReferencers(
            new HashSet<>(Arrays.asList(d2i1.getId(), d2i2.getId())));

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(2, result.get(d2i1.getId()).size());
    assertEquals(2, result.get(d2i2.getId()).size());

    Set<String> i1r = EasyCrudDtoUtils.enumerateIds(result.get(d2i1.getId()));
    assertTrue(i1r.contains(d3i1.getId()));
    assertTrue(i1r.contains(d3i2.getId()));

    Set<String> i2r = EasyCrudDtoUtils.enumerateIds(result.get(d2i2.getId()));
    assertTrue(i2r.contains(d3i2.getId()));
    assertTrue(i2r.contains(d3i3.getId()));
  }

  @Test
  public void testAddReferenceeExpectGracefulResponseIfReferencesNotFound() {
    PostRow d2i1 = new PostRow();
    d2i1.setTitle("required");
    d2i1.setBody("required");
    d2i1.setAuthorId("someid");
    d2i1 = postRowServiceBasicAuth.create(d2i1);
    PostRow d2i2 = new PostRow();
    d2i2.setTitle("required");
    d2i2.setBody("required");
    d2i2.setAuthorId("someid");
    d2i2 = postRowServiceBasicAuth.create(d2i2);

    UserRow d3i1 = new UserRow();
    d3i1.setName("required");
    d3i1.setAbout("required");
    d3i1 = userRowService.create(d3i1);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());

    Map<Long, List<UserRow>> result =
        m2mService.findReferenceeByReferencers(
            new HashSet<>(Arrays.asList(d2i1.getId(), d2i2.getId())));

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(d2i1.getId()).size());

    Set<String> i2r = EasyCrudDtoUtils.enumerateIds(result.get(d2i1.getId()));
    assertTrue(i2r.contains(d3i1.getId()));
  }

  @Test
  public void testAddReferenceeExpectNotFoundAfterDeleted() {
    PostRow d2i1 = new PostRow();
    d2i1.setTitle("required");
    d2i1.setBody("required");
    d2i1.setAuthorId("someid");
    d2i1 = postRowServiceBasicAuth.create(d2i1);

    UserRow d3i1 = new UserRow();
    d3i1.setName("required");
    d3i1.setAbout("required");
    d3i1 = userRowService.create(d3i1);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());
    List<UserRow> ree = m2mService.findReferenceeByReferencer(d2i1.getId());
    assertEquals(1, ree.size());

    m2mService.removeReferencee(d2i1.getId(), d3i1.getId());
    ree = m2mService.findReferenceeByReferencer(d2i1.getId());
    assertEquals(0, ree.size());
  }
}
