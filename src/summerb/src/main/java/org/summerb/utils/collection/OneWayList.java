package org.summerb.utils.collection;

import java.util.Iterator;

import com.google.common.base.Preconditions;

/**
 * This is one way linked list intended for use in concurrent scenarios where we
 * try to avoid using locking (lock-free algorithms). It is not completely lock
 * free, but at least for read it's lock free.
 * 
 * @author sergeyk
 *
 * @param <T>
 */
public class OneWayList<T> implements Iterable<T> {
	private volatile Entry<T> first;
	private volatile Entry<T> last;

	private Object syncRoot = new Object();

	public OneWayList() {

	}

	public boolean isEmpty() {
		return first == null;
	}

	public T getFirst() {
		Entry<T> f = first;
		return f == null ? null : f.data;
	}

	public T getLast() {
		Entry<T> l = last;
		return l == null ? null : l.data;
	}

	@Override
	public OneWayIterator iterator() {
		return new OneWayIterator(first);
	}

	public void append(T data) {
		Entry<T> newEntry = new Entry<T>(data);
		if (first == null) {
			first = last = newEntry;
			return;
		}

		Entry<T> curLast = last;
		last = newEntry;
		curLast.next = newEntry;
	}

	public void appendThreadSafe(T data) {
		synchronized (syncRoot) {
			append(data);
		}
	}

	public class OneWayIterator implements Iterator<T> {
		private Entry<T> current;
		private Entry<T> startingElement;
		private Entry<T> prevCur;

		public OneWayIterator(Entry<T> first) {
			this.startingElement = first;
		}

		@Override
		public boolean hasNext() {
			Entry<T> cur = getCurrent();
			if (cur == null) {
				return startingElement != null;
			}

			return cur.next != null;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new IndexOutOfBoundsException("No more elements");
			}

			Entry<T> cur = getCurrent();
			if (cur == null) {
				setCurrent(startingElement);
				return startingElement.data;
			}

			Entry<T> next = cur.next;
			setCurrent(next);
			return next.data;
		}

		public void shiftListHead() {
			Entry<T> cur = getCurrent();
			if (cur == null) {
				throw new IndexOutOfBoundsException("No more elements");
			}

			OneWayList.this.first = (Entry) cur;
		}

		public void insertNewBeforeCurrent(T newItem) {
			Preconditions.checkState(current != null, "Cannot insertNewBeforeCurrent for iteration that not started");

			Entry<T> e = new Entry<T>(newItem);
			e.next = current;

			if (prevCur == null) {
				// ok, this is new first item
				first = e;
			} else {
				// ok, inserted item in the middle
				prevCur.next = e;
			}
		}

		@Override
		public void remove() {
			throw new IllegalStateException("Remove operation is not supported by OneWayList");
		}

		private Entry<T> getCurrent() {
			return current;
		}

		private void setCurrent(Entry<T> cur) {
			prevCur = this.current;
			this.current = cur;
		}
	}

	private static class Entry<T> {
		T data;
		volatile Entry next;

		public Entry(T data) {
			this.data = data;
		}
	}
}
