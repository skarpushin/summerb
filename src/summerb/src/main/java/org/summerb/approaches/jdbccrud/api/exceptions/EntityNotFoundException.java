package org.summerb.approaches.jdbccrud.api.exceptions;

import org.summerb.approaches.i18n.HasMessageArgs;
import org.summerb.approaches.i18n.HasMessageCode;

/**
 * Base class for exceptions for case when something wasn't found by it's
 * identity
 * 
 * @author sergey.karpushin
 * 
 */
public abstract class EntityNotFoundException extends Exception implements HasMessageCode, HasMessageArgs {
	private static final long serialVersionUID = 3254284449960233351L;

	protected Object identity;

	/**
	 * @deprecated Used only for io
	 */
	@Deprecated
	public EntityNotFoundException() {
	}

	public EntityNotFoundException(Object identity) {
		this(identity, null);
	}

	public EntityNotFoundException(Object identity, Throwable cause) {
		this("Entity identified by '" + identity + "' not found", identity, cause);
	}

	public EntityNotFoundException(String techMessage, Object identity, Throwable cause) {
		super(techMessage, cause);
		this.identity = identity;
	}

	@Override
	public Object[] getMessageArgs() {
		return new Object[] { identity };
	}

	public Object getIdentity() {
		return identity;
	}

	public void setIdentity(Object identity) {
		this.identity = identity;
	}

}
