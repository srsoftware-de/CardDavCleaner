package de.keawe.carddavcleaner;

import java.util.Arrays;
import java.util.TreeSet;

public class NameSet {

	private TreeSet<String> names = new TreeSet<String>();
	
	public TreeSet<String> similarTo(NameSet set2) {
		TreeSet<String> result = new TreeSet<String>();
		if (names.isEmpty() || set2.names.isEmpty()) return result;
		for (String name:names) if (set2.names.contains(name)) result.add(name);
		for (String name:set2.names) if (names.contains(name)) result.add(name);
		return result;
	}

	public void addName(String value){
		value = value.trim();
		if (value.isEmpty()) return;
		String[] parts = value.split(" ");
		Arrays.sort(parts,String.CASE_INSENSITIVE_ORDER);
		names.add(String.join(" ", parts));
	}

}
