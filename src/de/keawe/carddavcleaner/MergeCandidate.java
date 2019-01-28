package de.keawe.carddavcleaner;

import java.util.Vector;

public class MergeCandidate {

	private Contact contactB;
	private Contact contactA;
	private Vector<Tag> similarities;

	public MergeCandidate(Contact contact, Contact contact2, Vector<Tag> similarities) {
		this.contactA = contact;
		this.contactB = contact2;
		this.similarities = similarities;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("MergeCandidate: "+contactA.card().filename()+" / "+contactB.card().filename()+"\n");
		sb.append(contactA.card());
		sb.append("\n==\n");
		sb.append(contactB.card());
		sb.append("Similarities:");
		for (Tag t:similarities) sb.append(" - "+t+"\n");
		return sb.toString();
	}

	public Vector<Tag> similarities() {
		return similarities;
	}

	public Contact contactA() {
		return contactA;
	}

	public Contact contactB() {
		return contactB;
	}
}
