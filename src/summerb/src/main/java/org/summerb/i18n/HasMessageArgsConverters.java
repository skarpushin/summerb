package org.summerb.i18n;

/**
 * Object which implements {@link HasMessageArgs} might also want to clarify
 * which converter to use for certain message arg. IF converter is not
 * specified, then toString will be used.
 * 
 * @author skarpushin
 * 
 */
public interface HasMessageArgsConverters {
	MessageArgConverter[] getMessageArgsConverters();
}
