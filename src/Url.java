import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Url {
	
	private boolean home=false;

	public Url(String line) throws UnknownObjectException, InvalidFormatException {
		if (!line.startsWith("URL;")) throw new InvalidFormatException("Url does not start with \"URL;\"");
		line=line.substring(4);
		while(!line.startsWith(":")){
			if (line.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			} 
			throw new UnknownObjectException(line);
		}
		readUrl(line.substring(1));		
	}

	private void readUrl(String line) {
		throw new NotImplementedException();
	}
}
