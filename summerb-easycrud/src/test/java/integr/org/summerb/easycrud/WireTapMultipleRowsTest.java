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

import static integr.org.summerb.easycrud.QueryTest.buildRow;
import static org.junit.jupiter.api.Assertions.*;

import integr.org.summerb.easycrud.config.EasyCrudIntegrTestConfig;
import integr.org.summerb.easycrud.config.EmbeddedDbConfig;
import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.testbeans.TestDto1Service;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapAbstract;
import org.summerb.easycrud.scaffold.api.EasyCrudScaffold;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDbConfig.class, EasyCrudIntegrTestConfig.class})
@ProfileValueSourceConfiguration()
@Transactional
@AutoConfigureEmbeddedDatabase(type = DatabaseType.MARIADB, refresh = RefreshMode.AFTER_CLASS)
public class WireTapMultipleRowsTest {
  @Autowired private EasyCrudScaffold easyCrudScaffold;

  @Test
  public void testExpectAfterReadWillNotBeCalledTwicePetRow() {
    // GIVEN
    WireTapTest<TestDto1> wireTap =
        new WireTapTest<>(false) {
          @Override
          public boolean requiresOnRead() {
            return true;
          }
        };

    TestDto1Service service =
        easyCrudScaffold.fromService(
            TestDto1Service.class, TestDto1.class.getCanonicalName(), "forms_test_1", wireTap);

    service.create(buildRow("env1", 30));
    service.create(buildRow("env2", 20));
    service.create(buildRow("env3", 10));

    // WHEN - query multiple
    List<TestDto1> resultList =
        service
            .query()
            .ge(TestDto1::getMajorVersion, 20)
            .findAll(service.orderBy(TestDto1::getEnv).asc());

    // THEN - query multiple
    assertEquals(2, resultList.size());
    assertEquals(2, wireTap.afterReadIndividual.size());
    assertEquals("env1", wireTap.afterReadIndividual.get(0).getEnv());
    assertEquals("env2", wireTap.afterReadIndividual.get(1).getEnv());
    assertTrue(wireTap.afterReadMultiple.isEmpty());

    // WHEN - query single
    wireTap.afterReadIndividual.clear();
    TestDto1 resultOne = service.query().eq(TestDto1::getMajorVersion, 10).findOne();

    // THEN - query single
    assertNotNull(resultOne);
    assertTrue(wireTap.afterReadMultiple.isEmpty());
    assertEquals(1, wireTap.afterReadIndividual.size());
    assertEquals("env3", wireTap.afterReadIndividual.get(0).getEnv());
  }

  @Test
  public void testExpectAfterReadWillBeCalledSeparatelyForIndividualAndMultipleRows() {
    // GIVEN
    WireTapTest<TestDto1> wireTap =
        new WireTapTest<>(true) {
          @Override
          public boolean requiresOnReadMultiple() {
            return true;
          }

          @Override
          public boolean requiresOnRead() {
            return true;
          }
        };

    TestDto1Service service =
        easyCrudScaffold.fromService(
            TestDto1Service.class, TestDto1.class.getCanonicalName(), "forms_test_1", wireTap);

    service.create(buildRow("env1", 30));
    service.create(buildRow("env2", 20));
    service.create(buildRow("env3", 10));

    // WHEN - query multiple
    List<TestDto1> resultList =
        service
            .query()
            .ge(TestDto1::getMajorVersion, 20)
            .findAll(service.orderBy(TestDto1::getEnv).asc());

    // THEN - query multiple
    assertEquals(2, resultList.size());
    assertEquals(2, wireTap.afterReadMultiple.size());
    assertEquals("env1", wireTap.afterReadMultiple.get(0).getEnv());
    assertEquals("env2", wireTap.afterReadMultiple.get(1).getEnv());
    assertTrue(wireTap.afterReadIndividual.isEmpty());

    // WHEN - query single
    wireTap.afterReadMultiple.clear();
    TestDto1 resultOne = service.query().eq(TestDto1::getMajorVersion, 10).findOne();

    // THEN - query single
    assertNotNull(resultOne);
    assertTrue(wireTap.afterReadMultiple.isEmpty());
    assertEquals(1, wireTap.afterReadIndividual.size());
    assertEquals("env3", wireTap.afterReadIndividual.get(0).getEnv());
  }

  @Test
  public void testExpectScaffoldedMethodWillAlsoResultInMultipleReadWireTapCall() {
    // GIVEN
    WireTapTest<TestDto1> wireTap =
        new WireTapTest<>(true) {
          @Override
          public boolean requiresOnReadMultiple() {
            return true;
          }
        };

    TestDto1Service service =
        easyCrudScaffold.fromService(
            TestDto1Service.class, TestDto1.class.getCanonicalName(), "forms_test_1", wireTap);

    service.create(buildRow("env1", 30));
    service.create(buildRow("env2", 20));
    service.create(buildRow("env3", 10));

    // WHEN - query multiple
    List<TestDto1> resultList = service.getDtosWithSet(Set.of(10, 30));

    // THEN - query multiple
    assertEquals(2, resultList.size());
    assertEquals(2, wireTap.afterReadMultiple.size());
    assertEquals("env1", wireTap.afterReadMultiple.get(0).getEnv());
    assertEquals("env3", wireTap.afterReadMultiple.get(1).getEnv());
    assertTrue(wireTap.afterReadIndividual.isEmpty());
  }

  private static class WireTapTest<T> extends EasyCrudWireTapAbstract<T> {
    public List<T> afterReadMultiple = new LinkedList<>();
    public List<T> afterReadIndividual = new LinkedList<>();

    private final boolean overrideAfterReadMultiple;

    private WireTapTest(boolean overrideAfterReadMultiple) {
      this.overrideAfterReadMultiple = overrideAfterReadMultiple;
    }

    @Override
    public void afterRead(List<T> rows) {
      if (overrideAfterReadMultiple) {
        afterReadMultiple.addAll(rows);
      } else {
        super.afterRead(rows);
      }
    }

    @Override
    public void afterRead(T row) {
      afterReadIndividual.add(row);
    }
  }
}
