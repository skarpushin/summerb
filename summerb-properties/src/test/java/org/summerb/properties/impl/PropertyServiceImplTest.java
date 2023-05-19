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
package org.summerb.properties.impl;

import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;
import org.summerb.properties.api.PropertyService;

public class PropertyServiceImplTest {
  @Test
  public void testPutSubjectProperty_defensive_nullApp() {
    PropertyService fixture = PropertyServiceImplFactory.createInstance();
    assertThrows(
        IllegalArgumentException.class, () -> fixture.putSubjectProperty(null, "", "", "", ""));
  }

  @Test
  public void testPutSubjectProperty_defensive_emptyApp() {
    PropertyService fixture = PropertyServiceImplFactory.createInstance();
    assertThrows(
        IllegalArgumentException.class, () -> fixture.putSubjectProperty(null, "", "", "", ""));
  }
}
