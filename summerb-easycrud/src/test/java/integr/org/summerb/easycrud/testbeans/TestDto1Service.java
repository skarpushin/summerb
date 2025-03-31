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
package integr.org.summerb.easycrud.testbeans;

import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestDto2;
import java.util.List;
import java.util.Set;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.scaffold.api.Query;

public interface TestDto1Service extends EasyCrudService<String, TestDto1> {

  @Query("SELECT max(env) FROM forms_test_1 WHERE major_version >= :majorVersion")
  String getEnvMaxWithScalarParam(int majorVersion);

  @Query("SELECT count(env) FROM forms_test_1")
  int getEnvCountNoParams();

  @Query("SELECT env FROM forms_test_1 WHERE major_version IN (:majorVersions) ORDER BY env ASC")
  List<String> getEnvsWithArray(int[] majorVersions);

  @Query("SELECT env FROM forms_test_1 WHERE major_version IN (:majorVersions) ORDER BY env ASC")
  List<String> getEnvsWithSet(Set<Integer> majorVersions);

  @Query("SELECT * FROM forms_test_1 WHERE major_version IN (:majorVersions) ORDER BY env ASC")
  List<TestDto1> getDtosWithSet(Set<Integer> majorVersions);

  @Query(
      value = "SELECT * FROM forms_test_1 WHERE major_version IN (:majorVersions) ORDER BY env ASC",
      rowMapper = CustomMapperToTestDto2.class)
  List<TestDto2> getDtosWithSetAndCustomMapper(Set<Integer> majorVersions);

  @Query(
      modifying = true,
      value = "UPDATE forms_test_1 SET link_to_full_download = :linkToFullDownload WHERE id = :id")
  void updateReturnVoid(String id, String linkToFullDownload);

  @Query(
      modifying = true,
      value = "UPDATE forms_test_1 SET link_to_full_download = :linkToFullDownload WHERE id = :id")
  int updateReturnInt(String id, String linkToFullDownload);

  @Query(
      modifying = true,
      value = "UPDATE forms_test_1 SET link_to_full_download = :linkToFullDownload WHERE id = :id")
  Integer updateReturnIntBoxed(String id, String linkToFullDownload);

  @Query(
      modifying = true,
      value = "UPDATE forms_test_1 SET link_to_full_download = :linkToFullDownload WHERE id = :id")
  String updateReturnWrongReturnType(String id, String linkToFullDownload);

  default TestDto1 getUsingDefault(int majorVersion) {
    return query()
        .ge(TestDto1::getMajorVersion, majorVersion)
        .findFirst(orderBy(TestDto1::getMajorVersion).desc());
  }
}
