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
package org.summerb.easycrud.rest.querynarrower;

import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;

/**
 * Provide a sub-class that will alter queries before query will be issued to
 * service. I.e. useful to narrow queries only to items available to current
 * user OR which contained in current context identified by REST url path, i.e.
 * to select only devices which belong to a particular environment
 * <code>/rest/api/v1/env/{envId}/device</code>
 * 
 * @author sergeyk
 *
 */
public class QueryNarrowerStrategy {
	public Query narrow(Query optionalQuery, PathVariablesMap pathVariables) {
		return optionalQuery;
	}
}
