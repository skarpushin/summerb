package org.summerb.approaches.jdbccrud.impl.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.relations.ReferencesRegistry;

import com.google.common.base.Preconditions;
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
	private Map<String, Ref> refsByName;
	private Multimap<String, Ref> refsBySourceName;
	private Multimap<String, Ref> refsByAlias;

	public ReferencesRegistryPreconfiguredAbstract() {
	}

	private void initialize() {
		Multimap<String, Ref> refsBySourceNameLocal = discoverRefsBySources();
		Preconditions.checkState(refsBySourceNameLocal != null);
		Map<String, Ref> refsByNameLocal = new HashMap<>();

		for (Ref ref : refsBySourceNameLocal.values()) {
			Ref previous = refsByNameLocal.put(ref.getName(), ref);
			if (previous != null) {
				throw new RuntimeException("Duplicate reference name not allowed " + ref.getName());
			}
		}

		Multimap<String, Ref> refsByAliasLocal = discoverAliases();
		Preconditions.checkState(refsByAliasLocal != null);

		refsBySourceName = refsBySourceNameLocal;
		refsByName = refsByNameLocal;
		refsByAlias = refsByAliasLocal;
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
		if (refsByAlias == null) {
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
