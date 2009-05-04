/**
 * Copyright 2008 - 2009 Pro-Netics S.P.A.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.pronetics.madstore.repository.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * {@link java.util.List} decorator providing additional paging information, see:
 * <ul>
 * <li>{@link #getTotal()}</li>
 * <li>{@link #getMax()}</li>
 * <li>{@link #getOffset()}</li>
 * </ul>
 *
 * @author Salvatore Incandela
 */
public class PagingList<E> implements List<E> {

    private int total;
    private int max;
    private int offset;
    private List<E> delegate;

    public PagingList(List<E> delegate, int offset, int max, int total) {
        this.delegate = delegate;
        this.total = total;
        this.max = max;
        this.offset = offset;
    }

    /**
     * Get the total number of results that whould have been returned with no paging.
     * This is different from the list size, which represents the actual number of results contained in the list.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Get the max number of paged results.
     */
    public int getMax() {
        return max;
    }

    /**
     * Get the index of the first paged result.
     */
    public int getOffset() {
        return offset;
    }

    public boolean add(E o) {
        return delegate.add(o);
    }

    public void add(int index, E element) {
        delegate.add(index, element);
    }

    public boolean addAll(Collection<? extends E> c) {
        return delegate.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return delegate.addAll(index, c);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    public E get(int index) {
        return delegate.get(index);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    public E remove(int index) {
        return delegate.remove(index);
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public E set(int index, E element) {
        return delegate.set(index, element);
    }

    public int size() {
        return delegate.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }
}
