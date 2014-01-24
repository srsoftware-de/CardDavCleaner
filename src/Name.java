import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Name extends Mergable<Name> implements DocumentListener, Comparable<Name> {	
	
	private String family;
	private String first;
	private String prefix;
	private String suffix;
	private String middle;
	//private boolean invalid=false;
	
	private InputField prefBox,firstBox,middleBox,familyBox,sufBox;
	private VerticalPanel form;
	
	public Name(String line) throws UnknownObjectException, InvalidFormatException {		
		if (!line.startsWith("N:")) throw new InvalidFormatException("Name does not start with \"N:\"");
		line=line.substring(2).trim();
		if (line.contains(";")){
			String[] parts = line.split(";");
			if (parts.length>0) setFamily(parts[0].trim());
			if (parts.length>1) setFirst(parts[1].trim());
			if (parts.length>2) setMiddle(parts[2].trim());
			if (parts.length>3) setPrefix(parts[3].trim());
			if (parts.length>4) setSuffix(parts[4].trim());
			if (parts.length>5){
				System.err.println("Name with more than 5 parts found:");
				System.err.println(line);
				for (String p:parts){
					System.err.println(p);
				}
				throw new NotImplementedException();
			}
		} else family=line.substring(2); 
		
	}

	public String canonical() {
		TreeSet<String> parts=new TreeSet<String>();
		if (first!=null) parts.add(ascii(first).toLowerCase());
		if (middle!=null) parts.add(ascii(middle).toLowerCase());
		if (family!=null) parts.add(ascii(family).toLowerCase());	
		
		return parts.toString().replace("[", "").replace("]", ""); // sorted set of name parts
	}

	
	public void changedUpdate(DocumentEvent arg0) {
		update();
	}
	
	public int compareTo(Name o) {
		return canonical().compareTo(o.canonical());
	}
	
	public JPanel editForm(String title) {
		form=new VerticalPanel(title);
		
		form.add(prefBox=new InputField("Prefix",prefix));
		prefBox.addChangeListener(this);
		
		form.add(firstBox=new InputField("First Name",first));
		firstBox.addChangeListener(this);
		
		form.add(middleBox=new InputField("Middle Name",middle));
		middleBox.addChangeListener(this);
		
		form.add(familyBox=new InputField("Family Name",family));
		familyBox.addChangeListener(this);
		
		form.add(sufBox=new InputField("Suffix",suffix));
		sufBox.addChangeListener(this);
		
		form.scale();
		return form;
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

		if (suffix!=null){
			if (name.suffix==null) return false;
			if (!name.suffix.equals(suffix)) return false;
		} else if (name.suffix!=null) return false;

		return true;
	}
	
	public String first() {
		return first;
	}

	public String full(){
		return prefix+" "+first+" "+middle+" "+family+" "+suffix;
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}
	@Override
  public boolean isCompatibleWith(Name otherName) {
		if (different(first,otherName.first)) return false;
		if (different(family,otherName.family)) return false;
		if (different(prefix,otherName.prefix)) return false;
		if (different(suffix, otherName.suffix)) return false;
		if (different(middle,otherName.middle)) return false;
	  return true;
  }
	public boolean isEmpty() {
		return ((family==null) && (first==null));	
	}
	
	public String last() {
		return family;
	}
	
	@Override
  public boolean mergeWith(Name otherName) {
		family=merge(family,otherName.family);
		first=merge(first,otherName.first);
		prefix=merge(prefix,otherName.prefix);
		suffix=merge(suffix,otherName.suffix);
		middle=merge(middle,otherName.middle);
	  return true;
  }

	public String prefix(){
		return prefix;
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();
	}

	public String suffix(){
		return suffix;
	}
	
	public String title(){
		return prefix;
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
		if (suffix!=null) sb.append(suffix);
		return sb.toString();
	}

	private String ascii(String s) {
		return s.replace("Ä", "Ae").replace("ä", "ae").replace("Ö", "Oe").replace("ö", "oe").replace("Ü", "Ue").replace("ü", "ue").replace("ß", "ss").replace("é", "e");
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

	private void setPrefix(String string) {
		if (string.isEmpty()) return;
		prefix=string;
	}

	private void setSuffix(String string) {
		if (string.isEmpty()) return;
		suffix=string;
	}

	private void update() {
		prefix=prefBox.getText();
		first=firstBox.getText();
		middle=middleBox.getText();
		family=familyBox.getText();
		suffix=sufBox.getText();		
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(Color.green);
		}
	}
	protected Object clone() throws CloneNotSupportedException {		
		try {
			return new Email(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
}
