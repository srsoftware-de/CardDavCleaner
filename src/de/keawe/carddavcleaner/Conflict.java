package de.keawe.carddavcleaner;

import java.util.Vector;

public class Conflict {

	private Vector<Tag> tags = null;
	private String fieldName;

	public Conflict(String fieldName) {
		this.fieldName = fieldName;
	}
	
	

	@Override
	public String toString() {
		
		if (tags == null) {
			return "Missing "+fieldName+" tag!";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("No more than one "+fieldName+" entry allowed in contact, but found:");
		for (Tag t:tags) sb.append("\n"+t.code());
		return sb.toString();
	}



	public Conflict setTags(Vector<Tag> tags) {
		this.tags = tags;
		return this;
	}


	public String param() {
		if (tags == null) return null;
		return tags.firstElement().name();
	}

	public boolean param(String key) {
		if (tags==null) return false;
		return tags.firstElement().name(key);
	}



	public Vector<Tag> tags() {
		return tags;
	}
}
