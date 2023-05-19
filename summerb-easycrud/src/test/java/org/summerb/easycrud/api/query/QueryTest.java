package org.summerb.easycrud.api.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class QueryTest {

  @Test
  public void testSameQueriesAreEqual() {
    Query q1 = buildQuery(false);
    Query q2 = buildQuery(false);
    assertEquals(q1, q2);
  }

  @Test
  public void testDifferentQueriesAreNotEqual() {
    Query q1 = buildQuery(false);
    Query q2 = buildQuery(true);
    assertNotEquals(q1, q2);
  }

  protected Query buildQuery(boolean alternate) {
    Query q = Query.n();
    q.between("asd", 1, 2);
    q.between("asdasd", "asd", "asd");
    q.contains("asdad", "asd");
    q.eq("asad", !alternate ? 2 : 1);
    q.eq("asda", "asd");
    q.ge("asda", 1);
    q.in("asasd", 2L, 3L);
    q.in("asad", "asd", "asd");
    q.inLongs("asad", Arrays.asList(1L, 2L));
    q.inStrings("asd", Arrays.asList("asd", "asd"));
    q.isFalse("asd");
    q.isNotNull("asad");
    q.isNull("asdasd");
    q.isTrue("asd");
    q.le("asasd", 2L);
    q.lengthBetween("asdasd", 1, 4);
    q.ne("asdasd", false);
    q.ne("asdasd", 5L);
    q.ne("asdad", "asda");
    q.notBetween("asdasd", 1, 5);
    q.notContains("asdasd", "asdasd");
    q.notIn("asasd", 2L, 3L);
    q.notIn("asad", "asd", "asd");
    q.notInLongs("asad", Arrays.asList(1L, 2L));
    q.notInStrings("asd", Arrays.asList("asd", "asd"));
    q.notLe("asda", 5L);
    q.notStartsWith("asdasd", "asad");
    q.or(Query.n().eq("asd", "asd"));
    q.startsWith("asdasd", "asdasd");
    return q;
  }
}
