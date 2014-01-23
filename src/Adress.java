import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Adress extends Mergable<Adress> implements DocumentListener, ChangeListener, Comparable<Adress> {

	public static void test() {
		try {
			System.out.print("Adress creation (empty)...");
			Adress emptyA = new Adress("ADR:");
			Adress emptyB = new Adress("ADR:;;;;;;");
			if (emptyA.toString().equals("ADR:;;;;;;") && emptyB.toString().equals("ADR:;;;;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + emptyA);
			}

			System.out.print("Adress creation (postbox)...");
			Adress pbA = new Adress("ADR:postbox");
			Adress pbB = new Adress("ADR:xobtsop;;;;;;");
			if (pbA.toString().equals("ADR:postbox;;;;;;")&&pbB.toString().equals("ADR:xobtsop;;;;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + pbA);
			}

			System.out.print("Adress creation (extended)...");
			Adress extA = new Adress("ADR:;extended");
			Adress extB = new Adress("ADR:;dednetxe;;;;;");
			if (extA.toString().equals("ADR:;extended;;;;;") && extB.toString().equals("ADR:;dednetxe;;;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + extA);
			}

			System.out.print("Adress creation (street)...");
			Adress strA = new Adress("ADR:;;street");
			Adress strB = new Adress("ADR:;;teerts;;;;");
			if (strA.toString().equals("ADR:;;street;;;;")&&strB.toString().equals("ADR:;;teerts;;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + strA);
			}
			
			System.out.print("Adress creation (city)...");
			Adress citA = new Adress("ADR:;;;city");
			Adress citB = new Adress("ADR:;;;ytic;;;");
			if (citA.toString().equals("ADR:;;;city;;;")&&citB.toString().equals("ADR:;;;ytic;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + citA);
			}

			System.out.print("Adress creation (region)...");
			Adress regA = new Adress("ADR:;;;;region");
			Adress regB = new Adress("ADR:;;;;noiger;;");
			if (regA.toString().equals("ADR:;;;;region;;")&&regB.toString().equals("ADR:;;;;noiger;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + regA);
			}

			System.out.print("Adress creation (zip)...");
			Adress zipA = new Adress("ADR:;;;;;zip");
			Adress zipB = new Adress("ADR:;;;;;piz;");
			if (zipA.toString().equals("ADR:;;;;;zip;")&&zipB.toString().equals("ADR:;;;;;piz;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + zipA);
			}

			System.out.print("Adress creation (country)...");
			Adress ctrA = new Adress("ADR:;;;;;;country");
			Adress ctrB = new Adress("ADR:;;;;;;yrtnuoc");
			if (ctrA.toString().equals("ADR:;;;;;;country")&&ctrB.toString().equals("ADR:;;;;;;yrtnuoc")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + ctrA);
			}
			
			Adress [] adresses1={emptyA,pbA,extA,strA,citA,regA,zipA,ctrA};
			Adress [] adresses2={emptyB,pbB,extB,strB,citB,regB,zipB,ctrB};

			System.out.print("Compatibility test 1...");
			int comp=0;
			for (Adress a:adresses1){
				for (Adress b:adresses1){
					if (a.isCompatibleWith(b)) comp++;
				}
			}
			if (comp==64){
				System.out.println("ok");
			} else {
				System.err.println("fail!");
			}

			System.out.print("Compatibility test 2...");
			comp=0;
			for (Adress a:adresses1){
				for (Adress b:adresses2){
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat=(a+""+b).replace("ADR:","").replace(";", "");
						if (concat.equals("postboxxobtsop")) comp++;
						if (concat.equals("streetteerts")) comp++;
						if (concat.equals("cityytic")) comp++;
						if (concat.equals("regionnoiger")) comp++;
						if (concat.equals("zippiz")) comp++;
						if (concat.equals("countryyrtnuoc")) comp++;
					}
				}
			}
			if (comp==64){
				System.out.println("ok");
			} else {
				System.err.println("fail!");
			}

			// continue tests here
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	private boolean home = false;
	private boolean work = false;
	private boolean invalid = false;
	private String extendedAdress;
	private String streetAdress;
	private String postOfficeBox;
	private String zip;
	private String city;
	private String region;
	private String country;
	private VerticalPanel form;
	private InputField zipField, streetField, extendedField, cityField, regionField, countryField, postBoxField;

	private JCheckBox homeBox, workBox;

	public Adress(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("ADR")) throw new InvalidFormatException("Adress does not start with \"ADR;\": " + content);
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

	public void changedUpdate(DocumentEvent arg0) {
		update();
	}

	public int compareTo(Adress otherAdress) {
		return this.toString().compareTo(otherAdress.toString());
	}

	public VerticalPanel editForm() {
		form = new VerticalPanel("Adress");
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);

		form.add(streetField = new InputField("Street Adress", streetAdress));
		streetField.addChangeListener(this);
		form.add(extendedField = new InputField("Extended Adress", extendedAdress));
		extendedField.addChangeListener(this);
		form.add(zipField = new InputField("Zip", zip));
		zipField.addChangeListener(this);
		form.add(cityField = new InputField("City", city));
		cityField.addChangeListener(this);
		form.add(regionField = new InputField("Region", region));
		regionField.addChangeListener(this);
		form.add(countryField = new InputField("Country", country));
		countryField.addChangeListener(this);
		form.add(postBoxField = new InputField("Post Office Box", postOfficeBox));
		postBoxField.addChangeListener(this);
		form.add(homeBox = new JCheckBox("Home Adress", home));
		homeBox.addChangeListener(this);
		form.add(workBox = new JCheckBox("Work Adress", work));
		workBox.addChangeListener(this);
		form.scale();
		return form;
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	public boolean isCompatibleWith(Adress adr2) {
		if (different(country, adr2.country)) return false;
		if (different(region, adr2.region)) return false;
		if (different(city, adr2.city)) return false;
		if (different(zip, adr2.zip)) return false;
		if (different(postOfficeBox, adr2.postOfficeBox)) return false;
		if (different(streetAdress, adr2.streetAdress)) return false;
		if (different(extendedAdress, extendedAdress)) return false;
		return true;
	}

	public boolean isEmpty() {
		return ((postOfficeBox == null || postOfficeBox.isEmpty()) && (extendedAdress == null || extendedAdress.isEmpty()) && (streetAdress == null || streetAdress.isEmpty()) && (city == null || city.isEmpty()) && (region == null || region.isEmpty()) && (zip == null || zip.isEmpty()) && (country == null || country.isEmpty()));
	}

	public boolean isInvalid() {
		return invalid;
	}

	public boolean mergeWith(Adress adr2) {
		if (!isCompatibleWith(adr2)) return false;
		extendedAdress = merge(extendedAdress, adr2.extendedAdress);
		streetAdress = merge(streetAdress, adr2.streetAdress);
		postOfficeBox = merge(postOfficeBox, adr2.postOfficeBox);
		zip = merge(zip, adr2.zip);
		city = merge(city, adr2.city);
		region = merge(region, adr2.region);
		country = merge(country, adr2.country);
		if (adr2.home) home = true;
		if (adr2.work) work = true;
		return true;
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();
	}

	public void stateChanged(ChangeEvent arg0) {
		update();
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

	private void readAddr(String line) {
		postOfficeBox = null;
		extendedAdress = null;
		streetAdress = null;
		city = null;
		region = null;
		zip = null;
		country = null;
		if (line.equals(";;;;;;")) return;
		String[] parts = line.split(";");
		for (int index = 0; index < parts.length; index++) {
			String part = parts[index].trim();
			switch (index) {
			case 0:
				postOfficeBox = part;
				break;
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

	private void update() {
		invalid = false;
		home = homeBox.isSelected();
		work = workBox.isSelected();
		readAddr(postBoxField.getText() + ";" + extendedField.getText() + ";" + streetField.getText() + ";" + cityField.getText() + ";" + regionField.getText() + ";" + zipField.getText() + ";" + countryField.getText());
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid ? Color.red : Color.green);
		}
	}
}
