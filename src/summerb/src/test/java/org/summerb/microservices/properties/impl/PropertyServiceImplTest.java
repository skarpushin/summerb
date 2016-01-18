package org.summerb.microservices.properties.impl;

import org.junit.Test;
import org.summerb.microservices.properties.api.PropertyService;

public class PropertyServiceImplTest {
	@Test(expected = IllegalArgumentException.class)
	public void testPutSubjectProperty_defensive_nullApp() {
		PropertyService fixture = PropertyServiceImplFactory.createInstance();
		fixture.putSubjectProperty(null, "", "", "", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutSubjectProperty_defensive_emptyApp() {
		PropertyService fixture = PropertyServiceImplFactory.createInstance();
		fixture.putSubjectProperty(null, "", "", "", "");
	}

}
