package org.summerb.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.summerb.utils.exceptions.ExceptionUtils;

public class ExceptionUtilsTest {

	@Test
	public void testCalculateExceptionCode() {
		String code = null;
		for (int i = 0; i < 15; i++) {
			try {
				doThrow(i);
			} catch (Throwable t) {
				if (code == null) {
					code = ExceptionUtils.calculateExceptionCode(t);
				} else {
					assertEquals(code, ExceptionUtils.calculateExceptionCode(t));
				}
			}
		}
	}

	private void doThrow(int i) {
		throw new IllegalStateException("Some test exception with additonal data: " + i);
	}
}
