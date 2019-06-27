package org.summerb.approaches.security.elevation.api;

public interface ElevationStrategy {
	boolean isElevationRequired();

	/**
	 * Returns cookie, which is to be passed to {@link #deElevate(Object)} after
	 * 
	 * @return
	 */
	Object elevate();

	void deElevate(Object cookie);
}
