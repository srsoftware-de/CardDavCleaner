import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

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
				} else if (m == eICQ) {
					num++;
				} else if (m == eSkype) {
					num++;
				} else if (m == eMSN) {
					num++;
				} else if (m == eAIM) {
					num++;
				} else if (m == eFB) {
					num++;
				}
			}
			if (num == comp) {
				System.out.println("ok");
			} else {
				System.err.println(num + "/" + comp + " => failed");
				System.exit(-1);
			}

			/*
			System.out.print("Messenger compare test...");
			comp = 0;
			num = 0;
			for (Messenger m : mails) {
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

			System.out.print("Messenger compatibility test...");
			comp = 0;
			num = 0;
			for (Messenger a : mails) {
				for (Messenger b : mails) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("EMAIL", "").replace(";TYPE=INTERNET", "").replace(";TYPE=WORK", "").replace(";TYPE=HOME", "").replaceFirst(":", "");
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
							System.err.println(a + " <=> " + b);
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
			
			System.out.print("Messenger clone test...");
			comp=0;
			num=0;
			for (Messenger m:mails){
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
			for (Messenger m:mails){
				try {
					comp+=2;
					Messenger clone1=(Messenger) m.clone();
					Messenger clone2=(Messenger) netM.clone();
					
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
		} catch (UnknownObjectException e) {
      e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();

		}

	}
	private boolean aim=false;
	private boolean icq=false;
	private boolean skype=false;
	private boolean msn=false;
	private boolean facebook=false;
	private boolean invalid;
	private String nick=null;
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
				icq=true;
				line=line.substring(3);
				continue;
			}
			if (upper.startsWith("AIM")){
				aim=true;
				line=line.substring(3);
				continue;
			}
			if (upper.startsWith("SKYPE")){
				skype=true;
				line=line.substring(5);
				continue;
			} 
			if (upper.startsWith("MSN")){
				msn=true;
				line=line.substring(3);
				continue;
			} 
			if (upper.startsWith("FACEBOOK")){
				facebook=true;
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
		if (invalid) form.setBackground(Color.red);
		form.add(nickField=new InputField("Nickname",nick));
		nickField.addEditListener(this);
		form.add(aimBox=new JCheckBox("AIM",aim));
		aimBox.addChangeListener(this);
		form.add(icqBox=new JCheckBox("ICQ",icq));
		icqBox.addChangeListener(this);
		form.add(skypeBox=new JCheckBox("Skype",skype));
		skypeBox.addChangeListener(this);
		form.add(msnBox=new JCheckBox("MSN",msn));
		msnBox.addChangeListener(this);
		form.add(facebookBox=new JCheckBox("Facebook",facebook));
		facebookBox.addChangeListener(this);
		form.scale();
		return form;
	}
	
	public String id() throws UnknownObjectException {
		if (aim) return "aim:"+nick;
		if (icq) return "icq:"+nick;
		if (skype) return "skype:"+nick;
		if (msn) return "msn:"+nick;
		if (facebook) return "facebook"+nick;
		throw new UnknownObjectException("Messenger \""+nick+"\" has no known type!");
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
		return invalid;
	}

	public void merge(Messenger m) throws InvalidAssignmentException {
		if (!nick.equals(m.nick)) throw new InvalidAssignmentException("Trying to merge two messenger accounts with different ids!");
		if (m.aim) aim=true;
		if (m.icq) icq=true;
		if (m.skype) skype=true;
		if (m.msn) msn=true;
		if (m.facebook) facebook=true;
	}

	@Override
  public boolean mergeWith(Messenger other) {
		nick=merge(nick,other.nick);
		if (other.aim)aim =true;
		if (other.icq)icq =true;
		if (other.skype)skype =true;
		if (other.msn)msn =true;
		if (other.facebook) facebook=true;
	  return false;
  }

	public String nick() {
		return nick;
	}

	public void stateChanged(ChangeEvent ce) {
		Object source = ce.getSource();
		if (source==nickField){
			nick=nickField.getText().trim();
		}
		aim=aimBox.isSelected();
		icq=icqBox.isSelected();
		skype=skypeBox.isSelected();
		msn=msnBox.isSelected();
		facebook=facebookBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
			invalid=false;
		} else {
			invalid=(aim==false&&icq==false&&skype==false&&msn==false&&facebook==false); 
			form.setBackground(invalid?Color.red:Color.green);
		}
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("IMPP:");
		if (aim) sb.append("aim");
		if (icq) sb.append("icq");
		if (skype) sb.append("skype");
		if (msn) sb.append("msn");
		if (facebook) sb.append("facebook");
		sb.append(":");
		if (nick!=null && !nick.isEmpty()) sb.append(nick);
		return sb.toString();
	}

	private void readAddr(String line) {
		while (line.startsWith(":")) line=line.substring(1);
		if (line.trim().isEmpty()) return;		
		nick = line.toLowerCase();
	}



}
