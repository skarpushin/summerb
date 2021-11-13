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
package org.summerb.easycrud.api;

import org.summerb.easycrud.api.relations.DataSetLoader;

/**
 * Interface used to resolve services by
 * {@link EasyCrudService#getEntityTypeMessageCode()} or
 * {@link EasyCrudService#getDtoClass()}
 * 
 * Supposed to be used mostly by {@link DataSetLoader}
 * 
 * @author sergeyk
 *
 */
public interface EasyCrudServiceResolver {
	@SuppressWarnings("rawtypes")
	EasyCrudService resolveByEntityType(String entityName);

	@SuppressWarnings("rawtypes")
	EasyCrudService resolveByDtoClass(Class<?> entityClass);
}
