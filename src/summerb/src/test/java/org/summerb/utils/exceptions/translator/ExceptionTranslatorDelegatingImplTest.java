package org.summerb.utils.exceptions.translator;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

public class ExceptionTranslatorDelegatingImplTest {
	@Test
	public void testAppendJoiner1() {
		ExceptionTranslatorDelegatingImpl f = new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
		f.setJoinerString(". ");

		StringBuilder ret = new StringBuilder();
		ret.append("Asd. ");
		f.appendJoiner(ret);

		assertEquals("Asd. ", ret.toString());
	}

	@Test
	public void testAppendJoiner2() {
		ExceptionTranslatorDelegatingImpl f = new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
		f.setJoinerString(". ");

		StringBuilder ret = new StringBuilder();
		ret.append("Asd");
		f.appendJoiner(ret);

		assertEquals("Asd. ", ret.toString());
	}

	@Test
	public void testAppendJoiner3() {
		ExceptionTranslatorDelegatingImpl f = new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
		f.setJoinerString(". ");

		StringBuilder ret = new StringBuilder();
		ret.append("Asd.");
		f.appendJoiner(ret);

		assertEquals("Asd. ", ret.toString());
	}

	@Test
	public void testAppendJoiner4() {
		ExceptionTranslatorDelegatingImpl f = new ExceptionTranslatorDelegatingImpl(Collections.emptyList());
		f.setJoinerString(". ");

		StringBuilder ret = new StringBuilder();
		ret.append("asd. asd");
		f.appendJoiner(ret);

		assertEquals("asd. asd. ", ret.toString());
	}
}
