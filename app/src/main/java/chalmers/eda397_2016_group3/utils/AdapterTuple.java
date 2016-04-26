package chalmers.eda397_2016_group3.utils;

/**
 * Created by Sebastian Blomberg on 2016-04-21.
 */
public class AdapterTuple<K,V> {
    private K key;
    private V value;

    public AdapterTuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
