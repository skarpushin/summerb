package org.summerb.approaches.jdbccrud.api;

/**
 * 
 * @author sergeyk
 * @deprecated Inject auth strategy directly to consumer if needed
 */
@Deprecated
public interface HasEasyCrudTableAuthStrategy {
	EasyCrudTableAuthStrategy getTableAuthStrategy();
}
