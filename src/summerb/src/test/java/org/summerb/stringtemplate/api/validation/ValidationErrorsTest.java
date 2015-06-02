package org.summerb.stringtemplate.api.validation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.summerb.validation.errors.MustBeEqualsValidationError;

public class ValidationErrorsTest {

	/**
	 * This test is to confirm bugfix for
	 * https://github.com/skarpushin/summerb/issues/9 fixed
	 */
	@Test
	public void testMustBeEqualsValidationError_expextCorrectArgumentsRetention() throws Exception {
		MustBeEqualsValidationError ve = new MustBeEqualsValidationError("a", "b", "ft");
		assertArrayEquals(new Object[] { "a", "b" }, ve.getMessageArgs());
	}

}