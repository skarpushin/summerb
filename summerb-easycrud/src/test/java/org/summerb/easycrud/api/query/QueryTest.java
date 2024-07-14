package org.summerb.easycrud.api.query;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class QueryTest {

  @Test
  public void testSameQueriesAreEqual() {
    assertEquals(buildQuery(false), buildQuery(false));
  }

  @Test
  public void testDifferentQueriesAreNotEqual() {
    assertNotEquals(buildQuery(false), buildQuery(true));
  }

  protected Query<?> buildQuery(boolean alternate) {
    Query<?> q = Query.n();

    q.isNull("fn");
    q.isNotNull("fn");
    q.isTrue("fn");
    q.isFalse("fn");
    q.eq("fn", "value");
    q.ne("fn", "value");
    q.less("fn", alternate ? 55 : 5);
    q.le("fn", 6);
    q.greater("fn", 7);
    q.ge("fn", 8);
    q.in("fn", alternate ? Arrays.asList(1, 2, 3) : Arrays.asList(1, 2, 3, 4));
    q.notIn("fn", Arrays.asList(1, 2, 3));
    q.between("fn", 1, 8);
    q.notBetween("fn", 4, 7);
    q.stringLengthBetween("fn", 4, 9);
    q.stringLengthNotBetween("fn", 3, 6);
    q.like("fn", "asd");
    q.notLike("fn", "asd");
    q.contains("fn", "asd");
    q.notContains("fn", "asd");
    q.startsWith("fn", alternate ? "prefix" : "prefix2");
    q.notStartsWith("fn", "prefix");
    q.endsWith("fn", "suffix");
    q.notEndsWith("fn", "suffix");
    q.empty("fn");
    q.notEmpty("fn");
    q.lengthLe("fn", 400);
    q.lengthLess("fn", 300);
    q.lengthGe("fn", 200);
    q.lengthGreater("fn", 100);

    return q;
  }
}
