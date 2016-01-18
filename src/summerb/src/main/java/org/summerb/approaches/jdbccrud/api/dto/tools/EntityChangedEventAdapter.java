package org.summerb.approaches.jdbccrud.api.dto.tools;

import java.lang.reflect.Type;

import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent.ChangeType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
		Class klass = resolveParametersClass(jsonObject);
		JsonElement jsonElement = jsonObject.get(INSTANCE);

		Object value = context.deserialize(jsonElement, klass);
		ChangeType changeType = context.deserialize(jsonObject.get("ct"), ChangeType.class);

		return new EntityChangedEvent(value, changeType);
	}

	protected Class<?> resolveParametersClass(JsonObject jsonObject) {
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
		if (prim == null) {
			throw new IllegalArgumentException(
					"JSON Parse error: didn't find classname field named '" + CLASSNAME + "' in object: " + jsonObject);
		}

		String className = prim.getAsString();

		Class<?> klass = null;
		try {
			klass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// log.error("Failed to resolve class: " + className, e);
			throw new JsonParseException(e.getMessage());
		}
		return klass;
	}

}
