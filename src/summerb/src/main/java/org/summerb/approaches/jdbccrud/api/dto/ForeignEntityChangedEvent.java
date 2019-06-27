package org.summerb.approaches.jdbccrud.api.dto;

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
	 * @param base   even
	 * @param origin where this event came from, i.e. node id
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
