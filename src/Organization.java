import java.rmi.activation.UnknownObjectException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Organization {
	
	String name=null;
	String sub1=null;
		
	public Organization(String content) throws UnknownObjectException, InvalidFormatException {		
		if (!content.startsWith("ORG:")) throw new InvalidFormatException("Organization does not start with \"ORG:\"");
		String line=content.substring(4);
		if (line.contains(";")){
			String[] parts = line.split(";");
			for (int index=0; index<parts.length; index++){
				String part=parts[index];
				switch (index){
				case 0: if (!part.isEmpty()) name=part;
				break;
				case 1: if (!part.isEmpty()) sub1=part;
				break;
				default:
						System.err.println("Organization with several parts found: "+line+" => "+part);
						throw new NotImplementedException();				
				}
			}
		} else name=line; 
		
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("ORG:");
		if (name!=null) sb.append(name);
		sb.append(';');
		if (sub1!=null)sb.append(sub1);
		sb.append(';');
		return sb.toString();
	}


	public boolean isEmpty() {
		return (name==null) && (sub1==null);
	}

}
