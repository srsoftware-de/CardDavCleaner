package de.keawe.carddavcleaner;

import java.util.TreeSet;
import java.util.Vector;

public class Contact {

	private VCard card;
	private String[] lines;
	private NameSet canonicalNames = null;
	private TreeSet<String> canonicalNumbers = null;
	private TreeSet<String> emails = null;
	private TreeSet<String> messengers;


	public Contact(VCard card) {
		this.card = card;
		StringBuffer codeBuffer = fixLineBreaks(card.buffer());
		lines = codeBuffer.toString().replace("\r\n ", "").split("\r\n");
	}
	
	private TreeSet<String> emails() {
		if (emails == null) {
			emails = new TreeSet<String>();
			for (String line : lines) {
				Tag tag = new Tag(line);
				if (tag.value().isEmpty()) continue;
				if (tag.name().toUpperCase().equals("EMAIL")) emails.add(tag.value());
			}
		}
		return emails;
	}
	
	private TreeSet<String> messengers() {
		if (messengers  == null) {
			messengers = new TreeSet<String>();
			for (String line : lines) {
				Tag tag = new Tag(line);
				if (tag.value().isEmpty()) continue;
				String name = tag.name().toUpperCase();
				if (name.equals("IMPP")) messengers.add(tag.value());
				if (name.equals("X-AIM")) messengers.add(tag.value());
				if (name.equals("X-MS-IMADDRESS")) messengers.add(tag.value());
			}
		}
		return messengers;
	}
	
	private NameSet names() {
		if (canonicalNames == null) {
			canonicalNames = new NameSet();
			for (String line : lines) {
				Tag tag = new Tag(line);
				if (tag.value().isEmpty()) continue;
				String name=tag.name().toUpperCase();
				if (name.equals("FN")) canonicalNames.addName(tag.value());
				if (name.equals("N")) canonicalNames.addName(tag.value().replace(";"," "));
				if (name.equals("NICKNAME")) canonicalNames.addName(tag.value());
			}
		}
		return canonicalNames;
	}
	
	private TreeSet<String> numbers() {
		if (canonicalNumbers == null) {
			canonicalNumbers = new TreeSet<String>();
			for (String line : lines) {
				Tag tag = new Tag(line);
				if (tag.value().isEmpty()) continue;
				if (tag.name().toUpperCase().equals("TEL")) {
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
			similarTags.add(new Tag("NAME:"+name));
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

	public VCard card() {
		return card;
	}


}
