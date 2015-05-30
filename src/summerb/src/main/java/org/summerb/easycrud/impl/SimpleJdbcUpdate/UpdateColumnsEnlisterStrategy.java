package org.summerb.easycrud.impl.SimpleJdbcUpdate;

import java.util.Collection;

public interface UpdateColumnsEnlisterStrategy {

	Collection<? extends String> getColumnsForUpdate(TableMetaDataContext tableMetaDataContext);

}
