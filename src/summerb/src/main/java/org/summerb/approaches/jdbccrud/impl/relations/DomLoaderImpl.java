package org.summerb.approaches.jdbccrud.impl.relations;

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
import org.summerb.approaches.jdbccrud.api.EasyCrudServiceResolver;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.relations.DataSetLoader;
import org.summerb.approaches.jdbccrud.api.relations.DomLoader;
import org.summerb.utils.DtoBase;
import org.summerb.utils.ObjCopyUtils;

import com.google.common.base.Preconditions;

public class DomLoaderImpl implements DomLoader {
	private DataSetLoader dataSetLoader;
	private EasyCrudServiceResolver easyCrudServiceResolver;

	public DomLoaderImpl(DataSetLoader dataSetLoader, EasyCrudServiceResolver easyCrudServiceResolver) {
		this.dataSetLoader = dataSetLoader;
		this.easyCrudServiceResolver = easyCrudServiceResolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.DomLoader#
	 * load(java.lang.Class, TId,
	 * org.summerb.approaches.jdbccrud.api.dto.relations.Ref)
	 */
	@Override
	public <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> TDomClass load(
			Class<TDomClass> rootDomClass, TId rootDtoId, Ref... refsToResolve) {
		HashSet<TId> ids = new HashSet<>();
		ids.add(rootDtoId);
		List<TDomClass> ret = load(rootDomClass, ids, refsToResolve);
		Preconditions.checkState(ret != null && ret.size() == 1, "Expected exactly 1 result, but got %s", ret);
		return ret.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.skarpushin.smarthome.devicesgate.services.envconfig.impl.DomLoader#
	 * load(java.lang.Class, java.util.Set,
	 * org.summerb.approaches.jdbccrud.api.dto.relations.Ref)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> List<TDomClass> load(
			Class<TDomClass> rootDomClass, Set<TId> ids, Ref... refsToResolve) {

		try {
			Preconditions.checkArgument(rootDomClass != null, "rootDomClass must not be empty");
			Preconditions.checkArgument(ids != null && ids.size() > 0, "List of ids must not be empty");

			// Resolve root row entity name (MessageCode)
			String dtoMessageCode = resolveDtoMessageCodeFromDomClass(rootDomClass);

			// Use DataSetLoader to load data
			List<HasId> loadedRoots = dataSetLoader.loadObjectsByIds((Set<Object>) ids, dtoMessageCode);
			DataSet ds = new DataSet();
			ds.get(dtoMessageCode).putAll(loadedRoots);
			boolean haveRefsToLoad = refsToResolve != null && refsToResolve.length > 0;
			if (haveRefsToLoad) {
				dataSetLoader.resolveReferencedObjects(ds, refsToResolve);

			}
			Map<String, Ref> refs = !haveRefsToLoad ? Collections.emptyMap()
					: Arrays.stream(refsToResolve).collect(Collectors.toMap(x -> x.getName(), x -> x));

			// Construct root Dom objects
			Collection<TRowClass> rootRows = ds.get(dtoMessageCode).getRows().values();
			// map of already converted entities
			Map<EntityAndId, HasId> cache = new HashMap<>();
			List<TDomClass> ret = rootRows.stream().map(mapDtoToDom(dtoMessageCode, rootDomClass, refs, ds, cache))
					.collect(Collectors.toList());

			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to loadAndConvert DOM " + rootDomClass + " identified by " + ids, t);
		}
	}

	/**
	 * @param domClass
	 *            class of DOM objects
	 * @param refs
	 *            references to be traversed
	 * @param ds
	 *            dataset
	 * 
	 * @return mapper which can translate from ROW to DOM
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> Function<TRowClass, TDomClass> mapDtoToDom(
			String dtoMessageCode, Class<TDomClass> domClass, Map<String, Ref> refs, DataSet ds,
			Map<EntityAndId, HasId> cache) {
		return dtoRow -> {
			try {
				// try to get from cache first
				EntityAndId cacheKey = new EntityAndId(dtoMessageCode, dtoRow.getId());
				TDomClass dom = (TDomClass) cache.get(cacheKey);
				if (dom != null) {
					return dom;
				}

				// Construct instance of DOM and migrate simple field values
				dom = domClass.newInstance();
				ObjCopyUtils.assignFields(dtoRow, dom);
				cache.put(cacheKey, dom);

				// Discover fields that represent references
				if (refs.isEmpty()) {
					return dom;
				}

				// TBD: Skip that kind of mapping if there are no refs
				// from this entity, this way we'll save on spinning our wheels
				// with reflection

				List<Pair<Ref, PropertyDescriptor>> domFields = discoverDomFields(domClass, refs);
				domFields.forEach(mapDomField(dom, dtoRow, refs, ds, cache));

				// add to ret
				return dom;
			} catch (Throwable t) {
				throw new RuntimeException("Can't instantiate DOM " + domClass, t);
			}
		};
	}

	@SuppressWarnings("rawtypes")
	private <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> Consumer<? super Pair<Ref, PropertyDescriptor>> mapDomField(
			TDomClass dom, TRowClass dtoRow, Map<String, Ref> refs, DataSet ds, Map<EntityAndId, HasId> cache) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> void resolveSingleRef(TDomClass dom,
			Pair<Ref, PropertyDescriptor> domField, Map<String, Ref> refs, DataSet ds, Map<EntityAndId, HasId> cache)
			throws IllegalAccessException, InvocationTargetException {
		Preconditions.checkArgument(HasId.class.isAssignableFrom(domField.getValue().getPropertyType()),
				"DOM's class (%s) supposed to implement org.summerb.approaches.jdbccrud.api.dto, but it's not",
				domField.getValue().getPropertyType());

		PropertyDescriptor referenceeIdProp = BeanUtils.getPropertyDescriptor(dom.getClass(),
				domField.key.getFromField());
		Preconditions.checkState(referenceeIdProp != null, "can't resolve referencer fk id field %s",
				domField.key.getFromField());

		Object referenceeId = referenceeIdProp.getReadMethod().invoke(dom);
		if (referenceeId == null) {
			return;
		}

		HasId dto = ds.get(domField.getKey().getToEntity()).find(referenceeId);
		Preconditions.checkState(dto != null,
				"Reference %s was supposed to be resolved, but referenced object %s wasn't loaded by id %s",
				domField.getKey().getName(), domField.getKey().getToEntity(), referenceeId);
		Class<HasId> referenceeClass = (Class<HasId>) domField.getValue().getPropertyType();
		Function<HasId, HasId> mapper = mapDtoToDom(domField.getKey().getToEntity(), referenceeClass, refs, ds, cache);
		HasId referenceeDom = mapper.apply(dto);
		domField.getValue().getWriteMethod().invoke(dom, referenceeDom);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <TId, TDomClass extends TRowClass, TRowClass extends HasId<TId>> void resolveOneToManyList(TDomClass dom,
			Pair<Ref, PropertyDescriptor> domField, Map<String, Ref> refs, DataSet ds, Map<EntityAndId, HasId> cache)
			throws IllegalAccessException, InvocationTargetException {
		Class referenceeClass = resolveCollectionElementType(domField.getValue());

		// NOTE: We could actually pass initial DTO used to construct DOM but
		// since latter is subclass of former we can decrease number of
		// parameters of `resolveOneToManyList` method, and that's why
		// `referenceeClass` appears 2 times in next statement
		List<HasId<Object>> referenceeList = EasyCrudDomUtils.buildReferencedObjectsList(ds, dom, domField.getKey(),
				referenceeClass, mapDtoToDom(domField.getKey().getToEntity(), referenceeClass, refs, ds, cache));

		domField.getValue().getWriteMethod().invoke(dom, referenceeList);
	}

	protected <TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> Class<TDomClass> resolveCollectionElementType(
			PropertyDescriptor pd) {
		Class<?> propertyDomType = pd.getPropertyType();
		Preconditions.checkArgument(List.class.isAssignableFrom(propertyDomType),
				"When it's a oneToMany field is expected to be of type List<TDomType>");

		// TBD: Support Set<> and Map<>

		Type returnType = pd.getReadMethod().getGenericReturnType();
		Preconditions.checkArgument(returnType instanceof ParameterizedType,
				"Proprty supposed to be of parameterized type, i.e. List<User>. Check %s", pd.getName());
		ParameterizedType pt = (ParameterizedType) returnType;

		Type[] typeArgs = pt.getActualTypeArguments();
		Preconditions.checkArgument(typeArgs != null && typeArgs.length == 1,
				"Expect exactly one type parameter for this oneToMany relation");

		Type retType = typeArgs[0];
		Preconditions.checkArgument(retType instanceof Class, "Unexpected failure, Type %s cannot be cast to Class",
				retType.getTypeName());

		@SuppressWarnings("unchecked")
		Class<TDomClass> retClass = (Class<TDomClass>) retType;
		Preconditions.checkArgument(HasId.class.isAssignableFrom(retClass),
				"DOM's class (%s) supposed to implement org.summerb.approaches.jdbccrud.api.dto, but it's not",
				retClass);

		return retClass;
	}

	protected List<Pair<Ref, PropertyDescriptor>> discoverDomFields(Class<?> clazz, Map<String, Ref> refs) {
		try {
			Preconditions.checkArgument(clazz != null, "clazz must not be null");
			Preconditions.checkArgument(refs != null && refs.size() > 0, "refsToResolve must not be null");

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
		return clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1)
				+ referenceeName.substring(0, 1).toUpperCase() + referenceeName.substring(1);
	}

	protected <TDomClass> String resolveDtoMessageCodeFromDomClass(Class<TDomClass> domClass) {
		try {
			Class<? super TDomClass> dtoClass = domClass.getSuperclass();

			// TBD: Support case when Dom class contains list of Dto class (no
			// need to create Dom class for leafs, for example). I.e. If Device
			// class will have field List<AssetRow> ==>> then we don't nee dto
			// use superclass. MAYBE we can just go up one level until
			// easyCrudServiceResolver.resolveByDtoClass returns something

			Preconditions.checkArgument(dtoClass != null,
					"DOM class %s supposed to inherit from DTO class, but it's not", domClass);
			Preconditions.checkArgument(DtoBase.class.isAssignableFrom(dtoClass),
					"DOM's parent class (%s) supposed to implement org.summerb.approaches.jdbccrud.common.Dtobase, but it's not",
					dtoClass);
			Preconditions.checkArgument(HasId.class.isAssignableFrom(dtoClass),
					"DOM's parent class (%s) supposed to implement org.summerb.approaches.jdbccrud.api.dto, but it's not",
					dtoClass);

			return easyCrudServiceResolver.resolveByDtoClass(dtoClass).getEntityTypeMessageCode();
		} catch (Throwable t) {
			throw new RuntimeException(
					"Failed to resolve DTO class. It looks like your DOM object is not a subclass of appropriate DTO",
					t);
		}
	}

	public static class EntityAndId {
		private String entityTypeMessageCode;
		private Object id;

		public EntityAndId(String entityTypeMessageCode, Object id) {
			super();
			this.entityTypeMessageCode = entityTypeMessageCode;
			this.id = id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((entityTypeMessageCode == null) ? 0 : entityTypeMessageCode.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EntityAndId other = (EntityAndId) obj;
			if (entityTypeMessageCode == null) {
				if (other.entityTypeMessageCode != null)
					return false;
			} else if (!entityTypeMessageCode.equals(other.entityTypeMessageCode))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}

	public static class Pair<K, V> implements Map.Entry<K, V> {
		private K key;
		private V value;

		public Pair(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		public static <K, V> Pair<K, V> of(K key, V value) {
			return new Pair<>(key, value);
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}
	}

}
