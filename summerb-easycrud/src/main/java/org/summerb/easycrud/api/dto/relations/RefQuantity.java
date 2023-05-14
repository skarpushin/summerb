/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.easycrud.api.dto.relations;

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
