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

import integr.org.summerb.easycrud.dtos.UserRow;
import java.util.List;
import java.util.Set;
import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.scaffold.SqlQuery;

public interface UserRowService extends EasyCrudService<String, UserRow> {
  String TERM = "users_table";

  @SqlQuery("SELECT max(name) FROM users_table WHERE karma >= :karma")
  String getNameMaxWithScalarParam(int karma);

  @SqlQuery("SELECT count(name) FROM users_table")
  int getNameCountNoParams();

  @SqlQuery("SELECT name FROM users_table WHERE karma IN (:karmas) ORDER BY name ASC")
  List<String> getNamesWithArray(int[] karmas);

  @SqlQuery("SELECT name FROM users_table WHERE karma IN (:karmas) ORDER BY name ASC")
  Set<String> getNamesWithArrayAsSet(int[] karmas);

  @SqlQuery("SELECT name FROM users_table WHERE karma IN (:karmas) ORDER BY name ASC")
  List<String> getNamesWithSet(Set<Integer> karmas);

  @SqlQuery("SELECT * FROM users_table WHERE karma IN (:karmas) ORDER BY name ASC")
  List<UserRow> getDtosWithSet(Set<Integer> karmas);

  @SqlQuery(
      value = "SELECT * FROM users_table WHERE karma IN (:karmas) ORDER BY name ASC",
      rowMapper = UserRowCustomMapper.class)
  List<UserRow> getDtosWithSetAndCustomMapper(Set<Integer> karmas);

  @SqlQuery(modifying = true, value = "UPDATE users_table SET about = :about WHERE id = :id")
  void updateReturnVoid(String id, String about);

  @SqlQuery(modifying = true, value = "UPDATE users_table SET about = :about WHERE id = :id")
  int updateReturnInt(String id, String about);

  @SqlQuery(modifying = true, value = "UPDATE users_table SET about = :about WHERE id = :id")
  Integer updateReturnIntBoxed(String id, String about);

  @SqlQuery(modifying = true, value = "UPDATE users_table SET about = :about WHERE id = :id")
  String updateReturnWrongReturnType(String id, String about);

  default UserRow getUsingDefault(int karma) {
    return query().ge(UserRow::getKarma, karma).findFirst(orderBy(UserRow::getKarma).desc());
  }
}
