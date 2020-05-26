package org.summerb.dbupgrade.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringTokenizerTest {

	@Test
	public void testNext() {
		StringTokenizer f = new StringTokenizer("aaa,bbb--ccc/*ddd*/", ",", "--", "/*", "*/");
		assertEquals("aaa", f.next());
		assertEquals(",", f.next());
		assertEquals("bbb", f.next());
		assertEquals("--", f.next());
		assertEquals("ccc", f.next());
		assertEquals("/*", f.next());
		assertEquals("ddd", f.next());
		assertEquals("*/", f.next());
		assertNull(f.next());
	}

}
