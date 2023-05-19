package org.summerb.validation;

import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.summerb.validation.testDtos.TypicalBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ValidationContextConfig.class})
public class SmokeIntegrTest {
  protected static final BigDecimal BIG_DECIMAL_100 = BigDecimal.valueOf(100);

  protected Logger log = LoggerFactory.getLogger(getClass());

  @Autowired ValidationContextFactory ctxFactory;

  @Test
  void testSmoke() {
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
  }

  protected void runValidationsUsingPropertyNames(TypicalBean p) {
    var ctx = new ValidationContext<>();
    ctx.hasText(p.getProjectId(), "projectId");
    ctx.notNull(p.getPeriod(), "period");
    ctx.hasText(p.getType(), "type");

    ctx.lengthLe(p.getWorkProductLocation(), 10, "workProductLocation");
    ctx.lengthLe(p.getWorkProductLocationComment(), 10, "workProductLocationComment");
    ctx.lengthLe(p.getComment(), 10, "comment");

    ctx.between(p.getScore(), BigDecimal.ONE, BIG_DECIMAL_100, "score");

    try {
      ctx.throwIfHasErrors();
      fail("ValidationErrors expected, but got none");
    } catch (ValidationException ve) {
      Assertions.assertEquals(2, ve.getErrors().size());
    }
  }

  protected void runValidationsUsingGetters(TypicalBean p) {
    var ctx = ctxFactory.buildFor(p);
    ctx.hasText(TypicalBean::getProjectId);
    ctx.notNull(TypicalBean::getPeriod);
    ctx.hasText(TypicalBean::getType);

    ctx.lengthLe(TypicalBean::getWorkProductLocation, 10);
    ctx.lengthLe(TypicalBean::getWorkProductLocationComment, 10);
    ctx.lengthLe(TypicalBean::getComment, 10);

    ctx.between(TypicalBean::getScore, BigDecimal.ONE, BIG_DECIMAL_100);

    try {
      ctx.throwIfHasErrors();
      fail("ValidationErrors expected, but got none");
    } catch (ValidationException ve) {
      Assertions.assertEquals(2, ve.getErrors().size());
    }
  }

  protected void runJakartaValidations(TypicalBean p) {
    var ctx = ctxFactory.buildFor(p);
    ctx.processJakartaValidations();
    try {
      ctx.throwIfHasErrors();
      fail("ValidationErrors expected, but got none");
    } catch (ValidationException ve) {
      Assertions.assertEquals(2, ve.getErrors().size());
    }
  }
}
