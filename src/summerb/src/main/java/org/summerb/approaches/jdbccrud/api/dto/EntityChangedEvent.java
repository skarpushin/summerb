package org.summerb.approaches.jdbccrud.api.dto;

import java.io.Serializable;

import com.google.common.base.Preconditions;

public class EntityChangedEvent<T> implements Serializable {
	private static final long serialVersionUID = 8920065013673943648L;

	public enum ChangeType {
		ADDED, UPDATED, REMOVED
	}

	private T value;
	private ChangeType changeType;

	/**
	 * @deprecated fo IO purposes only
	 */
	@Deprecated
	public EntityChangedEvent() {
	}

	/**
	 * @deprecated recommended to use {@link #build(Object, ChangeType)} method
	 *             in order to simplify code
	 */
	@Deprecated
	public EntityChangedEvent(T value, ChangeType changeType) {
		Preconditions.checkArgument(value != null);
		this.value = value;
		this.changeType = changeType;
	}

	public static <T> Object build(T value, ChangeType changeType) {
		return new EntityChangedEvent<T>(value, changeType);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> EntityChangedEvent<T> added(T notNullObject) {
		Preconditions.checkArgument(notNullObject != null);
		return new EntityChangedEvent(notNullObject, ChangeType.ADDED);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> EntityChangedEvent<T> updated(T notNullObject) {
		Preconditions.checkArgument(notNullObject != null);
		return new EntityChangedEvent(notNullObject, ChangeType.UPDATED);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> EntityChangedEvent<T> removedObject(T notNullObject) {
		return new EntityChangedEvent(notNullObject, ChangeType.REMOVED);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isTypeOf(Class clazz) {
		Preconditions.checkArgument(clazz != null, "Clazz required");
		return clazz.isAssignableFrom(value.getClass());
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
		return "EntityChangedEvent [changeType=" + changeType + ", value=" + value + "]";
	}

}
