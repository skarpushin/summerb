/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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

import com.google.common.base.Preconditions;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.summerb.easycrud.api.EasyCrudServiceResolver;
import org.summerb.easycrud.api.relations.DataSetLoader;
import org.summerb.easycrud.api.relations.DomLoader;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.datapackage.DataSet;
import org.summerb.easycrud.api.row.relations.Ref;
import org.summerb.utils.DtoBase;
import org.summerb.utils.Pair;
import org.summerb.utils.objectcopy.ObjCopyUtils;

public class DomLoaderImpl implements DomLoader {
  protected DataSetLoader dataSetLoader;
  protected EasyCrudServiceResolver easyCrudServiceResolver;

  public DomLoaderImpl(
      DataSetLoader dataSetLoader, EasyCrudServiceResolver easyCrudServiceResolver) {
    this.dataSetLoader = dataSetLoader;
    this.easyCrudServiceResolver = easyCrudServiceResolver;
  }

  /*
   * (non-Javadoc)
   *
   * @see ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.DomLoader#
   * load(java.lang.Class, TId, org.summerb.easycrud.api.row.relations.Ref)
   */
  @Override
  public <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> TDomClass load(
      Class<TDomClass> rootDomClass, TId rootDtoId, Ref... refsToResolve) {
    HashSet<TId> ids = new HashSet<>();
    ids.add(rootDtoId);
    List<TDomClass> ret = load(rootDomClass, ids, refsToResolve);
    Preconditions.checkState(
        ret != null && ret.size() == 1, "Expected exactly 1 result, but got %s", ret);
    return ret.get(0);
  }

  /*
   * (non-Javadoc)
   *
   * @see ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.DomLoader#
   * load(java.lang.Class, java.util.Set,
   * org.summerb.easycrud.api.row.relations.Ref)
   */
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> List<TDomClass> load(
      Class<TDomClass> rootDomClass, Set<TId> ids, Ref... refsToResolve) {

    try {
      Preconditions.checkArgument(rootDomClass != null, "rootDomClass must not be empty");
      Preconditions.checkArgument(ids != null && !ids.isEmpty(), "List of ids must not be empty");

      // Resolve root row entity name (MessageCode)
      String rowMessageCode = resolveDtoMessageCodeFromDomClass(rootDomClass);

      // Use DataSetLoader to load data
      List<HasId> loadedRoots = dataSetLoader.loadObjectsByIds((Set<Object>) ids, rowMessageCode);
      DataSet ds = new DataSet();
      ds.get(rowMessageCode).putAll(loadedRoots);
      boolean haveRefsToLoad = refsToResolve != null && refsToResolve.length > 0;
      if (haveRefsToLoad) {
        dataSetLoader.loadReferencedObjects(ds, refsToResolve);
      }
      Map<String, Ref> refs =
          !haveRefsToLoad
              ? Collections.emptyMap()
              : Arrays.stream(refsToResolve).collect(Collectors.toMap(Ref::getName, x -> x));

      // Construct root Dom objects
      Collection<TRowClass> rootRows = ds.get(rowMessageCode).getRows().values();
      // map of already converted entities
      Map<EntityAndId, HasId> cache = new HashMap<>();
      List<TDomClass> ret =
          rootRows.stream()
              .map(mapDtoToDom(rowMessageCode, rootDomClass, refs, ds, cache))
              .collect(Collectors.toList());

      return ret;
    } catch (Throwable t) {
      throw new RuntimeException(
          "Failed to loadAndConvert DOM " + rootDomClass + " identified by " + ids, t);
    }
  }

  /**
   * @param dtoMessageCode dtoMessageCode
   * @param domClass class of DOM objects
   * @param refs references to be traversed
   * @param ds dataset
   * @param cache cache
   * @param <TId> id type
   * @param <TRowClass> row type
   * @param <TDomClass> dom type
   * @return mapper which can translate from ROW to DOM
   */
  @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
  protected <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass>
      Function<TRowClass, TDomClass> mapDtoToDom(
          String dtoMessageCode,
          Class<TDomClass> domClass,
          Map<String, Ref> refs,
          DataSet ds,
          Map<EntityAndId, HasId> cache) {
    return row -> {
      try {
        // try to get from cache first
        EntityAndId cacheKey = new EntityAndId(dtoMessageCode, row.getId());
        TDomClass dom = (TDomClass) cache.get(cacheKey);
        if (dom != null) {
          return dom;
        }

        // Construct instance of DOM and migrate simple field values
        dom = domClass.newInstance();
        ObjCopyUtils.assignFields(row, dom);
        cache.put(cacheKey, dom);

        // Discover fields that represent references
        if (refs.isEmpty()) {
          return dom;
        }

        // TBD: Skip that kind of mapping if there are no refs
        // from this entity, this way we'll save on spinning our wheels
        // with reflection

        List<Pair<Ref, PropertyDescriptor>> domFields = discoverDomFields(domClass, refs);
        domFields.forEach(mapDomField(dom, row, refs, ds, cache));

        // add to ret
        return dom;
      } catch (Throwable t) {
        throw new RuntimeException("Can't instantiate DOM " + domClass, t);
      }
    };
  }

  @SuppressWarnings("rawtypes")
  protected <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass>
      Consumer<? super Pair<Ref, PropertyDescriptor>> mapDomField(
          TDomClass dom,
          TRowClass row,
          Map<String, Ref> refs,
          DataSet ds,
          Map<EntityAndId, HasId> cache) {
    return domField -> {
      try {
        if (domField.getKey().isOneToMany()) {
          resolveOneToManyList(dom, domField, refs, ds, cache);
        } else if (domField.getKey().isManyToOne() || domField.getKey().isOneToOne()) {
          resolveSingleRef(dom, domField, refs, ds, cache);
        } else {
          // TBD: Impl for many to many
          throw new IllegalStateException("this case is not impl yet: " + domField.getKey());
        }
      } catch (Throwable t) {
        throw new RuntimeException("Failed to map " + domField.getKey().getName(), t);
      }
    };
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> void resolveSingleRef(
      TDomClass dom,
      Pair<Ref, PropertyDescriptor> domField,
      Map<String, Ref> refs,
      DataSet ds,
      Map<EntityAndId, HasId> cache)
      throws IllegalAccessException, InvocationTargetException {
    Preconditions.checkArgument(
        HasId.class.isAssignableFrom(domField.getValue().getPropertyType()),
        "DOM's class (%s) supposed to implement org.summerb.easycrud.api.row, but it's not",
        domField.getValue().getPropertyType());

    PropertyDescriptor referenceeIdProp =
        BeanUtils.getPropertyDescriptor(dom.getClass(), domField.getKey().getFromField());
    Preconditions.checkState(
        referenceeIdProp != null,
        "can't resolve referencer fk id field %s",
        domField.getKey().getFromField());

    Object referenceeId = referenceeIdProp.getReadMethod().invoke(dom);
    if (referenceeId == null) {
      return;
    }

    HasId row = ds.get(domField.getKey().getToEntity()).find(referenceeId);
    Preconditions.checkState(
        row != null,
        "Reference %s was supposed to be resolved, but referenced object %s wasn't loaded by id %s",
        domField.getKey().getName(),
        domField.getKey().getToEntity(),
        referenceeId);
    Class<HasId> referenceeClass = (Class<HasId>) domField.getValue().getPropertyType();
    Function<HasId, HasId> mapper =
        mapDtoToDom(domField.getKey().getToEntity(), referenceeClass, refs, ds, cache);
    HasId referenceeDom = mapper.apply(row);
    domField.getValue().getWriteMethod().invoke(dom, referenceeDom);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected <TId, TDomClass extends TRowClass, TRowClass extends HasId<TId>>
      void resolveOneToManyList(
          TDomClass dom,
          Pair<Ref, PropertyDescriptor> domField,
          Map<String, Ref> refs,
          DataSet ds,
          Map<EntityAndId, HasId> cache)
          throws IllegalAccessException, InvocationTargetException {
    Class referenceeClass = resolveCollectionElementType(domField.getValue());

    // NOTE: We could actually pass initial DTO used to construct DOM but
    // since latter is subclass of former we can decrease number of
    // parameters of `resolveOneToManyList` method, and that's why
    // `referenceeClass` appears 2 times in next statement
    List<HasId<Object>> referenceeList =
        EasyCrudDomUtils.buildReferencedObjectsList(
            ds,
            dom,
            domField.getKey(),
            referenceeClass,
            mapDtoToDom(domField.getKey().getToEntity(), referenceeClass, refs, ds, cache));

    domField.getValue().getWriteMethod().invoke(dom, referenceeList);
  }

  protected <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass>
      Class<TDomClass> resolveCollectionElementType(PropertyDescriptor pd) {
    Class<?> propertyDomType = pd.getPropertyType();
    Preconditions.checkArgument(
        List.class.isAssignableFrom(propertyDomType),
        "When it's a oneToMany field is expected to be of type List<TDomType>");

    // TBD: Support Set<> and Map<>

    Type returnType = pd.getReadMethod().getGenericReturnType();
    Preconditions.checkArgument(
        returnType instanceof ParameterizedType,
        "Proprty supposed to be of parameterized type, i.e. List<User>. Check %s",
        pd.getName());
    ParameterizedType pt = (ParameterizedType) returnType;

    Type[] typeArgs = pt.getActualTypeArguments();
    Preconditions.checkArgument(
        typeArgs != null && typeArgs.length == 1,
        "Expect exactly one type parameter for this oneToMany relation");

    Type retType = typeArgs[0];
    Preconditions.checkArgument(
        retType instanceof Class,
        "Unexpected failure, Type %s cannot be cast to Class",
        retType.getTypeName());

    @SuppressWarnings("unchecked")
    Class<TDomClass> retClass = (Class<TDomClass>) retType;
    Preconditions.checkArgument(
        HasId.class.isAssignableFrom(retClass),
        "DOM's class (%s) supposed to implement org.summerb.easycrud.api.row, but it's not",
        retClass);

    return retClass;
  }

  protected List<Pair<Ref, PropertyDescriptor>> discoverDomFields(
      Class<?> clazz, Map<String, Ref> refs) {
    try {
      Preconditions.checkArgument(clazz != null, "clazz must not be null");
      Preconditions.checkArgument(
          refs != null && !refs.isEmpty(), "refsToResolve must not be null");

      List<Pair<Ref, PropertyDescriptor>> ret = new ArrayList<>();
      PropertyDescriptor[] srcProps = BeanUtils.getPropertyDescriptors(clazz);
      for (PropertyDescriptor pd : srcProps) {
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null) {
          continue;
        }

        String targetRefName = tryResolveTargetRefName(pd, clazz);
        if (targetRefName == null) {
          continue;
        }

        // Search for applicable reference
        Ref ref = refs.get(targetRefName);
        if (ref == null) {
          continue;
        }

        ret.add(Pair.of(ref, pd));
      }

      return ret;
    } catch (Throwable t) {
      throw new RuntimeException("Failed to resolve DOM fields", t);
    }
  }

  protected String tryResolveTargetRefName(PropertyDescriptor pd, Class<?> clazz) {
    // TBD: Add ability to customize it using annotation and
    // configure exact ref name instead of using convention. Maybe
    // make it as simple as using Spring's @Qualifier

    String referenceeName = pd.getName();
    return clazz.getSimpleName().substring(0, 1).toLowerCase()
        + clazz.getSimpleName().substring(1)
        + referenceeName.substring(0, 1).toUpperCase()
        + referenceeName.substring(1);
  }

  protected <TDomClass> String resolveDtoMessageCodeFromDomClass(Class<TDomClass> domClass) {
    try {
      Class<? super TDomClass> rowClass = domClass.getSuperclass();

      // TBD: Support case when Dom class contains list of row class (no
      // need to create Dom class for leafs, for example). I.e. If Device
      // class will have field List<AssetRow> ==>> then we don't need row
      // use superclass. MAYBE we can just go up one level until
      // easyCrudServiceResolver.resolveByMessageCode returns something

      Preconditions.checkArgument(
          rowClass != null,
          "DOM class %s supposed to inherit from DTO class, but it's not",
          domClass);
      Preconditions.checkArgument(
          DtoBase.class.isAssignableFrom(rowClass),
          "DOM's parent class (%s) supposed to implement org.summerb.easycrud.common.Dtobase, but it's not",
          rowClass);
      Preconditions.checkArgument(
          HasId.class.isAssignableFrom(rowClass),
          "DOM's parent class (%s) supposed to implement org.summerb.easycrud.api.row, but it's not",
          rowClass);

      return easyCrudServiceResolver.resolveByRowClass(rowClass).getRowMessageCode();
    } catch (Throwable t) {
      throw new RuntimeException(
          "Failed to resolve DTO class. It looks like your DOM object is not a subclass of appropriate DTO",
          t);
    }
  }

  public static class EntityAndId {
    protected String entityTypeMessageCode;
    protected Object id;

    public EntityAndId(String entityTypeMessageCode, Object id) {
      super();
      this.entityTypeMessageCode = entityTypeMessageCode;
      this.id = id;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result =
          prime * result + ((entityTypeMessageCode == null) ? 0 : entityTypeMessageCode.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      EntityAndId other = (EntityAndId) obj;
      if (entityTypeMessageCode == null) {
        if (other.entityTypeMessageCode != null) return false;
      } else if (!entityTypeMessageCode.equals(other.entityTypeMessageCode)) return false;
      if (id == null) {
        if (other.id != null) return false;
      } else if (!id.equals(other.id)) return false;
      return true;
    }
  }
}
