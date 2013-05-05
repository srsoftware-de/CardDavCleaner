import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Name {
	
	
	
	private String name;

	public Name(String line) throws UnknownObjectException, InvalidFormatException {		
		if (!line.startsWith("N:")) throw new InvalidFormatException("Name does not start with \"N:\"");
		if (line.contains(";")){
			System.err.println("Structured name found: "+line);
			throw new NotImplementedException();
		} else name=line.substring(2); 
		
	}
	
	@Override
	public String toString() {
		return name;
	}

}
