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

import org.summerb.easycrud.api.dto.relations.Ref;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ReferencesRegistryPredefinedImpl extends ReferencesRegistryPreconfiguredAbstract {
	private Ref[] refs;

	public ReferencesRegistryPredefinedImpl(Ref... refs) {
		Preconditions.checkArgument(refs != null);
		this.refs = refs;
	}

	@Override
	protected Multimap<String, Ref> discoverRefsBySources() {
		Multimap<String, Ref> ret = HashMultimap.create();
		for (Ref ref : refs) {
			ret.put(ref.getFromEntity(), ref);
		}
		return ret;
	}

	@Override
	protected Multimap<String, Ref> discoverAliases() {
		return HashMultimap.create();
	}

}
