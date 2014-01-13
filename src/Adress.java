import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Adress {

	private boolean home = false;
	private boolean work = false;
	private boolean invalid = false;
	private String streetAdress;
	private String city;
	private String zip;
	private String country;
	private String region;
	private String extendedAdress;
	private String postOfficeBox;

	public VerticalPanel editForm() {
		VerticalPanel form=new VerticalPanel("Adress");
		if (invalid) form.setBackground(Color.red);
		form.add(new InputField("Street Adress",streetAdress));
		form.add(new InputField("Extended Adress",extendedAdress));
		form.add(new InputField("Zip",zip));
		form.add(new InputField("City",city));
		form.add(new InputField("Region",region));
		form.add(new InputField("Country",country));
		form.add(new InputField("Post Office Box",postOfficeBox));
		form.add(new JCheckBox("Home Adress",home));
		form.add(new JCheckBox("Work Adress",work));
		form.scale();
		return form;
	}

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
		if (postOfficeBox != null) sb.append(postOfficeBox);
		sb.append(';');
		if (extendedAdress != null) sb.append(extendedAdress);
		sb.append(';');
		if (streetAdress != null) sb.append(streetAdress);
		sb.append(';');
		if (city != null) sb.append(city);
		sb.append(';');
		if (region != null) sb.append(region);
		sb.append(';');
		if (zip != null) sb.append(zip);
		sb.append(';');
		if (country != null) sb.append(country);
		return sb.toString();
	}

	public Adress(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("ADR")) throw new InvalidFormatException("Adress does not start with \"ADR;\": "+content);
		String line = content.substring(3);
		while (!line.startsWith(":")) {
			String upper = line.toUpperCase();
			if (upper.startsWith(";TYPE=HOME")) {
				home = true;
				line = line.substring(10);
				continue;
			}
			if (upper.startsWith(";TYPE=WORK")) {
				work = true;
				line = line.substring(10);
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
				case 0:
					postOfficeBox=part.trim();
				case 1:
					extendedAdress = part;
					break;
				case 2:
					streetAdress = part;
					break;
				case 3:
					city = part;
					break;
				case 4:
					region = part;
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
		return ((postOfficeBox==null)&&(extendedAdress==null)&&(streetAdress==null)&&(city==null)&&(region==null)&&(zip==null)&&(country==null));
	}

	public boolean isInvalid() {
		return invalid;
	}



}
