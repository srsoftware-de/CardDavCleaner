import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Adress {
	
	private boolean home=false;
	private boolean work=false;

	public Adress(String line) throws UnknownObjectException, InvalidFormatException {
		if (!line.startsWith("ADR;")) throw new InvalidFormatException("Adress does not start with \"ADR;\"");
		line=line.substring(4);
		while(!line.startsWith(":")){
			if (line.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			} 
			if (line.startsWith("TYPE=WORK")){
				work=true;
				line=line.substring(9);
				continue;
			}
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line);
		}
		readAddr(line.substring(1));		
	}

	private void readAddr(String line) {
		if (line.equals(";;;;;;")) return;
		throw new NotImplementedException();
	}
}
