/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.validation.gson;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.summerb.validation.ValidationError;

/**
 * {@link Gson} IO helper that can serialize/deserialize {@link ValidationError} according to value
 * class. Also, it will correctly serialize/deserialize message arguments according to their types
 *
 * @author sergeyk
 */
public class ValidationErrorGsonTypeAdapter
    implements JsonSerializer<ValidationError>, JsonDeserializer<ValidationError> {

  private static final String CLASSNAME = "__class";
  private static final String ARGS_CLASSNAMES = "__args";

  /**
   * This is used to avoid StackOverflow when we want to use standard GSON (de)serialization but
   * just customize it a little
   */
  protected final Gson vanillaGson;

  public ValidationErrorGsonTypeAdapter() {
    this(new Gson());
  }

  public ValidationErrorGsonTypeAdapter(Gson vanillaGson) {
    Preconditions.checkArgument(vanillaGson != null, "vanillaGson required");
    this.vanillaGson = vanillaGson;
  }

  @Override
  public JsonElement serialize(ValidationError src, Type type, JsonSerializationContext ctx) {
    JsonElement ret;
    ret = vanillaGson.toJsonTree(src, type);
    JsonObject obj = ret.getAsJsonObject();
    if (!ValidationError.class.equals(src.getClass())) {
      retainValueClassname(src, obj);
    }
    retainArgsClassnames(src, obj);
    return ret;
  }

  protected void retainValueClassname(ValidationError src, JsonObject obj) {
    obj.addProperty(CLASSNAME, src.getClass().getCanonicalName());
  }

  protected void retainArgsClassnames(ValidationError src, JsonObject obj) {
    if (src.getMessageArgs() == null) {
      return;
    }

    JsonArray argsClassNames = new JsonArray();
    for (Object arg : src.getMessageArgs()) {
      if (arg == null) {
        argsClassNames.add((String) null);
      } else {
        argsClassNames.add(arg.getClass().getCanonicalName());
      }
    }
    obj.add(ARGS_CLASSNAMES, argsClassNames);
  }

  @Override
  public ValidationError deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    JsonElement argsClassnames = jsonObject.remove(ARGS_CLASSNAMES);
    JsonElement args = jsonObject.remove("messageArgs");

    Class<? extends ValidationError> klass = resolveSubtypeClass(jsonObject);
    ValidationError ret = vanillaGson.fromJson(jsonObject, klass);

    deserializeArgs(ret, args, argsClassnames, context);

    return ret;
  }

  @SuppressFBWarnings(
      value = "NP_NULL_ON_SOME_PATH",
      justification = "checked by preceeding Preconditions.checkArgument")
  protected void deserializeArgs(
      ValidationError ret,
      JsonElement args,
      JsonElement argsClassnames,
      JsonDeserializationContext context) {

    if (args == null) {
      return;
    }

    Preconditions.checkArgument(
        args.isJsonArray(), "args property supposed to be of type JsonArray");
    JsonArray argsArray = args.getAsJsonArray();

    Preconditions.checkArgument(
        argsClassnames != null && argsClassnames.isJsonArray(),
        "argsClassnames supposed to be present and of type JsonArray");
    JsonArray argsClassnamesArray = argsClassnames.getAsJsonArray();
    Preconditions.checkArgument(
        argsClassnamesArray.size() == argsArray.size(),
        "size of argsArray and argsClassnamesArray must match");

    Iterator<JsonElement> argsIter = argsArray.iterator();
    Iterator<JsonElement> argClassIter = argsClassnamesArray.iterator();

    List<Object> messageArgs = new ArrayList<>();
    int i = 0;
    while (argsIter.hasNext()) {
      JsonElement argElement = argsIter.next();
      if (argElement.isJsonNull()) {
        messageArgs.add(null);
        continue;
      }

      Preconditions.checkArgument(
          argElement.isJsonPrimitive(),
          "arg %s must be of primitive type, but got %s",
          i,
          argElement);

      JsonElement argClassElement = argClassIter.next();
      Preconditions.checkArgument(
          argClassElement.isJsonPrimitive(),
          "argClassElement %s must be non-null primitive type, but got %s",
          i,
          argClassElement);

      Class<?> argClass = resolveMessageArgClass(argClassElement.getAsString());
      messageArgs.add(context.deserialize(argElement, argClass));

      i++;
    }

    ret.setMessageArgs(messageArgs.toArray());
  }

  private Class<?> resolveMessageArgClass(String argClassName) {
    Class<?> ret = ValidationError.ALLOWED_ARGS_CLASSES.get(argClassName);
    Preconditions.checkArgument(
        ret != null,
        "Class %s is not listed as allowed in ValidationError.ALLOWED_ARGS_CLASSES, possible security breach attempt");
    return ret;
  }

  @SuppressWarnings("unchecked")
  protected <T extends ValidationError> Class<T> resolveSubtypeClass(JsonObject jsonObject) {
    JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
    if (prim == null || prim.isJsonNull()) {
      return (Class<T>) ValidationError.class;
    }

    String className = prim.getAsString();
    jsonObject.remove(CLASSNAME);

    try {
      Class<T> ret = (Class<T>) Class.forName(className);
      if (!ValidationError.class.isAssignableFrom(ret)) {
        throw new IllegalArgumentException(
            "Potentially security breach: attempt to deserilaize something that is not a subtype of ValidationError: "
                + className);
      }
      return ret;
    } catch (Exception e) {
      throw new JsonParseException("Failed to resolve class " + className, e);
    }
  }
}
