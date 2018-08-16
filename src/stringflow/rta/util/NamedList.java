package stringflow.rta.util;

import java.util.LinkedList;

public class NamedList<V extends NamedDataType> extends LinkedList<V> {

	public V get(String name) {
		for(V value : this) {
			if(value.name.equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}
}