/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.common.DaoExceptionUtils;
import org.summerb.properties.api.PropertyService;
import org.summerb.properties.api.dto.NamedProperty;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-properties-app-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class PropertyServiceImplTest {
	@Autowired
	private PropertyService propertyService;

	@BeforeTransaction
	public void verifyInitialDatabaseState() {
		// logic to verify the initial state before a transaction is started
	}

	@Before
	public void setUp() {
		// set up test data within the transaction
	}

	@Test
	public void testPutSubjectProperty_blackbox_expectToFindPropertyWhichWasJustSet() throws Exception {
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property", "value1");

		String result = propertyService.findSubjectProperty("test", "test.domain", "1", "some.property");
		assertEquals("value1", result);
	}

	@Test
	public void testPutSubjectProperty_blackbox_expectToFindExactlyTwoProperties() throws Exception {
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property1", "value1");
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");
		propertyService.putSubjectProperty("test", "test.domain", "2", "some.property3", "value3");

		Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
		assertEquals(2, result.size());
	}

	@Test
	public void testPutSubjectProperty_whitebox_expectCorretPropertyNames() throws Exception {
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property1", "value1");
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");
		propertyService.putSubjectProperty("test", "test.domain", "2", "some.property1", "value3");

		Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
		assertTrue(result.containsKey("some.property1"));
		assertTrue(result.containsKey("some.property2"));
	}

	@Test
	public void testPutSubjectProperty_expectNullValuesHandledOk() throws Exception {
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property1", "value1");
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", null);

		Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
		assertEquals("value1", result.get("some.property1"));
		assertNull(result.get("some.property2"));

		String value = propertyService.findSubjectProperty("test", "test.domain", "1", "some.property2");
		assertNull(value);
	}

	@Test
	public void testPutSubjectProperties_whitebox_expectCorretPropertyNames() throws Exception {
		List<NamedProperty> propList = new ArrayList<NamedProperty>();
		propList.add(new NamedProperty("some.property1", "value1"));
		propList.add(new NamedProperty("some.property2", "value2"));
		propertyService.putSubjectProperties("test", "test.domain", "1", propList);
		propertyService.putSubjectProperty("test", "test.domain", "2", "some.property1", "value3");

		Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
		assertTrue(result.containsKey("some.property1"));
		assertTrue(result.containsKey("some.property2"));
	}

	@Test
	public void testPutSubjectsProperty_whitebox_expectCorretPropertyNames() throws Exception {
		List<String> subjects = new ArrayList<String>();
		subjects.add("1");
		subjects.add("2");
		propertyService.putSubjectsProperty("test", "test.domain", subjects, "some.property1", "value3");
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

		Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
		assertTrue(result.containsKey("some.property1"));
		assertTrue(result.containsKey("some.property2"));
	}

	@Test
	public void testFindSubjectsProperties_whitebox_expectCorretPropertyNames() throws Exception {
		List<String> subjects = new ArrayList<String>();
		subjects.add("1");
		subjects.add("2");
		propertyService.putSubjectsProperty("test", "test.domain", subjects, "some.property1", "value3");
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

		Map<String, Map<String, String>> result = propertyService.findSubjectsProperties("test", "test.domain",
				subjects);
		assertTrue(result.containsKey("1"));
		assertTrue(result.containsKey("2"));

		assertEquals(2, result.get("1").size());
		assertEquals(1, result.get("2").size());
	}

	@Test
	public void testFindSubjectsProperty_whitebox_expectCorretPropertyNames() throws Exception {
		List<String> subjects = new ArrayList<String>();
		subjects.add("1");
		subjects.add("2");
		propertyService.putSubjectsProperty("test", "test.domain", subjects, "some.property1", "value3");
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

		Map<String, String> result = propertyService.findSubjectsProperty("test", "test.domain", subjects,
				"some.property2");
		assertEquals("value2", result.get("1"));
		assertEquals(1, result.size());
	}

	@Test
	public void testDeleteSubjectProperties_whitebox_expectCorretPropertyNames() throws Exception {
		propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

		propertyService.deleteSubjectProperties("test", "test.domain", "1");

		Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
		assertEquals(0, result.size());
	}

	@Test
	public void testDeleteSubjectsProperties_whitebox_expectCorretPropertyNames() throws Exception {
		List<String> subjects = new ArrayList<String>();
		subjects.add("1");
		subjects.add("2");
		propertyService.putSubjectsProperty("test", "test.domain", subjects, "some.property1", "value3");

		propertyService.deleteSubjectsProperties("test", "test.domain", subjects);

		Map<String, Map<String, String>> result = propertyService.findSubjectsProperties("test", "test.domain",
				subjects);
		assertEquals(0, result.size());
	}

	@Test
	public void testPutProperty_expectTruncationExceptionForPropertyName() {
		String propertyName = "some.property1";

		try {
			String value = generateLongString(256, "прол");
			propertyService.putSubjectProperty("test", "test.domain", "AAA", propertyName, value);
			fail("Should throw exception");
		} catch (Throwable t) {
			String foundName = DaoExceptionUtils.findTruncatedFieldNameIfAny(t);
			assertEquals(propertyName, foundName);
		}
	}

	@Test
	public void testPutProperty_expectTruncationExceptionForPropertyNameForMultipleProperties() {
		String propertyName = "some.property1";

		List<NamedProperty> props = new ArrayList<NamedProperty>();
		props.add(new NamedProperty("n1", "vvv1"));
		props.add(new NamedProperty(propertyName, generateLongString(256, "прол")));
		props.add(new NamedProperty("n2", "vvv2"));

		try {
			propertyService.putSubjectProperties("test", "test.domain", "AAA", props);
		} catch (Throwable t) {
			String foundName = DaoExceptionUtils.findTruncatedFieldNameIfAny(t);
			assertEquals(propertyName, foundName);
		}
	}

	private String generateLongString(int desiredStringLength, String exampleChars) {
		StringBuilder ret = new StringBuilder(desiredStringLength + exampleChars.length());

		while (ret.length() < desiredStringLength) {
			ret.append(exampleChars);
		}

		ret.setLength(desiredStringLength);

		return ret.toString();
	}

}
