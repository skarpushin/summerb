package org.summerb.utils.collection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OneWayListTest {

	@Test
	public void testExpectAllItemsListedByIterator() {
		OneWayList<Long> longs = new OneWayList<Long>();
		for (long l = 1; l <= 10; l++) {
			longs.append(l);
		}

		OneWayList<Long>.OneWayIterator iter = longs.iterator();
		for (long l = 1; l <= 10; l++) {
			assertEquals(Long.valueOf(l), iter.next());
		}
	}

	@Test
	public void testExpectHeadWillBeShiftedCorrectly() {
		OneWayList<Long> longs = new OneWayList<Long>();
		for (long l = 1; l <= 10; l++) {
			longs.append(l);
		}

		OneWayList<Long>.OneWayIterator iter = longs.iterator();
		for (long l = 1; l <= 10; l++) {
			iter.next();
			if (l == 5) {
				iter.shiftListHead();
			}
		}

		iter = longs.iterator();
		for (long l = 5; l <= 10; l++) {
			assertEquals(Long.valueOf(l), iter.next());
		}
	}

}
