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
package org.summerb.easycrud.api.dto.tools;

import java.lang.reflect.Type;

import org.summerb.easycrud.api.dto.EntityChangedEvent;
import org.summerb.easycrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.utils.DtoBase;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link Gson} IO helper that can serialize/deserialize
 * {@link EntityChangedEvent} according to value class.
 * 
 * IMPORTANT: In order to significantly decrease potential vulnerability of
 * using {@link Class#forName(String)} underlying DTOs are required to implement
 * {@link DtoBase} interface.
 * 
 * @author sergeyk
 *
 */
@SuppressWarnings("rawtypes")
public class EntityChangedEventAdapter
		implements JsonSerializer<EntityChangedEvent>, JsonDeserializer<EntityChangedEvent> {

	private static final String INSTANCE = "v";
	private static final String CLASSNAME = "vt";

	@Override
	public JsonElement serialize(EntityChangedEvent src, Type type, JsonSerializationContext ctx) {
		JsonObject retValue = new JsonObject();
		retValue.addProperty("ct", src.getChangeType().toString());
		retValue.addProperty(CLASSNAME, src.getValue().getClass().getCanonicalName());
		retValue.add(INSTANCE, ctx.serialize(src.getValue()));
		return retValue;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public EntityChangedEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		Class<? extends DtoBase> klass = resolveParametersClass(jsonObject);
		JsonElement jsonElement = jsonObject.get(INSTANCE);

		DtoBase value = context.deserialize(jsonElement, klass);
		ChangeType changeType = context.deserialize(jsonObject.get("ct"), ChangeType.class);

		return new EntityChangedEvent(value, changeType);
	}

	@SuppressWarnings("unchecked")
	protected <T extends DtoBase> Class<T> resolveParametersClass(JsonObject jsonObject) {
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
		if (prim == null) {
			throw new IllegalArgumentException(
					"JSON Parse error: didn't find classname field named '" + CLASSNAME + "' in object: " + jsonObject);
		}

		String className = prim.getAsString();

		Class<T> klass = null;
		try {
			klass = (Class<T>) Class.forName(className);
			if (!DtoBase.class.isAssignableFrom(klass)) {
				throw new IllegalArgumentException(
						"Potentially security breach. Attempt to Class.forName: " + className);
			}
		} catch (ClassNotFoundException e) {
			// log.error("Failed to resolve class: " + className, e);
			throw new JsonParseException(e.getMessage());
		}
		return klass;
	}

}
