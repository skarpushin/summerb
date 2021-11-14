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
package org.summerb.easycrud.api.dto;

import org.summerb.utils.DtoBase;

/**
 * This wrapper is used to indicate that this event is originating from other
 * origin (other server node for example). We can use that wrapper to ensure we
 * are not re-sending this event to other nodes
 * 
 * @author sergey.karpushin
 *
 * @param <T>
 */
public class ForeignEntityChangedEvent<T extends DtoBase> extends EntityChangedEvent<T> {
	private static final long serialVersionUID = -4248659723493913974L;

	private Object origin;

	/**
	 * @param base
	 *            even
	 * @param origin
	 *            where this event came from, i.e. node id
	 */
	@SuppressWarnings("deprecation")
	public ForeignEntityChangedEvent(EntityChangedEvent<T> base, Object origin) {
		super(base.getValue(), base.getChangeType());
		this.origin = origin;
	}

	public Object getOrigin() {
		return origin;
	}

	public void setOrigin(Object origin) {
		this.origin = origin;
	}
}
