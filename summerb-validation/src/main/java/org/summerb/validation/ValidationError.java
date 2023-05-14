/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.util.StringUtils;
import org.summerb.i18n.HasMessageArgs;
import org.summerb.i18n.HasMessageCode;
import org.summerb.validation.gson.ValidationErrorGsonTypeAdapter;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

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
  private static final long serialVersionUID = 2414529436328740490L;

  public static final Map<String, Class<?>> ALLOWED_ARGS_CLASSES;

  /** Field token. Actually name of object field */
  protected String propertyName;

  /** Message about this field */
  protected String messageCode;

  /** Message messageArgs */
  private Object[] messageArgs;

  /** @deprecated used only for serialization */
  @Deprecated
  public ValidationError() {}

  public ValidationError(@Nonnull String propertyName, @Nonnull String messageCode) {
    setMessageCode(messageCode);
    setPropertyName(propertyName);
  }

  public ValidationError(
      @Nonnull String propertyName, @Nonnull String messageCode, @Nullable Object... args) {
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

  protected void assertMessageArgs(Object[] args) {
    for (int i = 0; i < args.length; i++) {
      Object arg = args[i];
      if (arg == null) {
        continue;
      }
      Preconditions.checkArgument(
          ALLOWED_ARGS_CLASSES.values().contains(arg.getClass()),
          "Argument %s is of an unacceptable type %s. Only types listed in ValidationError::ALLOWED_ARGS_CLASSES are allowed: %s",
          i,
          arg.getClass(),
          ALLOWED_ARGS_CLASSES.values());
    }
  }

  @Override
  public @Nonnull String getMessageCode() {
    return messageCode;
  }

  public @Nonnull String getPropertyName() {
    return propertyName;
  }

  public void setMessageCode(@Nonnull String messageCode) {
    Preconditions.checkArgument(StringUtils.hasText(messageCode), "messageCode required");
    this.messageCode = messageCode;
  }

  public void setPropertyName(@Nonnull String propertyName) {
    Preconditions.checkArgument(StringUtils.hasText(propertyName), "propertyName required");
    this.propertyName = propertyName;
  }

  @Override
  public @Nullable Object[] getMessageArgs() {
    if (messageArgs == null) {
      return null;
    }
    // NOTE: We clone array to make sure that consumer will not be able to modify it
    return messageArgs.clone();
  }

  public void setMessageArgs(@Nullable Object[] messageArgs) {
    if (messageArgs == null) {
      this.messageArgs = null;
      return;
    }

    assertMessageArgs(messageArgs);
    // NOTE: We clone array to make sure that consumer will not be able to modify it
    this.messageArgs = messageArgs.clone();
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
