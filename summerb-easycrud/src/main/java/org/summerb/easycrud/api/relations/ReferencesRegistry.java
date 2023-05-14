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
package org.summerb.easycrud.api.relations;

import java.util.Collection;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.relations.Ref;

/**
 * This interface provides information regarding references structure. It's used
 * mostly for resolving instances of referenced objects.
 * 
 * @author sergeyk
 *
 */
public interface ReferencesRegistry {
	Ref getRefByName(String name) throws IllegalArgumentException;

	/**
	 * @param sourceEntityName set {@link EasyCrudService#getRowMessageCode()}
	 * 
	 * @return all references originating from provided table
	 */
	Collection<Ref> findRefsFromSource(String sourceEntityName);

	Collection<Ref> getRefsByAlias(String refsAlias) throws IllegalArgumentException;
}
