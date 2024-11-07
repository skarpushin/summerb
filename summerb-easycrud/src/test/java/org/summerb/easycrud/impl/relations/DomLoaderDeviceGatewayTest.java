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
package org.summerb.easycrud.impl.relations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.relations.DataSetLoader;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.relations.Ref;
import org.summerb.easycrud.impl.relations.example.Device;
import org.summerb.easycrud.impl.relations.example.Env;
import org.summerb.easycrud.impl.relations.example.Refs;
import org.summerb.utils.Pair;

public class DomLoaderDeviceGatewayTest {

  @Test
  public void testResolveCollectionElementType_expectCorrectFieldTypeResolution() {
    // Deps and fixture
    DataSetLoader dataSetLoader = mock(DataSetLoader.class);
    EasyCrudServiceResolver easyCrudServiceResolver = mock(EasyCrudServiceResolver.class);
    DomLoaderImpl f = new DomLoaderImpl(dataSetLoader, easyCrudServiceResolver);

    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(Env.class, "devices");

    // test
    Class<HasId<Object>> elType = f.resolveCollectionElementType(pd);
    assertEquals(Device.class, elType);
  }

  @Test
  public void testDiscoverDomFields() {
    DataSetLoader dataSetLoader = mock(DataSetLoader.class);
    EasyCrudServiceResolver easyCrudServiceResolver = mock(EasyCrudServiceResolver.class);
    DomLoaderImpl f = new DomLoaderImpl(dataSetLoader, easyCrudServiceResolver);

    Map<String, Ref> refs = Collections.singletonMap(Refs.envDevices.getName(), Refs.envDevices);
    List<Pair<Ref, PropertyDescriptor>> domFields = f.discoverDomFields(Env.class, refs);
    assertNotNull(domFields);
    assertEquals(1, domFields.size());
    assertEquals("devices", domFields.get(0).getValue().getName());
  }

}
