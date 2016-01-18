package org.summerb.microservices.properties.impl;

public class PropertyServiceImplFactory {

	public static PropertyServiceImpl createInstance() {
		PropertyServiceImpl ret = new PropertyServiceImpl();

		// PropertyDao propertyDao = new PropertyStoreDaoMySqlImpl();
		// ret.setPropertyDao(propertyDao);

		return ret;
	}
}
