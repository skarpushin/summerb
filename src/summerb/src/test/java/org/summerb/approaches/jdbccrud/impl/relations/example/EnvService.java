package org.summerb.approaches.jdbccrud.impl.relations.example;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

public interface EnvService extends EasyCrudService<Long, EnvironmentRow> {
	String ENTITY_TYPE_MESSAGE_CODE = "term.environment";
}
