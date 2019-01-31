package de.keawe.carddavcleaner;

import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.keawe.gui.HorizontalPanel;
import de.keawe.gui.Translations;
import de.keawe.gui.VerticalPanel;

public class MergeCandidate {

	private Contact contactB;
	private Contact contactA;
	private Vector<Tag> similarities;

	static String _(String text) {
		return Translations.get(text);
	}

	static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}

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

	public void merge() {
		Vector<Tag> tagsOfA = contactA.tags();
		Vector<Tag> tagsOfB = contactB.tags();
		Vector<Tag> mergedTags = new Vector<Tag>();
		Vector<Tag> unmergedTagsOfA = new Vector<Tag>();
		
		while (!tagsOfA.isEmpty()) { // go through all tags of A
			Tag tagOfA = tagsOfA.remove(0);
			String key = tagOfA.name().toUpperCase();
			if (key.equals("BEGIN") || key.equals("END") || key.equals("PRODID")) continue; // ignore BEGIN:VCARD and END:VCARD
			if (tagOfA.value().isEmpty()) continue;
			
			int index = 0;
			boolean aMerged = false;
			while (index<tagsOfB.size()) { // loop through B
				Tag tagOfB = tagsOfB.elementAt(index);
				
				key = tagOfB.name().toUpperCase();
				if (key.equals("BEGIN") || key.equals("END") || key.equals("PRODID")) {
					tagsOfB.removeElementAt(index); // remove B-tag from B-list
					continue; // ignore BEGIN:VCARD and END:VCARD
				}
				if (tagOfB.value().isEmpty()) {
					tagsOfB.removeElementAt(index); // remove B-tag from B-list
					continue; // ignore empty tags		
				}
				
				Tag mergedTag = tagOfA.mergeWith(tagOfB);
				
				if (mergedTag != null) { // if tags coud be merged:
					mergedTags.add(mergedTag);
					tagsOfB.removeElementAt(index); // remove B-tag from B-list, A-tag has already been removed
					aMerged = true;
					break;
				}
				index++;
			}
			if (!aMerged) unmergedTagsOfA.add(tagOfA); // merge has not been successfull: remember A
		}

		String[] newLines = new String[mergedTags.size()+unmergedTagsOfA.size()+tagsOfB.size()+2];
		int i=0;
		newLines[i++] = "BEGIN:VCARD";
		for (Tag t:mergedTags) newLines[i++] = t.code();
		for (Tag t:unmergedTagsOfA) newLines[i++] = t.code();
		for (Tag t:tagsOfB) newLines[i++] = t.code();
		newLines[i++] = "END:VCARD";
		System.out.println(String.join("\n", newLines));
		contactA.updateLines(newLines);
		contactB.markForRemoval();
	}

	public int propose() {
		String text = "";
		int num = similarities.size();
		for (int i =0; i<similarities.size(); i++) {
			Tag t = similarities.get(i);
			text = text + t.shortString();
			if (i+1 < num) {
				text += (i+2 == num) ? " and " : ", ";
			} else text = _("# "+(num>1?"are":"is")+" used by the following contacts:",text);
		}
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel(text));
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel("<html><br>" + contactA().card().toString().replace("\n", "&nbsp<br>")));
		hp.add(new JLabel("<html><br>" + contactB().card().toString().replace("\n", "<br>")));
		hp.scale();
		vp.add(hp);
		vp.add(new JLabel(_("<html><br>Shall those contacts be <i>merged</i>?")));
/*		if (contact.birthday() != null && contact2.birthday() != null && !contact.birthday().equals(contact2.birthday())) {
			vp.add(new JLabel(_("<html><font color=\"red\">Warning! Those contacts contain unequal birth dates!")));
		}*/
		vp.scale();
		return JOptionPane.showConfirmDialog(null, vp, _("Please decide!"), JOptionPane.YES_NO_CANCEL_OPTION);
	}
}
