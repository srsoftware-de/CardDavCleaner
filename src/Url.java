import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Url {
	
	private boolean home=false;
	private boolean work=false;

	public Url(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("URL;")) throw new InvalidFormatException("Url does not start with \"URL;\"");
		String line = content.substring(4);
		while(!line.startsWith(":")){
			if (line.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			} 
			if (line.startsWith("TYPE=WORK")){
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
		readUrl(line.substring(1));		
	}

	private void readUrl(String line) {
		if (line.isEmpty()) return;
		System.err.println(line);
		throw new NotImplementedException();
	}
}
