import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Adress {
	
	private boolean home=false;
	private boolean work=false;
	private String street;
	private String city;
	private String zip;
	private String country;
	private String state;

	public Adress(String line) throws UnknownObjectException, InvalidFormatException {
		if (!line.startsWith("ADR;")) throw new InvalidFormatException("Adress does not start with \"ADR;\"");
		line=line.substring(4);
		while(!line.startsWith(":")){
			String upper=line.toUpperCase();
			if (upper.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			} 
			if (upper.startsWith("TYPE=WORK")){
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
		for (int index=0; index<parts.length; index++){
			String part=parts[index];
			if (!part.isEmpty()){
				switch (index){
				case 2: street=part; break;
				case 3: city=part; break;
				case 4: state=part; break;
				case 5: zip=part; break;
				case 6: country=part; break;
				default: 
					System.err.println("found "+part+" @"+index+" in "+line);
					throw new NotImplementedException();				
				}
			}
		}
	}
}
