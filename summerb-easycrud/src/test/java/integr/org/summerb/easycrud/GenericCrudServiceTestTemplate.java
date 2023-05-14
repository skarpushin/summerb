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
package integr.org.summerb.easycrud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.ValidationException;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Transactional
public abstract class GenericCrudServiceTestTemplate {
	@Autowired
	protected EventBus eventBus;

	@Autowired
	protected CurrentUserResolverTestImpl currentUserResolver;

	@Autowired
	protected EasyCrudServiceResolver easyCrudServiceResolver;

	public abstract EasyCrudService<String, TestDto1> getTestDto1Service();

	public abstract EasyCrudService<Long, TestDto2> getTestDto2Service();

	public abstract EasyCrudService<String, TestDto1> getTestDto1ServiceEb();

	@SuppressWarnings("rawtypes")
	@Test
	public void testServiceResolver_expectOneServicesFound() {
		EasyCrudService service = easyCrudServiceResolver.resolveByRowMessageCode(TestDto1.class.getCanonicalName());
		assertNotNull(service);
		assertEquals(TestDto1.class.getCanonicalName(), service.getRowMessageCode());
	}

	@Test
	public void testBeanWrapper() throws Exception {
		TestDto1 dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("uat");
		dto.setLinkToFullDonwload("link-to-full-download");
		dto.setMajorVersion(2);
		dto.setMinorVersion(1);

		BeanWrapperImpl w = new BeanWrapperImpl(dto);
		assertTrue(Integer.valueOf(2).equals(w.getPropertyValue("majorVersion")));
		assertTrue("link-to-full-download".equals(w.getPropertyValue("linkToFullDonwload")));
	}

	@Test
	public void testCreateDto1() throws Exception {
		TestDto1 dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("uat");
		dto.setLinkToFullDonwload("link-to-full-download");
		dto.setMajorVersion(2);
		dto.setMinorVersion(1);

		TestDto1 result = getTestDto1Service().create(dto);

		assertNotNull(result);
		assertNotNull(result.getId());

		TestDto1 found = getTestDto1Service().findById(result.getId());
		assertNotNull(found);
		assertEquals("uat", found.getEnv());
		assertEquals("link-to-full-download", found.getLinkToFullDonwload());
		assertEquals(2, found.getMajorVersion());
		assertEquals(1, found.getMinorVersion());
	}

	@Test
	public void testEventBus_expectEventOnCreate() throws Exception {
		final TestDto1 dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("uat");
		dto.setLinkToFullDonwload("link-to-full-download");
		dto.setMajorVersion(2);
		dto.setMinorVersion(1);

		final AtomicInteger flag = new AtomicInteger(0);
		eventBus.register(new Object() {
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
	public void testDeleteById() throws Exception {
		TestDto1 dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("uat");
		dto.setLinkToFullDonwload("link-to-full-download");
		dto.setMajorVersion(2);
		dto.setMinorVersion(1);

		TestDto1 result = getTestDto1Service().create(dto);
		getTestDto1Service().deleteById(result.getId());

		TestDto1 found = getTestDto1Service().findById(result.getId());
		assertNull(found);
	}

	@Test
	public void testEventBus_expectEventOnDeleteById() throws Exception {
		final TestDto1 v0 = new TestDto1();
		v0.setActive(true);
		v0.setEnv("uat");
		v0.setLinkToFullDonwload("link-to-full-download");
		v0.setMajorVersion(2);
		v0.setMinorVersion(1);

		final TestDto1 v1 = getTestDto1ServiceEb().create(v0);

		final AtomicInteger flag = new AtomicInteger(0);
		eventBus.register(new Object() {
			@Subscribe
			public void handle(EntityChangedEvent<TestDto1> evt) {
				if (evt.getValue().getId().equals(v1.getId()) && evt.getChangeType() == ChangeType.REMOVED) {
					flag.incrementAndGet();
				}
			}
		});

		getTestDto1ServiceEb().deleteById(v1.getId());
		assertEquals(1, flag.get());
	}

	@Test
	public void testDeleteByIdOptimistic() throws Exception {
		TestDto1 dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("uat");
		dto.setLinkToFullDonwload("link-to-full-download");
		dto.setMajorVersion(2);
		dto.setMinorVersion(1);

		TestDto1 result = getTestDto1Service().create(dto);
		getTestDto1Service().deleteByIdOptimistic(result.getId(), result.getModifiedAt());

		TestDto1 found = getTestDto1Service().findById(result.getId());
		assertNull(found);
	}

	@Test
	public void testEventBus_expectEventOnDeleteByIdOptimistic() throws Exception {
		final TestDto1 v0 = new TestDto1();
		v0.setActive(true);
		v0.setEnv("uat");
		v0.setLinkToFullDonwload("link-to-full-download");
		v0.setMajorVersion(2);
		v0.setMinorVersion(1);

		final TestDto1 v1 = getTestDto1ServiceEb().create(v0);

		final AtomicInteger flag = new AtomicInteger(0);
		eventBus.register(new Object() {
			@Subscribe
			public void handle(EntityChangedEvent<TestDto1> evt) {
				if (evt.getValue().getId().equals(v1.getId()) && evt.getChangeType() == ChangeType.REMOVED) {
					flag.incrementAndGet();
				}
			}
		});

		getTestDto1ServiceEb().deleteByIdOptimistic(v1.getId(), v1.getModifiedAt());
		assertEquals(1, flag.get());
	}

	@Test
	public void testEventBus_expectEventOnDeleteByIdOptimisticFail() throws Exception {
		final TestDto1 v0 = new TestDto1();
		v0.setActive(true);
		v0.setEnv("uat");
		v0.setLinkToFullDonwload("link-to-full-download");
		v0.setMajorVersion(2);
		v0.setMinorVersion(1);

		final TestDto1 v1 = getTestDto1ServiceEb().create(v0);

		final AtomicInteger flag = new AtomicInteger(0);
		eventBus.register(new Object() {
			@Subscribe
			public void handle(EntityChangedEvent<TestDto1> evt) {
				if (evt.getValue().getId().equals(v1.getId()) && evt.getChangeType() == ChangeType.REMOVED) {
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
	public void testCreateDto2() throws Exception {
		TestDto2 dto = new TestDto2();
		dto.setActive(true);
		dto.setEnv("uat1");
		dto.setLinkToFullDonwload("link-to-full-download1");
		dto.setMajorVersion(5);
		dto.setMinorVersion(6);

		TestDto2 result = getTestDto2Service().create(dto);

		assertNotNull(result);
		assertNotNull(result.getId());

		TestDto2 found = getTestDto2Service().findById(result.getId());
		assertNotNull(found);
		assertEquals("uat1", found.getEnv());
		assertEquals("link-to-full-download1", found.getLinkToFullDonwload());
		assertEquals(5, found.getMajorVersion());
		assertEquals(6, found.getMinorVersion());
	}

	@Test
	public void testFindByQueryString() throws Exception {
		createTestData();

		TestDto1 result = getTestDto1Service().findOneByQuery(Query.n().eq("env", "env-UAT"));
		assertNotNull(result);
		assertEquals("link-to-full-download123", result.getLinkToFullDonwload());

		result = getTestDto1Service().findOneByQuery(Query.n().eq("env", "env-PILOT"));
		assertNotNull(result);
		assertEquals("link-to-full-download456", result.getLinkToFullDonwload());
	}

	@Test
	public void testFindByQueryStringNe() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().ne("env", "env-uat"));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryContains() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().contains("env", "env-p"));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryNotContains() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().notContains("env", "env-P"));
		assertEquals(1, result.getTotalResults());
	}

	@Test
	public void testFindByQueryIn() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().in("env", "env-UAT", "env-pilot"));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryNotIn() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().notIn("env", "env-UAT", "env-pilot"));
		assertEquals(1, result.getTotalResults());
	}

	@Test
	public void testFindByQueryOr() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100), Query.n()
				.eq("majorVersion", 3L).or(Query.n().eq("minorVersion", 4L), Query.n().eq("minorVersion", 5L)));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryNumericNe() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().ne("majorVersion", 3));
		assertEquals(1, result.getTotalResults());
	}

	@Test
	public void testFindByQueryNumericGe() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().ge("majorVersion", 3));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryBetween() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().between("minorVersion", 4, 5));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryOutside() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().notBetween("minorVersion", 4, 5));
		assertEquals(1, result.getTotalResults());
	}

	@Test
	public void testFindByQueryNumberOneOf() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().in("minorVersion", 4L, 5L));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryNumberNotIn() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().notIn("minorVersion", 4L, 5L));
		assertEquals(1, result.getTotalResults());
	}

	@Test
	public void testFindByQueryIsNull() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().isNull("linkToPatchToNextVersion"));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryIsNotNull() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().isNotNull("linkToPatchToNextVersion"));
		assertEquals(1, result.getTotalResults());
	}

	@Test
	public void testFindByQueryBooleanFalse() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().isFalse("active"));
		assertEquals(1, result.getTotalResults());

		result = getTestDto1Service().find(new PagerParams(0, 100), Query.n().ne("active", true));
		assertEquals(1, result.getTotalResults());
	}

	@Test
	public void testFindByQueryBooleanTrue() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100), Query.n().isTrue("active"));
		assertEquals(2, result.getTotalResults());

		result = getTestDto1Service().find(new PagerParams(0, 100), Query.n().ne("active", false));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testFindByQueryStringLengthBetween() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().lengthBetween("env", 6, 7));
		assertEquals(2, result.getTotalResults());
	}

	@Test
	public void testDeleteByQuery() throws Exception {
		createTestData();

		int result = getTestDto1Service().deleteByQuery(Query.n().lengthBetween("env", 6, 7));
		assertEquals(2, result);
	}

	@Test
	public void testEventBus_expectEventOnDeleteByQuery() throws Exception {
		createTestData();

		String[] idsArr = new String[] { "env-uat", "env-prd" };
		final List<String> envs = new ArrayList<String>(Arrays.asList(idsArr));
		final AtomicInteger flag = new AtomicInteger(0);
		eventBus.register(new Object() {
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

		int affected = getTestDto1ServiceEb().deleteByQuery(Query.n().in("env", idsArr));
		assertEquals(2, affected);
		assertEquals(2, flag.get());
	}

	@Test
	public void testFindOneByQuery_expectNullForNotFound() throws Exception {
		createTestData();

		TestDto1 result = getTestDto1Service().findOneByQuery(Query.n().lengthBetween("env", 1, 2));
		assertNull(result);
	}

	@Test
	public void testQuery_expectPaginationWorksCorrectly() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 2),
				Query.n().contains("env", "env-"));
		assertEquals(3, result.getTotalResults());
		assertEquals(2, result.getItems().size());
	}

	@Test
	public void testQuery_expectOrderingWorksCorrectly() throws Exception {
		createTestData();

		PaginatedList<TestDto1> result = getTestDto1Service().find(new PagerParams(0, 100),
				Query.n().contains("env", "env-"), OrderBy.Asc("minorVersion"));
		assertEquals("env-prd", result.getItems().get(2).getEnv());
		assertEquals("env-pilot", result.getItems().get(1).getEnv());
		assertEquals("env-uat", result.getItems().get(0).getEnv());

		result = getTestDto1Service().find(new PagerParams(0, 100), Query.n().contains("env", "env-"),
				OrderBy.Desc("minorVersion"));
		assertEquals("env-prd", result.getItems().get(0).getEnv());
		assertEquals("env-pilot", result.getItems().get(1).getEnv());
		assertEquals("env-uat", result.getItems().get(2).getEnv());
	}

	@Test
	public void testFindById_expectNullForNotFound() throws Exception {
		createTestData();

		TestDto1 result = getTestDto1Service().findById("asdasdasd");
		assertNull(result);
	}

	@Test
	public void testUpdate_expectOk() throws Exception {
		createTestData();

		TestDto1 r1 = getTestDto1Service().findOneByQuery(Query.n().eq("env", "env-uat"));
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
	public void testEventBus_expectEventOnUpdate() throws Exception {
		final TestDto1 v1 = new TestDto1();
		v1.setActive(true);
		v1.setEnv("uat");
		v1.setLinkToFullDonwload("link-to-full-download");
		v1.setMajorVersion(2);
		v1.setMinorVersion(1);

		final AtomicInteger flag = new AtomicInteger(0);
		eventBus.register(new Object() {
			@Subscribe
			public void handle(EntityChangedEvent<TestDto1> evt) {
				if (evt.getValue().getEnv().equals("uat") && evt.getChangeType() == ChangeType.UPDATED) {
					flag.incrementAndGet();
				}
			}
		});

		TestDto1 result = getTestDto1ServiceEb().create(v1);
		result.setLinkToFullDonwload("asdasdasd");
		getTestDto1ServiceEb().update(result);
		assertEquals(1, flag.get());
	}

	@Test
	public void testUpdate_expectNotChangeableColumnsAreNotChanged() throws Exception {
		TestDto1 dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("env-uat");
		dto.setLinkToFullDonwload("link-to-full-download123");
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

	private void createTestData() throws ValidationException, NotAuthorizedException {
		TestDto1 dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("env-uat");
		dto.setLinkToFullDonwload("link-to-full-download123");
		dto.setMajorVersion(1);
		dto.setMinorVersion(2);
		getTestDto1Service().create(dto);

		dto = new TestDto1();
		dto.setActive(true);
		dto.setEnv("env-pilot");
		dto.setLinkToFullDonwload("link-to-full-download456");
		dto.setMajorVersion(3);
		dto.setMinorVersion(4);
		getTestDto1Service().create(dto);

		dto = new TestDto1();
		dto.setActive(false);
		dto.setEnv("env-prd");
		dto.setLinkToFullDonwload("link-to-full-download456");
		dto.setLinkToPatchToNextVersion("link-to-patch");
		dto.setMajorVersion(3);
		dto.setMinorVersion(5);
		getTestDto1Service().create(dto);
	}

}
