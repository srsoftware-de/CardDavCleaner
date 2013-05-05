import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sun.media.sound.InvalidFormatException;


public class Organization {
	
	String name=null;
		
	public Organization(String content) throws UnknownObjectException, InvalidFormatException {		
		if (!content.startsWith("ORG:")) throw new InvalidFormatException("Organization does not start with \"ORG:\"");
		String line=content.substring(4);
		if (line.contains(";")){
			if (line.equals(";")) return;
			System.err.println("Organization with several parts found: "+line);
			throw new NotImplementedException();
		} else name=line; 
		
	}
	

	@Override
	public String toString() {
		return name;
	}

}
