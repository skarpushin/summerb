package org.summerb.easycrud.api.query.restrictions;

import java.io.Serializable;

import org.summerb.easycrud.api.query.CanHaveNegativeMeaning;

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
}
