package org.summerb.approaches.jdbccrud.api.dto.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.microservices.users.api.dto.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EntityChangedEventAdapterTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testDeserializeExpectOkForAllowedClass() {
		Gson gson = getFixture();

		EntityChangedEvent<User> evt = gson.fromJson(
				"{\"ct\": \"asdasd\", \"vt\": \"org.summerb.microservices.users.api.dto.User\", \"v\": {\"email\": \"asd\"}}",
				EntityChangedEvent.class);

		assertNotNull(evt);
		assertEquals("asd", evt.getValue().getEmail());
	}

	private Gson getFixture() {
		GsonBuilder b = new GsonBuilder();
		b.registerTypeAdapter(EntityChangedEvent.class, new EntityChangedEventAdapter());
		Gson gson = b.create();
		return gson;
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("rawtypes")
	public void testDeserializeExpectExceptionForNotAllowedClass() {
		Gson gson = getFixture();

		EntityChangedEvent evt = gson.fromJson(
				"{\"ct\": \"asdasd\", \"vt\": \"javax.print.event.PrintEvent\", \"v\": {\"source\": \"asd\"}}",
				EntityChangedEvent.class);

		fail("We should be dead by now");
	}
}
