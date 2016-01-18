package org.summerb.approaches.jdbccrud.impl.SimpleJdbcUpdate;

import java.util.Collection;

public interface UpdateColumnsEnlisterStrategy {

	Collection<? extends String> getColumnsForUpdate(TableMetaDataContext tableMetaDataContext);

}
