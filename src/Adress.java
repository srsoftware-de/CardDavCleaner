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
			System.out.print("Adress creation (null)...");
			try {
				Adress nullA = new Adress(null);
				System.err.println("failed: " + nullA);
				System.exit(-1);
			} catch (InvalidFormatException ife) {				
				System.out.println("ok");
			}
			
			System.out.print("Adress creation (empty)...");
			String testcase="ADR:;;;;;;";
			Adress emptyA = new Adress(testcase.replace(";",""));
			Adress emptyB = new Adress(testcase);
			if (emptyA.toString().equals(testcase) && emptyB.toString().equals(testcase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + emptyA);
				System.exit(-1);
			}

			System.out.print("Adress creation (postbox)...");
			testcase="ADR:xobtsop;;;;;;";
			Adress pbA = new Adress(Tests.reversed(testcase));
			Adress pbB = new Adress(testcase);
			if (pbA.toString().equals("ADR:postbox;;;;;;")&&pbB.toString().equals("ADR:xobtsop;;;;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + pbA+"/"+pbB);
				System.exit(-1);
			}

			System.out.print("Adress creation (extended)...");
			testcase="ADR:;dednetxe;;;;;";
			Adress extA = new Adress(Tests.reversed(testcase));
			Adress extB = new Adress(testcase);
			if (extA.toString().equals("ADR:;extended;;;;;") && extB.toString().equals("ADR:;dednetxe;;;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + extA);
				System.exit(-1);
			}

			System.out.print("Adress creation (street)...");
			testcase="ADR:;;teerts;;;;";
			Adress strA = new Adress(Tests.reversed(testcase));
			Adress strB = new Adress(testcase);
			if (strA.toString().equals("ADR:;;street;;;;")&&strB.toString().equals("ADR:;;teerts;;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + strA);
				System.exit(-1);
			}
			
			System.out.print("Adress creation (city)...");
			testcase="ADR:;;;ytic;;;";
			Adress citA = new Adress(Tests.reversed(testcase));
			Adress citB = new Adress(testcase);
			if (citA.toString().equals("ADR:;;;city;;;")&&citB.toString().equals("ADR:;;;ytic;;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + citA);
				System.exit(-1);
			}

			System.out.print("Adress creation (region)...");
			testcase="ADR:;;;;noiger;;";
			Adress regA = new Adress(Tests.reversed(testcase));
			Adress regB = new Adress(testcase);
			if (regA.toString().equals("ADR:;;;;region;;")&&regB.toString().equals("ADR:;;;;noiger;;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + regA);
				System.exit(-1);
			}

			System.out.print("Adress creation (zip)...");
			testcase="ADR:;;;;;piz;";
			Adress zipA = new Adress(Tests.reversed(testcase));
			Adress zipB = new Adress(testcase);
			if (zipA.toString().equals("ADR:;;;;;zip;")&&zipB.toString().equals("ADR:;;;;;piz;")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + zipA);
				System.exit(-1);
			}

			System.out.print("Adress creation (country)...");
			testcase="ADR:;;;;;;yrtnuoc";
			Adress ctrA = new Adress(Tests.reversed(testcase));
			Adress ctrB = new Adress(testcase);
			if (ctrA.toString().equals("ADR:;;;;;;country")&&ctrB.toString().equals("ADR:;;;;;;yrtnuoc")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + ctrA);
				System.exit(-1);
			}
			
			System.out.print("Adress creation (home adress)...");
			testcase="ADR;TYPE=HOME:postbox;extended;street;city;region;zip;country";
			Adress homeA=new Adress(testcase);
			if (homeA.toString().equals(testcase)){
				System.out.println("ok");
			} else {
				System.err.println(homeA);
				System.exit(-1);
			}
			
			System.out.print("Adress creation (work adress)...");
			testcase="ADR;TYPE=WORK:xobtsop;dednetxe;teerts;ytic;noiger;piz;yrtnuoc";
			Adress workB=new Adress(testcase);
			if (workB.toString().equals(testcase)){
				System.out.println("ok");
			} else {
				System.err.println(workB);
				System.exit(-1);
			}
			
			
			Adress [] adresses1={emptyA,pbA,extA,strA,citA,regA,zipA,ctrA,homeA};
			Adress [] adresses2={emptyB,pbB,extB,strB,citB,regB,zipB,ctrB,workB};
			
			System.out.print("Adress isEmpty test...");
			int comp=0;
			int num=0;
			for (Adress a:adresses1){
				comp++;
				if (!a.isEmpty()){
					num++;
				} else if (a==emptyA) {
					num++;
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Adress compare test...");
			comp=0;
			num=0;
			for (Adress a:adresses1){
				num++;
				if (a.compareTo(workB)!=0 && a.compareTo(workB)==-workB.compareTo(a)){
					comp++;
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
				
			System.out.print("Adress compatibility test 1 (self)...");
			comp=0;
			num=0;
			for (Adress a:adresses1){
				for (Adress b:adresses1){
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						System.err.println(a+" <=> "+b);
					}
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}

		
			System.out.print("Adress compatibility test 2 (other)...");
			comp=0;
			num=0;
			for (Adress a:adresses1){
				for (Adress b:adresses2){
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat=(a+""+b).replace("ADR:","").replace(";", "");
						if (concat.equals("postboxxobtsop") ||
							  concat.equals("extendeddednetxe") ||
							  concat.equals("streetteerts") ||
						    concat.equals("cityytic") ||
						    concat.equals("regionnoiger") ||
						    concat.equals("zippiz") ||
						    concat.equals("countryyrtnuoc") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountryADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountryyrtnuoc") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountrypiz") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountrynoiger") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountryytic") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountryteerts") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountrydednetxe") ||
						    concat.equals("ADRTYPE=HOME:postboxextendedstreetcityregionzipcountryxobtsop") ||
						    concat.equals("countryADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc") ||
						    concat.equals("zipADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc") ||
						    concat.equals("regionADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc") ||
						    concat.equals("cityADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc") ||
						    concat.equals("streetADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc") ||
						    concat.equals("extendedADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc") ||
						    concat.equals("postboxADRTYPE=WORK:xobtsopdednetxeteertsyticnoigerpizyrtnuoc")){
							comp++;
						} else {
							System.err.println(a+" <=> "+b);
						}
					}
				}
				
			}
			if (comp==num){
				System.out.println("ok");
			} else {
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Adress clone test...");
			comp=0;
			num=0;
			for (Adress a:adresses1){
				num++;
				try {
					if (a.toString().equals(a.clone().toString())){
						comp++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			for (Adress b:adresses2){
				num++;
				try {
					if (b.toString().equals(b.clone().toString())){
						comp++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			
			System.out.print("Adress merge test 1 (compatible,home)...");
			comp=0;
			num=0;
			for (Adress a:adresses1){
				try {
					comp+=2;
					Adress clone1=(Adress) a.clone();
					Adress clone2=(Adress) homeA.clone();
					
					if (clone1.mergeWith(homeA) && clone1.toString().equals(homeA.toString())) num++;
					if (clone2.mergeWith(a) && clone2.toString().equals(homeA.toString())) num++;
				
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Adress merge test 2 (compatible,work)...");
			comp=0;
			num=0;
			for (Adress b:adresses2){
				try {
					comp+=2;
					Adress clone1=(Adress) b.clone();
					Adress clone2=(Adress) workB.clone();					
					if (clone1.mergeWith(workB) && clone1.toString().equals(workB.toString())) num++;
					if (clone2.mergeWith(b) && clone2.toString().equals(workB.toString())) num++;				
				} catch (CloneNotSupportedException e) {
				}				
			}
			
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Adress merge test 3 (incompatible,home)...");
			comp=0;
			num=0;
			for (Adress b:adresses2){
				try {
					comp+=2;
					Adress clone1=(Adress) b.clone();
					Adress clone2=(Adress) homeA.clone();
					
					if (!clone1.mergeWith(homeA)) {
						num++;
					} else if (b==emptyB){
						num++;
					}
					if (!clone2.mergeWith(b)) {
						num++;
					} else if (b==emptyB){
						num++;
					}
				
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Adress merge test 4 (incompatible,work)...");
			comp=0;
			num=0;
			for (Adress a:adresses1){
				try {
					comp+=2;
					Adress clone1=(Adress) a.clone();
					Adress clone2=(Adress) workB.clone();
					
					if (!clone1.mergeWith(workB)) {
						num++;
					} else if (a==emptyA){
						num++;
					}
					if (!clone2.mergeWith(a)) {
						num++;
					} else if (a==emptyA){
						num++;
					}
				
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
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
	//private boolean invalid = false;
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
		if (content==null || !content.startsWith("ADR")) throw new InvalidFormatException("Adress does not start with \"ADR;\": " + content);
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
	
	@Override
	protected Object clone() throws CloneNotSupportedException {		
		try {
			return new Adress(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
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
		if (different(extendedAdress, adr2.extendedAdress)) return false;
		return true;
	}

	public boolean isEmpty() {
		return ((postOfficeBox == null || postOfficeBox.isEmpty()) && (extendedAdress == null || extendedAdress.isEmpty()) && (streetAdress == null || streetAdress.isEmpty()) && (city == null || city.isEmpty()) && (region == null || region.isEmpty()) && (zip == null || zip.isEmpty()) && (country == null || country.isEmpty()));
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
		home = homeBox.isSelected();
		work = workBox.isSelected();
		readAddr(postBoxField.getText() + ";" + extendedField.getText() + ";" + streetField.getText() + ";" + cityField.getText() + ";" + regionField.getText() + ";" + zipField.getText() + ";" + countryField.getText());
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(Color.green);
		}
	}
}
