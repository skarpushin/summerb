/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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

import org.summerb.easycrud.api.query.Restriction;

public class NumberBetweenRestriction extends NegativableRestrictionBase implements Restriction<Long> {
	private static final long serialVersionUID = -7489366611902539609L;

	private long lowerBound;
	private long upperBound;

	public NumberBetweenRestriction() {

	}

	public NumberBetweenRestriction(long lowerBound, long upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public long getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(long lowerBound) {
		this.lowerBound = lowerBound;
	}

	public long getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(long upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	public boolean isMeet(Long subjectValue) {
		if (subjectValue == null) {
			return isNegative();
		}

		return isNegative() ? subjectValue < lowerBound || upperBound > subjectValue
				: lowerBound <= subjectValue && subjectValue <= upperBound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (lowerBound ^ (lowerBound >>> 32));
		result = prime * result + (int) (upperBound ^ (upperBound >>> 32));
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
		NumberBetweenRestriction other = (NumberBetweenRestriction) obj;
		if (lowerBound != other.lowerBound)
			return false;
		if (upperBound != other.upperBound)
			return false;
		return true;
	}
}
