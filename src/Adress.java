import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Adress {
	
	private boolean home=false;
	

	public Adress(String line) throws UnknownObjectException, InvalidFormatException {
		if (!line.startsWith("ADR;")) throw new InvalidFormatException("Adress does not start with \"ADR;\"");
		line=line.substring(4);
		while(true){
			if (line.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
			} else System.err.println(line);
			if (line.startsWith(":")) {
				readAddr(line.substring(1));
				break;
			}
		}
		throw new UnknownObjectException(line);
	}

	private void readAddr(String line) {
		if (line.equals(";;;;;;")) return;
		throw new NotImplementedException();
	}

}
