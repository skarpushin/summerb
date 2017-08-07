package org.summerb.approaches.jdbccrud.api;

/**
 * @author sergeyk
 * @deprecated Inject auth strategy directly to consumer if needed. Do not use
 *             -- will be removed in future releases. for time a being it's kept
 *             only for backwards compatibility.
 */
@Deprecated
public interface HasEasyCrudTableAuthStrategy {
	EasyCrudTableAuthStrategy getTableAuthStrategy();
}
