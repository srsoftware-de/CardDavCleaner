import java.util.TreeSet;

public class Label extends Mergable<Label> implements Comparable<Label>{
	
	public static void test() {
		try {
			System.out.print("Label creation test (null)...");
			String testCase = null;
			try {
				Label nL = new Label(testCase);
				System.err.println("failed: " + nL);
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println("ok");
      }

			System.out.print("Label creation test (empty)...");
			testCase = "LABEL:";
			Label eL = new Label(testCase);
			if (eL.toString().equals(testCase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + eL);
				System.exit(-1);
			}

			System.out.print("Label creation test (simple)...");
			testCase = "LABEL:this is a test label";
			Label sL = new Label(testCase);
			if (sL.toString().equals(testCase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + sL);
				System.exit(-1);
			}

			System.out.print("Label creation test (encoded)...");
			testCase = "LABEL;ENCODING=UTF8:this is a test label";
			Label cL = new Label(testCase);
			if (cL.toString().equals(testCase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + cL);
				System.exit(-1);
			}
			
			System.out.print("Label creation test (with types)...");
			testCase = "LABEL;TYPE=HOME;TYPE=WORK:this is a test label";
			Label tL = new Label(testCase);
			if (tL.toString().equals(testCase) && tL.types.toString().equals("[HOME, WORK]")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + tL);
				System.exit(-1);
			}
			
			System.out.print("Label creation test (full)...");
			testCase = "LABEL;TYPE=HOME;TYPE=TEST;ENCODING=UTF-8:full label";
			Label fL = new Label(testCase);
			if (fL.toString().equals(testCase) && fL.types.toString().equals("[HOME, TEST]")) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + fL);
				System.exit(-1);
			}

			/*
			System.out.print("Label creation test (invalid)...");
			testCase = "Label:steinlaus";
			Label iM = new Label(testCase);
			if (iM.toString().equals(testCase) && iM.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + iM);
				System.exit(-1);
			}
			
			System.out.print("Label creation test (valid work)...");
			testCase = "Label;TYPE=WORK:work@example.com";
			Label workM = new Label(testCase);
			if (workM.toString().equals(testCase) && !workM.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + workM);
				System.exit(-1);
			}

			System.out.print("Label creation test (valid home)...");
			testCase = "Label;TYPE=HOME:home@example.com";
			Label homeM = new Label(testCase);
			if (homeM.toString().equals(testCase) && !homeM.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + homeM);
				System.exit(-1);
			}

			System.out.print("Label creation test (valid internet)...");
			testCase = "Label;TYPE=INTERNET:net@example.com";
			Label netM = new Label(testCase);
			if (netM.toString().equals(testCase) && !netM.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + netM);
				System.exit(-1);
			}
			
			Label[] mails = { eM,vM,iM,workM,homeM,netM };

			System.out.print("Label isEmpty test...");
			int comp = 0;
			int num = 0;
			for (Label m : mails) {
				comp++;
				if (!m.isEmpty()) {
					num++;
				} else if (m == eM) {
					num++;
				}
			}
			if (num == comp) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}

			System.out.print("Label compare test...");
			comp = 0;
			num = 0;
			for (Label m : mails) {
				comp++;
				if (m.compareTo(netM) != 0 && m.compareTo(netM) == -netM.compareTo(m)) {
					num++;
				} else {
					if (netM==m){
						num++;
					}
				}
			}
			if (comp == num) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}

			System.out.print("Label compatibility test...");
			comp = 0;
			num = 0;
			for (Label a : mails) {
				for (Label b : mails) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("Label", "").replace(";TYPE=INTERNET", "").replace(";TYPE=WORK", "").replace(";TYPE=HOME", "").replaceFirst(":", "");
						if (concat.equals("net@example.com:test.test-24+a@test.example.com") ||
								concat.equals("net@example.com:steinlaus") ||
								concat.equals("net@example.com:work@example.com") ||
								concat.equals("net@example.com:home@example.com") ||
								concat.equals("steinlaus:home@example.com") ||
								concat.equals("steinlaus:net@example.com") ||
								concat.equals("steinlaus:test.test-24+a@test.example.com") ||
								concat.equals("steinlaus:work@example.com") ||
								concat.equals("test.test-24+a@test.example.com:home@example.com") ||
								concat.equals("test.test-24+a@test.example.com:net@example.com") ||
								concat.equals("test.test-24+a@test.example.com:steinlaus") ||
								concat.equals("test.test-24+a@test.example.com:work@example.com") ||
								concat.equals("work@example.com:home@example.com") ||
								concat.equals("work@example.com:net@example.com") ||
								concat.equals("work@example.com:steinlaus") ||
								concat.equals("work@example.com:test.test-24+a@test.example.com") ||
								concat.equals("home@example.com:steinlaus") ||
								concat.equals("home@example.com:net@example.com") ||
								concat.equals("home@example.com:test.test-24+a@test.example.com") ||
								concat.equals("home@example.com:work@example.com")) {
							comp++;
						} else {
							System.err.println(concat);
							//System.err.println(a + " <=> " + b);
						}
					}
				}
			}
			if (comp == num) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}
			
			System.out.print("Label clone test...");
			comp=0;
			num=0;
			for (Label m:mails){
				comp++;
				try {
					if (m.toString().equals(m.clone().toString())){
						num++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}

			System.out.print("Label merge test...");
			comp=0;
			num=0;
			for (Label m:mails){
				try {
					comp+=2;
					Label clone1=(Label) m.clone();
					Label clone2=(Label) netM.clone();
					
					if (clone1.mergeWith(netM) && clone1.toString().equals(netM.toString())) num++;
					if (clone2.mergeWith(m) && clone2.toString().equals(netM.toString())) num++;
					if (comp>num){
						if ((m.adress!=null && !m.adress.isEmpty()) && (netM.adress!=null && !netM.adress.isEmpty()) && !m.address().equals(netM.adress)){
							num+=2;
						}
					}
					if (comp>num){
						System.out.println();
						System.out.println("fb: "+netM);
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
				System.out.println("ok");
			} else {				
				System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
/**/
		} catch (InvalidFormatException e) {
	    e.printStackTrace();
    }

	}
	
	TreeSet<String> types=new TreeSet<String>();
	String encoding=null;
	String label;
	
	public Label(String line) throws InvalidFormatException {		
		if (line==null || !line.startsWith("LABEL")) throw new InvalidFormatException("LABEL does not start with \"LABEL\": "+line);
		String data=line.substring(5);
		if (!data.startsWith(":")){
			int p=data.indexOf(":");
			if (p<0) throw new InvalidFormatException("LABEL does not contain colon: "+line);
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
		label=merge(label,other.label);
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
}