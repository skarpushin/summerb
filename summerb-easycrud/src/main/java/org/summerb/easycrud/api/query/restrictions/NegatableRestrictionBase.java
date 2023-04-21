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
package org.summerb.easycrud.api.query.restrictions;

import java.io.Serializable;

import org.summerb.easycrud.api.query.CanHaveNegativeMeaning;
import org.summerb.easycrud.api.query.Restriction;

public abstract class NegatableRestrictionBase<T> implements CanHaveNegativeMeaning, Serializable, Restriction<T> {
	private static final long serialVersionUID = 5995985611595165600L;

	private boolean negative;

	@Override
	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public NegatableRestrictionBase<T> asNegative() {
		setNegative(true);
		return this;
	}

}
