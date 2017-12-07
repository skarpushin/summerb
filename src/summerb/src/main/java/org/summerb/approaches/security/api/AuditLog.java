package org.summerb.approaches.security.api;

public interface AuditLog {
	void report(String auditEventCode, String data);
}
