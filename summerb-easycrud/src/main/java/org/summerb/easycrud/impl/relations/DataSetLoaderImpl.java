/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.impl.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.relations.DataSetLoader;
import org.summerb.easycrud.api.relations.EasyCrudM2mService;
import org.summerb.easycrud.api.relations.ReferencesRegistry;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.datapackage.DataSet;
import org.summerb.easycrud.api.row.datapackage.DataTable;
import org.summerb.easycrud.api.row.datapackage.DataTable.RefToReferencedObjectsIdsMap;
import org.summerb.easycrud.api.row.datapackage.DataTable.RowIdToBackReferencesMap;
import org.summerb.easycrud.api.row.relations.Ref;
import org.summerb.easycrud.api.row.tools.EasyCrudDtoUtils;
import org.summerb.security.api.exceptions.NotAuthorizedException;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Simple impl that resolves references. It's not 100% efficient, but allows for quick bootstrap.
 * You can always override impl for more efficient impl if(when) needed. It's assumed that you'll
 * make use of aliases to know which cases to override.
 *
 * <p>OFFTOPIC: While writing this impl I was constantly asking myself: am I crossing the line
 * between reasonable custom micro framework and attempt to reinvent O/R mapping framework which I
 * tried to differentiate from when introducing EasyCrud concept...
 *
 * @author sergeyk
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class DataSetLoaderImpl implements DataSetLoader {
  protected ReferencesRegistry referencesRegistry;
  protected EasyCrudServiceResolver easyCrudServiceResolver;

  public DataSetLoaderImpl(
      ReferencesRegistry referencesRegistry, EasyCrudServiceResolver easyCrudServiceResolver) {
    Preconditions.checkArgument(referencesRegistry != null);
    Preconditions.checkArgument(easyCrudServiceResolver != null);

    this.referencesRegistry = referencesRegistry;
    this.easyCrudServiceResolver = easyCrudServiceResolver;
  }

  @Override
  public List<HasId> loadObjectsByIds(Set<Object> ids, String entityTypeName)
      throws EntityNotFoundException, NotAuthorizedException {
    Preconditions.checkArgument(!CollectionUtils.isEmpty(ids));

    EasyCrudService service = easyCrudServiceResolver.resolveByRowMessageCode(entityTypeName);
    Object firstId = ids.iterator().next();
    List<HasId> ret = new ArrayList<>(ids.size());
    if (ids.size() == 1) {
      HasId loaded = loadOne(firstId, service);
      ret.add(loaded);
    } else {
      Query<?> query = Query.n().in(HasId.FN_ID, ids);
      List loaded = loadMultipleByQuery(query, service);
      assertFound(
          loaded.size() == ids.size(), entityTypeName, "One of: " + Arrays.toString(ids.toArray()));
      ret.addAll(loaded);
    }
    return ret;
  }

  @Override
  public void loadObjectsByIds(Set<Object> ids, String entityTypeName, DataSet dataSet)
      throws EntityNotFoundException, NotAuthorizedException {
    Preconditions.checkArgument(dataSet != null);

    List<HasId> loaded = loadObjectsByIds(ids, entityTypeName);
    dataSet.get(entityTypeName).putAll(loaded);
  }

  protected HasId loadOne(Object id, EasyCrudService<Object, HasId<Object>> service)
      throws NotAuthorizedException, GenericEntityNotFoundException {
    HasId ret = service.findById(id);
    String entityTypeName = service.getRowMessageCode();
    assertFound(ret != null, entityTypeName, id);
    return ret;
  }

  protected List<HasId> loadMultipleByQuery(Query query, EasyCrudService service)
      throws NotAuthorizedException, GenericEntityNotFoundException {
    return new ArrayList<>(service.findAll(query));
  }

  protected void assertFound(
      boolean expressionToBeTrue, String subjectTypeMessageCode, Object identifier)
      throws GenericEntityNotFoundException {
    if (!expressionToBeTrue) {
      throw new GenericEntityNotFoundException(subjectTypeMessageCode, identifier.toString());
    }
  }

  @Override
  public void loadObjectsByIds(Map<String, Set<Object>> ids, DataSet dataSet)
      throws EntityNotFoundException, NotAuthorizedException {
    Preconditions.checkArgument(dataSet != null);
    Preconditions.checkArgument(!CollectionUtils.isEmpty(ids));

    EntityTypeToObjectsMap loadObjectsByIds = loadObjectsByIds(ids);
    addAllObjects(loadObjectsByIds, dataSet);
  }

  protected EntityTypeToObjectsMap loadObjectsByIds(Map<String, Set<Object>> ids)
      throws EntityNotFoundException, NotAuthorizedException {
    Preconditions.checkArgument(!CollectionUtils.isEmpty(ids));

    EntityTypeToObjectsMap ret = new EntityTypeToObjectsMap();
    for (String entityTypeName : ids.keySet()) {
      List<HasId> loadObjectsByIds = loadObjectsByIds(ids.get(entityTypeName), entityTypeName);
      ret.put(entityTypeName, loadObjectsByIds);
    }
    return ret;
  }

  @Override
  public void loadObjectAndItsRefs(
      Object id, String entityTypeName, DataSet dataSet, Ref... references)
      throws EntityNotFoundException, NotAuthorizedException {

    loadObjectsByIds(new HashSet<>(Arrays.asList(id)), entityTypeName, dataSet);
    loadReferencedObjects(dataSet, references);
  }

  @Override
  public void loadReferencedObjects(DataSet dataSet, Ref... references)
      throws EntityNotFoundException, NotAuthorizedException {
    Preconditions.checkArgument(dataSet != null);
    Preconditions.checkArgument(references != null);

    DataSet nextDataSet = dataSet;
    while (nextDataSet != null) {
      DataSet curDataSet = nextDataSet;
      nextDataSet = null;

      DataSet newDataSet = new DataSet();
      Map<String, Set<Object>> idsToLoad =
          enumerateOutgoingReferences(curDataSet, dataSet, references);
      if (!idsToLoad.isEmpty()) {
        EntityTypeToObjectsMap loadObjects = loadObjectsByIds(idsToLoad);
        addAllObjects(loadObjects, dataSet);
        addAllObjects(loadObjects, newDataSet);
      }

      // Handle one2many
      Map<Ref, Set<Object>> oneToManyReferencesToLoad =
          enumerateOneToManyReferences(curDataSet, references);
      if (!oneToManyReferencesToLoad.isEmpty()) {
        EntityTypeToObjectsMap loadedOneToManyRefs =
            loadOneToManyReferences(oneToManyReferencesToLoad);
        addAllObjects(loadedOneToManyRefs, dataSet);
        addAllObjects(loadedOneToManyRefs, newDataSet);
        populateBackReferencesOne2Many(loadedOneToManyRefs, oneToManyReferencesToLoad, dataSet);
      }

      // Handle many2many
      Map<Ref, Set<Object>> manyToManyRefToReferencersIds =
          enumerateManyToManyReferences(curDataSet, references);
      if (!manyToManyRefToReferencersIds.isEmpty()) {
        ManyToManyRefToReferenceesMap loadedManyToManyRefs =
            loadManyToManyReferences(manyToManyRefToReferencersIds);
        addAllObjects(loadedManyToManyRefs, dataSet);
        addAllObjects(loadedManyToManyRefs, newDataSet);
        populateBackReferencesMany2Many(loadedManyToManyRefs, dataSet);
      }

      // next iteration
      if (!newDataSet.isEmpty()) {
        nextDataSet = newDataSet;
      }
    }
  }

  protected void populateBackReferencesMany2Many(
      ManyToManyRefToReferenceesMap manyToManyRefs, DataSet dataSet) {
    for (Entry<Ref, Map<Object, List<HasId>>> refToReferenceeListPair : manyToManyRefs.entrySet()) {
      DataTable sourceTable = dataSet.get(refToReferenceeListPair.getKey().getFromEntity());
      @SuppressWarnings("deprecation")
      RowIdToBackReferencesMap backRefs = sourceTable.getBackRefs();

      for (Entry<Object, List<HasId>> referencerToReferencesListPair :
          refToReferenceeListPair.getValue().entrySet()) {
        Object referencerId = referencerToReferencesListPair.getKey();
        RefToReferencedObjectsIdsMap refToObjsMap = backRefs.get(referencerId);
        if (refToObjsMap == null) {
          backRefs.put(referencerId, refToObjsMap = new RefToReferencedObjectsIdsMap());
        }

        String refName = refToReferenceeListPair.getKey().getName();
        Set<Object> referenceeIdsList = refToObjsMap.get(refName);
        if (referenceeIdsList == null) {
          refToObjsMap.put(refName, referenceeIdsList = new HashSet<>());
        }
        Set referenceeIds =
            EasyCrudDtoUtils.enumerateIds(referencerToReferencesListPair.getValue());
        referenceeIdsList.addAll(referenceeIds);
      }
    }
  }

  protected void addAllObjects(ManyToManyRefToReferenceesMap manyToManyRefs, DataSet dataSet) {
    for (Entry<Ref, Map<Object, List<HasId>>> refToReferenceeListPair : manyToManyRefs.entrySet()) {
      DataTable targetTable = dataSet.get(refToReferenceeListPair.getKey().getToEntity());
      for (Entry<Object, List<HasId>> referencerToReferencesListPair :
          refToReferenceeListPair.getValue().entrySet()) {
        targetTable.putAll(referencerToReferencesListPair.getValue());
      }
    }
  }

  protected ManyToManyRefToReferenceesMap loadManyToManyReferences(
      Map<Ref, Set<Object>> manyToManyReferences) {
    // NOTE: Queries to same types of referencees are not grouped. Should we
    // impl this like we did for one2many?
    ManyToManyRefToReferenceesMap ret = new ManyToManyRefToReferenceesMap();
    for (Entry<Ref, Set<Object>> refToReferencersEntry : manyToManyReferences.entrySet()) {
      EasyCrudService m2mServiceTmp =
          easyCrudServiceResolver.resolveByRowMessageCode(
              refToReferencersEntry.getKey().getM2mEntity());
      Preconditions.checkState(m2mServiceTmp instanceof EasyCrudM2mService);
      EasyCrudM2mService m2mService = (EasyCrudM2mService) m2mServiceTmp;

      Map<Object, List<HasId>> referenceeMap =
          m2mService.findReferenceeByReferencers(refToReferencersEntry.getValue());
      ret.put(refToReferencersEntry.getKey(), referenceeMap);
    }
    return ret;
  }

  protected static class ManyToManyRefToReferenceesMap
      extends HashMap<Ref, Map<Object, List<HasId>>> {
    private static final long serialVersionUID = 8251505694174701237L;
  }

  protected void populateBackReferencesOne2Many(
      EntityTypeToObjectsMap rowsMap, Map<Ref, Set<Object>> refs, DataSet dataSet) {
    for (Entry<String, List<HasId>> entry : rowsMap.entrySet()) {
      for (HasId row : entry.getValue()) {
        PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(row);
        for (Ref ref : refs.keySet()) {
          if (!ref.getToEntity().equals(entry.getKey())) {
            continue;
          }

          Object referencedId = null;
          try {
            referencedId = propertyAccessor.getPropertyValue(ref.getToField());
          } catch (Throwable t) {
            throw new RuntimeException(
                "Failed to read property " + ref.getFromField() + " from " + row, t);
          }
          if (referencedId == null) {
            continue;
          }

          @SuppressWarnings("deprecation")
          RowIdToBackReferencesMap backRefs = dataSet.get(ref.getFromEntity()).getBackRefs();
          if (backRefs.get(referencedId) == null) {
            backRefs.put(referencedId, new RefToReferencedObjectsIdsMap());
          }

          RefToReferencedObjectsIdsMap refToObjsMap = backRefs.get(referencedId);
          if (refToObjsMap.get(ref.getName()) == null) {
            refToObjsMap.put(ref.getName(), new HashSet<>());
          }

          refToObjsMap.get(ref.getName()).add(row.getId());
        }
      }
    }
  }

  /**
   * key - is an entity type code
   *
   * <p>value - is a list of objects
   */
  protected static class EntityTypeToObjectsMap extends HashMap<String, List<HasId>> {
    protected static final long serialVersionUID = -522735093281679526L;
  }

  protected void addAllObjects(EntityTypeToObjectsMap loadedObjects, DataSet dataSet) {
    for (Entry<String, List<HasId>> entry : loadedObjects.entrySet()) {
      DataTable table = dataSet.get(entry.getKey());
      table.putAll(entry.getValue());
    }
  }

  protected EntityTypeToObjectsMap loadOneToManyReferences(
      Map<Ref, Set<Object>> refToReferencersIds) throws NotAuthorizedException {
    Multimap<String, Entry<Ref, Set<Object>>> targetEntityToRef = HashMultimap.create();
    for (Entry<Ref, Set<Object>> entry : refToReferencersIds.entrySet()) {
      targetEntityToRef.put(entry.getKey().getToEntity(), entry);
    }

    EntityTypeToObjectsMap ret = new EntityTypeToObjectsMap();
    for (String entityTypeCode : targetEntityToRef.keySet()) {
      Collection<Entry<Ref, Set<Object>>> entries = targetEntityToRef.get(entityTypeCode);
      List<Query<?>> queries = new ArrayList<>(entries.size());
      for (Entry<Ref, Set<Object>> entry : entries) {
        Set<Object> ids = entry.getValue();
        Ref ref = entry.getKey();

        queries.add(Query.FACTORY.build().in(ref.getToField(), ids));
      }
      Query<?> q = queries.size() == 1 ? queries.get(0) : (Query<?>) Query.n().or(queries);

      EasyCrudService service = easyCrudServiceResolver.resolveByRowMessageCode(entityTypeCode);
      ret.put(entityTypeCode, new ArrayList<>(service.findAll(q)));
    }

    return ret;
  }

  /**
   * @param scanForReferences dataSet to scan for Many2one and One2one referenced objects
   * @param checkForExistence data set that contains already loaded objects so that we can skip
   *     loading these objects again
   * @param references references to use
   * @return map entityTypeCode to list of ids of these entities to be loaded
   */
  protected Map<String, Set<Object>> enumerateOutgoingReferences(
      DataSet scanForReferences, DataSet checkForExistence, Ref[] references) {
    Map<String, Set<Object>> ret = new HashMap<>();
    for (DataTable table : scanForReferences.getTables().values()) {
      List<Ref> outgoingRefs = enumOutgoingRefsToTableOrNull(references, table.getName());
      if (outgoingRefs == null) {
        continue;
      }

      for (Object rowObj : table.getRows().values()) {
        HasId row = (HasId) rowObj;

        PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(row);
        for (Ref ref : outgoingRefs) {
          Object referencedId = null;
          try {
            referencedId = propertyAccessor.getPropertyValue(ref.getFromField());
          } catch (Throwable t) {
            throw new RuntimeException(
                "Failed to read property " + ref.getFromField() + " from " + row, t);
          }
          if (referencedId == null) {
            continue;
          }

          if (checkForExistence.get(ref.getToEntity()).find(referencedId) != null) {
            // that one is already loaded, skip
            continue;
          }

          Set<Object> referencedIds = ret.get(ref.getToEntity());
          if (referencedIds == null) {
            ret.put(ref.getToEntity(), referencedIds = new HashSet<>());
          }
          referencedIds.add(referencedId);
        }
      }
    }
    return ret;
  }

  protected List<Ref> enumOutgoingRefsToTableOrNull(Ref[] references, String entityName) {
    List<Ref> ret = null;
    for (Ref ref : references) {
      if (!entityName.equals(ref.getFromEntity())) {
        continue;
      }
      if (!ref.isManyToOne() && !ref.isOneToOne()) {
        continue;
      }

      if (ret == null) {
        ret = new LinkedList<>();
      }
      ret.add(ref);
    }
    return ret;
  }

  protected Map<Ref, Set<Object>> enumerateOneToManyReferences(DataSet dataSet, Ref[] references) {
    Map<Ref, Set<Object>> ret = new HashMap<>();
    for (Ref ref : references) {
      if (!ref.isOneToMany()) {
        continue;
      }
      String entityTypeCode = ref.getFromEntity();
      Set<Object> ids =
          EasyCrudDtoUtils.enumerateIds(dataSet.get(entityTypeCode).getRows().values());
      if (!ids.isEmpty()) {
        ret.put(ref, ids);
      }
    }
    return ret;
  }

  protected Map<Ref, Set<Object>> enumerateManyToManyReferences(DataSet dataSet, Ref[] references) {
    Map<Ref, Set<Object>> ret = new HashMap<>();
    for (Ref ref : references) {
      if (!ref.isManyToMany()) {
        continue;
      }
      String entityTypeCode = ref.getFromEntity();
      Set<Object> ids =
          EasyCrudDtoUtils.enumerateIds(dataSet.get(entityTypeCode).getRows().values());
      if (ids.size() != 0) {
        ret.put(ref, ids);
      }
    }
    return ret;
  }

  public ReferencesRegistry getReferencesRegistry() {
    return referencesRegistry;
  }

  public EasyCrudServiceResolver getEasyCrudServiceResolver() {
    return easyCrudServiceResolver;
  }
}
