import java.rmi.activation.UnknownObjectException;

import com.sun.media.sound.InvalidFormatException;


public class Email {
	
	private boolean work=false;
	private boolean home=false;
	private String adress=null;
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("EMAIL");
		if (home) sb.append(";TYPE=HOME");
		if (work) sb.append(";TYPE=WORK");
		sb.append(":");
		sb.append(adress);
		return sb.toString();
	}

	public Email(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("EMAIL;")) throw new InvalidFormatException("Mail adress does not start with \"EMAIL;\"");
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
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line+" in "+content);
		}
		readAddr(line.substring(1));		
	}

	private void readAddr(String line) {
		if (line.isEmpty()) return;
		adress = line;
	}
	
	public boolean isEmpty() {
		return adress==null;
	}
}
