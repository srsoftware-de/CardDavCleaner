import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Email {
	
	private boolean work=false;
	private boolean home=false;
	private String adress=null;

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
	
	@Override
	public String toString() {
		return adress;
	}

	public boolean hasAdress() {
		return adress!=null;
	}
}
