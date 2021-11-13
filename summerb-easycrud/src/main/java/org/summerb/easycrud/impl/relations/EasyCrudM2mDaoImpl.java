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
package org.summerb.easycrud.impl.relations;

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.relations.ManyToManyDto;
import org.summerb.easycrud.impl.mysql.EasyCrudDaoMySqlImpl;

public class EasyCrudM2mDaoImpl<T1Id, T1Dto extends HasId<T1Id>, T2Id, T2Dto extends HasId<T2Id>>
		extends EasyCrudDaoMySqlImpl<Long, ManyToManyDto<T1Id, T2Id>> {

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		if (getDtoClass() == null) {
			ManyToManyDto<T1Id, T2Id> d = new ManyToManyDto<T1Id, T2Id>();
			// NOTE: I have no idea what is wrong with compiler or my
			// understanding of generics. That's why I did this dirty workaround
			setDtoClass((Class<ManyToManyDto<T1Id, T2Id>>) d.getClass());
		}
		super.afterPropertiesSet();
	}
}
