package org.summerb.approaches.jdbccrud.api;

public interface StringIdGenerator {

	/**
	 * Generate new ID for given DTO. Id doesn't have to depend on DTO, it's purely
	 * Voluntarily.
	 * 
	 * @param optionalDto dto to create ID for, could be null
	 * @return Some unique string that represents ID
	 */
	String generateNewId(Object optionalDto);

	/**
	 * Check if specific DTO confirms to the format used by this specific
	 * implementation
	 * 
	 * @param id not null/not empty id
	 * @return true if valid
	 */
	boolean isValidId(String id);

}
