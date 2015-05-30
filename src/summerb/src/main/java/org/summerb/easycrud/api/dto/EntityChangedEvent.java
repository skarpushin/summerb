package org.summerb.easycrud.api.dto;

import com.google.common.base.Preconditions;

/**
 * 
 * @author sergey.karpushin
 *
 * @param <T>
 */
public class EntityChangedEvent<T> {

	public enum ChangeType {
		ADDED, UPDATED, REMOVED
	}

	private Class<T> clazz;
	private T value;
	private ChangeType changeType;

	/**
	 * @deprecated recommended to use {@link #build(Class, Object, ChangeType)}
	 *             method in order to simplify code
	 */
	@Deprecated
	public EntityChangedEvent(Class<T> clazz, T value, ChangeType changeType) {
		Preconditions.checkArgument(clazz != null);
		Preconditions.checkArgument(value != null);
		this.clazz = clazz;
		this.value = value;
		this.changeType = changeType;
	}

	public static <T> Object build(Class<T> clazz, T value, ChangeType changeType) {
		return new EntityChangedEvent<T>(clazz, value, changeType);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> EntityChangedEvent<T> added(T notNullObject) {
		Preconditions.checkArgument(notNullObject != null);
		return new EntityChangedEvent(notNullObject.getClass(), notNullObject, ChangeType.ADDED);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> EntityChangedEvent<T> updated(T notNullObject) {
		Preconditions.checkArgument(notNullObject != null);
		return new EntityChangedEvent(notNullObject.getClass(), notNullObject, ChangeType.UPDATED);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> EntityChangedEvent<T> removed(Class<T> class1) {
		return new EntityChangedEvent(class1, null, ChangeType.REMOVED);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> EntityChangedEvent<T> removedObject(T notNullObject) {
		return new EntityChangedEvent(notNullObject.getClass(), notNullObject, ChangeType.REMOVED);
	}

	public boolean isTypeOf(Class<T> clazz) {
		return value.getClass().equals(clazz);
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	@Override
	public String toString() {
		return "EntityChangedEvent [changeType=" + changeType + ", clazz=" + clazz + ", value=" + value + "]";
	}

}
