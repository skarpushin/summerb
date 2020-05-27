/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.datapackage.DataSet;
import org.summerb.easycrud.api.dto.relations.Ref;
import org.summerb.easycrud.api.dto.relations.RefQuantity;
import org.summerb.easycrud.api.dto.relations.RelationType;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.relations.DataSetLoader;
import org.summerb.easycrud.api.relations.EasyCrudM2mService;
import org.summerb.easycrud.api.relations.ReferencesRegistry;
import org.summerb.easycrud.impl.relations.DataSetLoaderImpl;
import org.summerb.easycrud.impl.relations.ReferencesRegistryPredefinedImpl;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:summerb-integr-test-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
@SuppressWarnings("unchecked")
public class DataSetLoaderTest {
	@Autowired
	protected EasyCrudServiceResolver easyCrudServiceResolver;

	@Autowired
	@Qualifier("testDto1Service")
	private EasyCrudService<String, TestDto1> testDto1Service;

	@Autowired
	@Qualifier("testDto2Service")
	private EasyCrudService<Long, TestDto2> testDto2Service;

	@Autowired
	@Qualifier("testDto3Service")
	private EasyCrudService<Long, TestDto3> testDto3Service;

	@Autowired
	@Qualifier("m2mService")
	private EasyCrudM2mService<Long, TestDto2, String, TestDto3> m2mService;

	@Test
	public void testLoadObjectsByIds_ExpectCoupleOneLevelObjectsLoadedOK()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("d2i1");
		d2i1.setLinkToFullDonwload("asdad");
		d2i1 = testDto2Service.create(d2i1);

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(d2i1.getId());
		d3i1.setLinkToDtoTwo(0);
		d3i1 = testDto3Service.create(d3i1);

		DataSet result = new DataSet();
		Map<String, Set<Object>> idsToLoad = new HashMap<String, Set<Object>>();
		idsToLoad.put(testDto2Service.getEntityTypeMessageCode(), ids(d2i1.getId()));
		idsToLoad.put(testDto3Service.getEntityTypeMessageCode(), ids(d3i1.getId()));
		loader.loadObjectsByIds(idsToLoad, result);

		assertNotNull(result.get(testDto2Service.getEntityTypeMessageCode()).find(d2i1.getId()));
		assertNotNull(result.get(testDto3Service.getEntityTypeMessageCode()).find(d3i1.getId()));
	}

	@Test
	public void testLoadObjectsByIds_ExpectTwoObjsOfSameTypeLoadedOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("d2i1");
		d2i1.setLinkToFullDonwload("asdad");
		d2i1 = testDto2Service.create(d2i1);

		TestDto2 d2i2 = new TestDto2();
		d2i2.setEnv("d2i2");
		d2i2.setLinkToFullDonwload("asdad");
		d2i2 = testDto2Service.create(d2i2);

		List<HasId> result = loader.loadObjectsByIds(ids(d2i1, d2i2), testDto2Service.getEntityTypeMessageCode());
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	public void testLoadObjectsByIds_ExpectOneLoadedOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("d2i1");
		d2i1.setLinkToFullDonwload("asdad");
		d2i1 = testDto2Service.create(d2i1);

		List<HasId> result = loader.loadObjectsByIds(ids(d2i1), testDto2Service.getEntityTypeMessageCode());
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testResolveReferencedObjects_ExpectWillNotTryToLoadNullReferences()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("required");
		d2i1.setLinkToFullDonwload("required");
		d2i1 = testDto2Service.create(d2i1);

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(d2i1.getId());
		d3i1 = testDto3Service.create(d3i1);

		DataSet result = new DataSet();
		Map<String, Set<Object>> idsToLoad = new HashMap<String, Set<Object>>();
		idsToLoad.put(testDto3Service.getEntityTypeMessageCode(), ids(d3i1.getId()));
		loader.loadObjectsByIds(idsToLoad, result);

		loader.resolveReferencedObjects(result, Refs.ref3to2mand, Refs.ref3to1);

		assertNotNull(result.get(testDto2Service.getEntityTypeMessageCode()).find(d2i1.getId()));
	}

	@Test
	public void testResolveReferencedObjects_ExpectDirectReferencesLoadedOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto1 d1i1 = new TestDto1();
		d1i1.setEnv("d1i1");
		d1i1.setLinkToFullDonwload("required");
		d1i1 = testDto1Service.create(d1i1);

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv(d1i1.getId());
		d2i1.setLinkToFullDonwload("required");
		d2i1 = testDto2Service.create(d2i1);

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(d2i1.getId());
		d3i1 = testDto3Service.create(d3i1);

		DataSet result = new DataSet();
		Map<String, Set<Object>> idsToLoad = new HashMap<String, Set<Object>>();
		idsToLoad.put(testDto3Service.getEntityTypeMessageCode(), ids(d3i1.getId()));
		loader.loadObjectsByIds(idsToLoad, result);

		loader.resolveReferencedObjects(result, Refs.ref3to2mand, Refs.ref2to1);

		assertNotNull(result.get(testDto2Service.getEntityTypeMessageCode()).find(d2i1.getId()));
		assertNotNull(result.get(testDto1Service.getEntityTypeMessageCode()).find(d1i1.getId()));
	}

	@Test
	public void testResolveReferencedObjects_ExpectOneToManyDirectsLoadedOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("required");
		d2i1.setLinkToFullDonwload("required");
		d2i1 = testDto2Service.create(d2i1);

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(d2i1.getId());
		d3i1 = testDto3Service.create(d3i1);

		TestDto3 d3i2 = new TestDto3();
		d3i2.setLinkToDtoTwo(d2i1.getId());
		d3i2 = testDto3Service.create(d3i2);

		DataSet result = new DataSet();
		result.get(testDto2Service.getEntityTypeMessageCode()).put(d2i1);

		loader.resolveReferencedObjects(result, Refs.ref3to2mand.reverse());

		assertNotNull(result.get(testDto3Service.getEntityTypeMessageCode()).find(d3i1.getId()));
		assertNotNull(result.get(testDto3Service.getEntityTypeMessageCode()).find(d3i2.getId()));
	}

	@Test
	public void testResolveReferencedObjects_ExpectOneToManyRecoursiveLoadedOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("required");
		d2i1.setLinkToFullDonwload("required");
		d2i1 = testDto2Service.create(d2i1);

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(d2i1.getId());
		d3i1 = testDto3Service.create(d3i1);

		TestDto1 d1i1 = new TestDto1();
		d1i1.setEnv("d1i1");
		d1i1.setLinkToFullDonwload("required");
		d1i1 = testDto1Service.create(d1i1);

		TestDto3 d3i2 = new TestDto3();
		d3i2.setLinkToDtoTwo(d2i1.getId());
		d3i2.setLinkToDtoOneOptional(d1i1.getId());
		d3i2 = testDto3Service.create(d3i2);

		DataSet result = new DataSet();
		result.get(testDto2Service.getEntityTypeMessageCode()).put(d2i1);

		loader.resolveReferencedObjects(result, Refs.ref3to2mand.reverse(), Refs.ref3to1);

		assertNotNull(result.get(testDto3Service.getEntityTypeMessageCode()).find(d3i1.getId()));
		assertNotNull(result.get(testDto3Service.getEntityTypeMessageCode()).find(d3i2.getId()));
		assertNotNull(result.get(testDto1Service.getEntityTypeMessageCode()).find(d1i1.getId()));
	}

	@Test
	public void testResolveReferencedObjects_ExpectOneToManyTwoParentsOneChildLoadedOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("required");
		d2i1.setLinkToFullDonwload("required");
		d2i1 = testDto2Service.create(d2i1);

		TestDto1 d1i1 = new TestDto1();
		d1i1.setEnv("required");
		d1i1.setLinkToFullDonwload("required");
		d1i1 = testDto1Service.create(d1i1);

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(d2i1.getId());
		d3i1 = testDto3Service.create(d3i1);

		TestDto3 d3i2 = new TestDto3();
		d3i2.setLinkToDtoTwo(d2i1.getId());
		d3i2.setLinkToDtoOneOptional(d1i1.getId());
		d3i2 = testDto3Service.create(d3i2);

		DataSet result = new DataSet();
		result.get(testDto2Service.getEntityTypeMessageCode()).put(d2i1);
		result.get(testDto1Service.getEntityTypeMessageCode()).put(d1i1);

		loader.resolveReferencedObjects(result, Refs.ref3to2mand.reverse(), Refs.ref3to1.reverse());

		assertNotNull(result.get(testDto3Service.getEntityTypeMessageCode()).find(d3i1.getId()));
		assertNotNull(result.get(testDto3Service.getEntityTypeMessageCode()).find(d3i2.getId()));
	}

	@Test
	public void testResolveReferencedObjects_ExpectBackReferencesLoadedOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("required");
		d2i1.setLinkToFullDonwload("required");
		d2i1 = testDto2Service.create(d2i1);

		TestDto1 d1i1 = new TestDto1();
		d1i1.setEnv("required");
		d1i1.setLinkToFullDonwload("required");
		d1i1 = testDto1Service.create(d1i1);

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(d2i1.getId());
		d3i1 = testDto3Service.create(d3i1);

		TestDto3 d3i2 = new TestDto3();
		d3i2.setLinkToDtoTwo(d2i1.getId());
		d3i2.setLinkToDtoOneOptional(d1i1.getId());
		d3i2 = testDto3Service.create(d3i2);

		String t1 = testDto1Service.getEntityTypeMessageCode();
		String t2 = testDto2Service.getEntityTypeMessageCode();
		String t3 = testDto3Service.getEntityTypeMessageCode();
		DataSet result = new DataSet();
		result.get(t2).put(d2i1);
		result.get(t1).put(d1i1);

		loader.resolveReferencedObjects(result, Refs.ref3to2mand.reverse(), Refs.ref3to1.reverse());

		// NOTE: This is ugly construction! We definitely need some sugar here
		assertEquals(1, result.get(t1).getBackRefs().getForRow(d1i1).getForRef(Refs.ref3to1.reverse()).size());
		assertEquals(2, result.get(t2).getBackRefs().getForRow(d2i1).getForRef(Refs.ref3to2mand.reverse()).size());

		assertNotNull(result.get(t3).find(d3i1.getId()));
		assertNotNull(result.get(t3).find(d3i2.getId()));
	}

	@Test
	public void testResolveReferencedObjects_ExpectM2mLoadedOkOk()
			throws EntityNotFoundException, NotAuthorizedException, FieldValidationException {
		DataSetLoader loader = buildLoaderCase1();

		TestDto3 d3i1 = new TestDto3();
		d3i1.setLinkToDtoTwo(1L); // non-existing link
		d3i1 = testDto3Service.create(d3i1);

		TestDto1 d1i1 = new TestDto1();
		d1i1.setEnv(d3i1.getId());
		d1i1.setLinkToFullDonwload("required");
		d1i1 = testDto1Service.create(d1i1);

		TestDto1 d1i2 = new TestDto1();
		d1i2.setEnv(d3i1.getId());
		d1i2.setLinkToFullDonwload("required");
		d1i2 = testDto1Service.create(d1i2);

		TestDto2 d2i1 = new TestDto2();
		d2i1.setEnv("required");
		d2i1.setLinkToFullDonwload("required");
		d2i1 = testDto2Service.create(d2i1);

		m2mService.addReferencee(d2i1.getId(), d1i1.getId());
		m2mService.addReferencee(d2i1.getId(), d1i2.getId());

		// -----------------------------------
		String t1 = testDto1Service.getEntityTypeMessageCode();
		String t2 = testDto2Service.getEntityTypeMessageCode();
		String t3 = testDto3Service.getEntityTypeMessageCode();
		DataSet result = new DataSet();
		loader.loadObjectAndItsRefs(d2i1.getId(), t2, result, Refs.ref2to1m2m, Refs.ref1to3);

		assertNotNull(result.get(t2).find(d2i1.getId()));
		assertNotNull(result.get(t1).find(d1i1.getId()));
		assertNotNull(result.get(t1).find(d1i2.getId()));
		assertNotNull(result.get(t3).find(d3i1.getId()));
		Set<Object> d2refsTo1 = result.get(t2).getBackRefs().getForRow(d2i1).getForRef(Refs.ref2to1m2m);
		assertEquals(2, d2refsTo1.size());
		assertTrue(d2refsTo1.contains(d1i1.getId()));
		assertTrue(d2refsTo1.contains(d1i2.getId()));
	}

	private Set<Object> ids(Object... pids) {
		Set<Object> ids = new HashSet<>(Arrays.asList(pids));
		return ids;
	}

	private Set<Object> ids(HasId... pids) {
		Set<Object> ids = new HashSet<>();
		for (HasId hasId : pids) {
			ids.add(hasId.getId());
		}
		return ids;
	}

	private DataSetLoader buildLoaderCase1() {
		DataSetLoaderImpl loader = new DataSetLoaderImpl();
		loader.setEasyCrudServiceResolver(easyCrudServiceResolver);
		loader.setReferencesRegistry(Refs.buildReferencesRegistry());
		return loader;
	}

	private static class Refs {
		private static Ref ref1to3;
		private static Ref ref2to1;
		private static Ref ref3to2mand;
		private static Ref ref3to2opt;
		private static Ref ref3to3;
		private static Ref ref3to1;
		private static Ref ref2to1m2m;

		private static ReferencesRegistry buildReferencesRegistry() {
			return new ReferencesRegistryPredefinedImpl(ref1to3, ref2to1, ref3to2mand, ref3to2opt, ref3to3, ref3to1,
					ref2to1m2m);
		}

		static {
			String et1 = "integr.org.summerb.easycrud.TestDto1";
			String et2 = "integr.org.summerb.easycrud.TestDto2";
			String et3 = "integr.org.summerb.easycrud.TestDto3";

			// 1.env to 3.id => many2one // check one round trip to Dto2
			ref1to3 = new Ref("1to3", et1, "env", et3, "id", RelationType.References, RefQuantity.Many2One);
			// 2.env to 3.id => many2one // check one round trip to Dto2
			ref2to1 = new Ref("2to1", et2, "env", et1, "id", RelationType.References, RefQuantity.Many2One);
			// 2.linkToFullDonwload to 1.id => many2many
			ref2to1m2m = new Ref("2to1m2m", et2, "linkToFullDonwload", et1, "id", RelationType.References,
					RefQuantity.Many2Many);
			// 3.linkToDto2 to 2.id => many2one // check subsequent refs
			// resolution
			ref3to2mand = new Ref("3to2mand", et3, "linkToDtoTwo", et2, "id", RelationType.References,
					RefQuantity.Many2One);
			// 3.linkToDto2Optional to 2.id => one2one // check subsequent refs
			// resolution
			ref3to2opt = new Ref("3to2opt", et3, "linkToDtoTwoOptional", et2, "id", RelationType.References,
					RefQuantity.One2One);
			// 3.linkToSelfOptional to 3.id => one2one // check links to self
			ref3to3 = new Ref("3to3", et3, "linkToSelfOptional", et2, "id", RelationType.References,
					RefQuantity.Many2One);
			// 3.linkToDto1Optional to 1.id => many2one // check cyclic
			// references
			ref3to1 = new Ref("3to1", et3, "linkToDtoOneOptional", et1, "id", RelationType.References,
					RefQuantity.Many2One);
		}
	}
}
