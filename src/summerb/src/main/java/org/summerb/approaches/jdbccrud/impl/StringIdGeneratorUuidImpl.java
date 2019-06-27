package org.summerb.approaches.jdbccrud.impl;

import java.util.UUID;

import org.summerb.approaches.jdbccrud.api.StringIdGenerator;

/**
 * Simple impl based on {@link UUID} class.
 * 
 * @author sergeyk
 *
 */
public class StringIdGeneratorUuidImpl implements StringIdGenerator {
	private boolean strictUuidMode = false;

	@Override
	public String generateNewId(Object optionalDto) {
		return UUID.randomUUID().toString();
	}

	@Override
	public boolean isValidId(String id) {
		try {
			return id != null && id.length() == 36 && (!strictUuidMode || UUID.fromString(id) != null);
		} catch (IllegalArgumentException exc) {
			return false;
		}
	}

	/**
	 * If true, will use UUID to verify format. If false will only verify length of
	 * the string which must be 36 char long.
	 * 
	 * For legacy compatibility is set to false by default.
	 */
	public boolean isStrictUuidMode() {
		return strictUuidMode;
	}

	public void setStrictUuidMode(boolean strictUuidMode) {
		this.strictUuidMode = strictUuidMode;
	}
}
