package org.summerb.approaches.jdbccrud.impl.relations;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.relations.ManyToManyDto;
import org.summerb.approaches.jdbccrud.impl.mysql.EasyCrudDaoMySqlImpl;

public class EasyCrudM2mDaoImpl<T1Id, T1Dto extends HasId<T1Id>, T2Id, T2Dto extends HasId<T2Id>>
		extends EasyCrudDaoMySqlImpl<Long, ManyToManyDto<T1Id, T2Id>> {

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		if (getDtoClass() == null) {
			ManyToManyDto<T1Id, T2Id> d = new ManyToManyDto<T1Id, T2Id>();
			// NOTE: I have no idea what is wrong with compiler or my
			// understanding of generics. That's why I did this dirty workaround
			setDtoClass((Class<ManyToManyDto<T1Id, T2Id>>) d.getClass());
		}
		super.afterPropertiesSet();
	}
}
