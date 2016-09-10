package org.summerb.approaches.jdbccrud.impl.relations;

import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ReferencesRegistryPredefinedImpl extends ReferencesRegistryPreconfiguredAbstract {
	private Ref[] refs;

	public ReferencesRegistryPredefinedImpl(Ref... refs) {
		Preconditions.checkArgument(refs != null);
		this.refs = refs;
	}

	@Override
	protected Multimap<String, Ref> discoverRefsBySources() {
		Multimap<String, Ref> ret = HashMultimap.create();
		for (Ref ref : refs) {
			ret.put(ref.getFromEntity(), ref);
		}
		return ret;
	}

	@Override
	protected Multimap<String, Ref> discoverAliases() {
		return HashMultimap.create();
	}

}
