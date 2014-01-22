import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Url implements ChangeListener, Comparable<Url> {
	
	private boolean home=false;
	private boolean work=false;
	private boolean invalid=false;
	private String url;
	private InputField urlField;
	private JCheckBox homeBox;
	private JCheckBox workBox;
	private VerticalPanel form;
	private Pattern ptr = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

	public VerticalPanel editForm() {
		form=new VerticalPanel("Internetadresse");
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);
		form.add(urlField=new InputField("URL",url));
		urlField.addEditListener(this);
		form.add(homeBox=new JCheckBox("Zuhause",home));
		homeBox.addChangeListener(this);
		form.add(workBox=new JCheckBox("Arbeit",work));
		workBox.addChangeListener(this);
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
		if (!content.startsWith("URL")) throw new InvalidFormatException("Url beginnt nicht mit \"URL\"");
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
		line=line.trim();
		if (line.isEmpty()) {
			line=null;
		}
		url = line;
		checkValidity();
	}

	private void checkValidity() {
		invalid=false;
		if (url==null) return;
		invalid=!ptr.matcher(url).matches();
	}

	public boolean isEmpty() {
		return url==null || url.isEmpty();
	}

	public void stateChanged(ChangeEvent arg0) {
		update();
	}
	
	private void update(){
		readUrl(urlField.getText());
		home=homeBox.isSelected();
		work=workBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.red:Color.green);
		}	
	}

	public int compareTo(Url otherUrl) {
		return this.toString().compareTo(otherUrl.toString());
	}

}
