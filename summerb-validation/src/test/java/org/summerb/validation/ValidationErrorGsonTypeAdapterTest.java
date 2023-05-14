package org.summerb.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.summerb.validation.errors.MustHaveText;
import org.summerb.validation.gson.ValidationErrorGsonTypeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

class ValidationErrorGsonTypeAdapterTest {

  // NOTE: Other tests are not created here because same functionality is already covered by
  // ValidationContextTest -- so we only add tests that are needed to complete coverage/mutational
  // coverage

  @Test
  void test_expectIae() {
    assertThrows(IllegalArgumentException.class, () -> new ValidationErrorGsonTypeAdapter(null));
  }

  @Test
  void testMsgArgs() {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(ValidationError.class, new ValidationErrorGsonTypeAdapter())
            .create();

    String json = gson.toJson(new ValidationError("pn", "mc"));
    assertEquals("{\"propertyName\":\"pn\",\"messageCode\":\"mc\"}", json);

    // null message args are not serialized
    Object[] args = null;
    json = gson.toJson(new ValidationError("pn", "mc", args));
    assertEquals("{\"propertyName\":\"pn\",\"messageCode\":\"mc\"}", json);
    ValidationError ve = gson.fromJson(json, ValidationError.class);
    assertNull(ve.getMessageArgs());

    // empty message args serialized as empty array
    args = new Object[0];
    json = gson.toJson(new ValidationError("pn", "mc", args));
    assertEquals(
        "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[],\"__args\":[]}", json);
    ve = gson.fromJson(json, ValidationError.class);
    assertEquals(0, ve.getMessageArgs().length);

    // null arg
    json = gson.toJson(new ValidationError("pn", "mc", (String) null));
    assertEquals(
        "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[null],\"__args\":[null]}",
        json);
    ve = gson.fromJson(json, ValidationError.class);
    assertEquals(1, ve.getMessageArgs().length);
    assertNull(ve.getMessageArgs()[0]);
  }

  @Test
  void testDeserializationPreconditions() {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(ValidationError.class, new ValidationErrorGsonTypeAdapter())
            .create();

    // illegal - args: object
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":{},\"__args\":[]}",
                ValidationError.class));

    // illegal - args: number
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":123,\"__args\":[]}",
                ValidationError.class));

    // illegal - args classes: object
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[],\"__args\":{}}",
                ValidationError.class));

    // illegal - args classes: number
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[],\"__args\":123}",
                ValidationError.class));

    // illegal - args classes: null, while args are not null
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[],\"__args\":null}",
                ValidationError.class));

    // illegal - args classes: missing, while args are not null
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[]}",
                ValidationError.class));

    // illegal - different args array sizes
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[null],\"__args\":[null, null]}",
                ValidationError.class));

    // illegal - arg is of non-primitive type
    IllegalArgumentException ex =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                gson.fromJson(
                    "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[1, {}],\"__args\":[\"java.lang.Integer\", null]}",
                    ValidationError.class));
    assertEquals("arg 1 must be of primitive type, but got {}", ex.getMessage());

    // illegal - arg class is of non-primitive type
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[1],\"__args\":[{}]}",
                ValidationError.class));

    // illegal - arg class null, while arg is not
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[1],\"__args\":[null]}",
                ValidationError.class));

    // illegal - arg class incorrect
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[1],\"__args\":[\"incorrect-name\"]}",
                ValidationError.class));

    // illegal - arg class not allowed
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[1],\"__args\":[\"org.summerb.validation.ValidationError\"]}",
                ValidationError.class));

    // illegal - arg class empty, while arg is not
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[1],\"__args\":[\"\"]}",
                ValidationError.class));

    // valid case
    ValidationError ve =
        gson.fromJson(
            "{\"propertyName\":\"pn\",\"messageCode\":\"mc\",\"messageArgs\":[7],\"__args\":[\"java.lang.Integer\"]}",
            ValidationError.class);
    assertEquals(7, ve.getMessageArgs()[0]);
    assertEquals(Integer.class, ve.getMessageArgs()[0].getClass());
  }

  @Test
  void testSubclassNamDeserialization() {
    Gson gson =
        new GsonBuilder()
            .registerTypeHierarchyAdapter(
                ValidationError.class, new ValidationErrorGsonTypeAdapter())
            .create();

    MustHaveText ve = new MustHaveText("pn");

    String json = gson.toJson(ve);

    String jsonInvalid = json.replace("org.summerb.validation.errors.MustHaveText", "invalid");
    assertThrows(JsonParseException.class, () -> gson.fromJson(jsonInvalid, MustHaveText.class));

    String jsonNotAllowed =
        json.replace("org.summerb.validation.errors.MustHaveText", "java.lang.String");
    assertThrows(JsonParseException.class, () -> gson.fromJson(jsonNotAllowed, MustHaveText.class));
  }
}
