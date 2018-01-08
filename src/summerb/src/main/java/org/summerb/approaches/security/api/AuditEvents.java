package org.summerb.approaches.security.api;

import java.io.Serializable;

import org.summerb.approaches.security.api.dto.ScalarValue;

public interface AuditEvents {
	public static final String AUDIT_INJECTION_ATTEMPT = "INJ";

	/**
	 * report record to audit log
	 * 
	 * @param auditEventCode
	 *            record type code. It's expected that all events with this code
	 *            will have same type of data argument
	 * @param data
	 *            event data that will be serialized to audit log. Underlying
	 *            implementation will choose serialization format. Default is JSON.
	 *            If you want to put simple scalar value - it's recommended to use
	 *            {@link ScalarValue} instance
	 */
	void report(String auditEventCode, Serializable data);
}
