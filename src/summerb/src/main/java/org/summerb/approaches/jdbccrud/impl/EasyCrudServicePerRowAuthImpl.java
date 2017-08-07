package org.summerb.approaches.jdbccrud.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.approaches.jdbccrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.jdbccrud.api.EasyCrudWireTap;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapValidationImpl;

import com.google.common.eventbus.EventBus;

/**
 * 
 * @author sergey.karpushin
 * @deprecated Use {@link EasyCrudServicePluggableImpl} instead
 */
@Deprecated
public class EasyCrudServicePerRowAuthImpl<TId, TDto extends HasId<TId>, TDao extends EasyCrudDao<TId, TDto>>
		extends EasyCrudServicePluggableImpl<TId, TDto, EasyCrudDao<TId, TDto>> implements InitializingBean {

	private EasyCrudValidationStrategy<TDto> validationStrategy;
	private EasyCrudPerRowAuthStrategy<TDto> perRowAuthStrategy;
	protected EventBus eventBus;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<EasyCrudWireTap<TId, TDto>> chain = new LinkedList<>();
		if (validationStrategy != null) {
			chain.add(new EasyCrudWireTapValidationImpl<TId, TDto>(validationStrategy));
		}

		if (perRowAuthStrategy != null) {
			chain.add(new EasyCrudWireTapPerRowAuthImpl<TId, TDto>(perRowAuthStrategy));
		}

		if (eventBus != null) {
			chain.add(new EasyCrudWireTapEventBusImpl<TId, TDto>(eventBus));
		}

		EasyCrudWireTapDelegatingImpl<TId, TDto> newWireTap = new EasyCrudWireTapDelegatingImpl<TId, TDto>(chain);
		setWireTap(newWireTap);
		super.afterPropertiesSet();
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

	public EasyCrudPerRowAuthStrategy<TDto> getPerRowAuthStrategy() {
		return perRowAuthStrategy;
	}

	public void setPerRowAuthStrategy(EasyCrudPerRowAuthStrategy<TDto> perRowAuthStrategy) {
		this.perRowAuthStrategy = perRowAuthStrategy;
	}

}
