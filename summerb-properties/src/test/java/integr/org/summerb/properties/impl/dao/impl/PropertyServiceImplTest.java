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
package integr.org.summerb.properties.impl.dao.impl;

import static org.junit.jupiter.api.Assertions.*;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.api.exceptions.ServiceDataTruncationException;
import org.summerb.properties.PropertiesConfig;
import org.summerb.properties.api.PropertyService;
import org.summerb.properties.api.dto.NamedProperty;
import org.summerb.properties.internal.StringIdAliasService;
import org.summerb.properties.internal.StringIdAliasServiceVisibleForTesting;
import org.summerb.utils.exceptions.ExceptionUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, PropertiesConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class PropertyServiceImplTest {
  @Autowired protected PropertyService propertyService;
  @Autowired protected StringIdAliasService appAliasService;
  @Autowired protected StringIdAliasService domainAliasService;
  @Autowired protected StringIdAliasService propertyNameAliasService;

  @BeforeEach
  public void beforeEachTest() {
    ((StringIdAliasServiceVisibleForTesting) appAliasService).clearCache();
    ((StringIdAliasServiceVisibleForTesting) domainAliasService).clearCache();
    ((StringIdAliasServiceVisibleForTesting) propertyNameAliasService).clearCache();
  }

  @Test
  public void testPutSubjectProperty_blackbox_expectToFindPropertyWhichWasJustSet() {
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property", "value1");

    String result =
        propertyService.findSubjectProperty("test", "test.domain", "1", "some.property");
    assertEquals("value1", result);
  }

  @Test
  public void testPutSubjectProperty_blackbox_expectToFindExactlyTwoProperties() {
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property1", "value1");
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");
    propertyService.putSubjectProperty("test", "test.domain", "2", "some.property3", "value3");

    Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
    assertEquals(2, result.size());
  }

  @Test
  public void testPutSubjectProperty_whitebox_expectCorrectPropertyNames() {
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property1", "value1");
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");
    propertyService.putSubjectProperty("test", "test.domain", "2", "some.property1", "value3");

    Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
    assertTrue(result.containsKey("some.property1"));
    assertTrue(result.containsKey("some.property2"));
  }

  @Test
  public void testPutSubjectProperty_expectNullValuesHandledOk() {
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property1", "value1");
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", null);

    Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
    assertEquals("value1", result.get("some.property1"));
    assertNull(result.get("some.property2"));

    String value =
        propertyService.findSubjectProperty("test", "test.domain", "1", "some.property2");
    assertNull(value);
  }

  @Test
  public void testPutSubjectProperties_whitebox_expectCorrectPropertyNames() {
    List<NamedProperty> propList = new ArrayList<>();
    propList.add(new NamedProperty("some.property1", "value1"));
    propList.add(new NamedProperty("some.property2", "value2"));
    propertyService.putSubjectProperties("test", "test.domain", "1", propList);
    propertyService.putSubjectProperty("test", "test.domain", "2", "some.property1", "value3");

    Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
    assertTrue(result.containsKey("some.property1"));
    assertTrue(result.containsKey("some.property2"));
  }

  @Test
  public void testPutSubjectsProperty_whitebox_expectCorrectPropertyNames() {
    List<String> subjects = new ArrayList<>();
    subjects.add("1");
    subjects.add("2");
    propertyService.putSubjectsProperty(
        "test", "test.domain", subjects, "some.property1", "value3");
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

    Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
    assertTrue(result.containsKey("some.property1"));
    assertTrue(result.containsKey("some.property2"));
  }

  @Test
  public void testFindSubjectsProperties_whitebox_expectCorrectPropertyNames() {
    List<String> subjects = new ArrayList<>();
    subjects.add("1");
    subjects.add("2");
    propertyService.putSubjectsProperty(
        "test", "test.domain", subjects, "some.property1", "value3");
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

    Map<String, Map<String, String>> result =
        propertyService.findSubjectsProperties("test", "test.domain", subjects);
    assertTrue(result.containsKey("1"));
    assertTrue(result.containsKey("2"));

    assertEquals(2, result.get("1").size());
    assertEquals(1, result.get("2").size());
  }

  @Test
  public void testFindSubjectsProperty_whitebox_expectCorrectPropertyNames() {
    List<String> subjects = new ArrayList<>();
    subjects.add("1");
    subjects.add("2");
    propertyService.putSubjectsProperty(
        "test", "test.domain", subjects, "some.property1", "value3");
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

    Map<String, String> result =
        propertyService.findSubjectsProperty("test", "test.domain", subjects, "some.property2");
    assertEquals("value2", result.get("1"));
    assertEquals(1, result.size());
  }

  @Test
  public void testDeleteSubjectProperties_whitebox_expectCorrectPropertyNames() {
    propertyService.putSubjectProperty("test", "test.domain", "1", "some.property2", "value2");

    propertyService.deleteSubjectProperties("test", "test.domain", "1");

    Map<String, String> result = propertyService.findSubjectProperties("test", "test.domain", "1");
    assertEquals(0, result.size());
  }

  @Test
  public void testDeleteSubjectsProperties_whitebox_expectCorrectPropertyNames() {
    List<String> subjects = new ArrayList<>();
    subjects.add("1");
    subjects.add("2");
    propertyService.putSubjectsProperty(
        "test", "test.domain", subjects, "some.property1", "value3");

    propertyService.deleteSubjectsProperties("test", "test.domain", subjects);

    Map<String, Map<String, String>> result =
        propertyService.findSubjectsProperties("test", "test.domain", subjects);
    assertEquals(0, result.size());
  }

  @Test
  public void testPutProperty_expectTruncationExceptionForPropertyName() {
    String propertyName = "some.property1";

    try {
      String value = generateLongString(2048, "прол");
      propertyService.putSubjectProperty("test", "test.domain", "AAA", propertyName, value);
    } catch (Throwable t) {
      ServiceDataTruncationException exc =
          ExceptionUtils.findExceptionOfType(t, ServiceDataTruncationException.class);

      assertNotNull(exc);
      assertEquals(propertyName, exc.getFieldTokenBeingTruncated());
      return;
    }

    fail("Should throw exception");
  }

  @Test
  public void testPutProperty_expectTruncationExceptionForPropertyNameForMultipleProperties() {
    String propertyName = "some.property1";

    List<NamedProperty> props = new ArrayList<>();
    props.add(new NamedProperty("n1", "vvv1"));
    props.add(new NamedProperty(propertyName, generateLongString(256, "прол")));
    props.add(new NamedProperty("n2", "vvv2"));

    try {
      propertyService.putSubjectProperties("test", "test.domain", "AAA", props);
    } catch (Throwable t) {
      ServiceDataTruncationException exc =
          ExceptionUtils.findExceptionOfType(t, ServiceDataTruncationException.class);

      assertNotNull(exc);
      assertEquals(propertyName, exc.getFieldTokenBeingTruncated());
    }
  }

  protected String generateLongString(int desiredStringLength, String exampleChars) {
    StringBuilder ret = new StringBuilder(desiredStringLength + exampleChars.length());

    while (ret.length() < desiredStringLength) {
      ret.append(exampleChars);
    }

    ret.setLength(desiredStringLength);

    return ret.toString();
  }
}
