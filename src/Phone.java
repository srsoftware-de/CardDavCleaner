import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Phone {
	
	private boolean fax=false;
	private boolean home=false;

	public Phone(String line) throws UnknownObjectException, InvalidFormatException {
		if (!line.startsWith("TEL;")) throw new InvalidFormatException("Phone does not start with \"TEL;\"");
		line=line.substring(4);
		while(!line.startsWith(":")){
			if (line.startsWith("TYPE=FAX")){
				fax=true;
				line=line.substring(9);
				continue;
			}
			if (line.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			}
			throw new UnknownObjectException(line);
		}
		readPhone(line.substring(1));		
	}

	private void readPhone(String line) {
		if (line.isEmpty())return;
		System.err.println(line);
		throw new NotImplementedException();
	}
}
