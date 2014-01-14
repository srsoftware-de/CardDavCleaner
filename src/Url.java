import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;


public class Url {
	
	private boolean home=false;
	private boolean work=false;
	private boolean invalid=false;
	private String url;
	
	public VerticalPanel editForm() {
		VerticalPanel form=new VerticalPanel("Web Adress");
		if (invalid) form.setBackground(Color.red);
		form.add(new InputField("URL",url));
		form.add(new JCheckBox("Home",home));
		form.add(new JCheckBox("Work",work));
		form.scale();
		return form;
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("URL");
		if (home) sb.append(";TYPE=HOME");
		if (work) sb.append(";TYPE=WORK");
		sb.append(":");
		sb.append(url);
		return sb.toString();
	}

	public Url(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("URL")) throw new InvalidFormatException("Url does not start with \"URL\"");
		String line = content.substring(3);
		while(!line.startsWith(":")){
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			if (line.toUpperCase().startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			} 
			if (line.toUpperCase().startsWith("TYPE=WORK")){
				work=true;
				line=line.substring(9);
				continue;
			} 
			if (line.toUpperCase().startsWith("WORK=")){
				work=true;
				line=line.substring(5);
				continue;
			} 
			throw new UnknownObjectException(line+" in "+content);
		}
		readUrl(line.substring(1));		
	}

	private void readUrl(String line) {
		if (line.isEmpty()) return;
		url = line;
	}

	public boolean isEmpty() {
		return url==null;
	}


}
