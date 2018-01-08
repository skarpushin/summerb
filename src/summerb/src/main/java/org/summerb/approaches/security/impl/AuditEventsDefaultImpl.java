package org.summerb.approaches.security.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.summerb.approaches.security.api.AuditEvents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AuditEventsDefaultImpl implements AuditEvents {
	private static Logger audit = Logger.getLogger("AUDIT");

	private Gson gson = new GsonBuilder().create();

	@Override
	public void report(String auditEventCode, Serializable data) {
		audit.trace(auditEventCode + "\t" + sanitizeForLog(gson.toJson(data)));
	}

	public static String sanitizeForLog(String str) {
		return str.replaceAll("\t", "\\t").replaceAll("\r", "\\r").replaceAll("\n", "\\n");
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
}
