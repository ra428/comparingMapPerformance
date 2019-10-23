package comparingMapPerformance;

import java.util.*;

public class SynchronisedMap<K, V> implements Map<K, V> {

    private final Map<K, V> map;

    public SynchronisedMap(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public synchronized int size() {
        return map.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public synchronized boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public synchronized V get(Object key) {
        return map.get(key);
    }

    @Override
    public synchronized V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public synchronized V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public synchronized void clear() {
        map.clear();
    }

    @Override
    public synchronized Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public synchronized Collection<V> values() {
        return map.values();
    }

    @Override
    public synchronized Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    static SynchronisedMap hashMap() {
        return new SynchronisedMap<>(new HashMap<>());
    }

    static SynchronisedMap treeMap() {
        return new SynchronisedMap<>(new TreeMap<>());
    }
}
