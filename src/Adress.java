import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;

public class Adress {

	private boolean home = false;
	private boolean work = false;
	private String street;
	private String city;
	private String zip;
	private String country;
	private String state;
	private String name;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ADR");
		if (home) sb.append(";TYPE=HOME");
		if (work) sb.append(";TYPE=WORK");
		sb.append(':');
		sb.append(canonical());
		return sb.toString();
	}
	
	public String canonical() {
		StringBuffer sb = new StringBuffer();
		if (name != null) sb.append(name);
		sb.append(';');
		if (street != null) sb.append(street);
		sb.append(';');
		if (city != null) sb.append(city);
		sb.append(';');
		if (state != null) sb.append(state);
		sb.append(';');
		if (zip != null) sb.append(zip);
		sb.append(';');
		if (country != null) sb.append(country);
		return sb.toString();
	}

	public Adress(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("ADR;")) throw new InvalidFormatException("Adress does not start with \"ADR;\"");
		String line = content.substring(4);
		while (!line.startsWith(":")) {
			String upper = line.toUpperCase();
			if (upper.startsWith("TYPE=HOME")) {
				home = true;
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("TYPE=WORK")) {
				work = true;
				line = line.substring(9);
				continue;
			}
			if (line.startsWith(";")) {
				line = line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line);
		}
		readAddr(line.substring(1));
	}

	private void readAddr(String line) {
		if (line.equals(";;;;;;")) return;
		String[] parts = line.split(";");
		for (int index = 0; index < parts.length; index++) {
			String part = parts[index];
			if (!part.isEmpty()) {
				switch (index) {
				case 1:
					name = part;
					break;
				case 2:
					street = part;
					break;
				case 3:
					city = part;
					break;
				case 4:
					state = part;
					break;
				case 5:
					zip = part;
					break;
				case 6:
					country = part;
					break;
				default:
					System.err.println("found " + part + " @" + index + " in " + line);
					throw new NotImplementedException();
				}
			}
		}
	}

	public boolean isEmpty() {
		return ((street==null) && (city==null) && (zip==null) && (country==null) && (state==null) && (name==null));
	}


}
