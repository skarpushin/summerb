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
import java.util.Set;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.datapackage.DataSet;
import org.summerb.easycrud.api.row.relations.Ref;

/**
 * Interface used to load Dom object trees. This might be more convenient than dealing with {@link
 * DataSet} and {@link DataSetLoader}, but it will require creation of classes which will represent
 * domain object model.
 *
 * <p>See example: TBD (write article on wiki and put link here)
 *
 * <p>High-level pre-requisites:
 *
 * <ul>
 *   <li>for each Dom entity you need to create class
 *   <li>class for Dom entity must extend Row class
 *   <li>Dom entity can have fields to hold referenced Dom entities (could be direct reference to 1
 *       instance or it could be a list)
 *   <li>Name of the field (references) is used to calculate reference name. If class "Env" contains
 *       field "List&lt;Device&gt; devices", then it's expected to have reference with name
 *       "envDevices"
 * </ul>
 *
 * @author sergeyk
 */
public interface DomLoader {

  <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> TDomClass load(
      Class<TDomClass> rootDomClass, TId rootDtoId, Ref... refsToResolve);

  <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> List<TDomClass> load(
      Class<TDomClass> rootDomClass, Set<TId> ids, Ref... refsToResolve);
}
