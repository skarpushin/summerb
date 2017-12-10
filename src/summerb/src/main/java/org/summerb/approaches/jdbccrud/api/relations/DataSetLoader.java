package org.summerb.approaches.jdbccrud.api.relations;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

/**
 * This interface provides convenient methods for loading multiple objects at
 * once. It allows to follow references which make it look like mini ORD
 * framework that I was hoping to avoid.
 * 
 * Impl is supposed to avoid n+1 performance issue and perform as few requests
 * as possible.
 * 
 * @author sergeyk
 *
 */
public interface DataSetLoader {
	/**
	 * Simplified version of {@link #loadObjectsByIds(Map, DataSet)} allows to
	 * load only objects of certain type
	 * 
	 * @param ids
	 *            of objects to load
	 * @param entityTypeName
	 *            type of entity. Same as returned by it's service
	 */
	List<HasId> loadObjectsByIds(Set<Object> ids, String entityTypeName)
			throws EntityNotFoundException, NotAuthorizedException;

	void loadObjectsByIds(Set<Object> ids, String entityTypeName, DataSet dataSet)
			throws EntityNotFoundException, NotAuthorizedException;

	void loadObjectAndItsRefs(Object id, String entityTypeName, DataSet dataSet, Ref... references)
			throws EntityNotFoundException, NotAuthorizedException;

	/**
	 * Loads all objects specified in ids param into provided dataSet (it might
	 * be empty).
	 * 
	 * It will NOT load referenced objects. Use
	 * {@link #resolveReferencedObjects(DataSet, Ref...)} if needed
	 * 
	 * @param ids
	 *            map of ids to load. Entitype is mapped to list of ids
	 * @param dataSet
	 *            target for loaded data.
	 */
	void loadObjectsByIds(Map<String, Set<Object>> ids, DataSet dataSet)
			throws EntityNotFoundException, NotAuthorizedException;

	/**
	 * That method will load into dataset all objects that are referenced by
	 * referenceNames but not exists in dataSet yet.
	 * 
	 * In case dataSet and/or references and/or non of the dataSet rows have
	 * matched references - method will end silently without exceptions.
	 * 
	 * @param dataSet
	 *            data set with some objects present in it
	 * @param references
	 *            list of references that needs to be satisfied (referenced
	 *            objects loaded)
	 * @throws EntityNotFoundException
	 *             in case referenced object not found
	 * @throws NotAuthorizedException
	 *             in case user is not authorized to read certain objects
	 * @throws IllegalArgumentException
	 *             in case dataSet and/or references is null
	 */
	void resolveReferencedObjects(DataSet dataSet, Ref... references)
			throws EntityNotFoundException, NotAuthorizedException;

}
