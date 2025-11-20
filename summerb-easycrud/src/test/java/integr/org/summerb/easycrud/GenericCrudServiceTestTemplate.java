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
import integr.org.summerb.easycrud.dtos.PostRow;
import integr.org.summerb.easycrud.dtos.UserRow;
import integr.org.summerb.easycrud.dtos.UserStatus;
import integr.org.summerb.easycrud.utils.CurrentUserResolverTestImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.EasyCrudServiceResolver;
import org.summerb.easycrud.exceptions.EntityNotFoundException;
import org.summerb.easycrud.query.OrderBy;
import org.summerb.easycrud.query.Query;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.utils.easycrud.api.dto.PagerParams;
import org.summerb.utils.easycrud.api.dto.PaginatedList;

@Transactional
public abstract class GenericCrudServiceTestTemplate {
  @Autowired protected Environment environment;
  @Autowired protected EventBus eventBus;
  @Autowired protected CurrentUserResolverTestImpl currentUserResolver;
  @Autowired protected EasyCrudServiceResolver easyCrudServiceResolver;

  public abstract EasyCrudService<String, UserRow> getUserRowService();

  public abstract EasyCrudService<Long, PostRow> getPostRowServiceBasicAuth();

  public abstract EasyCrudService<String, UserRow> getUserRowServiceEb();

  @SuppressWarnings("rawtypes")
  @Test
  public void testServiceResolver_expectOneServicesFound() {
    EasyCrudService service =
        easyCrudServiceResolver.resolveByRowMessageCode(UserRow.class.getCanonicalName());
    assertNotNull(service);
    assertEquals(UserRow.class.getCanonicalName(), service.getRowMessageCode());
  }

  @Test
  public void testBeanWrapper() {
    UserRow dto = new UserRow();
    dto.setActive(true);
    dto.setName("uat");
    dto.setAbout("link-to-full-download");
    dto.setKarma(2);

    BeanWrapperImpl w = new BeanWrapperImpl(dto);
    assertEquals(2, w.getPropertyValue("karma"));
    assertEquals("link-to-full-download", w.getPropertyValue("about"));
  }

  @Test
  public void testCreateDto1() {
    UserRow dto = new UserRow();
    dto.setActive(true);
    dto.setName("uat");
    dto.setAbout("link-to-full-download");
    dto.setKarma(2);

    UserRow result = getUserRowService().create(dto);

    assertNotNull(result);
    assertNotNull(result.getId());

    UserRow found = getUserRowService().findById(result.getId());
    assertNotNull(found);
    assertEquals("uat", found.getName());
    assertEquals("link-to-full-download", found.getAbout());
    assertEquals(2, found.getKarma());
  }

  @Test
  public void testEventBus_expectEventOnCreate() {
    final UserRow dto = new UserRow();
    dto.setActive(true);
    dto.setName("uat");
    dto.setAbout("link-to-full-download");
    dto.setKarma(2);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<UserRow> evt) {
            if (evt.getValue().getName().equals("uat") && evt.getChangeType() == ChangeType.ADDED) {
              flag.incrementAndGet();
            }
          }
        });

    UserRow result = getUserRowServiceEb().create(dto);
    assertNotNull(result);
    assertEquals(1, flag.get());
  }

  @Test
  public void testDeleteById() {
    UserRow dto = new UserRow();
    dto.setActive(true);
    dto.setName("uat");
    dto.setAbout("link-to-full-download");
    dto.setKarma(2);

    UserRow result = getUserRowService().create(dto);
    getUserRowService().deleteById(result.getId());

    UserRow found = getUserRowService().findById(result.getId());
    assertNull(found);
  }

  @Test
  public void testEventBus_expectEventOnDeleteById() {
    final UserRow v0 = new UserRow();
    v0.setActive(true);
    v0.setName("uat");
    v0.setAbout("link-to-full-download");
    v0.setKarma(2);

    final UserRow v1 = getUserRowServiceEb().create(v0);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<UserRow> evt) {
            if (evt.getValue().getId().equals(v1.getId())
                && evt.getChangeType() == ChangeType.REMOVED) {
              flag.incrementAndGet();
            }
          }
        });

    getUserRowServiceEb().deleteById(v1.getId());
    assertEquals(1, flag.get());
  }

  @Test
  public void testDeleteByIdOptimistic() {
    UserRow dto = new UserRow();
    dto.setActive(true);
    dto.setName("uat");
    dto.setAbout("link-to-full-download");
    dto.setKarma(2);

    UserRow result = getUserRowService().create(dto);
    getUserRowService().deleteByIdOptimistic(result.getId(), result.getModifiedAt());

    UserRow found = getUserRowService().findById(result.getId());
    assertNull(found);
  }

  @Test
  public void testEventBus_expectEventOnDeleteByIdOptimistic() {
    final UserRow v0 = new UserRow();
    v0.setActive(true);
    v0.setName("uat");
    v0.setAbout("link-to-full-download");
    v0.setKarma(2);

    final UserRow v1 = getUserRowServiceEb().create(v0);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<UserRow> evt) {
            if (evt.getValue().getId().equals(v1.getId())
                && evt.getChangeType() == ChangeType.REMOVED) {
              flag.incrementAndGet();
            }
          }
        });

    getUserRowServiceEb().deleteByIdOptimistic(v1.getId(), v1.getModifiedAt());
    assertEquals(1, flag.get());
  }

  @Test
  public void testEventBus_expectEventOnDeleteByIdOptimisticFail() {
    final UserRow v0 = new UserRow();
    v0.setActive(true);
    v0.setName("uat");
    v0.setAbout("link-to-full-download");
    v0.setKarma(2);

    final UserRow v1 = getUserRowServiceEb().create(v0);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<UserRow> evt) {
            if (evt.getValue().getId().equals(v1.getId())
                && evt.getChangeType() == ChangeType.REMOVED) {
              flag.incrementAndGet();
            }
          }
        });

    try {
      getUserRowServiceEb().deleteByIdOptimistic(v1.getId(), v1.getModifiedAt() - 1);
    } catch (Throwable t) {
      // dont' care
    }
    assertEquals(0, flag.get());
  }

  @Test
  public void testCreatePost() {
    PostRow dto = new PostRow();

    dto.setTitle("uat1");
    dto.setBody("link-to-full-download1");
    dto.setAuthorId("someid");
    dto.setLikes(5);
    dto.setDislikes(6);
    dto.setAuthorId("someid");

    PostRow result = getPostRowServiceBasicAuth().create(dto);

    assertNotNull(result);
    assertNotNull(result.getId());

    PostRow found = getPostRowServiceBasicAuth().findById(result.getId());
    assertNotNull(found);
    assertEquals("uat1", found.getTitle());
    assertEquals("link-to-full-download1", found.getBody());
    assertEquals(5, found.getLikes());
    assertEquals(6, found.getDislikes());
  }

  @Test
  public void testFindByQueryString() {
    createTestData();

    UserRow result = getUserRowService().query().eq(UserRow::getName, "env-UAT").findOne();
    assertNotNull(result);
    assertEquals("link-to-full-download123", result.getAbout());

    result =
        getUserRowService()
            .findOneByQuery(getUserRowService().query().eq(UserRow::getName, "env-PILOT"));
    assertNotNull(result);
    assertEquals("link-to-full-download456", result.getAbout());
  }

  @Test
  public void testFindByQueryStringNe() {
    createTestData();

    Query<String, UserRow> q = getUserRowService().query().ne(UserRow::getName, "env-uat");
    PagerParams pagerParams = new PagerParams(0, 100);
    PaginatedList<UserRow> result = getUserRowService().find(pagerParams, q);
    assertEquals(2, result.getTotalResults());

    // Also test findPage
    List<UserRow> page = q.findPage(pagerParams);
    assertEquals(2, page.size());
    for (int i = 0; i < 2; i++) {
      assertEquals(result.getItems().get(i).getId(), page.get(i).getId());
    }
  }

  @Test
  public void testFindByQueryContains() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().contains(UserRow::getName, "env-p"));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNotContains() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().notContains(UserRow::getName, "env-P"));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryIn() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService()
                    .query()
                    .in(UserRow::getName, Arrays.asList("env-UAT", "env-pilot")));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNotIn() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService()
                    .query()
                    .notIn(UserRow::getName, Arrays.asList("env-UAT", "env-pilot")));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryOr() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService()
                    .query()
                    .eq(UserRow::getAbout, "link-to-full-download456")
                    .or(
                        getUserRowService().query().eq(UserRow::getKarma, 10L),
                        getUserRowService().query().eq(UserRow::getKarma, 5L)));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNumericNe() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(new PagerParams(0, 100), getUserRowService().query().ne(UserRow::getKarma, 3));
    assertEquals(3, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNumericGe() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(new PagerParams(0, 100), getUserRowService().query().ge(UserRow::getKarma, 3));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryBetween() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().between(UserRow::getKarma, 1, 5));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryOutside() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().notBetween(UserRow::getKarma, 1, 5));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryNumberOneOf() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().in(UserRow::getKarma, Arrays.asList(1L, 5L)));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testQueryWithEmptyInWillReturnEmptyResultset() {
    createTestData();

    // `in` is empty
    EasyCrudService<String, UserRow> service = getUserRowService();
    List<UserRow> result = service.findAll(service.query().in(UserRow::getKarma, List.of()));
    assertEquals(0, result.size());

    // we have or with one empty `in`
    result =
        service
            .query()
            .or(
                service.query().in(UserRow::getKarma, List.of()),
                service.query().ne(UserRow::getKarma, 1))
            .findAll(service.orderBy(UserRow::getKarma).asc());
    assertEquals(2, result.size());
    assertEquals(5, result.get(0).getKarma());
    assertEquals(10, result.get(1).getKarma());

    // we have `or` with all `in` queries empty
    result =
        service
            .query()
            .or(
                service.query().in(UserRow::getKarma, List.of()),
                service.query().in(UserRow::getAbout, null))
            .findAll(service.orderBy(UserRow::getKarma).asc());
    assertEquals(0, result.size());

    // also test notIn with empty argument
    result =
        service
            .query()
            .notIn(UserRow::getKarma, List.of())
            .findAll(service.orderBy(UserRow::getKarma).asc());
    assertEquals(3, result.size());
  }

  @Test
  public void testFindByQueryNumberNotIn() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().notIn(UserRow::getKarma, Arrays.asList(1L, 5L)));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryIsNull() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(new PagerParams(0, 100), getUserRowService().query().isNull(UserRow::getStatus));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryIsNotNull() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100), getUserRowService().query().isNotNull(UserRow::getStatus));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryBooleanFalse() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(new PagerParams(0, 100), getUserRowService().query().isFalse("active"));
    assertEquals(1, result.getTotalResults());

    result =
        getUserRowService()
            .find(new PagerParams(0, 100), getUserRowService().query().ne("active", true));
    assertEquals(1, result.getTotalResults());
  }

  @Test
  public void testFindByQueryBooleanTrue() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(new PagerParams(0, 100), getUserRowService().query().isTrue("active"));
    assertEquals(2, result.getTotalResults());

    result =
        getUserRowService()
            .find(new PagerParams(0, 100), getUserRowService().query().ne("active", false));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testFindByQueryStringLengthBetween() {
    createTestData();

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().stringLengthBetween(UserRow::getName, 6, 7));
    assertEquals(2, result.getTotalResults());
  }

  @Test
  public void testDeleteByQuery() {
    createTestData();

    int result =
        getUserRowService()
            .deleteByQuery(getUserRowService().query().stringLengthBetween(UserRow::getName, 6, 7));
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
          public void handle(EntityChangedEvent<UserRow> evt) {
            if (evt.getChangeType() != ChangeType.REMOVED) {
              return;
            }
            if (envs.contains(evt.getValue().getName())) {
              flag.incrementAndGet();
            }
          }
        });

    int affected =
        getUserRowServiceEb().deleteByQuery(getUserRowService().query().in(UserRow::getName, ids));
    assertEquals(2, affected);
    assertEquals(2, flag.get());
  }

  @Test
  public void testFindOneByQuery_expectNullForNotFound() {
    createTestData();

    UserRow result =
        getUserRowService()
            .findOneByQuery(
                getUserRowService().query().stringLengthBetween(UserRow::getName, 1, 2));
    assertNull(result);
  }

  @Test
  public void testQuery_expectPaginationWorksCorrectly() {
    createTestData();

    Query<String, UserRow> q = getUserRowService().query().contains(UserRow::getName, "env-");
    PagerParams pagerParams = new PagerParams(0, 2);
    PaginatedList<UserRow> result = getUserRowService().find(pagerParams, q);
    assertEquals(3, result.getTotalResults());
    assertEquals(2, result.getItems().size());

    // Also test findPage
    List<UserRow> page = q.findPage(pagerParams);
    assertEquals(2, page.size());
    for (int i = 0; i < 2; i++) {
      assertEquals(result.getItems().get(i).getId(), page.get(i).getId());
    }
  }

  @Test
  public void testQuery_expectOrderingWorksCorrectly() {
    createTestData();

    OrderBy[] karmaAsc1 = getUserRowService().parseOrderBy("karma,asc");
    OrderBy[] karmaAsc2 = getUserRowService().parseOrderBy(new String[] {"karma"});
    OrderBy[] karmaAsc3 = new OrderBy[] {getUserRowService().orderBy(UserRow::getKarma).asc()};

    assertEquals(karmaAsc1[0].getFieldName(), karmaAsc2[0].getFieldName());
    assertArrayEquals(karmaAsc1, karmaAsc3);

    PaginatedList<UserRow> result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().contains(UserRow::getName, "env-"),
                karmaAsc1);
    assertEquals("env-uat", result.getItems().get(0).getName());
    assertEquals("env-pilot", result.getItems().get(1).getName());
    assertEquals("env-prd", result.getItems().get(2).getName());

    result =
        getUserRowService()
            .find(
                new PagerParams(0, 100),
                getUserRowService().query().contains(UserRow::getName, "env-"),
                getUserRowService().orderBy(UserRow::getKarma).desc());
    assertEquals("env-prd", result.getItems().get(0).getName());
    assertEquals("env-pilot", result.getItems().get(1).getName());
    assertEquals("env-uat", result.getItems().get(2).getName());
  }

  @Test
  public void testFindById_expectNullForNotFound() {
    createTestData();

    UserRow result = getUserRowService().findById("asdasdasd");
    assertNull(result);
  }

  @Test
  public void testUpdate_expectOk() throws Exception {
    createTestData();

    UserRow r1 =
        getUserRowService()
            .findOneByQuery(getUserRowService().query().eq(UserRow::getName, "env-uat"));
    assertNotNull(r1);
    assertEquals(currentUserResolver.user1.getUsername(), r1.getModifiedBy());
    Thread.sleep(20);

    r1.setName("env-uat2");
    UserRow r2;
    try {
      currentUserResolver.user = currentUserResolver.user2;

      r2 = getUserRowService().update(r1);
      assertNotNull(r2);
      assertTrue(r2.getModifiedAt() > r1.getModifiedAt());
      assertEquals(currentUserResolver.user2.getUsername(), r2.getModifiedBy());
      assertEquals("env-uat2", r2.getName());
    } finally {
      currentUserResolver.user = currentUserResolver.user1;
    }

    UserRow r3 = getUserRowService().findById(r1.getId());
    assertNotNull(r2);
    assertTrue(r3.getModifiedAt() > r1.getModifiedAt());
    assertEquals("env-uat2", r3.getName());
    assertEquals(currentUserResolver.user2.getUsername(), r3.getModifiedBy());
  }

  @Test
  public void testEventBus_expectEventOnUpdate() {
    final UserRow v1 = new UserRow();
    v1.setActive(true);
    v1.setName("uat");
    v1.setAbout("link-to-full-download");
    v1.setKarma(2);

    final AtomicInteger flag = new AtomicInteger(0);
    eventBus.register(
        new Object() {
          @Subscribe
          public void handle(EntityChangedEvent<UserRow> evt) {
            if (evt.getValue().getName().equals("uat")
                && evt.getChangeType() == ChangeType.UPDATED) {
              flag.incrementAndGet();
            }
          }
        });

    UserRow result = getUserRowServiceEb().create(v1);
    result.setAbout("asdasdasd");
    getUserRowServiceEb().update(result);
    assertEquals(1, flag.get());
  }

  @Test
  public void testUpdate_expectNotChangeableColumnsAreNotChanged() {
    UserRow dto = new UserRow();
    dto.setActive(true);
    dto.setName("env-uat");
    dto.setAbout("link-to-full-download123");
    dto.setKarma(1);
    dto = getUserRowService().create(dto);

    assertTrue(dto.getCreatedAt() > 0);
    assertNotNull(dto.getCreatedBy());

    long initialCreateAt = dto.getCreatedAt();
    String initialCreatedBy = dto.getCreatedBy();

    dto.setCreatedAt(20);
    dto.setCreatedBy("by");

    getUserRowService().update(dto);
    dto = getUserRowService().findById(dto.getId());

    assertEquals(initialCreateAt, dto.getCreatedAt());
    assertEquals(initialCreatedBy, dto.getCreatedBy());
  }

  @Test
  public void testCount() {
    createTestData();

    assertEquals(3, getUserRowService().count());
    assertEquals(
        2, getUserRowService().count(getUserRowService().query().isNull(UserRow::getStatus)));
  }

  @Test
  public void testGetOneByQuery_expectFound() {
    createTestData();

    UserRow result =
        getUserRowService()
            .getOneByQuery(getUserRowService().query().eq(UserRow::getName, "env-uat"));

    assertNotNull(result);
    assertEquals("env-uat", result.getName());
    assertEquals("link-to-full-download123", result.getAbout());
  }

  @Test
  public void testGetOneByQuery_expectExceptionWhenNotFound() {
    createTestData();

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          getUserRowService()
              .getOneByQuery(getUserRowService().query().eq(UserRow::getName, "non-existent-user"));
        });
  }

  @Test
  public void testGetFirstByQuery_expectFound() {
    createTestData();

    // Test with ordering to ensure we get the first one by karma
    UserRow result =
        getUserRowService()
            .getFirstByQuery(
                getUserRowService().query().ge(UserRow::getKarma, 1),
                getUserRowService().orderBy(UserRow::getKarma).asc());

    assertNotNull(result);
    assertEquals("env-uat", result.getName()); // Should be the one with lowest karma (1)
    assertEquals(1, result.getKarma());
  }

  @Test
  public void testGetFirstByQuery_expectExceptionWhenNotFound() {
    createTestData();

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          getUserRowService()
              .getFirstByQuery(
                  getUserRowService().query().eq(UserRow::getName, "non-existent-user"));
        });
  }

  @Test
  public void testGetAll_expectAllItems() {
    createTestData();

    List<UserRow> result = getUserRowService().getAll(null);

    assertNotNull(result);
    assertEquals(3, result.size());

    // Verify all expected users are present
    List<String> userNames = result.stream().map(UserRow::getName).toList();
    assertTrue(userNames.contains("env-uat"));
    assertTrue(userNames.contains("env-pilot"));
    assertTrue(userNames.contains("env-prd"));
  }

  @Test
  public void testGetAllWithQuery_expectFilteredItems() {
    createTestData();

    List<UserRow> result = getUserRowService().getAll(getUserRowService().query().isTrue("active"));

    assertNotNull(result);
    assertEquals(2, result.size()); // Only active users

    List<String> userNames = result.stream().map(UserRow::getName).toList();
    assertTrue(userNames.contains("env-uat"));
    assertTrue(userNames.contains("env-pilot"));
    assertFalse(userNames.contains("env-prd")); // This one is inactive
  }

  @Test
  public void testGetAllWithQueryAndOrdering_expectOrderedItems() {
    createTestData();

    List<UserRow> result =
        getUserRowService()
            .getAll(
                getUserRowService().query().contains(UserRow::getName, "env-"),
                getUserRowService().orderBy(UserRow::getKarma).desc());

    assertNotNull(result);
    assertEquals(3, result.size());

    // Verify order: highest karma first
    assertEquals("env-prd", result.get(0).getName());
    assertEquals("env-pilot", result.get(1).getName());
    assertEquals("env-uat", result.get(2).getName());
  }

  private boolean isPostgres() {
    return environment.matchesProfiles("postgres");
  }

  @Test
  public void testOrderByWithCollation() {
    if (!isPostgres()) {
      return;
    }

    createTestData();

    List<UserRow> result =
        getUserRowService()
            .getAll(
                getUserRowService().query().contains(UserRow::getName, "env-"),
                getUserRowService().orderBy(UserRow::getName).asc().withCollate("C"));

    assertNotNull(result);
    assertEquals(3, result.size());

    // Verify order: highest karma first
    assertEquals("env-pilot", result.get(0).getName());
    assertEquals("env-prd", result.get(1).getName());
    assertEquals("env-uat", result.get(2).getName());
  }

  @Test
  public void testGetAll_expectExceptionWhenNoItems() {
    // Don't create test data for this test

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          getUserRowService().getAll(null);
        });
  }

  @Test
  public void testGetAllWithQuery_expectExceptionWhenNoItemsMatch() {
    createTestData();

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          getUserRowService()
              .getAll(getUserRowService().query().eq(UserRow::getName, "non-existent-user"));
        });
  }

  private void createTestData() {
    UserRow dto = new UserRow();
    dto.setActive(true);
    dto.setName("env-uat");
    dto.setAbout("link-to-full-download123");
    dto.setKarma(1);
    getUserRowService().create(dto);

    dto = new UserRow();
    dto.setActive(true);
    dto.setName("env-pilot");
    dto.setAbout("link-to-full-download456");
    dto.setKarma(5);
    getUserRowService().create(dto);

    dto = new UserRow();
    dto.setActive(false);
    dto.setName("env-prd");
    dto.setAbout("link-to-full-download456");
    dto.setStatus(UserStatus.ACTIVE);
    dto.setKarma(10);
    getUserRowService().create(dto);
  }
}
