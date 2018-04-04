package org.summerb.approaches.jdbccrud.impl.relations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.summerb.approaches.jdbccrud.api.EasyCrudServiceResolver;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.relations.DataSetLoader;
import org.summerb.approaches.jdbccrud.api.relations.ReferencesRegistry;
import org.summerb.approaches.jdbccrud.impl.relations.DomLoaderImpl.Pair;
import org.summerb.approaches.jdbccrud.impl.relations.example.Device;
import org.summerb.approaches.jdbccrud.impl.relations.example.DeviceRow;
import org.summerb.approaches.jdbccrud.impl.relations.example.DeviceService;
import org.summerb.approaches.jdbccrud.impl.relations.example.Env;
import org.summerb.approaches.jdbccrud.impl.relations.example.EnvService;
import org.summerb.approaches.jdbccrud.impl.relations.example.EnvironmentRow;
import org.summerb.approaches.jdbccrud.impl.relations.example.Refs;

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
	public void testDiscoverDomFields() throws Exception {
		DataSetLoader dataSetLoader = mock(DataSetLoader.class);
		EasyCrudServiceResolver easyCrudServiceResolver = mock(EasyCrudServiceResolver.class);
		DomLoaderImpl f = new DomLoaderImpl(dataSetLoader, easyCrudServiceResolver);

		Map<String, Ref> refs = Collections.singletonMap(Refs.envDevices.getName(), Refs.envDevices);
		List<Pair<Ref, PropertyDescriptor>> domFields = f.discoverDomFields(Env.class, refs);
		assertNotNull(domFields);
		assertEquals(1, domFields.size());
		assertEquals("devices", domFields.get(0).getValue().getName());
	}

	@Test
	public void testMapDtoToDom_expectCorrectFieldTypeResolution() throws Exception {
		// Deps and fixture
		ReferencesRegistry referencesRegistry = new Refs();
		EasyCrudServiceResolver easyCrudServiceResolver = mock(EasyCrudServiceResolver.class);

		DeviceService deviceService = mock(DeviceService.class);
		when(deviceService.getEntityTypeMessageCode()).thenReturn(DeviceService.ENTITY_TYPE_MESSAGE_CODE);
		when(easyCrudServiceResolver.resolveByDtoClass(DeviceRow.class)).thenReturn(deviceService);
		when(easyCrudServiceResolver.resolveByEntityType(DeviceService.ENTITY_TYPE_MESSAGE_CODE))
				.thenReturn(deviceService);

		EnvService envService = mock(EnvService.class);
		when(envService.getEntityTypeMessageCode()).thenReturn(EnvService.ENTITY_TYPE_MESSAGE_CODE);
		when(easyCrudServiceResolver.resolveByDtoClass(EnvironmentRow.class)).thenReturn(envService);
		when(easyCrudServiceResolver.resolveByEntityType(EnvService.ENTITY_TYPE_MESSAGE_CODE)).thenReturn(envService);

		DataSetLoaderImpl dataSetLoader = new DataSetLoaderImpl();
		dataSetLoader.setEasyCrudServiceResolver(easyCrudServiceResolver);
		dataSetLoader.setReferencesRegistry(referencesRegistry);

		DomLoaderImpl f = new DomLoaderImpl(dataSetLoader, easyCrudServiceResolver);

		// Setup DataSet
		// DataSet ds = new DataSet();

		EnvironmentRow envRow = new EnvironmentRow();
		envRow.setId(1L);
		envRow.setName("Hurray");
		when(envService.query(any(), any(), any()))
				.thenReturn(new PaginatedList<>(PagerParams.ALL, Arrays.asList(envRow), 1));
		when(envService.findById(1L)).thenReturn(envRow);
		// ds.get(EnvService.ENTITY_TYPE_MESSAGE_CODE).put(envRow);

		DeviceRow deviceRow = new DeviceRow();
		deviceRow.setId(2L);
		deviceRow.setEnvId(1);
		deviceRow.setName("Yes it is");
		when(deviceService.query(any(), any()))
				.thenReturn(new PaginatedList<>(PagerParams.ALL, Arrays.asList(deviceRow), 1));
		when(deviceService.findById(2L)).thenReturn(deviceRow);
		// ds.get(DeviceService.ENTITY_TYPE_MESSAGE_CODE).put(deviceRow);

		// when(dataSetLoader.loadObjectsByIds(any(),
		// anyString())).thenReturn(Arrays.asList(envRow));

		// Now let's invoke it
		Env env = f.load(Env.class, 1L, Refs.envDevices, Refs.deviceEnv);
		// verify(deviceService, times(1)).query(any(), any(), any());
		// verify(envService, times(1)).query(any(), any(), any());

		// Verify
		assertNotNull(env);
		assertEquals("Hurray", env.getName());
		assertNotNull(env.getDevices());
		assertEquals(1, env.getDevices().size());
		Device device = env.getDevices().get(0);
		assertEquals("Yes it is", device.getName());
		assertNotNull(device.getEnv());
		assertEquals(env, device.getEnv());
	}

}
