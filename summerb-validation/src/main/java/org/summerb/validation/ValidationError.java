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
package org.summerb.validation;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.gson.ValidationErrorGsonTypeAdapter;

/**
 * This is a base class for ValidationError. Most important idea here is to use {@link #messageCode}
 * and {@link #messageArgs} which are language-agnostic, instead of user-facing text baked in into
 * backend. Then facade layer or front-end would translate these into user facing text for user
 * language.
 *
 * <p>For security reasons we allow only primitive types as message args. Map {@link
 * #ALLOWED_ARGS_CLASSES} contains mapping of allowed class names to allowed classes. This is used
 * in instantiation to validate arg types as well as during deserialization
 *
 * <p>In case you're planning to serialize and deserialize these objects using {@link Gson}, make
 * sure to register {@link ValidationErrorGsonTypeAdapter} so that subclasses of {@link
 * ValidationError} will be correctly serialized and deserialized
 *
 * @author sergey.karpushin
 */
public class ValidationError implements Serializable, HasMessageCode, HasMessageArgs {
  @Serial private static final long serialVersionUID = 2414529436328740490L;

  public static final Map<String, Class<?>> ALLOWED_ARGS_CLASSES;

  /** Field token. Actually name of object field */
  protected String propertyName;

  /** Message about this field */
  protected String messageCode;

  /** Message messageArgs */
  private Object[] messageArgs;

  /**
   * @deprecated used only for serialization
   */
  @Deprecated
  public ValidationError() {}

  public ValidationError(String propertyName, String messageCode) {
    setMessageCode(messageCode);
    setPropertyName(propertyName);
  }

  public ValidationError(String propertyName, String messageCode, Object... args) {
    this(propertyName, messageCode);
    setMessageArgs(args);
  }

  static {
    HashMap<String, Class<?>> allowed = new HashMap<>();
    allow(BigDecimal.class, allowed);
    allow(BigInteger.class, allowed);
    allow(Boolean.class, allowed);
    allow(Byte.class, allowed);
    allow(Double.class, allowed);
    allow(Float.class, allowed);
    allow(Integer.class, allowed);
    allow(Long.class, allowed);
    allow(Short.class, allowed);
    allow(String.class, allowed);
    ALLOWED_ARGS_CLASSES = Collections.unmodifiableMap(allowed);
  }

  private void replaceNotAllowedTypesWithToString(Object[] args) {
    for (int i = 0; i < args.length; i++) {
      Object arg = args[i];
      if (arg == null || ALLOWED_ARGS_CLASSES.containsValue(arg.getClass())) {
        continue;
      }
      args[i] = String.valueOf(args[i]);
    }
  }

  @Override
  public String getMessageCode() {
    return messageCode;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setMessageCode(String messageCode) {
    Preconditions.checkArgument(StringUtils.hasText(messageCode), "messageCode required");
    this.messageCode = messageCode;
  }

  public void setPropertyName(String propertyName) {
    Preconditions.checkArgument(StringUtils.hasText(propertyName), "propertyName required");
    this.propertyName = propertyName;
  }

  @Override
  public Object[] getMessageArgs() {
    if (messageArgs == null) {
      return null;
    }
    // NOTE: We clone array to make sure that consumer will not be able to modify it
    return messageArgs.clone();
  }

  public void setMessageArgs(Object[] messageArgs) {
    if (messageArgs == null || messageArgs.length == 0) {
      this.messageArgs = null;
      return;
    }

    // NOTE: We clone array to make sure that consumer will not be able to modify it
    Object[] argsArrayCopy = messageArgs.clone();
    replaceNotAllowedTypesWithToString(argsArrayCopy);
    this.messageArgs = argsArrayCopy;
  }

  @Override
  public String toString() {
    if (messageArgs == null || messageArgs.length == 0) {
      return getPropertyName() + ": code = '" + getMessageCode() + "'";
    } else {
      return getPropertyName()
          + ": code = '"
          + getMessageCode()
          + "', args = "
          + Arrays.toString(messageArgs);
    }
  }

  private static void allow(Class<?> clazz, Map<String, Class<?>> to) {
    to.put(clazz.getCanonicalName(), clazz);
  }
}
