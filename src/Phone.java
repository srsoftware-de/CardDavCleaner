import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.security.InvalidParameterException;
import java.util.TreeSet;

import javax.swing.JCheckBox;
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
				return "Cell Phone";
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
		PAGER{
			@Override
			public String toString() {				
				return "Pager";
			}
		}, 
		PREFERED{
			@Override
			public String toString() {				
				return "Preferred Phone";
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
	private JCheckBox homeBox, voiceBox, workBox, cellBox, faxBox, prefBox, pagerBox;

	public Phone(String content) throws UnknownObjectException, InvalidFormatException {
		if (content == null || !content.startsWith("TEL")) throw new InvalidFormatException(_("Phone enty does not start with \"TEL\": #", content));
		String line = content.substring(3);
		while (!line.startsWith(":")) {
			String upper = line.toUpperCase();
			if (upper.startsWith("TYPE=FAX")) {
				categories.add(Category.FAX);
				line = line.substring(8);
				continue;
			}
			if (upper.startsWith("TYPE=PREF")) {
				categories.add(Category.PREFERED);
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("PREF=1")) {
				categories.add(Category.PREFERED);
				line = line.substring(6);
				continue;
			}

			if (upper.startsWith("TYPE=HOME")) {
				categories.add(Category.HOME);
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,HOME")) {
				categories.add(Category.HOME);
				line = line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=CELL")) {
				categories.add(Category.CELL);
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,CELL")) {
				categories.add(Category.CELL);
				line = line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=WORK")) {
				categories.add(Category.WORK);
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,WORK")) {
				categories.add(Category.WORK);
				line = line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=VOICE")) {
				categories.add(Category.VOICE);
				line = line.substring(10);
				continue;
			}
			if (upper.startsWith("\\,VOICE")) {
				categories.add(Category.VOICE);
				line = line.substring(7);
				continue;
			}
			if (upper.startsWith("TYPE=PAGER")) {
				categories.add(Category.PAGER);
				line = line.substring(10);
				continue;
			}
			if (upper.startsWith("\\,PAGER")) {
				categories.add(Category.PAGER);
				line = line.substring(7);
				continue;
			}
			if (line.startsWith(";")) {
				line = line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line + " in " + content);
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
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);

		form.add(numField = new InputField(_("Number"), number));
		numField.addChangeListener(this);

		form.add(prefBox = new JCheckBox(_("Preferred Phone"), isPreferedPhone()));
		prefBox.addChangeListener(this);
		form.add(homeBox = new JCheckBox(_("Home Phone"), isHomePhone()));
		homeBox.addChangeListener(this);
		form.add(voiceBox = new JCheckBox(_("Voice Phone"), isVoice()));
		voiceBox.addChangeListener(this);
		form.add(workBox = new JCheckBox(_("Work Phone"), isWorkPhone()));
		workBox.addChangeListener(this);
		form.add(cellBox = new JCheckBox(_("Cell Phone"), isCellPhone()));
		cellBox.addChangeListener(this);
		form.add(faxBox = new JCheckBox(_("Fax"), isFax()));
		faxBox.addChangeListener(this);
		form.add(pagerBox = new JCheckBox(_("Pager"), isPager()));
		pagerBox.addChangeListener(this);
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
		return categories.contains(Category.PREFERED);
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
		update();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TEL");
		if (isPreferedPhone()) sb.append(";PREF=1");
		if (isFax()) sb.append(";TYPE=FAX");
		if (isHomePhone()) sb.append(";TYPE=HOME");
		if (isCellPhone()) sb.append(";TYPE=CELL");
		if (isWorkPhone()) sb.append(";TYPE=WORK");
		if (isVoice()) sb.append(";TYPE=VOICE");
		if (isPager()) sb.append(";TYPE=PAGER");
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
		invalid = false;
		readPhone(numField.getText());
		if (homeBox.isSelected()){
			categories.add(Category.HOME);
		}
		if (workBox.isSelected()){
			categories.add(Category.WORK);
		}
		if (voiceBox.isSelected()){
			categories.add(Category.VOICE);
		}
		if (cellBox.isSelected()){
			categories.add(Category.CELL);
		}
		if (faxBox.isSelected()){
			categories.add(Category.FAX);
		}
		if (pagerBox.isSelected()){
			categories.add(Category.PAGER);
		}
		if (prefBox.isSelected()){
			categories.add(Category.PREFERED);
		}
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid ? Color.red : Color.green);
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
