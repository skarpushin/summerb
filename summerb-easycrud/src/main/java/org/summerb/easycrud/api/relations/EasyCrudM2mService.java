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
package org.summerb.easycrud.api.relations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.relations.ManyToManyRow;

/**
 * Service for m2m references. It's based on {@link EasyCrudService}, but it's more for
 * compatibility reasons. Primarily methods of this interface should be used.
 *
 * <p>it's assumed that there are many referencers, but few referencee
 *
 * @author sergeyk
 * @param <T1Id> id of referencer
 * @param <T1Row> referencer. Dto that suppose to reference dictionary objects
 * @param <T2Id> id of referencee
 * @param <T2Row> referencee row. The one is referenced by many referencers
 */
public interface EasyCrudM2mService<
        T1Id, T1Row extends HasId<T1Id>, T2Id, T2Row extends HasId<T2Id>>
    extends EasyCrudService<Long, ManyToManyRow<T1Id, T2Id>> {

  List<T2Row> findReferenceeByReferencer(T1Id referencerId);

  /**
   * @param referencerIds referencer ids
   * @return mapping between referencer id and list of referencee
   */
  Map<T1Id, List<T2Row>> findReferenceeByReferencers(Set<T1Id> referencerIds);

  ManyToManyRow<T1Id, T2Id> addReferencee(T1Id referencerId, T2Id referenceeId);

  void removeReferencee(T1Id referencerId, T2Id referenceeId);
}
