package org.summerb.microservices.properties.internal;

/**
 * Simple service which able to keep registry of aliases string-2-long.
 * 
 * It's up to impl on how this will be implemented. It might be cached table in
 * database, or it might be persisted on disk and be completely in-memory.
 * Primary goal is to be as fast as possible even if it will sacrifice some
 * reasonable amount of memory.
 * 
 * @author skarpushin
 * 
 */
public interface StringIdAliasService {
	/**
	 * Get 'long' alias for string value
	 * 
	 * @param str
	 *            string value
	 * @return unique long value associated with provided string
	 */
	long getAliasFor(String str);

	/**
	 * This is reverse lookup. Find name by it's alias
	 * 
	 * @param alias
	 * @return
	 */
	String getNameByAlias(long alias);
}
