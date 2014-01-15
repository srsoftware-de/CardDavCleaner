import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;


public class Messenger {
	
	private boolean aim=false;
	private boolean icq=false;
	private boolean skype=false;
	private boolean msn=false;
	private boolean facebook=false;
	private boolean invalid;
	private String nick=null;
	
	public VerticalPanel editForm() {
		VerticalPanel form=new VerticalPanel("Messenger");
		if (invalid) form.setBackground(Color.red);
		form.add(new InputField("Nickname",nick));
		form.add(new JCheckBox("AIM",aim));
		form.add(new JCheckBox("ICQ",icq));
		form.add(new JCheckBox("Skype",skype));
		form.add(new JCheckBox("MSN",msn));
		form.add(new JCheckBox("Facebook",facebook));
		form.scale();
		return form;
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("IMPP:");
		if (aim) sb.append("aim");
		if (icq) sb.append("icq");
		if (skype) sb.append("skype");
		if (msn) sb.append("msn");
		if (facebook) sb.append("facebook");
		sb.append(":");
		sb.append(nick);
		return sb.toString();
	}

	public Messenger(String content) throws InvalidFormatException, UnknownObjectException {
		if (!content.startsWith("IMPP:")) throw new InvalidFormatException("Messenger adress does not start with \"IMPP:\"");
		String line = content.substring(5);
		while(!line.startsWith(":")){
			String upper = line.toUpperCase();
			if (upper.startsWith("ICQ")){
				icq=true;
				line=line.substring(3);
				continue;
			}
			if (upper.startsWith("AIM")){
				aim=true;
				line=line.substring(3);
				continue;
			}
			if (upper.startsWith("SKYPE")){
				skype=true;
				line=line.substring(5);
				continue;
			} 
			if (upper.startsWith("MSN")){
				msn=true;
				line=line.substring(3);
				continue;
			} 
			if (upper.startsWith("FACEBOOK")){
				facebook=true;
				line=line.substring(8);
				continue;
			} 
			throw new UnknownObjectException(line+" in "+content);
		}
		readAddr(line.substring(1));		
	}

	private void readAddr(String line) {
		while (line.startsWith(":")) line=line.substring(1);
		if (line.isEmpty()) return;		
		nick = line.toLowerCase();
	}
	
	public boolean isEmpty() {
		return nick==null;
	}

	public String nick() {
		return nick;
	}

	public void merge(Messenger m) throws InvalidAssignmentException {
		if (!nick.equals(m.nick)) throw new InvalidAssignmentException("Trying to merge two messenger accounts with different ids!");
		if (m.aim) aim=true;
		if (m.icq) icq=true;
		if (m.skype) skype=true;
		if (m.msn) msn=true;
		if (m.facebook) facebook=true;
	}

	public String id() throws UnknownObjectException {
		if (aim) return "aim:"+nick;
		if (icq) return "icq:"+nick;
		if (skype) return "skype:"+nick;
		if (msn) return "msn:"+nick;
		if (facebook) return "facebook"+nick;
		throw new UnknownObjectException("Messenger \""+nick+"\" has no known type!");
  }

	public boolean isInvalid() {
		return invalid;
	}

}
