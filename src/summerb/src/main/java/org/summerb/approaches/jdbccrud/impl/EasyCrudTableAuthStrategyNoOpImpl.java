package org.summerb.approaches.jdbccrud.impl;

import org.summerb.approaches.jdbccrud.api.EasyCrudTableAuthStrategy;

public class EasyCrudTableAuthStrategyNoOpImpl implements EasyCrudTableAuthStrategy {

	@Override
	public void assertAuthorizedToCreate() {
		// Permissive
	}

	@Override
	public void assertAuthorizedToUpdate() {
		// Permissive
	}

	@Override
	public void assertAuthorizedToRead() {
		// Permissive
	}

	@Override
	public void assertAuthorizedToDelete() {
		// Permissive
	}

}
