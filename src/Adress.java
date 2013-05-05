import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Adress {
	
	private boolean home=false;
	private boolean work=false;
	private String street;
	private String city;
	private String zip;

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
		String[] parts = line.split(";");
		if (!parts[0].isEmpty()) {
			System.err.println("found "+parts[0]+" @0 in "+line);
			throw new NotImplementedException();
		}
		if (!parts[1].isEmpty()) {
			System.err.println("found "+parts[1]+" @1 in "+line);
			throw new NotImplementedException();
		}
		if (!parts[2].isEmpty()) {
			street=parts[2];
		}
		if (!parts[3].isEmpty()) {
			city=parts[3];
		}
		if (!parts[4].isEmpty()) {
			System.err.println("found "+parts[4]+" @4 in "+line);
			throw new NotImplementedException();
		}
		if (!parts[5].isEmpty()) {
			zip=parts[5];
		}
		if (parts.length>6){
			System.err.println("found more than 6 parts in "+line);
			throw new NotImplementedException();
		}
	}
}
