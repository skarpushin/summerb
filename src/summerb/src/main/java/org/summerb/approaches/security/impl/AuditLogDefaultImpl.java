package org.summerb.approaches.security.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.summerb.approaches.security.api.AuditLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AuditLogDefaultImpl implements AuditLog {
	private static Logger audit = Logger.getLogger("AUDIT");

	private Gson gson = new GsonBuilder().create();

	@Override
	public void report(String auditEventCode, Serializable data) {
		audit.trace(auditEventCode + "\t" + sanitizeForLog(gson.toJson(data)));
	}

	public static String sanitizeForLog(String str) {
		return str.replaceAll("\t", "\\t").replaceAll("\r", "\\r").replaceAll("\n", "\\n");
	}
}
