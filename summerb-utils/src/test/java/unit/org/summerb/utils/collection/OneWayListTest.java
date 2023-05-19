/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package unit.org.summerb.utils.collection;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.summerb.utils.collection.OneWayList;

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
