import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class Nickname implements DocumentListener, ChangeListener, Comparable<Nickname> {
	
	private boolean work=false;
	private boolean home=false;
	private boolean internet=false;
	private String nick=null;

	private boolean invalid=false;
	private InputField nickField;
	private JCheckBox homeBox,workBox,internetBox;
	private VerticalPanel form;
	
	public VerticalPanel editForm() {
		form=new VerticalPanel("Nickname");
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);
		
		form.add(nickField=new InputField("Nickname",nick));
		nickField.addChangeListener(this);
		
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
		sb.append("NICKNAME");
		if (home) sb.append(";TYPE=HOME");
		if (work) sb.append(";TYPE=WORK");
		if (internet) sb.append(";TYPE=INTERNET");
		sb.append(":");
		sb.append(nick);
		return sb.toString();
	}

	public Nickname(String content) throws UnknownObjectException, InvalidFormatException {
		if (content.startsWith("NICKNAME")){
			String line = content.substring(8);
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
			readNick(line.substring(1));
		} else if (content.startsWith("NICKNAME:")){
			if (content.contains(";")) throw new InvalidFormatException("content");
			nick=content.substring(9);
		} else throw new InvalidFormatException("Nickname adress does not start with \"NICKNAME;\" or \"NICKNAME:\"");
	}

	private void readNick(String line) {
		line=line.trim();
		if (line.isEmpty()) {
			nick=null;
		} else {
			nick = line;
		}
	}
	
	public boolean isEmpty() {
		return nick==null || nick.trim().isEmpty();
	}

	public String name() {
		return nick;
	}

	public void merge(Nickname nick) throws InvalidAssignmentException {
		if (!nick.equals(nick)) throw new InvalidAssignmentException("Trying to merge two nicknames with different strings!");
		if (nick.home) home=true;
		if (nick.work) work=true;
		if (nick.internet) internet=true;
	}

	public boolean isHomeNick() {
		return home;
	}

	public boolean isWorkNick() {
		return work;
	}
	
	public boolean isInternetNick(){
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

	public void setInternet() {
		work=false;
		home=false;
		internet=true;
	}

	public String category() {
		if (work) return "work";
		if (home) return "home";
		if (internet) return "internet";
		return "no category";
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
		readNick(nickField.getText());
		home=homeBox.isSelected();
		work=workBox.isSelected();
		internet=internetBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.red:Color.green);
		}
	}

	public int compareTo(Nickname o) {
		return this.toString().compareTo(o.toString());
	}

}
