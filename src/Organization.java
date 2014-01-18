import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Organization implements ChangeListener {
	
	String name=null;
	String extended=null;
	private boolean invalid=false;
	private InputField nameField;
	private InputField extField;
	private VerticalPanel form;
	

	public VerticalPanel editForm() {
		form=new VerticalPanel("Organization");
		if (invalid) form.setBackground(Color.red);
		form.add(nameField=new InputField("Name",name));
		nameField.addEditListener(this);
		form.add(extField=new InputField("Extended",extended));
		extField.addEditListener(this);
		form.scale();
		return form;
	}
	public Organization(String content) throws UnknownObjectException, InvalidFormatException {		
		if (!content.startsWith("ORG:")) throw new InvalidFormatException("Organisation fängt nicht mit \"ORG:\" an");
		String line=content.substring(4);
		if (line.contains(";")){
			String[] parts = line.split(";",0);
			if (!parts[0].isEmpty()) name=parts[0];
			if (parts.length>1 && !parts[1].isEmpty()) extended=parts[1];
		} else name=line; 
		
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


	public boolean isEmpty() {
		return (name==null || name.isEmpty()) && (extended==null || extended.isEmpty());
	}

	public boolean isInvalid() {
		return invalid;
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
			form.setBackground(invalid?Color.red:Color.green);
		}	
	}


}
