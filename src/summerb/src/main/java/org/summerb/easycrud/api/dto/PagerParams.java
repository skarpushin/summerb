package org.summerb.easycrud.api.dto;

import java.io.Serializable;

/**
 * 
 * @author sergey.karpushin
 *
 */public class PagerParams implements Serializable {

	private static final long serialVersionUID = -4006916172860172208L;

	public static final String FIELD_OFFSET = "offset";
	public static final String FIELD_MAX = "max";

	private long offset = 0;
	private long max = 20;

	public PagerParams() {
	}

	public PagerParams(long offset, long max) {
		setOffset(offset);
		setMax(max);
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
		if (getClass() != obj.getClass())
			return false;
		PagerParams other = (PagerParams) obj;
		if (max != other.max)
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}
}
