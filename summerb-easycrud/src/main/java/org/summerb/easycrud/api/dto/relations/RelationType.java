/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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

public enum RelationType {
	/**
	 * That type of reference is used when Source just references Target (i.e. we
	 * just referencing dictionary item). If Source is deleted that will not affect
	 * Target.
	 */
	References,

	/**
	 * That means the Source contains Target (like tree holds leafs). Target is
	 * meaningless without Source. So if Source is deleted then same affects Target
	 */
	Aggregates,

	/**
	 * Opposite to 'Aggregates'
	 */
	PartOf,

	/**
	 * That means that Source is actually is a part of Target, but target is not
	 * aware of exact Source. In that case typically multiple types of Sources are
	 * referenced to same type of Target (i.e.: Audit log, ACL, etc...).
	 */
	Aspect;
}
