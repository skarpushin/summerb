package org.summerb.approaches.jdbccrud.impl.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.EasyCrudServiceResolver;
import org.summerb.approaches.jdbccrud.api.dataset.DataSetUpdaterOnEntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent.ChangeType;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataTable;

/**
 * This impl will simply update dataSet tables with updated entities
 * 
 * WARNING: It doesn't not update any back-refs. Only table rows and only if
 * table is created before this operation. Former is actually hard to implement
 * since DataSet is not carrying information regarding references and it's not
 * clear how to avoid n+1 problems.
 * 
 * @author sergeyk
 *
 */
public class DataSetUpdaterOnEntityChangedEventImpl implements DataSetUpdaterOnEntityChangedEvent {
	private EasyCrudServiceResolver easyCrudServiceResolver;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void updateDataSet(DataSet dataSet, EntityChangedEvent<?> e) {
		if (!e.isTypeOf(HasId.class)) {
			return;
		}

		EasyCrudService service = easyCrudServiceResolver.resolveByDtoClass(e.getValue().getClass());
		if (!dataSet.getTables().containsKey(service.getEntityTypeMessageCode())) {
			return;
		}

		DataTable table = dataSet.getTables().get(service.getEntityTypeMessageCode());
		HasId dto = (HasId) e.getValue();
		if (e.getChangeType() == ChangeType.REMOVED) {
			table.getRows().remove(dto.getId());
		} else {
			table.put(dto);
		}
	}

	public EasyCrudServiceResolver getEasyCrudServiceResolver() {
		return easyCrudServiceResolver;
	}

	@Autowired
	public void setEasyCrudServiceResolver(EasyCrudServiceResolver easyCrudServiceResolver) {
		this.easyCrudServiceResolver = easyCrudServiceResolver;
	}

}
