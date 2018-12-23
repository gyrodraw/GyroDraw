package ch.epfl.sweng.GyroDraw.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class containing methods for sorting.
 */
public final class SortUtils {

    private SortUtils() {
    }

    /**
     * Sorts a {@link Map} by value.
     *
     * @param map Map to be sorted
     * @param <K> Key type
     * @param <V> Value Type
     * @return The sorted map
     * @see <a href="https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values">StackOverflow
     * answer</a>
     */
    public static <K, V> List<K> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return -((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue())
                        .compareTo(((Map.Entry<K, V>) (o2)).getValue());
            }
        });

        List<K> result = new ArrayList<>();
        for (Entry<K, V> entry : list) {
            result.add(entry.getKey());
        }

        return result;
    }

}
