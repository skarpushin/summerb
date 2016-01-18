package org.summerb.utils.cache;

/**
 * That event is reserved for cases when we want to invalidate all caches in the
 * app
 * 
 * @author sergey.karpushin
 *
 */
public class CachesInvalidationNeeded {
	private String cause;

	public CachesInvalidationNeeded() {

	}

	public CachesInvalidationNeeded(String cause) {
		this.cause = cause;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	@Override
	public String toString() {
		return "CachesInvalidationNeeded [cause=" + cause + "]";
	}

}
