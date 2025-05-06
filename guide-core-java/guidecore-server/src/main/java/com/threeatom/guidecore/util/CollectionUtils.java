package com.threeatom.guidecore.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtils {

    public static <K, V> List<V> flattenValues(Map<K, List<V>> map) {
        return map.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public static <T, K, V> Map<K, List<V>> groupByAndMap(
        Collection<T> items,
        Function<T, K> keyExtractor,
        Function<T, V> valueMapper,
        Predicate<T> filter
    ) {
        return items.stream()
            .filter(filter)
            .collect(Collectors.groupingBy(
                keyExtractor,
                Collectors.mapping(valueMapper, Collectors.toList())
            ));
    }

    public static <T, R> List<R> mapToList(Collection<T> items, Function<T, R> mapper) {
        return items.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
}
