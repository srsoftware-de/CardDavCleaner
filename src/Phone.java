import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Phone {
	
	private boolean fax=false;
	private boolean home=false;
	private boolean cell=false;
	private boolean work=false;
	private String number;
	
	static TreeSet<String> numbers=new TreeSet<String>(ObjectComparator.get());
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("TEL");
		if (fax) sb.append(";TYPE=FAX");
		if (home) sb.append(";TYPE=HOME");
		if (cell) sb.append(";TYPE=CELL");
		if (work) sb.append(";TYPE=WORK");
		sb.append(':');
		sb.append(number);
		return sb.toString();
	}

	public Phone(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("TEL;")) throw new InvalidFormatException("Phone does not start with \"TEL;\"");
		String line=content.substring(4);
		while(!line.startsWith(":")){
			String upper=line.toUpperCase();
			if (upper.startsWith("TYPE=FAX")){
				fax=true;
				line=line.substring(8);
				continue;
			}
			if (upper.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,HOME")){
				home=true;
				line=line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=CELL")){
				cell=true;
				line=line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,CELL")){
				cell=true;
				line=line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=WORK")){
				work=true;
				line=line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,WORK")){
				work=true;
				line=line.substring(6);
				continue;
			}
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line+" in "+content);
		}
		readPhone(line.substring(1));		
	}

	private void readPhone(String line) {
		if (line.isEmpty())return;
		line=line.replace(" ", "").replace("/", "").replace("-", "");
		for (char c:line.toCharArray()){
			if (!Character.isDigit(c) && c!='+' && c!='(' && c!=')') {
				System.err.println(line);
				throw new NotImplementedException();				
			}
		}
		number = line;
		numbers.add(number);
	}

	public boolean isEmpty() {
		return (number==null);
	}

	public String number() {
		return number;
	}

	public void merge(Phone phone) throws InvalidAssignmentException {
		if (!number.equals(phone.number)) throw new InvalidAssignmentException("Trying to unite two phone number entries with different numbers!");
		if (phone.home) home=true;
		if (phone.work) work=true;
		if (phone.cell)cell= true;
		if (phone.fax) fax=true;
	}

	public String simpleNumber() {
		String number=this.number.replace("(", "").replace(")", "");
		if (number.startsWith("+49")) number=0+number.substring(3);
		if (number.startsWith("0049")) number=0+number.substring(4);
		return number;
}
}
