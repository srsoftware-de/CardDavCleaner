package de.keawe.carddavcleaner;

public class MergeCandidate {

	private Contact contactB;
	private Contact contactA;

	public MergeCandidate(Contact contact, Contact contact2) {
		// TODO Auto-generated constructor stub
		this.contactA = contact;
		this.contactB = contact2;
	}
	
	@Override
	public String toString() {
		
		return "MergeCandidate: "+contactA.card().filename()+" / "+contactB.card().filename()+"\n"+
		contactA.card()+"\n===\n"+contactB.card();
	}

}
