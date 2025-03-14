package performance.org.summerb.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;
import org.summerb.validation.SmokeIntegrTest;
import org.summerb.validation.testDtos.TypicalBean;

public class PerformanceIntegrTest extends SmokeIntegrTest {

  @Test
  void testPerformance() {
    TypicalBean p = new TypicalBean();
    p.setProjectId("123");
    p.setPeriod(LocalDate.now());
    p.setType(" "); // invalid -- blank
    p.setWorkProductLocation("1234567");
    p.setScore(BigDecimal.ONE);
    p.setWorkProductLocationComment("12345678901234567890"); // invalid -- too long

    // warm-up
    runJakartaValidations(p);
    runValidationsUsingGetters(p);
    runValidationsUsingPropertyNames(p);

    // Now measure
    StopWatch stopWatch = new StopWatch("Measuring Validation performance");
    stopWatch.start("Test: Jakarta annotations");
    int iterations = 3000;
    for (int i = 0; i < iterations; i++) {
      runJakartaValidations(p);
    }
    stopWatch.stop();
    TaskInfo timingJakartaValidations = stopWatch.getLastTaskInfo();

    stopWatch.start("Test: ValidationContext w/ method references");
    for (int i = 0; i < iterations; i++) {
      runValidationsUsingGetters(p);
    }
    stopWatch.stop();
    TaskInfo timingMethodReferences = stopWatch.getLastTaskInfo();

    stopWatch.start("Test: ValidationContext w/ propertyNames");
    for (int i = 0; i < iterations; i++) {
      runValidationsUsingPropertyNames(p);
    }
    stopWatch.stop();
    TaskInfo timingPropertyNames = stopWatch.getLastTaskInfo();

    log.info("Performance test results: \n{}", stopWatch.prettyPrint());

    log.info(
        "Time per test ({} iterations, of total {} ms): \n\tJakarta: {} (nanos) =\t {} (ms)\n\tGetters: {} (nanos) =\t {} (ms)\n\t  Names: {} (nanos) =\t {} (ms)\n",
        iterations,
        stopWatch.getTotalTimeMillis(),
        timingJakartaValidations.getTimeNanos() / iterations,
        timingJakartaValidations.getTimeMillis() / iterations,
        timingMethodReferences.getTimeNanos() / iterations,
        timingMethodReferences.getTimeMillis() / iterations,
        timingPropertyNames.getTimeNanos() / iterations,
        timingPropertyNames.getTimeMillis() / iterations);
  }
}
