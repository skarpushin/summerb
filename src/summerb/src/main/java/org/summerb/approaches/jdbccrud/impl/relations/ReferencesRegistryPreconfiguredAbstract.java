package org.summerb.approaches.jdbccrud.impl.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.relations.ReferencesRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Simple base class impl that assuming workflow: a). all references discovered
 * and then b). lookup using maps.
 * 
 * Subclass must provide references list.
 * 
 * @author sergeyk
 *
 */
public abstract class ReferencesRegistryPreconfiguredAbstract implements ReferencesRegistry {
	private Map<String, Ref> refsByName = new HashMap<>();
	private Multimap<String, Ref> refsBySourceName = HashMultimap.create();
	private Multimap<String, Ref> refsByAlias = HashMultimap.create();
	private boolean isInitialized;

	public ReferencesRegistryPreconfiguredAbstract() {
	}

	private void initialize() {
		refsBySourceName = discoverRefsBySources();
		Preconditions.checkState(refsBySourceName != null);

		for (Ref ref : refsBySourceName.values()) {
			Ref previous = refsByName.put(ref.getName(), ref);
			if (previous != null) {
				throw new RuntimeException("Duplicate reference name not allowed " + ref.getName());
			}
		}

		refsByAlias = discoverAliases();
		Preconditions.checkState(refsByAlias != null);
	}

	protected abstract Multimap<String, Ref> discoverRefsBySources();

	protected abstract Multimap<String, Ref> discoverAliases();

	@Override
	public Ref getRefByName(String name) throws IllegalArgumentException {
		ensureInitialized();
		Ref ret = refsByName.get(name);
		Preconditions.checkArgument(ret != null, "There is not ref named as " + name);
		return ret;
	}

	private void ensureInitialized() {
		if (!isInitialized) {
			initialize();
		}
	}

	@Override
	public Collection<Ref> findRefsFromSource(String sourceEntityName) {
		ensureInitialized();
		return refsBySourceName.get(sourceEntityName);
	}

	@Override
	public Collection<Ref> getRefsByAlias(String refsAlias) throws IllegalArgumentException {
		ensureInitialized();
		Collection<Ref> ret = refsByAlias.get(refsAlias);
		Preconditions.checkArgument(ret != null, "There is no references alias named as " + refsAlias);
		return ret;
	}

}
