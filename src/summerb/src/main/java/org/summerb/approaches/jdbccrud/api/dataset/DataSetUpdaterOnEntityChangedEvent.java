package org.summerb.approaches.jdbccrud.api.dataset;

import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;

public interface DataSetUpdaterOnEntityChangedEvent {
	void updateDataSet(DataSet dataSet, EntityChangedEvent<?> e);
}
