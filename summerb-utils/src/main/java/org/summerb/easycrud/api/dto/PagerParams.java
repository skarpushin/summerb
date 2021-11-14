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
package org.summerb.easycrud.api.dto;

import java.io.Serializable;

/**
 * Pagination params for queries
 * 
 * @author sergey.karpushin
 *
 */
public class PagerParams implements Serializable {
	private static final long serialVersionUID = -4006916172860172208L;

	public static final PagerParams ALL = new PagerParams(0, Integer.MAX_VALUE).unmodifiable();

	public static final String FIELD_OFFSET = "offset";
	public static final String FIELD_MAX = "max";

	protected long offset = 0;
	protected long max = 20;

	public PagerParams() {
	}

	public PagerParams(long offset, long max) {
		setOffset(offset);
		setMax(max);
	}

	public PagerParams unmodifiable() {
		return new Unmodifiable(this);
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		if (offset < 0) {
			throw new IndexOutOfBoundsException("Offset must not be less then 0");
		}
		this.offset = offset;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		if (max < 1) {
			throw new IndexOutOfBoundsException("Max must not be less then 1");
		}
		this.max = max;
	}

	@Override
	public String toString() {
		return "PagerParams [offset=" + offset + ", max=" + max + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (max ^ (max >>> 32));
		result = prime * result + (int) (offset ^ (offset >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// Minor LSP violation
		if (!(obj instanceof PagerParams))
			return false;
		PagerParams other = (PagerParams) obj;
		if (max != other.max)
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}

	private static class Unmodifiable extends PagerParams {
		private static final long serialVersionUID = -6876008468615718964L;

		public Unmodifiable(PagerParams pagerParams) {
			this.offset = pagerParams.offset;
			this.max = pagerParams.max;
		}

		@Override
		public void setOffset(long offset) {
			throw new IllegalStateException("Not allowed for unmodifiable object");
		}

		@Override
		public void setMax(long max) {
			throw new IllegalStateException("Not allowed for unmodifiable object");
		}
	}
}
