import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Name {	
	
	private String last;
	private String first;
	private String title;
	
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("N:");
		if (last!=null) sb.append(last);
		sb.append(';');
		if (first!=null) sb.append(first);
		sb.append(";;");
		if (title!=null) sb.append(title);
		sb.append(";");
		return sb.toString();
	}
	
	public String full(){
		return title+" "+first+" "+last;
	}
	
	public Name(String line) throws UnknownObjectException, InvalidFormatException {		
		if (!line.startsWith("N:")) throw new InvalidFormatException("Name does not start with \"N:\"");
		line=line.substring(2).trim();
		if (line.contains(";")){
			String[] parts = line.split(";");
			if (parts.length>0) setLast(parts[0].trim());
			if (parts.length>1) setFirst(parts[1].trim());
			if (parts.length>2 && !parts[2].isEmpty()) {
				System.err.println("3rd part not implemented, yet:");
				System.err.println(line);
				for (String p:parts){
					System.err.println(p);
				}
				throw new NotImplementedException();
			}
			if (parts.length>3) setTitle(parts[3].trim());
			if (parts.length>4){
				System.err.println("Name with more than two parts found:");
				System.err.println(line);
				for (String p:parts){
					System.err.println(p);
				}
				throw new NotImplementedException();
			}
		} else last=line.substring(2); 
		
	}
	
	private void setTitle(String string) {
		if (string.isEmpty()) return;
		title=string;
	}

	private void setLast(String string) {
		if (string.isEmpty()) return;
		last=string;		
	}
	private void setFirst(String string) {
		if (string.isEmpty()) return;
		first=string;		
	}
	
	public boolean isEmpty() {
		return ((last==null) && (first==null));	
	}
	
	public boolean equals(Name name){		
		if (first!=null){
			if (name.first==null) return false;
			if (!name.first.equals(first)) return false;
		} else if (name.first!=null) return false;		
		
		if (last!=null){
			if (name.last==null) return false;
			if (!name.last.equals(last)) return false;
		} else if (name.last!=null) return false;
		return true;
	}

	public String title(){
		return title;
	}

	public String last() {
		return last;
	}

	public String first() {
		return first;
	}


	public String canonical() {
		TreeSet<String> parts=new TreeSet<String>(ObjectComparator.get());
		if (first!=null) parts.add(first);
		if (last!=null) parts.add(last);	
		
		return parts.toString().replace("[", "").replace("]", ""); // sorted set of name parts
	}
}
