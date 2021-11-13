/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.properties.internal;

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
