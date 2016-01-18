package org.summerb.approaches.springmvc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Presentation model used to facilitate some operations needed while jsp
 * processing
 * 
 * @author sergey.karpushin
 * 
 */
public class ListPm<T> {
	/**
	 * Underlining items
	 */
	private List<T> items;

	public ListPm(List<T> items) {
		this.items = items;
	}

	public ListPm() {
		this(new ArrayList<T>());
	}

	public int getSize() {
		return items.size();
	}

	public boolean getHasItems() {
		return items.size() > 0;
	}

	public void add(T item) {
		items.add(item);
	}

	public List<T> getItems() {
		return items;
	}
}
