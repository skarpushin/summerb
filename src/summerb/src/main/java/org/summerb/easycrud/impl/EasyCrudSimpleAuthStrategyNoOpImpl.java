package org.summerb.easycrud.impl;

import org.summerb.easycrud.api.EasyCrudSimpleAuthStrategy;

public class EasyCrudSimpleAuthStrategyNoOpImpl implements EasyCrudSimpleAuthStrategy {

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
