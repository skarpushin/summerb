package org.summerb.utils.exceptions.translator;

import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.web.util.NestedServletException;

public interface ExceptionUnwindingStrategy {
	/**
	 * Get first exception that actually might mean something. Impl supposed to skip
	 * meaningless exceptions like {@link NestedServletException},
	 * {@link UndeclaredThrowableException}, etc..
	 * 
	 * @param current
	 *            current exception at hand
	 * @return either the same exception or more meaningful.
	 */
	Throwable getNextMeaningfulExc(Throwable current);
}
