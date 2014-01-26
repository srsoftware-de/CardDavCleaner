import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Messenger extends Mergable<Messenger> implements ChangeListener, Comparable<Messenger> {
	public static void test() {
		try {
			System.out.print("Messenger creation test (null)...");
			String testCase = null;
			try {
				Messenger nM = new Messenger(testCase);
				System.err.println("failed: " + nM);
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println("ok");
      }

			System.out.print("Messenger creation test (empty)...");
			testCase = "IMPP:";
			try {
				Messenger eM = new Messenger(testCase);
				System.err.println("failed: " + eM);
				System.exit(-1);
			} catch (UnknownObjectException e){
				System.out.println("ok");
			}

			System.out.print("Messenger creation test (empty ICQ)...");
			testCase = "IMPP:icq:";
			Messenger eICQ = new Messenger(testCase);
			if (eICQ.toString().equals(testCase) && !eICQ.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + eICQ);
				System.exit(-1);
			}

			System.out.print("Messenger creation test (empty Skype)...");
			testCase = "IMPP:skype:";
			Messenger eSkype = new Messenger(testCase);
			if (eSkype.toString().equals(testCase) && !eSkype.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + eSkype);
				System.exit(-1);
			}
			
			System.out.print("Messenger creation test (empty MSN)...");
			testCase = "IMPP:msn:";
			Messenger eMSN = new Messenger(testCase);
			if (eMSN.toString().equals(testCase) && !eMSN.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + eMSN);
				System.exit(-1);
			}
			
			System.out.print("Messenger creation test (empty AIM)...");
			testCase = "IMPP:aim:";
			Messenger eAIM = new Messenger(testCase);
			if (eAIM.toString().equals(testCase) && !eAIM.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + eAIM);
				System.exit(-1);
			}
			
			System.out.print("Messenger creation test (empty Facebook)...");
			testCase = "IMPP:facebook:";
			Messenger eFB = new Messenger(testCase);
			if (eFB.toString().equals(testCase) && !eFB.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + eFB);
				System.exit(-1);
			}
			
			System.out.print("Messenger creation test (ICQ)...");
			testCase = "IMPP:icq:123456";
			Messenger icq = new Messenger(testCase);
			if (icq.toString().equals(testCase) && !icq.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + icq);
				System.exit(-1);
			}

			System.out.print("Messenger creation test (Skype)...");
			testCase = "IMPP:skype:test";
			Messenger skype = new Messenger(testCase);
			if (skype.toString().equals(testCase) && !skype.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + skype);
				System.exit(-1);
			}
			
			System.out.print("Messenger creation test (MSN)...");
			testCase = "IMPP:msn:test@example.com";
			Messenger msn = new Messenger(testCase);
			if (msn.toString().equals(testCase) && !msn.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + msn);
				System.exit(-1);
			}
			
			System.out.print("Messenger creation test (AIM)...");
			testCase = "IMPP:aim:test@example.com";
			Messenger aim = new Messenger(testCase);
			if (aim.toString().equals(testCase) && !aim.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + aim);
				System.exit(-1);
			}
			
			System.out.print("Messenger creation test (Facebook)...");
			testCase = "IMPP:facebook:toast@example.com";
			Messenger fb = new Messenger(testCase);
			if (fb.toString().equals(testCase) && !fb.isInvalid()) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + fb);
				System.exit(-1);
			}
			
			Messenger[] messengers = { eICQ,eSkype,eMSN,eAIM,eFB,icq,skype,msn,aim,fb };

			System.out.print("Messenger isEmpty test...");
			int comp = 0;
			int num = 0;
			for (Messenger m : messengers) {
				comp++;
				if (!m.isEmpty()) {
					num++;
				}
				if (m == eICQ||m == eSkype||m == eMSN||m == eAIM||m == eFB) {
					comp--;
				}
			}
			if (num == comp) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}

			
			System.out.print("Messenger compare test...");
			comp = 0;
			num = 0;
			for (Messenger m : messengers) {
				comp++;
				if (m.compareTo(icq) != 0 && m.compareTo(icq) == -icq.compareTo(m)) {
					num++;
				} else {
					if (icq==m){
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

			System.out.print("Messenger compatibility test...");
			comp = 0;
			num = 0;
			for (Messenger a : messengers) {
				for (Messenger b : messengers) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("IMPP:", "").replace("icq", "").replace("skype", "").replace("msn", "").replace("aim", "").replace("facebook", "").replaceFirst(":", "");
						if (concat.equals("123456:test") ||
								concat.equals("123456:test@example.com") ||
								concat.equals("123456:toast@example.com") ||
								concat.equals("test:123456") ||
								concat.equals("test:test@example.com") ||
								concat.equals("test:toast@example.com") ||
								concat.equals("test@example.com:123456") ||
								concat.equals("test@example.com:test") ||
								concat.equals("test@example.com:toast@example.com") ||
								concat.equals("toast@example.com:123456") ||
								concat.equals("toast@example.com:test") ||
								concat.equals("toast@example.com:test@example.com")) {
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
				System.err.println(comp + "/" + num + " => failed");
				System.exit(-1);
			}
			
			System.out.print("Messenger clone test...");
			comp=0;
			num=0;
			for (Messenger m:messengers){
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

			System.out.print("Messenger merge test...");
			comp=0;
			num=0;
			for (Messenger m:messengers){
				try {
					comp+=2;
					Messenger clone1=(Messenger) m.clone();
					Messenger clone2=(Messenger) icq.clone();
					
					if (clone1.mergeWith(icq) && clone1.nick.equals(icq.nick) && clone1.types.containsAll(m.types) && clone1.types.containsAll(icq.types)) num++;
					if (clone2.mergeWith(m) && clone2.nick.equals(icq.nick) && clone2.types.containsAll(m.types) && clone2.types.containsAll(icq.types)) num++;
					if (comp>num){
						if ((m.nick!=null && !m.nick.isEmpty()) && (icq.nick!=null && !icq.nick.isEmpty()) && !m.nick.equals(icq.nick)){
							num+=2;
						}
					}
					if (comp>num){						
						System.out.println();
						System.out.println(comp+" : "+num);
						System.out.println("fb: <"+icq+">");
						System.out.println(" b: <"+m+">");
						System.out.println("merged:");
						System.out.println("fb: <"+clone2+">");
						System.out.println(" b: <"+clone1+">");
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
		} catch (UnknownObjectException e) {
      e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();

		}

	}
	private String nick=null;
	private TreeSet<String> types=new TreeSet<String>();
	private InputField nickField;
	private JCheckBox aimBox;
	private JCheckBox icqBox;
	private JCheckBox msnBox;
	private JCheckBox skypeBox;
	private JCheckBox facebookBox;
	private VerticalPanel form;
	
	public Messenger(String content) throws InvalidFormatException, UnknownObjectException {
		if (content==null ||!content.startsWith("IMPP:")) throw new InvalidFormatException("Messenger adress does not start with \"IMPP:\"");
		String line = content.substring(5);
		while(!line.startsWith(":")){
			String upper = line.toUpperCase();
			if (upper.startsWith("ICQ")){
				types.add("icq");
				line=line.substring(3);
				continue;
			}
			if (upper.startsWith("AIM")){
				types.add("aim");
				line=line.substring(3);
				continue;
			}			
			if (upper.startsWith("SKYPE")){
				types.add("skype");
				line=line.substring(5);
				continue;
			} 
			if (upper.startsWith("MSN")){
				types.add("msn");
				line=line.substring(3);
				continue;
			} 
			if (upper.startsWith("FACEBOOK")){
				types.add("facebook");
				line=line.substring(8);
				continue;
			} 
			throw new UnknownObjectException("\""+line+"\" in "+content);
		}
		readAddr(line.substring(1));		
	}

	public int compareTo(Messenger otherMessenger) {
		return this.toString().compareTo(otherMessenger.toString());
	}
	
	public VerticalPanel editForm() {
		form=new VerticalPanel("Messenger");
		if (isInvalid()) form.setBackground(Color.red);
		form.add(nickField=new InputField("Nickname",nick));
		nickField.addEditListener(this);
		form.add(aimBox=new JCheckBox("AIM",types.contains("aim")));
		aimBox.addChangeListener(this);
		form.add(icqBox=new JCheckBox("ICQ",types.contains("icq")));
		icqBox.addChangeListener(this);
		form.add(skypeBox=new JCheckBox("Skype",types.contains("skype")));
		skypeBox.addChangeListener(this);
		form.add(msnBox=new JCheckBox("MSN",types.contains("msn")));
		msnBox.addChangeListener(this);
		form.add(facebookBox=new JCheckBox("Facebook",types.contains("facebook")));
		facebookBox.addChangeListener(this);
		form.scale();
		return form;
	}
	
	public String id() throws UnknownObjectException {
		if (types.isEmpty()) throw new UnknownObjectException("Messenger \""+nick+"\" has no known type!");
		String result=types.toString().replace("[", "").replace("]", ":").replace(",", ":");
		if (nick==null || nick.isEmpty()) return result;
		return result+nick;
		
  }

	@Override
  public boolean isCompatibleWith(Messenger other) {
		if (different(nick, other.nick)) return false;
		return true;
  }

	public boolean isEmpty() {
		return nick==null || nick.isEmpty();
	}
	
	public boolean isInvalid() {
		return types.isEmpty();
	}

	@Override
  public boolean mergeWith(Messenger other) {
		if (!isCompatibleWith(other)) return false;
		nick=merge(nick,other.nick);
		types.addAll(other.types);
	  return true;
  }

	public String nick() {
		return nick;
	}

	public void stateChanged(ChangeEvent ce) {
		Object source = ce.getSource();
		if (source==nickField){
			nick=nickField.getText().trim();
		}
		if (aimBox.isSelected()){
			types.add("aim");
		} else {
			types.remove("aim");
		}
		if (icqBox.isSelected()){
			types.add("icq");
		} else {
			types.remove("icq");
		}
		if (skypeBox.isSelected()){
			types.add("skype");
		} else {
			types.remove("skype");
		}
		if (msnBox.isSelected()){
			types.add("msn");
		} else {
			types.remove("msn");
		}
		if (facebookBox.isSelected()){
			types.add("facebook");
		} else {
			types.remove("facebook");
		}
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(isInvalid()?Color.red:Color.green);
		}
	}

	public String toString() {
		try {
	    return "IMPP:"+id();
    } catch (UnknownObjectException e) {
	    e.printStackTrace();
	    return null;
    }
	}

	private void readAddr(String line) {
		while (line.startsWith(":")) line=line.substring(1);
		if (line.trim().isEmpty()) return;		
		nick = line.toLowerCase();
	}

	protected Object clone() throws CloneNotSupportedException {		
		try {
			return new Messenger(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

}
