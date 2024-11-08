package org.summerb.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactory;
import org.summerb.methodCapturers.MethodCapturerProxyClassFactoryImpl;
import org.summerb.methodCapturers.PropertyNameResolverFactory;
import org.summerb.methodCapturers.PropertyNameResolverFactoryImpl;
import org.summerb.validation.errors.LengthMustBeGreater;
import org.summerb.validation.errors.MustBeEmpty;
import org.summerb.validation.errors.MustHaveText;
import org.summerb.validation.gson.ValidationErrorGsonTypeAdapter;
import org.summerb.validation.testDtos.Bean;
import org.summerb.validation.testDtos.Beans;

class ValidationErrorsTest {

  MethodCapturerProxyClassFactory methodCapturerProxyClassFactory =
      new MethodCapturerProxyClassFactoryImpl();
  PropertyNameResolverFactory propertyNameResolverFactory =
      new PropertyNameResolverFactoryImpl(methodCapturerProxyClassFactory);
  ValidationContextFactory validationContextFactory =
      new ValidationContextFactoryImpl(propertyNameResolverFactory, null);

  @Test
  void test() {
    // list is initialized under the hood
    var f = new ValidationErrors();
    assertNotNull(f.getList());

    List<ValidationError> errors = new LinkedList<>();
    f.setList(errors);
    assertSame(f.getList(), errors);

    // via constructor
    f = new ValidationErrors(errors);
    assertSame(f.getList(), errors);
  }

  @Test
  void test_setList() {
    // list is initialized under the hood
    var f = new ValidationErrors();
    assertThrows(IllegalArgumentException.class, () -> f.setList(null));
  }

  @Test
  void test_add() {
    // illegal - null arg
    var f = new ValidationErrors("asd", new LinkedList<>());
    assertThrows(IllegalArgumentException.class, () -> f.add(null));

    // illegal - null list passed
    assertThrows(IllegalArgumentException.class, () -> new ValidationErrors("asd", null));

    // illegal - null list passed
    assertThrows(IllegalArgumentException.class, () -> new ValidationErrors(null));
  }

  @Test
  void test_locators() {
    ValidationErrors f = new ValidationErrors();
    f.add(new MustHaveText("asd"));

    assertTrue(f.hasErrorOfType(MustHaveText.class));
    assertFalse(f.hasErrorOfType(MustBeEmpty.class));

    assertEquals(0, f.findErrorsForField("qqq").size());
    List<ValidationError> vee = f.findErrorsForField("asd");
    assertEquals(1, vee.size());
    assertEquals(MustHaveText.class, vee.get(0).getClass());
  }

  @Test
  void test_toString() {
    var f = new ValidationErrors("asd", new LinkedList<>());
    assertTrue(f.toString().endsWith(" (empty)"));

    Beans beans = new Beans();
    Bean bean1 = new Bean();
    bean1.setString1("asd");
    Bean bean2 = new Bean();
    bean2.setString2("asda");
    beans.setBeans(Arrays.asList(bean1, bean2));

    ValidationContext<Beans> ctx = validationContextFactory.buildFor(beans);
    ObjectValidator<Bean> validator =
        new ObjectValidator<>() {
          @Override
          public void validate(
              Bean subject,
              String propertyName,
              ValidationContext<Bean> ctx,
              Collection<Bean> optionalSubjectCollection,
              ValidationContext<?> parentCtx) {
            ctx.hasText(Bean::getString1);
            ctx.lengthGe(Bean::getString2, 10);
          }
        };
    ValidationErrors ret = ctx.validateCollection(Beans::getBeans, validator);
    assertEquals(
        "beans: \n"
            + "	0: \n"
            + "		string2: code = 'validation.length.mustBe.greaterOrEqual', args = [10]\n"
            + "	1: \n"
            + "		string1: code = 'validation.must.haveText'\n"
            + "		string2: code = 'validation.length.mustBe.greaterOrEqual', args = [10]",
        ret.toString());
  }

  @Test
  void test_isHasErrors() {
    var f = new ValidationErrors("asd", new LinkedList<>());
    assertFalse(f.isHasErrors());

    f.add(new MustHaveText("a"));
    assertTrue(f.isHasErrors());
  }

  @Test
  void test_findErrorOfType() {
    var f = new ValidationErrors("asd", new LinkedList<>());
    f.add(new MustHaveText("a"));
    f.add(new LengthMustBeGreater("a", 1));

    LengthMustBeGreater ve = f.findErrorOfType(LengthMustBeGreater.class);
    assertNotNull(ve);
    assertEquals(1, ve.getMessageArgs()[0]);
  }

  @Test
  void test_setValidationErrors() {
    var f = new ValidationErrors("asd", new LinkedList<>());
    List<ValidationError> vee = new ArrayList<>();
    f.setList(vee);
    assertSame(vee, f.getList());
  }

  @Test
  void test_io() {
    var f = new ValidationErrors("asd", new LinkedList<>());
    f.add(new MustHaveText("a"));
    f.add(new LengthMustBeGreater("a", 1));

    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(ValidationError.class, new ValidationErrorGsonTypeAdapter());
    Gson gson = builder.create();
    String json = gson.toJson(f);
    f = gson.fromJson(json, ValidationErrors.class);

    assertNotNull(f);
    assertTrue(f.isHasErrors());
    assertEquals(2, f.getList().size());

    // check subtypes deserialization
    assertNotNull(f.findErrorOfType(LengthMustBeGreater.class));
    assertNotNull(f.findErrorOfType(MustHaveText.class));

    // check args deserialization
    assertEquals("a", f.findErrorOfType(MustHaveText.class).getPropertyName());
    assertEquals(1, f.findErrorOfTypeForField(LengthMustBeGreater.class, "a").getMessageArgs()[0]);
  }
}
