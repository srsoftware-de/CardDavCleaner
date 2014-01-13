import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Name {	
	
	private String family;
	private String first;
	private String prefix;
	private String middle;
	private boolean invalid=false;
	
	public HorizontalPanel editForm() {
		HorizontalPanel form=new HorizontalPanel("Name");
		if (invalid) form.setBackground(Color.red);
		form.add(new InputField("Prefix",prefix));
		form.add(new InputField("First Name",first));
		form.add(new InputField("Middle Name",middle));
		form.add(new InputField("Family Name",family));
		form.scale();
		return form;
	}
	
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("N:");
		if (family!=null) sb.append(family);
		sb.append(';');
		if (first!=null) sb.append(first);
		sb.append(';');
		if (middle!=null) sb.append(middle);
		sb.append(";");
		if (prefix!=null) sb.append(prefix);
		sb.append(";");
		return sb.toString();
	}
	
	public String full(){
		return prefix+" "+first+" "+middle+" "+family;
	}
	
	public Name(String line) throws UnknownObjectException, InvalidFormatException {		
		if (!line.startsWith("N:")) throw new InvalidFormatException("Name does not start with \"N:\"");
		line=line.substring(2).trim();
		if (line.contains(";")){
			String[] parts = line.split(";");
			if (parts.length>0) setFamily(parts[0].trim());
			if (parts.length>1) setFirst(parts[1].trim());
			if (parts.length>2) setMiddle(parts[2].trim());
			if (parts.length>3) setPrefix(parts[3].trim());
			if (parts.length>4){
				System.err.println("Name with more than 4 parts found:");
				System.err.println(line);
				for (String p:parts){
					System.err.println(p);
				}
				throw new NotImplementedException();
			}
		} else family=line.substring(2); 
		
	}
	
	private void setPrefix(String string) {
		if (string.isEmpty()) return;
		prefix=string;
	}

	private void setFamily(String string) {
		if (string.isEmpty()) return;
		family=string;		
	}
	private void setFirst(String string) {
		if (string.isEmpty()) return;
		first=string;		
	}
	private void setMiddle(String string) {
		if (string.isEmpty()) return;
		middle=string;		
	}
	
	public boolean isEmpty() {
		return ((family==null) && (first==null));	
	}
	
	public boolean equals(Name name){		
		if (first!=null){
			if (name.first==null) return false;
			if (!name.first.equals(first)) return false;
		} else if (name.first!=null) return false;
		
		if (middle!=null){
			if (name.middle==null) return false;
			if (!name.middle.equals(middle)) return false;
		} else if (name.middle!=null) return false;

		if (family!=null){
			if (name.family==null) return false;
			if (!name.family.equals(family)) return false;
		} else if (name.family!=null) return false;
		
		if (prefix!=null){
			if (name.prefix==null) return false;
			if (!name.prefix.equals(prefix)) return false;
		} else if (name.prefix!=null) return false;

		return true;
	}

	public String title(){
		return prefix;
	}

	public String last() {
		return family;
	}

	public String first() {
		return first;
	}

	private String ascii(String s) {
		return s.replace("Ä", "Ae").replace("ä", "ae").replace("Ö", "Oe").replace("ö", "oe").replace("Ü", "Ue").replace("ü", "ue").replace("ß", "ss").replace("é", "e");
	}

	public String canonical() {
		TreeSet<String> parts=new TreeSet<String>(ObjectComparator.get());
		if (first!=null) parts.add(ascii(first).toLowerCase());
		if (middle!=null) parts.add(ascii(middle).toLowerCase());
		if (family!=null) parts.add(ascii(family).toLowerCase());	
		
		return parts.toString().replace("[", "").replace("]", ""); // sorted set of name parts
	}

	public boolean isInvalid() {
		return invalid;
	}
}
