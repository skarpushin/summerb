package org.summerb.approaches.jdbccrud.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.summerb.approaches.jdbccrud.api.StringIdGenerator;

public class StringIdGeneratorAlphaNumericImplTest {
	@Test
	public void testGenerateNewId() {
		StringIdGenerator f = new StringIdGeneratorAlphaNumericImpl();
		String result = f.generateNewId(null);
		assertNotNull(result);
		assertEquals(8, result.length());
	}

	@Test
	public void testIsValidId() {
		StringIdGenerator f = new StringIdGeneratorAlphaNumericImpl();

		for (int i = 0; i < 1000; i++) {
			String result = f.generateNewId(null);
			// System.out.println("ID generated: " + result);

			assertNotNull(result);
			assertEquals(8, result.length());
			assertTrue(f.isValidId(result));
		}
	}
}
