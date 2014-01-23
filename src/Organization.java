import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Organization extends Mergable<Organization> implements ChangeListener, Comparable<Organization> {
	
	String name=null;
	String extended=null;
	private InputField nameField;
	private InputField extField;
	private VerticalPanel form;
	
	public Organization(String content) throws UnknownObjectException, InvalidFormatException {		
		if (!content.startsWith("ORG:")) throw new InvalidFormatException("Organization does not start with \"ORG:\"");
		String line=content.substring(4);
		if (line.contains(";")){
			String[] parts = line.split(";",0);
			if (parts.length>0 && !parts[0].isEmpty()) name=parts[0];
			if (parts.length>1 && !parts[1].isEmpty()) extended=parts[1];
		} else name=line; 
		
	}
	public int compareTo(Organization otherOrg) {
		return this.toString().compareTo(otherOrg.toString());
	}

	
	public VerticalPanel editForm() {
		form=new VerticalPanel("Organization");
		form.add(nameField=new InputField("Name",name));
		nameField.addEditListener(this);
		form.add(extField=new InputField("Extended",extended));
		extField.addEditListener(this);
		form.scale();
		return form;
	}
	@Override
  public boolean isCompatibleWith(Organization other) {
		if (different(name,other.name)) return false;
		if (different(extended, other.extended)) return false;
		return true;
  }
	
	public boolean isEmpty() {
		return (name==null || name.isEmpty()) && (extended==null || extended.isEmpty());
	}

	@Override
  public boolean mergeWith(Organization other) {
		name=merge(name,other.name);
		extended=merge(extended, other.extended);
	  return true;
  }

	public void stateChanged(ChangeEvent arg0) {
		Object source = arg0.getSource();
		if (source==nameField){
			name=nameField.getText().trim();
		}
		if (source==extField){
			extended=extField.getText().trim();
		}
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(Color.green);
		}	
	}
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("ORG:");
		if (name!=null) sb.append(name);
		sb.append(';');
		if (extended!=null)sb.append(extended);
		sb.append(';');
		return sb.toString();
	}

}
