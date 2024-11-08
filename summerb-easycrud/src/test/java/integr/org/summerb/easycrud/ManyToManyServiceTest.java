/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
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

import integr.org.summerb.easycrud.config.EasyCrudIntegrTestConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestDto2;
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
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.relations.EasyCrudM2mService;
import org.summerb.easycrud.api.row.tools.EasyCrudDtoUtils;
import org.summerb.security.api.exceptions.NotAuthorizedException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, EasyCrudIntegrTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class ManyToManyServiceTest {
  //  @BeforeAll
  //  static void setup(@Autowired DataSource dataSource) throws SQLException {
  //    try (Connection conn = dataSource.getConnection()) {
  //      // you'll have to make sure conn.autoCommit = true (default for e.g. H2)
  //      // e.g. url=jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1;MODE=MySQL
  //      ScriptUtils.executeSqlScript(conn, new ClassPathResource("mysql_init.sql"));
  //    }
  //  }

  @Autowired
  @Qualifier("testDto2ServiceBasicAuth")
  private EasyCrudService<Long, TestDto2> testDto2ServiceBasicAuth;

  @Autowired
  @Qualifier("testDto1Service")
  private EasyCrudService<String, TestDto1> testDto1Service;

  @Autowired
  @Qualifier("m2mService")
  private EasyCrudM2mService<Long, TestDto2, String, TestDto1> m2mService;

  @Test
  public void testAddReferenceeExpectFoundAfterAddition() {
    TestDto2 d2i1 = new TestDto2();
    d2i1.setEnv("required");
    d2i1.setLinkToFullDownload("required");
    d2i1 = testDto2ServiceBasicAuth.create(d2i1);

    TestDto1 d3i1 = new TestDto1();
    d3i1.setEnv("required");
    d3i1.setLinkToFullDownload("required");
    d3i1 = testDto1Service.create(d3i1);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());

    List<TestDto1> ree = m2mService.findReferenceeByReferencer(d2i1.getId());

    assertNotNull(ree);
    assertEquals(1, ree.size());

    // add another
    TestDto1 d3i2 = new TestDto1();
    d3i2.setEnv("required");
    d3i2.setLinkToFullDownload("required");
    d3i2 = testDto1Service.create(d3i2);
    m2mService.addReferencee(d2i1.getId(), d3i2.getId());
    ree = m2mService.findReferenceeByReferencer(d2i1.getId());
    assertNotNull(ree);
    assertEquals(2, ree.size());
  }

  @Test
  public void testAddReferenceeExpectNaeIfNotAllowedToUpdateSrc() {
    TestDto2 d2i1n = new TestDto2();
    d2i1n.setEnv("throwNaeOnUpdate");
    d2i1n.setLinkToFullDownload("required");
    TestDto2 d2i1 = testDto2ServiceBasicAuth.create(d2i1n);

    TestDto1 d3i1n = new TestDto1();
    d3i1n.setEnv("required");
    d3i1n.setLinkToFullDownload("required");
    TestDto1 d3i1 = testDto1Service.create(d3i1n);

    assertThrows(
        NotAuthorizedException.class, () -> m2mService.addReferencee(d2i1.getId(), d3i1.getId()));
  }

  @Test
  public void testAddReferenceeExpectMultipleFoundAfterAddition() {
    TestDto2 d2i1 = new TestDto2();
    d2i1.setEnv("required");
    d2i1.setLinkToFullDownload("required");
    d2i1 = testDto2ServiceBasicAuth.create(d2i1);
    TestDto2 d2i2 = new TestDto2();
    d2i2.setEnv("required");
    d2i2.setLinkToFullDownload("required");
    d2i2 = testDto2ServiceBasicAuth.create(d2i2);

    TestDto1 d3i1 = new TestDto1();
    d3i1.setEnv("required");
    d3i1.setLinkToFullDownload("required");
    d3i1 = testDto1Service.create(d3i1);
    TestDto1 d3i2 = new TestDto1();
    d3i2.setEnv("required");
    d3i2.setLinkToFullDownload("required");
    d3i2 = testDto1Service.create(d3i2);
    TestDto1 d3i3 = new TestDto1();
    d3i3.setEnv("required");
    d3i3.setLinkToFullDownload("required");
    d3i3 = testDto1Service.create(d3i3);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());
    m2mService.addReferencee(d2i1.getId(), d3i2.getId());
    m2mService.addReferencee(d2i2.getId(), d3i2.getId());
    m2mService.addReferencee(d2i2.getId(), d3i3.getId());

    Map<Long, List<TestDto1>> result =
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
  public void testAddReferenceeExpectGracefullResponseIfReferencesNotFound() {
    TestDto2 d2i1 = new TestDto2();
    d2i1.setEnv("required");
    d2i1.setLinkToFullDownload("required");
    d2i1 = testDto2ServiceBasicAuth.create(d2i1);
    TestDto2 d2i2 = new TestDto2();
    d2i2.setEnv("required");
    d2i2.setLinkToFullDownload("required");
    d2i2 = testDto2ServiceBasicAuth.create(d2i2);

    TestDto1 d3i1 = new TestDto1();
    d3i1.setEnv("required");
    d3i1.setLinkToFullDownload("required");
    d3i1 = testDto1Service.create(d3i1);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());

    Map<Long, List<TestDto1>> result =
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
    TestDto2 d2i1 = new TestDto2();
    d2i1.setEnv("required");
    d2i1.setLinkToFullDownload("required");
    d2i1 = testDto2ServiceBasicAuth.create(d2i1);

    TestDto1 d3i1 = new TestDto1();
    d3i1.setEnv("required");
    d3i1.setLinkToFullDownload("required");
    d3i1 = testDto1Service.create(d3i1);

    m2mService.addReferencee(d2i1.getId(), d3i1.getId());
    List<TestDto1> ree = m2mService.findReferenceeByReferencer(d2i1.getId());
    assertEquals(1, ree.size());

    m2mService.removeReferencee(d2i1.getId(), d3i1.getId());
    ree = m2mService.findReferenceeByReferencer(d2i1.getId());
    assertEquals(0, ree.size());
  }
}
