package org.summerb.approaches.jdbccrud.impl.relations.example;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.dto.relations.RefQuantity;
import org.summerb.approaches.jdbccrud.api.dto.relations.RelationType;
import org.summerb.approaches.jdbccrud.impl.relations.ReferencesRegistryPredefinedImpl;

public class Refs extends ReferencesRegistryPredefinedImpl {
	public static final Ref deviceEnv = new Ref("deviceEnv", DeviceService.ENTITY_TYPE_MESSAGE_CODE,
			DeviceRow.FN_ENV_ID, EnvService.ENTITY_TYPE_MESSAGE_CODE, HasId.FN_ID, RelationType.PartOf,
			RefQuantity.Many2One);

	public static final Ref envDevices = new Ref("envDevices", EnvService.ENTITY_TYPE_MESSAGE_CODE, HasId.FN_ID,
			DeviceService.ENTITY_TYPE_MESSAGE_CODE, DeviceRow.FN_ENV_ID, RelationType.Aggregates, RefQuantity.One2Many);

	public Refs() {
		super(new Ref[] { deviceEnv, envDevices });
	}
}
