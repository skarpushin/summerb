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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestDto2;
import integr.org.summerb.easycrud.dtos.TestEnumFieldType;
import integr.org.summerb.easycrud.utils.CurrentUserResolverTestImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

@Transactional
public abstract class GenericCrudServiceTestTemplate {
  @Autowired protected EventBus eventBus;

  @Autowired protected CurrentUserResolverTestImpl currentUserResolver;

  @Autowired protected EasyCrudServiceResolver easyCrudServiceResolver;

  public abstract EasyCrudService<String, TestDto1> getTestDto1Service();

  public abstract EasyCrudService<Long, TestDto2> getTestDto2Service();

  public abstract EasyCrudService<String, TestDto1> getTestDto1ServiceEb();

  @SuppressWarnings("rawtypes")
  @Test
  public void testServiceResolver_expectOneServicesFound() {
    EasyCrudService service =
        easyCrudServiceResolver.resolveByRowMessageCode(TestDto1.class.getCanonicalName());
    assertNotNull(service);
    assertEquals(TestDto1.class.getCanonicalName(), service.getRowMessageCode());
  }

  @Test
  public void testBeanWrapper() {
    TestDto1 dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("uat");
    dto.setLinkToFullDownload("link-to-full-download");
    dto.setMajorVersion(2);
    dto.setMinorVersion(1);

    BeanWrapperImpl w = new BeanWrapperImpl(dto);
    assertEquals(2, w.getPropertyValue("majorVersion"));
    assertEquals("link-to-full-download", w.getPropertyValue("linkToFullDownload"));
  }

  @Test
  public void testCreateDto1() {
    TestDto1 dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("uat");
    dto.setLinkToFullDownload("link-to-full-download");
    dto.setMajorVersion(2);
    dto.setMinorVersion(1);

    TestDto1 result = getTestDto1Service().create(dto);

    assertNotNull(result);
    assertNotNull(result.getId());

    TestDto1 found = getTestDto1Service().findById(result.getId());
    assertNotNull(found);
    assertEquals("uat", found.getEnv());
    assertEquals("link-to-full-download", found.getLinkToFullDownload());
    assertEquals(2, found.getMajorVersion());
    assertEquals(1, found.getMinorVersion());
  }

  @Test
  public void testEventBus_expectEventOnCreate() {
    final TestDto1 dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("uat");
    dto.setLinkToFullDownload("link-to-full-download");
    dto.setMajorVersion(2);
    dto.setMinorVersion(1);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<TestDto1> evt) {
            if (evt.getValue().getEnv().equals("uat") && evt.getChangeType() == ChangeType.ADDED) {
              flag.incrementAndGet();
            }
          }
        });

    TestDto1 result = getTestDto1ServiceEb().create(dto);
    assertNotNull(result);
    assertEquals(1, flag.get());
  }

  @Test
  public void testDeleteById() {
    TestDto1 dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("uat");
    dto.setLinkToFullDownload("link-to-full-download");
    dto.setMajorVersion(2);
    dto.setMinorVersion(1);

    TestDto1 result = getTestDto1Service().create(dto);
    getTestDto1Service().deleteById(result.getId());

    TestDto1 found = getTestDto1Service().findById(result.getId());
    assertNull(found);
  }

  @Test
  public void testEventBus_expectEventOnDeleteById() {
    final TestDto1 v0 = new TestDto1();
    v0.setActive(true);
    v0.setEnv("uat");
    v0.setLinkToFullDownload("link-to-full-download");
    v0.setMajorVersion(2);
    v0.setMinorVersion(1);

    final TestDto1 v1 = getTestDto1ServiceEb().create(v0);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<TestDto1> evt) {
            if (evt.getValue().getId().equals(v1.getId())
                && evt.getChangeType() == ChangeType.REMOVED) {
              flag.incrementAndGet();
            }
          }
        });

    getTestDto1ServiceEb().deleteById(v1.getId());
    assertEquals(1, flag.get());
  }

  @Test
  public void testDeleteByIdOptimistic() {
    TestDto1 dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("uat");
    dto.setLinkToFullDownload("link-to-full-download");
    dto.setMajorVersion(2);
    dto.setMinorVersion(1);

    TestDto1 result = getTestDto1Service().create(dto);
    getTestDto1Service().deleteByIdOptimistic(result.getId(), result.getModifiedAt());

    TestDto1 found = getTestDto1Service().findById(result.getId());
    assertNull(found);
  }

  @Test
  public void testEventBus_expectEventOnDeleteByIdOptimistic() {
    final TestDto1 v0 = new TestDto1();
    v0.setActive(true);
    v0.setEnv("uat");
    v0.setLinkToFullDownload("link-to-full-download");
    v0.setMajorVersion(2);
    v0.setMinorVersion(1);

    final TestDto1 v1 = getTestDto1ServiceEb().create(v0);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<TestDto1> evt) {
            if (evt.getValue().getId().equals(v1.getId())
                && evt.getChangeType() == ChangeType.REMOVED) {
              flag.incrementAndGet();
            }
          }
        });

    getTestDto1ServiceEb().deleteByIdOptimistic(v1.getId(), v1.getModifiedAt());
    assertEquals(1, flag.get());
  }

  @Test
  public void testEventBus_expectEventOnDeleteByIdOptimisticFail() {
    final TestDto1 v0 = new TestDto1();
    v0.setActive(true);
    v0.setEnv("uat");
    v0.setLinkToFullDownload("link-to-full-download");
    v0.setMajorVersion(2);
    v0.setMinorVersion(1);

    final TestDto1 v1 = getTestDto1ServiceEb().create(v0);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<TestDto1> evt) {
            if (evt.getValue().getId().equals(v1.getId())
                && evt.getChangeType() == ChangeType.REMOVED) {
              flag.incrementAndGet();
            }
          }
        });

    try {
      getTestDto1ServiceEb().deleteByIdOptimistic(v1.getId(), v1.getModifiedAt() - 1);
    } catch (Throwable t) {
      // dont' care
    }
    assertEquals(0, flag.get());
  }

  @Test
  public void testCreateDto2() {
    TestDto2 dto = new TestDto2();
    dto.setActive(true);
    dto.setEnv("uat1");
    dto.setLinkToFullDownload("link-to-full-download1");
    dto.setMajorVersion(5);
    dto.setMinorVersion(6);

    TestDto2 result = getTestDto2Service().create(dto);

    assertNotNull(result);
    assertNotNull(result.getId());

    TestDto2 found = getTestDto2Service().findById(result.getId());
    assertNotNull(found);
    assertEquals("uat1", found.getEnv());
    assertEquals("link-to-full-download1", found.getLinkToFullDownload());
    assertEquals(5, found.getMajorVersion());
    assertEquals(6, found.getMinorVersion());
  }

  @Test
  public void testFindByQueryString() {
    createTestData();

    TestDto1 result = getTestDto1Service().query().eq("env", "env-UAT").findOne();
    assertNotNull(result);
    assertEquals("link-to-full-download123", result.getLinkToFullDownload());

    result = getTestDto1Service().findOneByQuery(Query.n(TestDto1.class).eq("env", "env-PILOT"));
    assertNotNull(result);
    assertEquals("link-to-full-download456", result.getLinkToFullDownload());
  }

  @Test
  public void testFindByQueryStringNe() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).ne("env", "env-uat"));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryContains() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service().find(new PagerParams(0, 100), TestDto1.Q().contains("env", "env-p"));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNotContains() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).notContains("env", "env-P"));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryIn() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).in("env", Arrays.asList("env-UAT", "env-pilot")));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNotIn() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).notIn("env", Arrays.asList("env-UAT", "env-pilot")));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryOr() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class)
                    .eq("majorVersion", 3L)
                    .or(
                        Query.n(TestDto1.class).eq("minorVersion", 4L),
                        Query.n(TestDto1.class).eq("minorVersion", 5L)));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNumericNe() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).ne("majorVersion", 3));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNumericGe() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).ge("majorVersion", 3));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryBetween() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).between("minorVersion", 4, 5));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryOutside() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100), Query.n(TestDto1.class).notBetween("minorVersion", 4, 5));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNumberOneOf() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).in("minorVersion", Arrays.asList(4L, 5L)));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNumberNotIn() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).notIn("minorVersion", Arrays.asList(4L, 5L)));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryIsNull() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).isNull("linkToPatchToNextVersion"));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryIsNotNull() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).isNotNull("linkToPatchToNextVersion"));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryBooleanFalse() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).isFalse("active"));
    assertEquals(1, result.getTotalResults());

    result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).ne("active", true));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryBooleanTrue() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).isTrue("active"));
    assertEquals(2, result.getTotalResults());

    result =
        getTestDto1Service()
            .find(new PagerParams(0, 100), Query.n(TestDto1.class).ne("active", false));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryStringLengthBetween() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100), Query.n(TestDto1.class).stringLengthBetween("env", 6, 7));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testDeleteByQuery() {
    createTestData();

    int result =
        getTestDto1Service()
            .deleteByQuery(Query.n(TestDto1.class).stringLengthBetween("env", 6, 7));
    assertEquals(2, result);
  }

  @Test
  public void testEventBus_expectEventOnDeleteByQuery() {
    createTestData();

    List<String> ids = Arrays.asList("env-uat", "env-prd");
    final List<String> envs = new ArrayList<>(ids);
    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<TestDto1> evt) {
            if (evt.getChangeType() != ChangeType.REMOVED) {
              return;
            }
            if (envs.contains(evt.getValue().getEnv())) {
              flag.incrementAndGet();
            }
          }
        });

    int affected = getTestDto1ServiceEb().deleteByQuery(Query.n(TestDto1.class).in("env", ids));
    assertEquals(2, affected);
    assertEquals(2, flag.get());
  }

  @Test
  public void testFindOneByQuery_expectNullForNotFound() {
    createTestData();

    TestDto1 result =
        getTestDto1Service()
            .findOneByQuery(Query.n(TestDto1.class).stringLengthBetween("env", 1, 2));
    assertNull(result);
  }

  @Test
  public void testQuery_expectPaginationWorksCorrectly() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(new PagerParams(0, 2), Query.n(TestDto1.class).contains("env", "env-"));
    assertEquals(3, result.getTotalResults());
    assertEquals(2, result.getItems().size());
  }

  @Test
  public void testQuery_expectOrderingWorksCorrectly() {
    createTestData();

    PaginatedList<TestDto1> result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).contains("env", "env-"),
                OrderBy.Asc("minorVersion"));
    assertEquals("env-uat", result.getItems().get(0).getEnv());
    assertEquals("env-pilot", result.getItems().get(1).getEnv());
    assertEquals("env-prd", result.getItems().get(2).getEnv());

    result =
        getTestDto1Service()
            .find(
                new PagerParams(0, 100),
                Query.n(TestDto1.class).contains("env", "env-"),
                OrderBy.Desc("minorVersion"));
    assertEquals("env-prd", result.getItems().get(0).getEnv());
    assertEquals("env-pilot", result.getItems().get(1).getEnv());
    assertEquals("env-uat", result.getItems().get(2).getEnv());
  }

  @Test
  public void testFindById_expectNullForNotFound() {
    createTestData();

    TestDto1 result = getTestDto1Service().findById("asdasdasd");
    assertNull(result);
  }

  @Test
  public void testUpdate_expectOk() throws Exception {
    createTestData();

    TestDto1 r1 = getTestDto1Service().findOneByQuery(Query.n(TestDto1.class).eq("env", "env-uat"));
    assertNotNull(r1);
    assertEquals(currentUserResolver.user1.getUsername(), r1.getModifiedBy());
    Thread.sleep(20);

    r1.setEnv("env-uat2");
    TestDto1 r2;
    try {
      currentUserResolver.user = currentUserResolver.user2;

      r2 = getTestDto1Service().update(r1);
      assertNotNull(r2);
      assertTrue(r2.getModifiedAt() > r1.getModifiedAt());
      assertEquals(currentUserResolver.user2.getUsername(), r2.getModifiedBy());
      assertEquals("env-uat2", r2.getEnv());
    } finally {
      currentUserResolver.user = currentUserResolver.user1;
    }

    TestDto1 r3 = getTestDto1Service().findById(r1.getId());
    assertNotNull(r2);
    assertTrue(r3.getModifiedAt() > r1.getModifiedAt());
    assertEquals("env-uat2", r3.getEnv());
    assertEquals(currentUserResolver.user2.getUsername(), r3.getModifiedBy());
  }

  @Test
  public void testEventBus_expectEventOnUpdate() {
    final TestDto1 v1 = new TestDto1();
    v1.setActive(true);
    v1.setEnv("uat");
    v1.setLinkToFullDownload("link-to-full-download");
    v1.setMajorVersion(2);
    v1.setMinorVersion(1);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<TestDto1> evt) {
            if (evt.getValue().getEnv().equals("uat")
                && evt.getChangeType() == ChangeType.UPDATED) {
              flag.incrementAndGet();
            }
          }
        });

    TestDto1 result = getTestDto1ServiceEb().create(v1);
    result.setLinkToFullDownload("asdasdasd");
    getTestDto1ServiceEb().update(result);
    assertEquals(1, flag.get());
  }

  @Test
  public void testUpdate_expectNotChangeableColumnsAreNotChanged() {
    TestDto1 dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("env-uat");
    dto.setLinkToFullDownload("link-to-full-download123");
    dto.setMajorVersion(1);
    dto.setMinorVersion(2);
    dto = getTestDto1Service().create(dto);

    assertTrue(dto.getCreatedAt() > 0);
    assertNotNull(dto.getCreatedBy());

    long initialCreateAt = dto.getCreatedAt();
    String initialCreatedBy = dto.getCreatedBy();

    dto.setCreatedAt(20);
    dto.setCreatedBy("by");

    getTestDto1Service().update(dto);
    dto = getTestDto1Service().findById(dto.getId());

    assertEquals(initialCreateAt, dto.getCreatedAt());
    assertEquals(initialCreatedBy, dto.getCreatedBy());
  }

  private void createTestData() {
    TestDto1 dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("env-uat");
    dto.setLinkToFullDownload("link-to-full-download123");
    dto.setMajorVersion(1);
    dto.setMinorVersion(2);
    getTestDto1Service().create(dto);

    dto = new TestDto1();
    dto.setActive(true);
    dto.setEnv("env-pilot");
    dto.setLinkToFullDownload("link-to-full-download456");
    dto.setMajorVersion(3);
    dto.setMinorVersion(4);
    getTestDto1Service().create(dto);

    dto = new TestDto1();
    dto.setActive(false);
    dto.setEnv("env-prd");
    dto.setLinkToFullDownload("link-to-full-download456");
    dto.setLinkToPatchToNextVersion(TestEnumFieldType.ACTIVE);
    dto.setMajorVersion(3);
    dto.setMinorVersion(5);
    getTestDto1Service().create(dto);
  }
}
