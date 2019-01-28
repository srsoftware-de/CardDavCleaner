package de.keawe.carddavcleaner;

import java.util.Arrays;
import java.util.TreeSet;

public class NameSet {

	private TreeSet<String> names = new TreeSet<String>();
	
	public boolean similarTo(NameSet set2) {
		if (names.isEmpty() || set2.names.isEmpty()) return false;
		for (String name:names) if (set2.names.contains(name)) return true;
		for (String name:set2.names) if (names.contains(name)) return true;
		return false;
	}

	public void addName(String value){
		String[] parts = value.split(" ");
		Arrays.sort(parts,String.CASE_INSENSITIVE_ORDER);
		names.add(String.join(" ", parts));
	}

}
