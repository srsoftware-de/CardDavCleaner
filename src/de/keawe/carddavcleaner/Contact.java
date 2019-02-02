package de.keawe.carddavcleaner;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Vector;

import de.keawe.gui.Translations;

public class Contact {

	//original fields
	private Vector<Tag> tags = new Vector<Tag>();
	private boolean altered=false;
	private boolean markedForRemoval=false;

	// these fields can be reset and will be regenerated from lines
	private NameSet canonicalNames = null;
	private TreeSet<String> canonicalNumbers = null;
	private TreeSet<String> emails = null;
	private TreeSet<String> messengers = null;
	private String filename = null;
	
	static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
	
	public VCard card() {
		StringBuffer sb = new StringBuffer();
		sb.append("BEGIN:VCARD\r\n");
		for (Tag t:tags) sb.append(t.code()+"\r\n");
		sb.append("END:VCARD\r\n");
		return new VCard(sb,filename);
	}
	
	public String html() {
		StringBuffer sb = new StringBuffer();
		for (Tag t:tags) {
			if (t.name("PRODID")) continue;
			if (t.name("UID")) continue;
			if (t.name("VERSION")) continue;
			sb.append(t.shortCode()+"<br/>\n");
		}
		return sb.toString();
	}
	
	public Contact(VCard card, boolean dropEmptyFields) {
		filename = card.filename();
		StringBuffer codeBuffer = fixLineBreaks(card.buffer());
		for (String line:codeBuffer.toString().replace("\r\n ", "").split("\r\n")) {
			Tag t = new Tag(line);
			if (t.name("BEGIN")) continue; // ignore BEGIN:VCARD
			if (t.name("END")) continue; // ignore END:VCARD
			if (t.isEmpty() && dropEmptyFields) {
				altered = true;
				continue;
			}
			tags.add(t);
		}
	}
	
	private TreeSet<String> emails() {
		if (emails == null) {
			emails = new TreeSet<String>();
			for (Tag tag : tags) {
				if (!tag.isEmpty() && tag.name("EMAIL")) emails.add(tag.value());
			}
		}
		return emails;
	}
	
	private TreeSet<String> messengers() {
		if (messengers  == null) {
			messengers = new TreeSet<String>();
			for (Tag tag : tags) {
				if (tag.isEmpty()) continue;
				if (tag.name("IMPP")) messengers.add(tag.value());
				if (tag.name("X-AIM")) messengers.add(tag.value());
				if (tag.name("X-MS-IMADDRESS")) messengers.add(tag.value());
			}
		}
		return messengers;
	}
	
	private NameSet names() {
		if (canonicalNames == null) {
			canonicalNames = new NameSet();
			for (Tag tag : tags) {
				if (tag.isEmpty()) continue;
				if (tag.name("FN")) canonicalNames.addName(tag.value());
				if (tag.name("N")) canonicalNames.addName(tag.value().replace(";"," "));
				if (tag.name("NICKNAME")) canonicalNames.addName(tag.value());
			}
		}
		return canonicalNames;
	}
	
	private TreeSet<String> numbers() {
		if (canonicalNumbers == null) {
			canonicalNumbers = new TreeSet<String>();
			for (Tag tag : tags) {
				if (tag.name("TEL") && !tag.isEmpty()) {
					String val = tag.value().replace(" ","").replace("+49", "0").replace("(","").replace(")", "").replace("/","").replace("-","");;
					for (int i=0; i<val.length();i++) {
						if (!Character.isDigit(val.charAt(i))) {
							System.out.println("Found non-digit characters in phone number: "+val);
							break;
						}
					}
					canonicalNumbers.add(val);
				}
			}
		}
		return canonicalNumbers;
	}

	private static StringBuffer fixLineBreaks(StringBuffer buffer) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i<buffer.length(); i++) {
			char c = buffer.charAt(i);
			if (c == '\n') {
				if (i==0 || buffer.charAt(i-1) != '\r'){
					result.append('\r');
				}
			}
			result.append(c);
		}
		return result;
	}
	
	public Vector<Tag> similarTags(Contact b){
		Vector<Tag> similarTags = new Vector<Tag>();
		for (String name: names().similarTo(b.names())) {
			similarTags.add(new Tag("Name:"+name));
		}
		for (String number : numbers()) {
			if (b.numbers().contains(number)) similarTags.add(new Tag("TEL:"+number));
		}
		for (String email : emails()) {
			if (b.emails().contains(email)) similarTags.add(new Tag("EMAIL:"+email));
		}
		for (String messenger : messengers()) {
			if (b.messengers().contains(messenger)) similarTags.add(new Tag("IMPP:"+messenger));
		}
		return similarTags;
	}

	public static boolean test() {
		boolean error = false;
		error |= testFixLineBreaks();
		return error;
	}

	private static boolean testFixLineBreaks() {
		StringBuffer input = new StringBuffer("\nThis \nis\r\n a test\n");
		System.out.print("Testing Contact.fixLineBreaks...");
		if (fixLineBreaks(input).toString().equals("\r\nThis \r\nis\r\n a test\r\n")) {
			System.out.println("success");
			return false;
		}
		System.out.println("failed");
		return true;
	}
	
	public void resetCalculatedFields() {
		canonicalNames = null;
		canonicalNumbers = null;
		emails = null;
		messengers = null;
	}

	public Vector<Tag> tags() {
		Vector<Tag> copy = new Vector<Tag>();
		for (Tag t : tags) copy.add(t.clone());
		return copy;
	}

	public void updateTags(Collection<Tag> newTags) {
		tags = new Vector<Tag>();
		for (Tag t: newTags) tags.add(t);
		resetCalculatedFields();
		altered = true;
	}

	public void markForRemoval() {
		markedForRemoval=true;
	}

	public boolean markedForRemoval() {
		return markedForRemoval;
	}

	public String filename() {
		return filename;
	}

	/*------------+--------------------------------------------------\
    | Cardinality | Meaning                                          |
    +-------------+--------------------------------------------------+
    |      1      | Exactly one instance per vCard MUST be present.  |
    |      *1     | Exactly one instance per vCard MAY be present.   |
    |      1*     | One or more instances per vCard MUST be present. |
    |      *      | One or more instances per vCard MAY be present.  |
    \-------------+-------------------------------------------------*/
	
	public Conflict detectConflicts() {
		String[] fields = new String[] {"ANNIVERSARY","F","BDAY","GENDER","KIND","N","PRODID","REV","UID","VERSION"};
		
		for (String fieldName : fields) {
			Conflict conflict = detectMultiple(fieldName);
			if (conflict!= null) return conflict;
		}
		
		fields = new String[] { "FN", "VERSION" };
		for (String fieldName : fields) {
			Conflict conflict = detectMissing(fieldName);
			if (conflict!= null) return conflict;
		}
		return null;
	}

	private Conflict detectMissing(String fieldName) {
		Vector<Tag> matchingTags = new Vector<Tag>();
		for (Tag t : tags) {
			if (t.name(fieldName)) matchingTags.add(t);
		}
		if (tags.isEmpty()) return new Conflict(fieldName);
		return null;
	}

	private Conflict detectMultiple(String fieldName) {
		Vector<Tag> matchingTags = new Vector<Tag>();
		for (Tag t : tags) {
			if (t.name(fieldName)) matchingTags.add(t);
		}
		if (matchingTags.size()>1) return new Conflict(fieldName).setTags(matchingTags);
		return null;
	}

	public void dropConflictingUids(Conflict conflict) { // remove all but the first UID
		Vector<Tag> conflictTags = conflict.tags();
		conflictTags.remove(0);
		removeTags(conflictTags);
	}

	public void removeTags(Vector<Tag> conflictingTags) {
		tags.removeAll(conflictingTags);
		altered = true;
	}

	public boolean altered() {
		return altered;
	}
}
