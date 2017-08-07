package org.summerb.approaches.jdbccrud.api.relations;

import java.util.Collection;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;

/**
 * This interface provides information regarding references structure. It's used
 * mostly for resolving instances of referenced objects.
 * 
 * @author sergeyk
 *
 */
public interface ReferencesRegistry {
	Ref getRefByName(String name) throws IllegalArgumentException;

	/**
	 * Get all references originating from provided table
	 * 
	 * @param sourceEntityName
	 *            sett {@link EasyCrudService#getEntityTypeMessageCode()}
	 */
	Collection<Ref> findRefsFromSource(String sourceEntityName);

	Collection<Ref> getRefsByAlias(String refsAlias) throws IllegalArgumentException;
}
