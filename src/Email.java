import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class Email implements DocumentListener, ChangeListener {
	
	private boolean work=false;
	private boolean home=false;
	private boolean internet=false;
	private String adress=null;

	private boolean invalid=false;
	private InputField adressBox;
	private JCheckBox homeBox;
	private JCheckBox workBox;
	private JCheckBox internetBox;
	private VerticalPanel form;
	
	public VerticalPanel editForm() {
		form=new VerticalPanel("Email");
		
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);
		
		form.add(adressBox=new InputField("Adress",adress));
		adressBox.addChangeListener(this);
		
		form.add(homeBox=new JCheckBox("Home",home));
		homeBox.addChangeListener(this);
		form.add(workBox=new JCheckBox("Work",work));
		workBox.addChangeListener(this);
		form.add(internetBox=new JCheckBox("Internet",internet));
		internetBox.addChangeListener(this);
		form.scale();
		return form;
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("EMAIL");
		if (home) sb.append(";TYPE=HOME");
		if (work) sb.append(";TYPE=WORK");
		if (internet) sb.append(";TYPE=INTERNET");
		sb.append(":");
		sb.append(adress);
		return sb.toString();
	}

	public Email(String content) throws UnknownObjectException, InvalidFormatException {
		if (content.startsWith("EMAIL;")){
			String line = content.substring(6);
			while(!line.startsWith(":")){
				String upper = line.toUpperCase();
				if (upper.startsWith("TYPE=WORK")){
					work=true;
					line=line.substring(9);
					continue;
				}
				if (upper.startsWith("TYPE=HOME")){
					home=true;
					line=line.substring(9);
					continue;
				} 
				if (upper.startsWith("TYPE=INTERNET")){
					internet=true;
					line=line.substring(13);
					continue;
				} 
				if (line.startsWith(";")){
					line=line.substring(1);
					continue;
				}
				throw new UnknownObjectException(line+" in "+content);
			}
			readAddr(line.substring(1));
		} else if (content.startsWith("EMAIL:")){
			if (content.contains(";")) throw new InvalidFormatException("content");
			adress=content.substring(6);
		} else throw new InvalidFormatException("Mail adress does not start with \"EMAIL;\" or \"EMAIL:\"");
	}

	private void readAddr(String line) {
		if (line.isEmpty()) {
			adress=null;
			return;
		}
		adress = line.toLowerCase();
	}
	
	public boolean isEmpty() {
		return adress==null || adress.trim().isEmpty();
	}

	public String address() {
		return adress;
	}

	public void merge(Email mail) throws InvalidAssignmentException {
		if (!adress.equals(mail.adress)) throw new InvalidAssignmentException("Trying to merge two mails with different adresses!");
		if (mail.home) home=true;
		if (mail.work) work=true;
		if (mail.internet) internet=true;
	}

	public boolean isHomeMail() {
		return home;
	}

	public boolean isWorkMail() {
		return work;
	}
	
	public boolean isInternetMail(){
		return internet;
	}

	public void setWork() {
		work=true;
		home=false;
		internet=false;
	}

	public void setHome() {
		work=false;
		home=true;
		internet=false;		
	}
	
	public void setInternet(){
		work=false;
		home=false;
		internet=true;
	}

	public String category() {
		if (work) return "work";
		if (home) return "home";
		return "no category";
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void stateChanged(ChangeEvent arg0) {
		update();
	}

	public void changedUpdate(DocumentEvent e) {
		update();		
	}

	public void insertUpdate(DocumentEvent e) {
		update();		
	}

	public void removeUpdate(DocumentEvent e) {
		update();		
	}

	private void update() {
		invalid=false;
		readAddr(adressBox.getText());
		home=homeBox.isSelected();
		work=workBox.isSelected();
		internet=internetBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.red:Color.green);
		}
	}
}
