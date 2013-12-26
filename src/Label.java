import java.util.TreeSet;

public class Label {
	TreeSet<String> types=new TreeSet<String>(ObjectComparator.get());
	String encoding=null;
	String label;
	
	public Label(String data) throws InvalidFormatException {
		System.out.println(data);
		if (data.startsWith("LABEL;")) data=data.substring(6);
		String[] parts=null;
		if (data.contains("ENCODING=")) {
			parts = data.split("ENCODING=");
			for (String type:parts[0].split(";")){
				if (type.length()>0) types.add(type);
			}
			parts=parts[1].split(":", 0);
			encoding=parts[0];
			label=parts[1];
		} else {
			parts = data.split(":",0);
			for (String type:parts[0].split(";")){
				if (type.length()>0) types.add(type);
			}
			label=parts[1];			
		}
		System.out.println(this);
	}
	
	public static void main(String[] args) throws InvalidFormatException {
		Label label=new Label("LABEL;HOME=;ENCODING=QUOTED-PRINTABLE:Willy-Brandt-Platz 6=0D=0AErfurt 99084");
		System.out.println(label);
	}
	
	@Override
	public String toString() {
		String result="LABEL;";
		for (String type:types){
			result+=type+";";
		}
		if (encoding!=null){
			result+="ENCODING="+encoding;	
		}
		result+=":";
		return result.replace(";:", ":")+label;
	}
}


// LABEL;HOME=;ENCODING=QUOTED-PRINTABLE:Willy-Brandt-Platz 6=0D=0AErfurt 99084