package de.keawe.carddavcleaner;

import java.util.TreeMap;

public class Tag {

	
	private String group = null;
	private String name = null;
	private TreeMap<String,String> params = new TreeMap<String, String>();
	private String value = null;
	private String line;
	
	private void addParam(String substring) {
		String[] parts = substring.split("=", 2);
		params.put(parts[0], parts[1]);
	}

	public String name() {
		return name;
	}

	public Tag(String line) {
		this.line = line;
		int semicolonPos = line.indexOf(";");
		int endOfName = (semicolonPos < 0) ? line.indexOf(":") : Math.min(semicolonPos, line.indexOf(":"));
		name = line.substring(0, endOfName);
		int dotIndex = name.indexOf(".");
		if (dotIndex>-1) {
			group = name.substring(0,dotIndex);
			name = name.substring(dotIndex+1);
		}
		warnIfUnknown(name);
		String postFix = line.substring(endOfName);
		while (postFix.startsWith(";")) postFix = readParam(postFix);
		if (postFix.startsWith(":")) value = postFix.substring(1).trim();
	}
	
	private String readParam(String line) {
		boolean inString=false;
		int index=1;
		while (index<line.length()) {
			char c = line.charAt(index);
			switch (c) {
				case '"':
					inString = !inString;
					break;
				case ':':
				case ';':
					if (!inString) {
						addParam(line.substring(1, index));
						return line.substring(index);
					}
					break;
			}
			index++;
		}
		System.out.println("Tag.readParam("+line+")");
		return "";
	}


	public static boolean test() {
		boolean error = false;
		error |= testNameRecognition();
		error |= testParamRecognition();
		error |= testValueRecognition();
		return error;
	}

	private static boolean testNameRecognition() {
		boolean error = false;
		System.out.print("Testing name recognition...");
		Tag t = new Tag("NAME:Content");
		if (t.name.equals("NAME")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing name recognition (with param)...");
		t = new Tag("NAME;PARAM=Test:Content");
		if (t.name.equals("NAME")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing name recognition (with group)...");
		t = new Tag("GROUP.NAME:Content");
		if (t.name.equals("NAME")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing name recognition (with group and param)...");
		t = new Tag("GROUP.NAME;PARAM=Test:Content");
		if (t.name.equals("NAME")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		return error;
	}
	
	private static boolean testParamRecognition() {
		boolean error = false;
		System.out.print("Testing parameter recognition (no param)...");
		Tag t = new Tag("NAME:Content");
		if (t.params.isEmpty()) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing parameter recognition (single param)...");
		t = new Tag("NAME;PARAM=Test:Content");
		if (t.params.size()==1 && t.params.get("PARAM").equals("Test")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing parameter recognition (multi param)...");
		t = new Tag("NAME;PARAM=Test;PARAM2=Toast:Content");
		if (t.params.size()==2 && t.params.get("PARAM").equals("Test") && t.params.get("PARAM2").equals("Toast")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}
		
		System.out.print("Testing parameter recognition (quoted param)...");
		t = new Tag("NAME;PARAM=\"Quoted:Param\":Content");
		if (t.params.size()==1 && t.params.get("PARAM").equals("\"Quoted:Param\"")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing parameter recognition (quoted multi param)...");
		t = new Tag("NAME;PARAM=\"Quoted;Param\";PARAM2=Toast:Content");
		if (t.params.size()==2 && t.params.get("PARAM").equals("\"Quoted;Param\"") && t.params.get("PARAM2").equals("Toast")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}
		return error;
	}
	
	private static boolean testValueRecognition() {
		boolean error = false;
		System.out.print("Testing value recognition (no param)...");
		Tag t = new Tag("NAME:Content");
		if (t.value.equals("Content")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing value recognition (single param)...");
		t = new Tag("NAME;PARAM=Test:Content");
		if (t.value.equals("Content")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing value recognition (multi param)...");
		t = new Tag("NAME;PARAM=Test;PARAM2=Toast:Content");
		if (t.value.equals("Content")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}
		
		System.out.print("Testing value recognition (quoted param)...");
		t = new Tag("NAME;PARAM=\"Quoted:Param\":Content");
		if (t.value.equals("Content")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}

		System.out.print("Testing value recognition (quoted multi param)...");
		t = new Tag("NAME;PARAM=\"Quoted;Param\";PARAM2=Toast:Content");
		if (t.value.equals("Content")) {
			System.out.println("success");
		} else {
			System.out.println("error");
			error = true;
		}
		return error;
	}
	
	@Override
	public String toString() {
		return name()+":"+value();
	}
	
	public String value() {
		return value;
	}
	
	private void warnIfUnknown(String name) {
		String [] knownNames = new String[] {
			"ADR",
			"BDAY",
			"BEGIN",
			"CATEGORIES",
			"CLASS",
			"EMAIL",
			"END",
			"FN",
			"IMPP",
			"LABEL",
			"MAILER",
			"N",
			"NICKNAME",
			"NOTE",
			"ORG",
			"PHOTO",
			"PRODID",
			"PROFILE",
			"REV",
			"TEL",
			"TITLE",
			"UID",
			"URL",
			"VERSION",
			"X-ABLABEL",
			"X-MOZILLA-HTML",
			"X-MS-IMADDRESS",
			"X-THUNDERBIRD-ETAG"
		};
		boolean known = false;
		name = name.toUpperCase();
		for (int i=0; i<knownNames.length;i++) known |= name.equals(knownNames[i]);
		if (!known) System.err.println("Encountered unknown tag: "+this.line);
		if (name.equals("LABEL")) System.out.println(this.line);
	}

}
