import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Adress implements DocumentListener, ChangeListener,Comparable<Adress> {

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
	private VerticalPanel form;
	private InputField zipField,streetField,extendedField,cityField,regionField,countryField,postBoxField;
	private JCheckBox homeBox,workBox;

	public VerticalPanel editForm() {
		form=new VerticalPanel("Adress");
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);
		
		form.add(streetField=new InputField("Street Adress",streetAdress));
		streetField.addChangeListener(this);
		form.add(extendedField=new InputField("Extended Adress",extendedAdress));
		extendedField.addChangeListener(this);
		form.add(zipField=new InputField("Zip",zip));
		zipField.addChangeListener(this);
		form.add(cityField=new InputField("City",city));
		cityField.addChangeListener(this);
		form.add(regionField=new InputField("Region",region));
		regionField.addChangeListener(this);
		form.add(countryField=new InputField("Country",country));
		countryField.addChangeListener(this);
		form.add(postBoxField=new InputField("Post Office Box",postOfficeBox));
		postBoxField.addChangeListener(this);
		form.add(homeBox=new JCheckBox("Home Adress",home));
		homeBox.addChangeListener(this);
		form.add(workBox=new JCheckBox("Work Adress",work));
		workBox.addChangeListener(this);
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
		postOfficeBox=null;
		extendedAdress=null;
		streetAdress=null;
		city=null;
		region=null;
		zip=null;
		country=null;
		if (line.equals(";;;;;;")) return;
		String[] parts = line.split(";");
		for (int index = 0; index < parts.length; index++) {
			String part = parts[index].trim();
			switch (index) {
				case 0:
					postOfficeBox=part;
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
	
	public boolean isEmpty() {
		return ((postOfficeBox==null||postOfficeBox.isEmpty())&&(extendedAdress==null||extendedAdress.isEmpty())&&(streetAdress==null||streetAdress.isEmpty())&&(city==null||city.isEmpty())&&(region==null||region.isEmpty())&&(zip==null||zip.isEmpty())&&(country==null||country.isEmpty()));
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void changedUpdate(DocumentEvent arg0) {
		update();
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();
	}

	public void stateChanged(ChangeEvent arg0) {
		update();
	}	

	private void update() {
		invalid=false;
		home=homeBox.isSelected();
		work=workBox.isSelected();
		readAddr(postBoxField.getText()+";"+extendedField.getText()+";"+streetField.getText()+";"+cityField.getText()+";"+regionField.getText()+";"+zipField.getText()+";"+countryField.getText());		
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.red:Color.green);
		}
	}

	public int compareTo(Adress otherAdress) {
	return this.toString().compareTo(otherAdress.toString());
	}
}
