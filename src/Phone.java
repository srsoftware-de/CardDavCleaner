import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Phone extends Mergable<Phone> implements DocumentListener, ChangeListener, Comparable<Phone> {
	public static void test() {
		try {
			System.out.print(_("Phone creation test (null)..."));
			String testCase = null;
			try {
				Phone nM = new Phone(testCase);
				System.err.println(_("failed: #", nM));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok"));
			}

			System.out.print(_("Phone creation test (empty)..."));
			testCase = "TEL:";
			Phone eM = new Phone(testCase);
			if (eM.toString().equals(testCase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", eM));
				System.exit(-1);
			}

			System.out.print(_("Phone creation test (valid)..."));
			testCase = "TEL:+49(170)77-05-281";
			Phone vM = new Phone(testCase);
			if (vM.toString().equals(testCase) && !vM.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", vM));
				System.exit(-1);
			}

			System.out.print(_("Phone creation test (invalid)..."));
			testCase = "TEL:not a phone number";
			Phone iM = new Phone(testCase);
			if (iM.toString().equals(testCase) && iM.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", iM));
				System.exit(-1);
			}

			System.out.print(_("Phone creation test (valid work)..."));
			testCase = "TEL;TYPE=WORK:0123456";
			Phone workP = new Phone(testCase);
			if (workP.toString().equals(testCase) && !workP.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", workP));
				System.exit(-1);
			}

			System.out.print(_("Phone creation test (valid home)..."));
			testCase = "TEL;TYPE=HOME:23456";
			Phone homeP = new Phone(testCase);
			if (homeP.toString().equals(testCase) && !homeP.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", homeP));
				System.exit(-1);
			}

			System.out.print(_("Phone creation test (valid voice)..."));
			testCase = "TEL;TYPE=VOICE:2-3(45)6";
			Phone voiceP = new Phone(testCase);
			if (voiceP.toString().equals(testCase) && !voiceP.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", voiceP));
				System.exit(-1);
			}

			System.out.print(_("Phone creation test (valid cell)..."));
			testCase = "TEL;TYPE=CELL:5 2-8 1";
			Phone cellP = new Phone(testCase);
			if (cellP.toString().equals(testCase) && !cellP.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", cellP));
				System.exit(-1);
			}

			System.out.print(_("Phone creation test (valid fax)..."));
			testCase = "TEL;TYPE=FAX:52(81)77777";
			Phone faxP = new Phone(testCase);
			if (faxP.toString().equals(testCase) && !faxP.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", faxP));
				System.exit(-1);
			}

			Phone[] phones = { eM, vM, iM, workP, homeP, voiceP, voiceP, faxP };

			System.out.print(_("Phone isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Phone m : phones) {
				comp++;
				if (!m.isEmpty()) {
					num++;
				}
				if (m == eM) {
					comp--;
				}
			}
			if (num == comp) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Phone compare test..."));
			comp = 0;
			num = 0;
			for (Phone m : phones) {
				comp++;
				if (m.compareTo(voiceP) != 0 && m.compareTo(voiceP) == -voiceP.compareTo(m)) {
					num++;
				} else {
					if (voiceP == m) {
						num++;
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Phone compatibility test..."));
			comp = 0;
			num = 0;
			for (Phone a : phones) {
				for (Phone b : phones) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					}
					String concat = (a.simpleNumber() + ":" + b.simpleNumber());
					if (concat.equals("01707705281:notaphonenumber") || concat.equals("01707705281:0123456") || concat.equals("01707705281:23456") || concat.equals("01707705281:528177777") || concat.equals("notaphonenumber:01707705281") || concat.equals("notaphonenumber:0123456") || concat.equals("notaphonenumber:23456") || concat.equals("notaphonenumber:5281") || concat.equals("notaphonenumber:528177777") || concat.equals("0123456:01707705281") || concat.equals("0123456:notaphonenumber") || concat.equals("0123456:528177777") || concat.equals("23456:01707705281") || concat.equals("23456:notaphonenumber") || concat.equals("23456:528177777") || concat.equals("528177777:01707705281") || concat.equals("528177777:notaphonenumber") || concat.equals("528177777:0123456") || concat.equals("528177777:23456")) {
						num--;
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Phone clone test..."));
			comp = 0;
			num = 0;
			for (Phone m : phones) {
				comp++;
				try {
					if (m.toString().equals(m.clone().toString())) {
						num++;
					}
				} catch (CloneNotSupportedException e) {}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Phone merge test..."));
			comp = 0;
			num = 0;
			for (Phone m : phones) {
				try {
					comp += 2;
					Phone clone1 = (Phone) m.clone();
					Phone clone2 = (Phone) cellP.clone();

					if (clone1.mergeWith(cellP) && clone1.simpleNumber().equals(cellP.simpleNumber())) num++;
					if (clone2.mergeWith(m) && clone2.simpleNumber().equals(cellP.simpleNumber())) num++;
					if (comp > num) {
						if ((m.simpleNumber() != null && !m.simpleNumber().isEmpty()) && (cellP.simpleNumber() != null && !cellP.simpleNumber().isEmpty()) && !m.simpleNumber().equals(cellP.simpleNumber())) {
							num += 2;
						}
					}
					if (comp > num) {
						System.out.println();
						System.out.println("fb: " + cellP);
						System.out.println(" b: " + m);
						System.out.println(_("merged:"));
						System.out.println("fb: " + clone2);
						System.out.println(" b: " + clone1);
					}
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();

		}

	}

	public static enum Category {
		HOME{
			@Override
			public String toString() {				
				return "Home";
			}
		},
		WORK{
			@Override
			public String toString() {				
				return "Work";
			}
		}, 
		CELL{
			@Override
			public String toString() {				
				return "Cell";
			}
		}, 
		FAX{
			@Override
			public String toString() {				
				return "Fax";
			}
		}, 
		VOICE{
			@Override
			public String toString() {				
				return "Voice";
			}
		}, 
		VIDEO{
			@Override
			public String toString() {				
				return "Video";
			}
		}, 
		PAGER{
			@Override
			public String toString() {				
				return "Pager";
			}
		}, 
		PREF{
			@Override
			public String toString() {				
				return "Pref";
			}
		};
		
		public abstract String toString();

	};
	
	private static String _(String text) {
		return Translations.get(text);
	}

	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}

	private TreeSet<Category> categories=new TreeSet<Category>();

	private String number;
	private boolean invalid = false;

	private InputField numField;
	VerticalPanel form;

	public Phone(String content) throws UnknownObjectException, InvalidFormatException {
		if (content == null || !content.startsWith("TEL")) throw new InvalidFormatException(_("Phone enty does not start with \"TEL\": #", content));
		String line = content.substring(3);
		while (!line.startsWith(":")) {
			while (!line.startsWith(":")) {
				if (line.toUpperCase().startsWith(";TYPE=")) {
					line=line.substring(6);
					do {
						if (line.charAt(0)==',') line=line.substring(1);
						if (line.toUpperCase().startsWith("X-")) line = line.substring(2);
						if (line.toUpperCase().startsWith("CELL")) {
							categories.add(Category.CELL);
							line=line.substring(4);
						}
						if (line.toUpperCase().startsWith("COMPANY_MAIN")) {
							categories.add(Category.WORK);
							line=line.substring(12);
						}						
						if (line.toUpperCase().startsWith("FAX")) {
							categories.add(Category.FAX);
							line=line.substring(3);
						}
						if (line.toUpperCase().startsWith("HOME")) {
							categories.add(Category.HOME);
							line=line.substring(4);
						}
						if (line.toUpperCase().startsWith("PAGER")) {
							categories.add(Category.PAGER);
							line=line.substring(5);
						}
						if (line.toUpperCase().startsWith("PREF")) {
							categories.add(Category.PREF);
							line=line.substring(4);
						}
						if (line.toUpperCase().startsWith("VIDEO")) {
							categories.add(Category.VIDEO);
							line=line.substring(5);
						}
						if (line.toUpperCase().startsWith("VOICE")) {
							categories.add(Category.VOICE);
							line=line.substring(5);
						}
						if (line.toUpperCase().startsWith("WORK")) {
							categories.add(Category.WORK);
							line=line.substring(4);
						}
						if (line.toUpperCase().startsWith("SECONDARY")) line=line.substring(9);
						
						if (line.charAt(0)=='\\') line=line.substring(1); // i have seen entries like this: TEL;TYPE=WORK\,CELL:1230456
					} while (line.charAt(0)==',');
				} else if (line.toUpperCase().startsWith(";UNKNOWN=TYPE")){
					line=line.substring(13);
				} else throw new UnknownObjectException(content+" –– "+line);
			}
		}
		readPhone(line.substring(1));
	}

	public TreeSet<Category> categories() {
		return new TreeSet<Category>(categories);
	}

	public void changedUpdate(DocumentEvent arg0) {
		update();
	}

	public int compareTo(Phone otherPhone) {
		return this.toString().compareTo(otherPhone.toString());
	}

	public VerticalPanel editForm() {
		form = new VerticalPanel(_("Phone"));
		if (invalid) form.setBackground(Color.orange);
		if (isEmpty()) form.setBackground(Color.yellow);

		form.add(numField = new InputField(_("Number"), number));
		numField.addChangeListener(this);
		
		for (final Category cat: Category.values()){
			final JCheckBox box=new JCheckBox(_(cat.toString()), categories.contains(cat));
			box.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent arg0) {
					if (box.isSelected()){
						categories.add(cat);
					} else {
						categories.remove(cat);
					}
				}
			});
			form.add(box);				
		}

		form.scale();
		return form;
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	public boolean isCellPhone() {
		return categories.contains(Category.CELL);
	}

	@Override
	public boolean isCompatibleWith(Phone other) {
		if (simpleNumber() == null) return true;
		if (other.simpleNumber() == null) return true;
		if (simpleNumber().endsWith(other.simpleNumber())) return true;
		if (other.simpleNumber().endsWith(simpleNumber())) return true;
		return false;
	}

	public boolean isEmpty() {
		return number == null || number.trim().isEmpty();
	}

	public boolean isFax() {
		return categories.contains(Category.FAX);
	}
	
	public boolean isPager() {
		return categories.contains(Category.PAGER);
	}

	public boolean isHomePhone() {
		return categories.contains(Category.HOME);
	}
	
	public boolean isPreferedPhone(){
		return categories.contains(Category.PREF);
	}


	public boolean isVoice() {
		return categories.contains(Category.VOICE);
	}

	public boolean isWorkPhone() {
		return categories.contains(Category.WORK);
		}

	public boolean isInvalid() {
		return invalid;
	}

	@Override
	public boolean mergeWith(Phone other) {
		if (!isCompatibleWith(other)) return false;
		number = mergeNumber(simpleNumber(), other.simpleNumber());
		categories.addAll(other.categories);
		return true;
	}

	public String number() {
		return number;
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();
	}

	public String simpleNumber() {
		if (isEmpty()) return null;
		String simple = this.number.trim();
		while (simple.contains("(")) {
			simple = simple.replace("(", "");
		}
		while (simple.contains(")")) {
			simple = simple.replace(")", "");
		}
		while (simple.contains(" ")) {
			simple = simple.replace(" ", "");
		}
		while (simple.contains("-")) {
			simple = simple.replace("-", "");
		}
		while (simple.contains("/")) {
			simple = simple.replace("/", "");
		}
		if (simple.startsWith("+49")) simple = 0 + simple.substring(3);
		if (simple.startsWith("0049")) simple = 0 + simple.substring(4);
		if (simple.startsWith("+")) simple = 0 + simple.substring(1);
		return simple;
	}

	public void stateChanged(ChangeEvent arg0) {
		invalid = false;
		readPhone(numField.getText());
		update();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TEL");
		if (!categories.isEmpty()){
			sb.append(";TYPE=");
			
			for (Iterator<Category> it = categories.iterator(); it.hasNext();){
				Category cat=it.next();
				sb.append(cat.toString().toUpperCase());
				if (it.hasNext()){
					sb.append(',');
				}
			}
		}
		sb.append(':');
		if (number != null) sb.append(number);
		return sb.toString();
	}

	private String mergeNumber(String num1, String num2) {
		if (num1 == null || num1.isEmpty()) return num2;
		if (num2 == null || num2.isEmpty()) return num1;
		if (num1.endsWith(num2)) return num1;
		if (num2.endsWith(num1)) return num2;
		throw new InvalidParameterException(_("Trying to merge \"#\" with \"#\"!", new Object[] { num1, num2 }));
	}

	private void readPhone(String line) {
		if (line.isEmpty()) {
			number = null;
			return;
		}
		number = line;
		String simple = simpleNumber();
		for (char c : simple.toCharArray()) {
			if (!Character.isDigit(c)) invalid = true;
		}
	}

	private void update() {
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid ? Color.orange : UIManager.getColor ( "Panel.background" ));
		}
	}

	protected Phone clone() throws CloneNotSupportedException {
		try {
			return new Phone(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	public boolean is(Category c) {
		return categories.contains(c);
	}

	/**
	 * @param cloneCategories if set to false, categories of the ancestor will not be copied to the new phone
	 * @return a clone of thins phone
	 * @throws CloneNotSupportedException 
	 */
	public Phone clone(boolean cloneCategories) throws CloneNotSupportedException {
		Phone result=clone();
		result.categories.clear();
		return result;
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public void removeCategory(Category category) {
		categories.remove(category);
	}
}
