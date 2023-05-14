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

public class QueryNarrowerByCommonPathVariable extends QueryNarrowerStrategyFieldBased {
	private String commonParamName;

	public QueryNarrowerByCommonPathVariable(String commonParamName) {
		super(commonParamName);
		this.commonParamName = commonParamName;
	}

	@Override
	protected Query doNarrow(Query ret, PathVariablesMap allRequestParams) {
		Object val = allRequestParams.get(commonParamName);
		if (val instanceof String) {
			ret.eq(commonParamName, (String) val);
		} else if (val instanceof Long) {
			ret.eq(commonParamName, (Long) val);
		} else {
			throw new IllegalStateException("Current impl doesn't support args of type: " + val.getClass());
		}
		return ret;
	}
}
