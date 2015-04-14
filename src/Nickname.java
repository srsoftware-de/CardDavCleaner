import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class Nickname extends Mergable<Nickname> implements DocumentListener, ChangeListener, Comparable<Nickname> {

	public static void test() {
		try {
			System.out.print(_("Nickname creation test (null)..."));
			String testCase = null;
			try {
				Nickname nM = new Nickname(testCase);
				System.err.println(_("failed: #", nM));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok"));
      }

			System.out.print(_("Nickname creation test (empty)..."));
			testCase = "NICKNAME:";
			Nickname emptyN = new Nickname(testCase);
			if (emptyN.toString().equals(testCase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", emptyN));
				System.exit(-1);
			}

			System.out.print(_("Nickname creation test (simple)..."));
			testCase = "NICKNAME:Master of Daleks";
			Nickname simplN = new Nickname(testCase);
			if (simplN.toString().equals(testCase) && !simplN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", simplN));
				System.exit(-1);
			}

			System.out.print(_("Nickname creation test (work)..."));
			testCase = "NICKNAME;TYPE=WORK:Edward Snowden";
			Nickname workN = new Nickname(testCase);
			if (workN.toString().equals(testCase) && !workN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", workN));
				System.exit(-1);
			}

			System.out.print(_("Nickname creation test (home)..."));
			testCase = "NICKNAME;TYPE=HOME:KIMBLE";
			Nickname homeN = new Nickname(testCase);
			if (homeN.toString().equals(testCase) && !homeN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", homeN));
				System.exit(-1);
			}

			System.out.print(_("Nickname creation test (valid internet)..."));
			testCase = "NICKNAME;TYPE=INTERNET:";
			Nickname netN = new Nickname(testCase);
			if (netN.toString().equals(testCase) && !netN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", netN));
				System.exit(-1);
			}
			
			Nickname[] nicknames = { emptyN,simplN,workN,homeN,netN };

			System.out.print(_("Nickname isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Nickname m : nicknames) {
				comp++;
				if (!m.isEmpty()) {
					num++;
				} 
				if (m == emptyN||m==netN) {
					comp--;
				}
			}
			if (num == comp) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Nickname compare test..."));
			comp = 0;
			num = 0;
			for (Nickname m : nicknames) {
				comp++;
				if (m.compareTo(netN) != 0 && m.compareTo(netN) == -netN.compareTo(m)) {
					num++;
				} else {
					if (netN==m){
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

			System.out.print(_("Nickname compatibility test..."));
			comp = 0;
			num = 0;
			for (Nickname a : nicknames) {
				for (Nickname b : nicknames) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("NICKNAME", "").replace(";TYPE=INTERNET", "").replace(";TYPE=WORK", "").replace(";TYPE=HOME", "").replaceFirst(":", "");
						if (concat.equals("Master of Daleks:Edward Snowden") ||
								concat.equals("Master of Daleks:KIMBLE") ||
								concat.equals("Edward Snowden:Master of Daleks") ||
								concat.equals("Edward Snowden:KIMBLE") ||
								concat.equals("KIMBLE:Master of Daleks") ||
								concat.equals("KIMBLE:Edward Snowden")) {
							comp++;
						} else {
							System.err.println(concat);
							//System.err.println(a + " <=> " + b);
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
			
			System.out.print(_("Nickname clone test..."));
			comp=0;
			num=0;
			for (Nickname m:nicknames){
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

			System.out.print(_("Nickname merge test..."));
			comp=0;
			num=0;
			for (Nickname m:nicknames){
				try {
					comp+=2;
					Nickname clone1=(Nickname) m.clone();
					Nickname clone2=(Nickname) workN.clone();
					
					if (clone1.mergeWith(workN) && clone1.toString().equals(workN.toString())) num++;
					if (clone2.mergeWith(m) && clone2.toString().equals(workN.toString())) num++;
					if (clone1.toString().equals("NICKNAME;TYPE=WORK;TYPE=INTERNET:Edward Snowden")) num++;
					if (clone2.toString().equals("NICKNAME;TYPE=WORK;TYPE=INTERNET:Edward Snowden")) num++;if (comp>num){
						if ((m.nick!=null && !m.nick.isEmpty()) && (workN.nick!=null && !workN.nick.isEmpty()) && !m.nick.equals(workN.nick)){
							num+=2;
						}
					}
					if (comp>num){
						System.out.println();
						System.out.println("fb: "+workN);
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
		} catch (UnknownObjectException e) {
      e.printStackTrace();
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
	private boolean work=false;
	private boolean home=false;
	private boolean internet=false;
	private String nick=null;

	private boolean invalid=false;
	private InputField nickField;
	private JCheckBox homeBox,workBox,internetBox;
	private VerticalPanel form;
	
	public Nickname(String content) throws UnknownObjectException, InvalidFormatException {
		if (content==null || !content.startsWith("NICKNAME"))throw new InvalidFormatException(_("Nickname entry does not start with \"NICKNAME\": #",content));
		String line=content.substring(8);
		while(!line.startsWith(":")){
			String upper = line.toUpperCase();
			if (upper.startsWith("TYPE=WORK")){
				work=true;
				line=line.substring(9);
				continue;
			}
			if (upper.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			} 
			if (upper.startsWith("TYPE=INTERNET")){
				internet=true;
				line=line.substring(13);
				continue;
			} 
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line+" in "+content);
		}		
		readNick(line.substring(1));
	}
	
	public String category() {
		if (work) return "work";
		if (home) return "home";
		if (internet) return "internet";
		return "no category";
	}

	public void changedUpdate(DocumentEvent arg0) {
		update();
	}

	public int compareTo(Nickname o) {
		return this.toString().compareTo(o.toString());
	}
	
	public VerticalPanel editForm() {
		form=new VerticalPanel(_("Nickname"));
		if (invalid) form.setBackground(Color.orange);
		if (isEmpty()) form.setBackground(Color.yellow);
		
		form.add(nickField=new InputField(_("Nickname"),nick));
		nickField.addChangeListener(this);
		
		form.add(homeBox=new JCheckBox(_("Home"),home));
		homeBox.addChangeListener(this);
		form.add(workBox=new JCheckBox(_("Work"),work));
		workBox.addChangeListener(this);
		form.add(internetBox=new JCheckBox(_("Internet"),internet));
		internetBox.addChangeListener(this);
		form.scale();
		return form;
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	@Override
  public boolean isCompatibleWith(Nickname other) {
		if (different(nick,other.nick)) return false;
	  return true;
  }

	public boolean isEmpty() {
		return nick==null || nick.trim().isEmpty();
	}

	public boolean isHomeNick() {
		return home;
	}
	
	public boolean isInternetNick(){
		return internet;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public boolean isWorkNick() {
		return work;
	}

	public void merge(Nickname nick) throws InvalidAssignmentException {
		if (!nick.equals(nick)) throw new InvalidAssignmentException(_("Trying to merge two nicknames with different strings!"));
		if (nick.home) home=true;
		if (nick.work) work=true;
		if (nick.internet) internet=true;
	}

	@Override
  public boolean mergeWith(Nickname other) {
		if (!isCompatibleWith(other)) return false;
		nick=merge(nick,other.nick);
		if (other.home) home=true;
		if (other.work) work=true;
		if (other.internet) internet=true;
	  return true;
  }

	public String name() {
		return nick;
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();		
	}

	public void setHome() {
		work=false;
		home=true;
		internet=false;
	}

	public void setInternet() {
		work=false;
		home=false;
		internet=true;
	}

	public void setWork() {
		work=true;
		home=false;
		internet=false;
	}
	
	public void stateChanged(ChangeEvent arg0) {
		update();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("NICKNAME");
		if (home) sb.append(";TYPE=HOME");
		if (work) sb.append(";TYPE=WORK");
		if (internet) sb.append(";TYPE=INTERNET");
		sb.append(":");
		if (nick!=null)sb.append(nick);
		return sb.toString();
	}

	private void readNick(String line) {
		if (line==null) return;
		line=line.trim();
		if (line.isEmpty()) {
			nick=null;
		} else {
			nick = line;
		}
	}

	private void update() {
		invalid=false;
		readNick(nickField.getText());
		home=homeBox.isSelected();
		work=workBox.isSelected();
		internet=internetBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.orange:UIManager.getColor ( "Panel.background" ));
		}
	}

	protected Nickname clone() throws CloneNotSupportedException {		
		try {
			return new Nickname(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
}
