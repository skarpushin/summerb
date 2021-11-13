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
package org.summerb.easycrud.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudTableAuthStrategy;
import org.summerb.easycrud.api.EasyCrudValidationStrategy;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.HasEasyCrudTableAuthStrategy;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapTableAuthImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;

import com.google.common.eventbus.EventBus;

/**
 * 
 * @author sergey.karpushin
 * @deprecated Use {@link EasyCrudServicePluggableImpl} instead
 */
@Deprecated
public class EasyCrudServiceTableAuthImpl<TId, TDto extends HasId<TId>>
		extends EasyCrudServicePluggableImpl<TId, TDto, EasyCrudDao<TId, TDto>>
		implements HasEasyCrudTableAuthStrategy, InitializingBean {

	private EasyCrudValidationStrategy<TDto> validationStrategy;
	protected EasyCrudTableAuthStrategy tableAuthStrategy;
	protected EventBus eventBus;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<EasyCrudWireTap<TId, TDto>> chain = new LinkedList<>();
		if (validationStrategy != null) {
			chain.add(new EasyCrudWireTapValidationImpl<TId, TDto>(validationStrategy));
		}

		if (tableAuthStrategy != null) {
			chain.add(new EasyCrudWireTapTableAuthImpl<TId, TDto>(tableAuthStrategy));
		}

		if (eventBus != null) {
			chain.add(new EasyCrudWireTapEventBusImpl<TId, TDto>(eventBus));
		}

		EasyCrudWireTapDelegatingImpl<TId, TDto> newWireTap = new EasyCrudWireTapDelegatingImpl<TId, TDto>(chain);
		setWireTap(newWireTap);
		super.afterPropertiesSet();
	}

	@Override
	public EasyCrudTableAuthStrategy getTableAuthStrategy() {
		return tableAuthStrategy;
	}

	public void setTableAuthStrategy(EasyCrudTableAuthStrategy genericAuthorizationStrategy) {
		this.tableAuthStrategy = genericAuthorizationStrategy;
	}

	public EasyCrudValidationStrategy<TDto> getValidationStrategy() {
		return validationStrategy;
	}

	public void setValidationStrategy(EasyCrudValidationStrategy<TDto> validationStrategy) {
		this.validationStrategy = validationStrategy;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
