package org.summerb.approaches.security.impl;

import org.apache.log4j.Logger;
import org.summerb.approaches.security.api.AuditLog;

public class AuditLogDefaultImpl implements AuditLog {
	private static Logger audit = Logger.getLogger("AUDIT");

	@Override
	public void report(String auditEventCode, String data) {
		audit.trace(auditEventCode + "\t" + sanitizeForLog(data));
	}

	public static String sanitizeForLog(String str) {
		return str.replaceAll("\t", "\\t").replaceAll("\r", "\\r").replaceAll("\n", "\\n");
	}
}
