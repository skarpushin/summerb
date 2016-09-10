package org.summerb.approaches.jdbccrud.api.dto.relations;

public enum RefQuantity {
	/**
	 * Normally represents type break-down to different types (tables)
	 */
	One2One,

	/**
	 * Dictionary-like reference
	 */
	Many2One,

	/**
	 * Mater-detail like reference
	 */
	One2Many,

	/**
	 * Users to groups association
	 */
	Many2Many
}