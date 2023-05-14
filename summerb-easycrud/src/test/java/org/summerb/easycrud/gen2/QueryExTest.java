package org.summerb.easycrud.gen2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.summerb.easycrud.query.ConditionEx;
import org.summerb.easycrud.query.FieldConditionEx;

import integr.org.summerb.easycrud.TestDto1;

public class QueryExTest {

  @Test
  public void testExpectPropertyNameDeterminedCorrectly() {
    var q = TestDto1.Q();
    q.eq(TestDto1::getEnv, "asdad");
    q.eq(TestDto1::isActive, false);

    assertEquals(2, q.getConditions().size());

    ConditionEx c1 = q.getConditions().get(0);
    assertEquals(FieldConditionEx.class, c1.getClass());
    FieldConditionEx fc1 = (FieldConditionEx) c1;
    assertEquals("env", fc1.getFieldName());

    ConditionEx c2 = q.getConditions().get(1);
    assertEquals(FieldConditionEx.class, c2.getClass());
    FieldConditionEx fc2 = (FieldConditionEx) c2;
    assertEquals("active", fc2.getFieldName());
  }
}
