/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.security.impl;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.summerb.security.api.AuditEvents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AuditEventsDefaultImpl implements AuditEvents {
	private static Logger audit = LogManager.getLogger("AUDIT");

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
