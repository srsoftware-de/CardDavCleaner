import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Email {
	
	private boolean home=false;
	private boolean work=false;

	public Email(String line) throws UnknownObjectException, InvalidFormatException {
		if (!line.startsWith("EMAIL;")) throw new InvalidFormatException("Mail adress does not start with \"EMAIL;\"");
		line=line.substring(4);
		while(!line.startsWith(":")){
			if (line.startsWith("TYPE=WORK")){
				work=true;
				line=line.substring(9);
				continue;
			}
			throw new UnknownObjectException(line);
		}
		readAddr(line.substring(1));		
	}

	private void readAddr(String line) {
		if (line.isEmpty()) return;
		throw new NotImplementedException();
	}
}
