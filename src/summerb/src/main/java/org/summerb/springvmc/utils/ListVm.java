package org.summerb.springvmc.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Presentation model used to facilitate some operations needed while jsp
 * processing
 * 
 * @author sergey.karpushin
 * 
 */
public class ListVm<T> {
	/**
	 * Underlining items
	 */
	private List<T> items;

	public ListVm(List<T> items) {
		this.items = items;
	}

	public ListVm() {
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
