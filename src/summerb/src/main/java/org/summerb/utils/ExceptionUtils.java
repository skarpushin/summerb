package org.summerb.utils;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class ExceptionUtils {

	@SuppressWarnings("unchecked")
	public static <T> T findExceptionOfType(Throwable t, Class<T> exceptionClass) {
		Throwable cur = t;
		while (cur != null) {
			if (cur == cur.getCause())
				break;

			if (exceptionClass.isAssignableFrom(cur.getClass()))
				return (T) cur;

			cur = cur.getCause();
		}

		return null;
	}

	/**
	 * Find exception cause of specified type. Will fallback to first exception
	 * if target wasn't found
	 * 
	 * @param t
	 * @param exceptionClass
	 * @return found exception of requested type OR first exception
	 */
	public static <T extends Exception> Throwable getExceptionOfClassOrFallbackToOriginal(Throwable t,
			Class<T> exceptionClass) {
		T ret = findExceptionOfType(t, exceptionClass);
		return ret == null ? t : ret;
	}

}
