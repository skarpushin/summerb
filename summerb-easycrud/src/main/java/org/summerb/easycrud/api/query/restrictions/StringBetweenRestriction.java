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

import com.google.common.base.Preconditions;

public class StringBetweenRestriction extends NegatableRestrictionBase<String> {
	private static final long serialVersionUID = -1804000515828431204L;

	private String lowerBound;
	private String upperBound;

	public StringBetweenRestriction() {

	}

	public StringBetweenRestriction(String lowerBound, String upperBound) {
		Preconditions.checkArgument(lowerBound != null);
		Preconditions.checkArgument(upperBound != null);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public boolean isMeet(String subjectValue) {
		if (subjectValue == null) {
			return isNegative();
		}

		boolean lessThanLower = subjectValue.compareToIgnoreCase(lowerBound) < 0;
		boolean lessThanUpper = upperBound.compareToIgnoreCase(subjectValue) > 0;
		boolean greaterOrEqual = lowerBound.compareToIgnoreCase(subjectValue) <= 0;
		boolean lessOrEqual = subjectValue.compareToIgnoreCase(upperBound) <= 0;
		return isNegative() ? lessThanLower || lessThanUpper : greaterOrEqual && lessOrEqual;
	}

	public String getLowerBound() {
		return lowerBound;
	}

	public String getUpperBound() {
		return upperBound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((lowerBound == null) ? 0 : lowerBound.hashCode());
		result = prime * result + ((upperBound == null) ? 0 : upperBound.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringBetweenRestriction other = (StringBetweenRestriction) obj;
		if (lowerBound == null) {
			if (other.lowerBound != null)
				return false;
		} else if (!lowerBound.equals(other.lowerBound))
			return false;
		if (upperBound == null) {
			if (other.upperBound != null)
				return false;
		} else if (!upperBound.equals(other.upperBound))
			return false;
		return true;
	}

}
