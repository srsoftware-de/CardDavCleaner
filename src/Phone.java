import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.security.InvalidParameterException;

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
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
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
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
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
					if (concat.equals("01707705281:notaphonenumber") ||
							concat.equals("01707705281:0123456") ||
							concat.equals("01707705281:23456") ||
							concat.equals("01707705281:528177777") ||
							concat.equals("notaphonenumber:01707705281") ||
							concat.equals("notaphonenumber:0123456") ||
							concat.equals("notaphonenumber:23456") ||
							concat.equals("notaphonenumber:5281") ||
							concat.equals("notaphonenumber:528177777") ||
							concat.equals("0123456:01707705281") || 
							concat.equals("0123456:notaphonenumber") || 
							concat.equals("0123456:528177777") || 
							concat.equals("23456:01707705281") || 
							concat.equals("23456:notaphonenumber") ||
							concat.equals("23456:528177777") || 
							concat.equals("528177777:01707705281") ||
							concat.equals("528177777:notaphonenumber") ||
							concat.equals("528177777:0123456") || 
							concat.equals("528177777:23456")) {
						num--;
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
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
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
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

	private boolean fax = false;
	private boolean home = false;
	private boolean cell = false;
	private boolean work = false;
	private boolean voice = false;
	private boolean pager = false;
	private boolean preffered = false;
	private String number;
	private boolean invalid = false;

	private InputField numField;
	VerticalPanel form;
	private JCheckBox homeBox, voiceBox, workBox, cellBox, faxBox, prefBox, pagerBox;

	public Phone(String content) throws UnknownObjectException, InvalidFormatException {
		if (content == null || !content.startsWith("TEL")) throw new InvalidFormatException(_("Phone enty does not start with \"TEL\": #",content));
		String line = content.substring(3);
		while (!line.startsWith(":")) {
			String upper = line.toUpperCase();
			if (upper.startsWith("TYPE=FAX")) {
				fax = true;
				line = line.substring(8);
				continue;
			}
			if (upper.startsWith("TYPE=PREF")||upper.startsWith("PREF=1")) {
				preffered = true;
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("TYPE=HOME")) {
				home = true;
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,HOME")) {
				home = true;
				line = line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=CELL")) {
				cell = true;
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,CELL")) {
				cell = true;
				line = line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=WORK")) {
				work = true;
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,WORK")) {
				work = true;
				line = line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=VOICE")) {
				voice = true;
				line = line.substring(10);
				continue;
			}
			if (upper.startsWith("\\,VOICE")) {
				voice = true;
				line = line.substring(7);
				continue;
			}
			if (upper.startsWith("TYPE=PAGER")) {
				pager = true;
				line = line.substring(10);
				continue;
			}
			if (upper.startsWith("\\,PAGER")) {
				pager = true;
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

	public String category() {
		if (home) return "home";
		if (work) return "work";
		if (fax) return "fax";
		if (cell) return "cell";
		if (voice) return "voice";
		if (pager) return "pager";
		return "empty category";
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

		form.add(prefBox = new JCheckBox(_("Preferred Phone"), home));
		prefBox.addChangeListener(this);
		form.add(homeBox = new JCheckBox(_("Home Phone"), home));
		homeBox.addChangeListener(this);
		form.add(voiceBox = new JCheckBox(_("Voice Phone"), voice));
		voiceBox.addChangeListener(this);
		form.add(workBox = new JCheckBox(_("Work Phone"), work));
		workBox.addChangeListener(this);
		form.add(cellBox = new JCheckBox(_("Cell Phone"), cell));
		cellBox.addChangeListener(this);
		form.add(faxBox = new JCheckBox(_("Pager"), pager));
		faxBox.addChangeListener(this);
		form.add(pagerBox = new JCheckBox(_("Pager"), pager));
		pagerBox.addChangeListener(this);
		form.scale();
		return form;
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	public boolean isCellPhone() {
		return cell;
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
		return fax;
	}

	public boolean isPager() {
		return pager;
	}

	public boolean isHomePhone() {
		return home;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public boolean isVoice() {
		return voice;
	}

	public boolean isWorkPhone() {
		return work;
	}

	@Override
	public boolean mergeWith(Phone other) {
		if (!isCompatibleWith(other)) return false;
		number = mergeNumber(simpleNumber(), other.simpleNumber());
		if (other.preffered) preffered=true;
		if (other.home) home = true;
		if (other.work) work = true;
		if (other.cell) cell = true;
		if (other.fax) fax = true;
		if (other.pager) pager = true;
		if (other.voice) voice = true;
		return true;
	}

	public String number() {
		return number;
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();
	}

	public void setCell() {
		home = false;
		work = false;
		fax = false;
		cell = true;
		voice = false;
		pager = false;
	}

	public void setFax() {
		home = false;
		work = false;
		fax = true;
		cell = false;
		voice = false;
		pager = false;
	}

	public void setHome() {
		home = true;
		work = false;
		fax = false;
		cell = false;
		voice = false;
		pager = false;
	}

	public void setVoice() {
		home = false;
		work = false;
		fax = false;
		cell = false;
		voice = true;
		pager = false;
	}

	public void setWork() {
		home = false;
		work = true;
		fax = false;
		cell = false;
		voice = false;
		pager = false;
	}

	public void setPager() {
		home = false;
		work = false;
		fax = false;
		cell = false;
		voice = false;
		pager = true;
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
		if (preffered) sb.append(";PREF=1");
		if (fax) sb.append(";TYPE=FAX");
		if (home) sb.append(";TYPE=HOME");
		if (cell) sb.append(";TYPE=CELL");
		if (work) sb.append(";TYPE=WORK");
		if (voice) sb.append(";TYPE=VOICE");
		if (pager) sb.append(";TYPE=PAGER");
		sb.append(':');
		if (number != null) sb.append(number);
		return sb.toString();
	}

	private String mergeNumber(String num1, String num2) {
		if (num1 == null || num1.isEmpty()) return num2;
		if (num2 == null || num2.isEmpty()) return num1;
		if (num1.endsWith(num2)) return num1;
		if (num2.endsWith(num1)) return num2;
		throw new InvalidParameterException(_("Trying to merge \"#\" with \"#\"!",new Object[]{num1,num2}));
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
		home = homeBox.isSelected();
		work = workBox.isSelected();
		voice = voiceBox.isSelected();
		cell = cellBox.isSelected();
		fax = faxBox.isSelected();
		pager = pagerBox.isSelected();
		preffered = prefBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid ? Color.red : Color.green);
		}
	}

	protected Object clone() throws CloneNotSupportedException {
		try {
			return new Phone(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
}
