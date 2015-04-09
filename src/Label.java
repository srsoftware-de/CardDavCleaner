import java.util.TreeSet;

public class Label extends Mergable<Label> implements Comparable<Label>{
	public static void test() {
		try {
			System.out.print(_("Label creation test (null)..."));
			String testCase = null;
			try {
				Label nL = new Label(testCase);
				System.err.println(_("failed: #", nL));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok"));
      }

			System.out.print(_("Label creation test (empty)..."));
			testCase = "LABEL:";
			Label eL = new Label(testCase);
			if (eL.toString().equals(testCase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", eL));
				System.exit(-1);
			}

			System.out.print(_("Label creation test (simple)..."));
			testCase = "LABEL:this is a simple label";
			Label sL = new Label(testCase);
			if (sL.toString().equals(testCase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", sL));
				System.exit(-1);
			}

			System.out.print(_("Label creation test (encoded)..."));
			testCase = "LABEL;ENCODING=UTF-8:this is an encoded label";
			Label cL = new Label(testCase);
			if (cL.toString().equals(testCase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", cL));
				System.exit(-1);
			}
			
			System.out.print(_("Label creation test (with types)..."));
			testCase = "LABEL;TYPE=HOME;TYPE=WORK:this is a typed label";
			Label tL = new Label(testCase);
			if (tL.toString().equals(testCase) && tL.types.toString().equals("[HOME, WORK]")) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", tL));
				System.exit(-1);
			}
			
			System.out.print(_("Label creation test (full)..."));
			testCase = "LABEL;TYPE=HOME;TYPE=TEST;ENCODING=UTF-8:full label";
			Label fL = new Label(testCase);
			if (fL.toString().equals(testCase) && fL.types.toString().equals("[HOME, TEST]")) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", fL));
				System.exit(-1);
			}

			Label[] labels = { eL,sL,cL,tL,fL };

			System.out.print(_("Label isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Label l : labels) {
				comp++;
				if (!l.isEmpty()) {
					num++;
				} 
				if (l == eL) {
					comp--;
				}
			}
			if (num == comp) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Label compare test..."));
			comp = 0;
			num = 0;
			for (Label l : labels) {
				comp++;
				if (l.compareTo(fL) != 0 && l.compareTo(fL) == -fL.compareTo(l)) {
					num++;
				} else {
					if (fL==l){
						num++;
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Label compatibility test..."));
			comp = 0;
			num = 0;
			for (Label a : labels) {
				for (Label b : labels) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("LABEL", "").replace(";ENCODING=UTF-8", "").replace(";TYPE=TEST", "").replace(";TYPE=WORK", "").replace(";TYPE=HOME", "").replaceFirst(":", "");
						if (concat.equals("this is a simple label:this is an encoded label") ||
								concat.equals("this is a simple label:this is a typed label") ||
								concat.equals("this is a simple label:full label") ||
								concat.equals("this is an encoded label:this is a simple label") ||
								concat.equals("this is an encoded label:this is a typed label") ||
								concat.equals("this is an encoded label:full label") ||
								concat.equals("this is a typed label:this is a simple label") ||
								concat.equals("this is a typed label:this is an encoded label") ||
								concat.equals("this is a typed label:full label") ||
								concat.equals("full label:this is a simple label") ||
								concat.equals("full label:this is an encoded label") ||
								concat.equals("full label:this is a typed label")) {
							comp++;
						} else {
							System.err.println(a + " <=> " + b);
						}
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Label clone test..."));
			comp=0;
			num=0;
			for (Label m:labels){
				comp++;
				try {
					if (m.toString().equals(m.clone().toString())){
						num++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			if (comp==num){
				System.out.println(_("ok"));
			} else {				
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Label merge test..."));
			comp=0;
			num=0;
			for (Label m:labels){
				try {
					comp+=2;
					Label clone1=(Label) m.clone();
					Label clone2=(Label) fL.clone();
					
					if (clone1.mergeWith(fL) && clone1.toString().equals(fL.toString())) num++;
					if (clone2.mergeWith(m) && clone2.toString().equals(fL.toString())) num++;
					if (comp>num){
						if ((m.label!=null && !m.label.isEmpty()) && (fL.label!=null && !fL.label.isEmpty()) && !m.label.equals(fL.label)){
							num+=2;
						}
					}
					if (comp>num){
						System.out.println();
						System.out.println("fb: "+fL);
						System.out.println(" b: "+m);
						System.out.println("merged:");
						System.out.println("fb: "+clone2);
						System.out.println(" b: "+clone1);
					}
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println(_("ok"));
			} else {				
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
/**/
		} catch (InvalidFormatException e) {
	    e.printStackTrace();
    }

	}
	private static String _(String text) { 
		return Translations.get(text);
	}
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
	
	TreeSet<String> types=new TreeSet<String>();
	String encoding=null;
	String label;
	
	public Label(String line) throws InvalidFormatException {		
		if (line==null || !line.startsWith("LABEL")) throw new InvalidFormatException(_("LABEL does not start with \"LABEL\": #",line));
		String data=line.substring(5);
		if (!data.startsWith(":")){
			int p=data.indexOf(":");
			if (p<0) throw new InvalidFormatException(_("LABEL does not contain colon: #",line));
			String[] addInfo = data.substring(0,p).split(";");
			data=data.substring(p);
			for (String info:addInfo){
				if (info.startsWith("TYPE=")){
					types.add(info.substring(5));
				}
				if (info.startsWith("ENCODING=")){
					encoding=info.substring(9);
				}
			}
		} 
		label=data.substring(1);
	}
	
	public int compareTo(Label o) {
		return this.toString().compareTo(o.toString());
	}

	@Override
  public boolean isCompatibleWith(Label other) {
		if (different(label,other.label)) return false;
	  return true;
  }

	public boolean isEmpty() {
	  return label==null || label.trim().isEmpty();
  }

	@Override
  public boolean mergeWith(Label other) {
		if (!isCompatibleWith(other)) return false;
		label=merge(label,other.label);
		encoding=merge(encoding,other.encoding);
		types.addAll(other.types);
	  return true;
  }

	@Override
	public String toString() {
		String result="LABEL;";
		for (String type:types){
			result+="TYPE="+type+";";
		}
		if (encoding!=null){
			result+="ENCODING="+encoding;	
		}
		result+=":";
		result=result.replace(";:", ":");
		if (label!=null) return result+label;
		return result;
	}
	
	protected Label clone() throws CloneNotSupportedException {		
		try {
			return new Label(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

}