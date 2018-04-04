package org.summerb.approaches.jdbccrud.impl.relations.example;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

public interface DeviceService extends EasyCrudService<Long, DeviceRow> {
	String ENTITY_TYPE_MESSAGE_CODE = "term.device";
}
