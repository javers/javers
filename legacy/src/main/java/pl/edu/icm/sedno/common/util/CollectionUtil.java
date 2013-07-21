package pl.edu.icm.sedno.common.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanWrapperImpl;

import pl.edu.icm.crmanager.exception.UnsupportedMapping;
import pl.edu.icm.sedno.common.dao.CriterionIsNotUnique;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author bart
 */
public class CollectionUtil {

	/**
	 * @return never returns null
	 * @author bart
	 */
	public static <K,V> List<Entry<K,V>> flattenNestedMap(Map<K, Collection<V>> nestedMap) {
		nestedMap = CollectionUtil.wrapNullToImmutableEmptyMap(nestedMap);
		
		List<Entry<K,V>> flatMap = Lists.newArrayList();
		
		for (K key : nestedMap.keySet()) {
			Collection<V> values = nestedMap.get(key);
			
			if (values == null) {
				continue;
			}
			
			for (V value : values) {
				flatMap.add( new SimpleEntry(key, value));
			}			
		}
		return flatMap;
	}
	
	/**
	 * true is given property has specified expectedValue
	 */
	public static <E> Predicate<E> withProperty(final String propertyName, final Object value) {		
		return new Predicate<E>() {
			@Override
			public boolean apply(E input) {
				Object inputPropertyValue = new BeanWrapperImpl(input).getPropertyValue(propertyName);
				
				return Objects.equal(value, inputPropertyValue);
			}
		};
	}
	
	/**
	 * Picks up one element from collection	
	 * 
	 * @param exceptionMessageIfNotUnique message for CriterionIsNotUnique(), may by null
	 * @param filter must be unique 
	 * @return null if nothing found
	 * @throws CriterionIsNotUnique if more than one element is found
	 */
	public static <E> E getOne(Collection<E> fromCollection, Predicate<E> filter, String exceptionMessageIfNotUnique) {
		
		Iterable<E> filtered = Iterables.filter(fromCollection, filter);
		if (filtered == null)
			return null;
		
		E found = null;
		for (E e : filtered) {
			if (found != null) {
				if (exceptionMessageIfNotUnique != null) {
					throw new CriterionIsNotUnique(exceptionMessageIfNotUnique);
				}else {
					throw new CriterionIsNotUnique("getOne() - more than one element satisfies given filter");
				}
			}
			
			found = e;
		}
		
		return found;
	}

   /**
    * Reorders the list of {@link Orderable} objects according to indexes in the list. Invokes {@link Orderable#setPosition(Integer)} on every element of the list and sets the order position to
    * element index in the list + 1
    * @param <T>
    * @param list
    */
   public static <T extends Orderable> void reorder(List<T> list) {
       if (list == null) return;
       for (int i = 0; i < list.size(); i++) {
           list.get(i).setPosition(i+1);
       }
   }
   
   /**
    * Removes all empty records (@link Emptyable#isEmpty()} from the collection
    * @param <T>
    * @param collection
    */
   public static <T extends Emptyable> void removeEmpty(Collection<T> collection) {
       CollectionUtils.filter(collection, new org.apache.commons.collections.Predicate() {
            @Override
            public boolean evaluate(Object object) {
                @SuppressWarnings("unchecked")
                T obj = (T)object;
                return !obj.isEmpty();
            }
      });
   }
   
   /**
    * Removes all null elements from the given collection
    */
   public static <T> void removeNull(Collection<T> collection) {
       CollectionUtils.filter(collection, new org.apache.commons.collections.Predicate() {
           @Override
           public boolean evaluate(Object object) {
               @SuppressWarnings("unchecked")
               T obj = (T)object;
               return obj != null;
           }
     });
   }

   /**
    * Returns the number of all empty elements (@link Emptyable#isEmpty()} in the collection
    * @param <T>
    * @param collection
    */
   public static <T extends Emptyable> int getNumberOfEmptyElements(Collection<T> collection) {
       int numberOfEmpty = 0;
       for (Emptyable element : collection) {
           if (element.isEmpty()) {
              numberOfEmpty++;
           }
       }
       return numberOfEmpty;
   }

   
   /**
    * Returns the index of the first empty (see {@link Emptyable#isEmpty()}) element in the list.
    * @return index of the first empty element in the list or -1 if the list is null or does not contain any empty element. The null elements in the list are omitted.
    */
   public static <T extends Emptyable> int getFirstEmptyElementIndex(List<T> list) {
       if (list == null) return -1;
       for (int i = 0; i < list.size(); i++) {
           T element = list.get(i);
           if (element == null) continue;
           if (element.isEmpty()) return i;
       }
       return -1;
   }

	public static <M extends Map> M wrapNullToImmutableEmptyMap(M map) {
		if (map != null) {
			return map;
		}
		return (M)Collections.EMPTY_MAP;
	}

	public static <C extends Collection> C copy(C source) {
		C target = (C)newMutableCollectionInstance(source.getClass());
		target.addAll(source);
		return target;
	}
	
	public static <C extends Collection> C newMutableCollectionInstance(Class<C> expectedType) {
		if (Set.class.isAssignableFrom(expectedType))
			return (C)Sets.newHashSet();

		if (List.class.isAssignableFrom(expectedType))
			return (C)Lists.newArrayList();

		throw new UnsupportedMapping("This collection type: " + expectedType.getClass().getName() + " is not supported");
	}
	
	public static <C extends Collection> C wrapNullToImmutableCol(C col, Class<C> expectedType) {
		if (col != null)
			return col;

		if (Set.class.isAssignableFrom(expectedType))
			return (C)Collections.EMPTY_SET;

		if (List.class.isAssignableFrom(expectedType))
			return (C)Collections.EMPTY_LIST;

		throw new UnsupportedMapping("This collection type: " + expectedType.getClass().getName() + " is not supported");
	} 

}
