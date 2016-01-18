package org.summerb.approaches.jdbccrud.api.query.restrictions;

import java.io.Serializable;

import org.summerb.approaches.jdbccrud.api.query.CanHaveNegativeMeaning;

public abstract class NegativableRestrictionBase implements CanHaveNegativeMeaning, Serializable {
	private static final long serialVersionUID = 5995985611595165600L;

	private boolean negative;

	@Override
	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (negative ? 1231 : 1237);
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
		NegativableRestrictionBase other = (NegativableRestrictionBase) obj;
		if (negative != other.negative)
			return false;
		return true;
	}
}
