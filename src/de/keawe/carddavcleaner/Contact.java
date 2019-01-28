package de.keawe.carddavcleaner;

import java.util.TreeSet;

public class Contact {

	private VCard card;
	private String[] lines;

	public Contact(VCard card) {
		this.card = card;
		StringBuffer codeBuffer = fixLineBreaks(card.buffer());
		lines = codeBuffer.toString().replace("\r\n ", "").split("\r\n");
	}
	
	private TreeSet<String> emails() {
		TreeSet<String> result = new TreeSet<String>();
		for (String line : lines) {
			Tag tag = new Tag(line);
			if (tag.value().isEmpty()) continue;
			if (tag.name().toUpperCase().equals("EMAIL")) {
				result.add(tag.value());
			}
		}
		return result;
	}
	
	private static TreeSet<String> intersection(TreeSet<String> numbers, TreeSet<String> numbers2) {
		TreeSet<String> result = new TreeSet<String>();
		for (String number:numbers) {
			if (numbers2.contains(number)) result.add(number);
		}
		return result;
	}
	
	private TreeSet<String> messengers() {
		TreeSet<String> result = new TreeSet<String>();
		for (String line : lines) {
			Tag tag = new Tag(line);
			if (tag.value().isEmpty()) continue;
			if (tag.name().toUpperCase().equals("IMPP")) {
				result.add(tag.value());
			}
		}
		return result;
	}
	
	private NameSet names() {
		NameSet result = new NameSet();
		for (String line : lines) {
			Tag tag = new Tag(line);
			if (tag.value().isEmpty()) continue;
			String name=tag.name().toUpperCase();
			if (name.equals("FN")) {
				result.addName(tag.value());
			} else if(name.equals("N")) {
				result.addName(tag.value().replace(";"," "));
			}
		}
		return result;
	}
	
	private TreeSet<String> numbers() {
		TreeSet<String> result = new TreeSet<String>();
		for (String line : lines) {
			Tag tag = new Tag(line);
			if (tag.value().isEmpty()) continue;
			if (tag.name().toUpperCase().equals("TEL")) {
				String val = tag.value().replace(" ","").replace("+49", "0").replace("(","").replace(")", "").replace("/","");
				for (int i=0; i<val.length();i++) {
					if (!Character.isDigit(val.charAt(i))) {
						System.out.println("Found non-digit characters in phone number: "+val);
						break;
					}
				}

				result.add(val);
			}
		}
		return result;
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
	
	public boolean similarTo(Contact b) {
		if (names().similarTo(b.names())) return true;
		if (!intersection(numbers(),b.numbers()).isEmpty()) return true;
		if (!intersection(emails(), b.emails()).isEmpty()) return true;
		if (!intersection(messengers(), b.messengers()).isEmpty()) return true;
		return false;
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
