/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.easycrud.api.dto.tools;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.summerb.utils.easycrud.api.dto.EntityChangedEvent;
import org.summerb.utils.easycrud.api.dto.tools.EntityChangedEventAdapter;

public class EntityChangedEventAdapterTest {
  @SuppressWarnings("rawtypes")
  @Test
  public void testDeserializeExpectOkForAllowedClass() {
    Gson gson = getFixture();

    EntityChangedEvent evt =
        gson.fromJson(
            "{\"ct\": \"ADDED\", \"vt\": \"org.summerb.easycrud.api.dto.tools.TestDto\", \"v\": {\"email\": \"asd\"}}",
            EntityChangedEvent.class);

    assertNotNull(evt);
    assertEquals("asd", ((TestDto) evt.getValue()).getEmail());
  }

  private Gson getFixture() {
    GsonBuilder b = new GsonBuilder();
    b.registerTypeAdapter(EntityChangedEvent.class, new EntityChangedEventAdapter());
    return b.create();
  }

  @Test
  public void testDeserializeExpectExceptionForNotAllowedClass() {
    Gson gson = getFixture();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            gson.fromJson(
                "{\"ct\": \"asdasd\", \"vt\": \"javax.print.event.PrintEvent\", \"v\": {\"source\": \"asd\"}}",
                EntityChangedEvent.class));
  }
}
