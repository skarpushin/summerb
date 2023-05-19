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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.relations.ReferencesRegistry;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.datapackage.DataSet;

import integr.org.summerb.easycrud.dtos.TestDto1;
import integr.org.summerb.easycrud.dtos.TestDto2;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DataSetLoaderImplTest {

  private DataSetLoaderImpl buildMockedInstance() {
    EasyCrudServiceResolver easyCrudServiceResolver = Mockito.mock(EasyCrudServiceResolver.class);
    ReferencesRegistry referencesRegistry = Mockito.mock(ReferencesRegistry.class);
    return new DataSetLoaderImpl(referencesRegistry, easyCrudServiceResolver);
  }

  @Test
  public void testLoadObjectsByIds_ExpectIaeForEmptyIds() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();

    assertThrows(IllegalArgumentException.class, () -> fixture.loadObjectsByIds(null, "asdasd"));
  }

  @Test
  public void testLoadObjectsByIds_ExpectIaeForEmptyIds2() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    assertThrows(
        IllegalArgumentException.class, () -> fixture.loadObjectsByIds(new HashSet<>(), "asdasd"));
  }

  @Test
  public void testLoadObjectsByIds_ExpectOneLoadOk() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    EasyCrudService service = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto1")).thenReturn(service);
    TestDto1 dto = new TestDto1();
    when(service.findById(1)).thenReturn(dto);

    List<HasId> ret = fixture.loadObjectsByIds(ids(1), "dto1");
    assertNotNull(ret);
    assertEquals(1, ret.size());
    assertEquals(dto, ret.get(0));
  }

  @Test
  public void testLoadObjectsByIds_ExpectOneLoadNfe() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    EasyCrudService service = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto1")).thenReturn(service);
    when(service.findById(1)).thenReturn(null);

    assertThrows(
        GenericEntityNotFoundException.class, () -> fixture.loadObjectsByIds(ids(1), "dto1"));
  }

  private Set<Object> ids(Object... pids) {
    Set<Object> ids = new HashSet<>(Arrays.asList(pids));
    return ids;
  }

  @Test
  public void testLoadObjectsByIds_ExpectManyLoadByLongsOk() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    EasyCrudService service = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto1")).thenReturn(service);

    PaginatedList mockret =
        new PaginatedList<>(new PagerParams(), Arrays.asList(new TestDto1(), new TestDto1()), 2);
    when(service.find(any(PagerParams.class), eq(Query.n().in(HasId.FN_ID, new Long[] {1L, 2L}))))
        .thenReturn(mockret);

    List<HasId> ret = fixture.loadObjectsByIds(ids(1L, 2L), "dto1");
    assertNotNull(ret);
    assertEquals(2, ret.size());
  }

  @Test
  public void testLoadObjectsByIds_ExpectManyLoadByLongsNfe() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    EasyCrudService service = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto1")).thenReturn(service);

    PaginatedList mockret = new PaginatedList<>(new PagerParams(), Collections.emptyList(), 0);
    when(service.find(any(PagerParams.class), any(Query.class))).thenReturn(mockret);

    assertThrows(
        GenericEntityNotFoundException.class, () -> fixture.loadObjectsByIds(ids(1L, 2L), "dto1"));
  }

  @Test
  public void testLoadObjectsByIds_ExpectManyLoadByStrings() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    EasyCrudService service = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto1")).thenReturn(service);

    PaginatedList mockret =
        new PaginatedList<>(new PagerParams(), Arrays.asList(new TestDto1(), new TestDto1()), 2);
    when(service.find(
            any(PagerParams.class), eq(Query.n().in(HasId.FN_ID, new String[] {"s1", "s2"}))))
        .thenReturn(mockret);

    List<HasId> ret = fixture.loadObjectsByIds(ids("s1", "s2"), "dto1");
    assertNotNull(ret);
    assertEquals(2, ret.size());
  }

  @Test
  public void testLoadObjectsByIds_ExpectManyLoadByUnknownType() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    EasyCrudService service = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto1")).thenReturn(service);

    UUID d1 = UUID.randomUUID();
    UUID d2 = UUID.randomUUID();

    when(service.findById(d1)).thenReturn(new TestDto1());
    when(service.findById(d2)).thenReturn(new TestDto1());

    List<HasId> ret = fixture.loadObjectsByIds(ids(d1, d2), "dto1");
    assertNotNull(ret);
    assertEquals(2, ret.size());
  }

  @Test
  public void testLoadObjectsByIds_ExpectTwoDifferentObjectsLoadedOk() throws Exception {
    DataSetLoaderImpl fixture = buildMockedInstance();
    EasyCrudService service1 = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto1")).thenReturn(service1);
    TestDto1 dto1 = new TestDto1();
    dto1.setId("d1");
    when(service1.findById("d1")).thenReturn(dto1);

    EasyCrudService service2 = Mockito.mock(EasyCrudService.class);
    when(fixture.getEasyCrudServiceResolver().resolveByRowMessageCode("dto2")).thenReturn(service2);
    TestDto2 dto2 = new TestDto2();
    dto2.setId(2L);
    when(service2.findById(2L)).thenReturn(dto2);

    DataSet ret = new DataSet();
    Map<String, Set<Object>> ids = new HashMap<>();
    ids.put("dto1", ids("d1"));
    ids.put("dto2", ids(2L));
    fixture.loadObjectsByIds(ids, ret);

    assertNotNull(ret.get("dto1"));
    assertNotNull(ret.get("dto1").find("d1"));

    assertNotNull(ret.get("dto2"));
    assertNotNull(ret.get("dto2").find(2L));
  }
}
