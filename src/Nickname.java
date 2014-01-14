import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;


public class Nickname {
	
	private boolean work=false;
	private boolean home=false;
	private boolean internet=false;
	private String nick=null;

	private boolean invalid=false;
	
	public VerticalPanel editForm() {
		VerticalPanel form=new VerticalPanel("Nickname");
		if (invalid) form.setBackground(Color.red);
		form.add(new InputField("Adress",nick));
		form.add(new JCheckBox("Home",home));
		form.add(new JCheckBox("Work",work));
		form.add(new JCheckBox("Internet",internet));
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
			readAddr(line.substring(1));
		} else if (content.startsWith("NICKNAME:")){
			if (content.contains(";")) throw new InvalidFormatException("content");
			nick=content.substring(9);
		} else throw new InvalidFormatException("Nickname adress does not start with \"NICKNAME;\" or \"NICKNAME:\"");
		JOptionPane.showMessageDialog(null, this);
	}

	private void readAddr(String line) {
		if (line.isEmpty()) return;
		nick = line.toLowerCase();
	}
	
	public boolean isEmpty() {
		return nick==null;
	}

	public String text() {
		return nick;
	}

	public void merge(Nickname nick) throws InvalidAssignmentException {
		if (!nick.equals(nick)) throw new InvalidAssignmentException("Trying to merge two nicknames with strings!");
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
}
