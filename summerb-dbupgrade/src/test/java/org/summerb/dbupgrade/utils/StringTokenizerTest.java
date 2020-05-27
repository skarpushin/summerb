package org.summerb.dbupgrade.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.summerb.dbupgrade.utils.StringTokenizer.SubString;

public class StringTokenizerTest {
	private static final SubString COMMA = new SubString(",");
	private static final SubString ESCAPED_COMMA = new SubString("\\,");
	private static final SubString SINGLE_LINE_COMMENT = new SubString("--");
	private static final SubString MULTI_LINE_COMMENT_OPEN = new SubString("/*");
	private static final SubString MULTI_LINE_COMMENT_CLOSE = new SubString("*/");
	private static final SubString LARGE_DELIM = new SubString("&&&&&");

	@Test
	public void testNext() {
		StringTokenizer f = new StringTokenizer("aaa,bbb--ccc/*ddd*/", COMMA, SINGLE_LINE_COMMENT,
				MULTI_LINE_COMMENT_OPEN, MULTI_LINE_COMMENT_CLOSE);
		assertEquals("aaa", toString(f.next()));
		assertEquals(",", toString(f.next()));
		assertEquals("bbb", toString(f.next()));
		assertEquals("--", toString(f.next()));
		assertEquals("ccc", toString(f.next()));
		assertEquals("/*", toString(f.next()));
		assertEquals("ddd", toString(f.next()));
		assertEquals("*/", toString(f.next()));
		assertNull(toString(f.next()));
	}

	private String toString(SubString next) {
		if (next == null) {
			return null;
		}
		return new StringBuilder(next).toString();
	}

	@Test
	public void testNextNoDelims() {
		StringTokenizer f = new StringTokenizer("aaaasdasdasdasdasd", COMMA, SINGLE_LINE_COMMENT,
				MULTI_LINE_COMMENT_OPEN, MULTI_LINE_COMMENT_CLOSE);
		assertEquals("aaaasdasdasdasdasd", toString(f.next()));
		assertNull(toString(f.next()));
	}

	@Test
	public void testNextExpectDalrgeDelimDiscarded() {
		StringTokenizer f = new StringTokenizer("x,xx&&&&&xxxx", COMMA, LARGE_DELIM);
		assertEquals("x", toString(f.next()));
		assertEquals(",", toString(f.next()));
		assertEquals("xx", toString(f.next()));
		assertEquals("&&&&&", toString(f.next()));
		assertEquals("xxxx", toString(f.next()));
		assertNull(toString(f.next()));
	}

	@Test
	public void testNextExpectEscapedDelimeterRecognizedFirst() {
		StringTokenizer f = new StringTokenizer(",aaa\\,bbb,", COMMA, ESCAPED_COMMA);
		assertEquals(",", toString(f.next()));
		assertEquals("aaa", toString(f.next()));
		assertEquals("\\,", toString(f.next()));
		assertEquals("bbb", toString(f.next()));
		assertEquals(",", toString(f.next()));
		assertNull(toString(f.next()));
	}

	@Test
	public void testNextSomeDelimsRemovedRightAway() {
		StringTokenizer f = new StringTokenizer("aaaa,sdasdasdasdasd", COMMA, SINGLE_LINE_COMMENT,
				MULTI_LINE_COMMENT_OPEN, MULTI_LINE_COMMENT_CLOSE);
		assertEquals("aaaa", toString(f.next()));
		assertEquals(",", toString(f.next()));
		assertEquals("sdasdasdasdasd", toString(f.next()));
		assertNull(toString(f.next()));
	}

}
