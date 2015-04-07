import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Name extends Mergable<Name> implements DocumentListener, Comparable<Name> {
	public static void test() {
		try {
			System.out.print(_("Name creation (null)..."));
			try {
				Name nullA = new Name(null);
				System.err.println("failed: " + nullA);
				System.exit(-1);
			} catch (InvalidFormatException ife) {
				System.out.println(_("ok."));
			}

			System.out.print(_("Name creation (empty)..."));
			String testcase = "N:;;;;";
			Name emptyA = new Name(testcase.replace(";", ""));
			Name emptyB = new Name(testcase);
			if (emptyA.toString().equals(testcase) && emptyB.toString().equals(testcase)) {
				System.out.println(_("ok."));
			} else {
				System.err.println("failed: " + emptyA);
				System.exit(-1);
			}

			System.out.print(_("Name creation (family)..."));
			testcase = "N:ylimaf;;;;";
			Name famA = new Name(Tests.reversed(testcase));
			Name famB = new Name(testcase);
			if (famA.toString().equals(Tests.reversed(testcase) + ";;;;") && famB.toString().equals(testcase)) {
				System.out.println(_("ok."));
			} else {
				System.err.println("failed: " + famA + "/" + famB);
				System.exit(-1);
			}

			System.out.print(_("Name creation (first)..."));
			testcase = "N:;tsrif;;;";
			Name firstA = new Name(Tests.reversed(testcase));
			Name firstB = new Name(testcase);
			if (firstA.toString().equals(Tests.reversed(testcase) + ";;;") && firstB.toString().equals(testcase)) {
				System.out.println(_("ok."));
			} else {
				System.err.println("failed: " + firstA + "/" + firstB);
				System.exit(-1);
			}

			System.out.print(_("Name creation (middle)..."));
			testcase = "N:;;elddim;;";
			Name midA = new Name(Tests.reversed(testcase));
			Name midB = new Name(testcase);
			if (midA.toString().equals(Tests.reversed(testcase) + ";;") && midB.toString().equals(testcase)) {
				System.out.println(_("ok."));
			} else {
				System.err.println("failed: " + midA + "/" + midB);
				System.exit(-1);
			}

			System.out.print(_("Name creation (prefix)..."));
			testcase = "N:;;;xiferp;";
			Name prefA = new Name(Tests.reversed(testcase));
			Name prefB = new Name(testcase);
			if (prefA.toString().equals(Tests.reversed(testcase) + ";") && prefB.toString().equals(testcase)) {

				System.out.println(_("ok."));
			} else {
				System.err.println("failed: " + prefA + "/" + prefB);
				System.exit(-1);
			}

			System.out.print(_("Name creation (suffix)..."));
			testcase = "N:;;;;xiffus";
			Name sufA = new Name(Tests.reversed(testcase));
			Name sufB = new Name(testcase);
			if (sufA.toString().equals(Tests.reversed(testcase)) && sufB.toString().equals(testcase)) {
				System.out.println(_("ok."));
			} else {
				System.err.println("failed: " + sufA + "/" + sufB);
				System.exit(-1);
			}

			System.out.print(_("Name creation (complete)..."));
			testcase = "N:ylimaf;tsrif;elddim;xiferp;xiffus";
			Name fullA = new Name(Tests.reversed(testcase));
			Name fullB = new Name(testcase);
			if (fullA.toString().equals(Tests.reversed(testcase)) && fullB.toString().equals(testcase)) {
				System.out.println(_("ok."));
			} else {
				System.err.println("failed: " + fullA + "/" + fullB);
				System.exit(-1);
			}

			Name[] names1 = { emptyA, famA, firstA, midA, prefA, sufA, fullA };
			Name[] names2 = { emptyB, famB, firstB, midB, prefB, sufB, fullB };

			System.out.print(_("Name isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Name a : names1) {
				comp++;
				if (!a.isEmpty()) {
					num++;
				}
				if (a == emptyA || a == sufA || a == prefA) {
					comp--;
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Name compare test..."));
			comp = 0;
			num = 0;
			for (Name a : names1) {
				num++;
				if (a.compareTo(fullB) != 0 && a.compareTo(fullB) == -fullB.compareTo(a)) {
					comp++;
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Name compatibility test 1 (self)..."));
			comp = 0;
			num = 0;
			for (Name a : names1) {
				for (Name b : names1) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						System.err.println(a + " <=> " + b);
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Name compatibility test 2 (other)..."));
			comp = 0;
			num = 0;
			for (Name a : names1) {
				for (Name b : names2) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("N:", "").replace(";", "");
						if (concat.equals("familyylimaf") || concat.equals("familyylimaftsrifelddimxiferpxiffus") || concat.equals("firsttsrif") || concat.equals("firstylimaftsrifelddimxiferpxiffus") || concat.equals("middleelddim") || concat.equals("middleylimaftsrifelddimxiferpxiffus") || concat.equals("prefixxiferp") || concat.equals("prefixylimaftsrifelddimxiferpxiffus") || concat.equals("suffixxiffus") || concat.equals("suffixylimaftsrifelddimxiferpxiffus") || concat.equals("familyfirstmiddleprefixsuffixylimaf") || concat.equals("familyfirstmiddleprefixsuffixtsrif") || concat.equals("familyfirstmiddleprefixsuffixelddim") || concat.equals("familyfirstmiddleprefixsuffixxiferp") || concat.equals("familyfirstmiddleprefixsuffixxiffus") || concat.equals("familyfirstmiddleprefixsuffixylimaftsrifelddimxiferpxiffus")) {
							comp++;
						} else {
							// System.err.println(concat);
							System.err.println(a + " <=> " + b);
						}
					}
				}

			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Name clone test..."));
			comp = 0;
			num = 0;
			for (Name a : names1) {
				num++;
				try {
					if (a.toString().equals(a.clone().toString())) {
						comp++;
					}
				} catch (CloneNotSupportedException e) {}
			}
			for (Name b : names2) {
				num++;
				try {
					if (b.toString().equals(b.clone().toString())) {
						comp++;
					}
				} catch (CloneNotSupportedException e) {}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Name merge test 1 (compatible)..."));
			comp = 0;
			num = 0;
			for (Name a : names1) {
				try {
					comp += 2;
					Name clone1 = (Name) a.clone();
					Name clone2 = (Name) fullA.clone();

					if (clone1.mergeWith(fullA) && clone1.toString().equals(fullA.toString())) num++;
					if (clone2.mergeWith(a) && clone2.toString().equals(fullA.toString())) num++;

				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Name merge test 2 (incompatible)..."));
			comp = 0;
			num = 0;
			for (Name b : names2) {
				try {
					comp += 2;
					Name clone1 = (Name) b.clone();
					Name clone2 = (Name) fullA.clone();

					if (!clone1.mergeWith(fullA)) {
						num++;
					} else if (b == emptyB) {
						num++;
					}
					if (!clone2.mergeWith(b)) {
						num++;
					} else if (b == emptyB) {
						num++;
					}

				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Name merge test 4 (incompatible,work)..."));
			comp = 0;
			num = 0;
			for (Name a : names1) {
				try {
					comp += 2;
					Name clone1 = (Name) a.clone();
					Name clone2 = (Name) fullB.clone();

					if (!clone1.mergeWith(fullB)) {
						num++;
					} else if (a == emptyA) {
						num++;
					}
					if (!clone2.mergeWith(a)) {
						num++;
					} else if (a == emptyA) {
						num++;
					}

				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}
			// continue tests here
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

	private String family;
	private String first;
	private String prefix;

	private String suffix;
	private String middle;
	// private boolean invalid=false;

	private InputField prefBox, firstBox, middleBox, familyBox, sufBox;

	private VerticalPanel form;

	public Name(String line) throws UnknownObjectException, InvalidFormatException {
		if (line == null || !line.startsWith("N:")) throw new InvalidFormatException(_("Name does not start with \"N:\": #", line));
		line = line.substring(2).trim();
		if (line.isEmpty()) return;
		if (line.contains(";")) {
			String[] parts = line.split(";");
			if (parts.length > 0) setFamily(parts[0].trim());
			if (parts.length > 1) setFirst(parts[1].trim());
			if (parts.length > 2) setMiddle(parts[2].trim());
			if (parts.length > 3) setPrefix(parts[3].trim());
			if (parts.length > 4) setSuffix(parts[4].trim());
			if (parts.length > 5) {
				System.err.println(_("Name with more than 5 parts found:"));
				System.err.println(line);
				for (String p : parts) {
					System.err.println(p);
				}
				throw new NotImplementedException();
			}
		} else
			family = line;

	}

	public String canonical() {
		TreeSet<String> parts = new TreeSet<String>();
		if (first != null) parts.add(ascii(first).toLowerCase());
		if (middle != null) parts.add(ascii(middle).toLowerCase());
		if (family != null) parts.add(ascii(family).toLowerCase());

		return parts.toString().replace("[", "").replace("]", ""); // sorted set of name parts
	}

	public void changedUpdate(DocumentEvent arg0) {
		update();
	}

	public int compareTo(Name o) {
		return canonical().compareTo(o.canonical());
	}

	public JPanel editForm(String title) {
		form = new VerticalPanel(title);

		form.add(prefBox = new InputField(_("Prefix"), prefix));
		prefBox.addChangeListener(this);

		form.add(firstBox = new InputField(_("First Name"), first));
		firstBox.addChangeListener(this);

		form.add(middleBox = new InputField(_("Middle Name"), middle));
		middleBox.addChangeListener(this);

		form.add(familyBox = new InputField(_("Family Name"), family));
		familyBox.addChangeListener(this);

		form.add(sufBox = new InputField(_("Suffix"), suffix));
		sufBox.addChangeListener(this);

		form.scale();
		return form;
	}

	public boolean equals(Name name) {
		if (first != null) {
			if (name.first == null) return false;
			if (!name.first.equals(first)) return false;
		} else if (name.first != null) return false;

		if (middle != null) {
			if (name.middle == null) return false;
			if (!name.middle.equals(middle)) return false;
		} else if (name.middle != null) return false;

		if (family != null) {
			if (name.family == null) return false;
			if (!name.family.equals(family)) return false;
		} else if (name.family != null) return false;

		if (prefix != null) {
			if (name.prefix == null) return false;
			if (!name.prefix.equals(prefix)) return false;
		} else if (name.prefix != null) return false;

		if (suffix != null) {
			if (name.suffix == null) return false;
			if (!name.suffix.equals(suffix)) return false;
		} else if (name.suffix != null) return false;

		return true;
	}

	public String prefix() {
		return prefix;
	}

	public String first() {
		return first;
	}

	public String middle() {
		return middle;
	}

	public String last() {
		return family;
	}

	public String suffix() {
		return suffix;
	}

	public String main() {
		return ((first == null) ? "" : first + " ") + ((middle == null) ? "" : middle + " ") + ((family == null) ? "" : family);
	}

	public String full() {
		return ((prefix == null) ? "" : prefix + " ") + main() + ((suffix == null) ? "" : " " + suffix);
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	@Override
	public boolean isCompatibleWith(Name otherName) {
		if (different(first, otherName.first)) return false;
		if (different(family, otherName.family)) return false;
		if (different(prefix, otherName.prefix)) return false;
		if (different(suffix, otherName.suffix)) return false;
		if (different(middle, otherName.middle)) return false;
		return true;
	}

	public boolean isEmpty() {
		return ((family == null) && (first == null) && (middle == null));
	}

	@Override
	public boolean mergeWith(Name otherName) {
		if (!isCompatibleWith(otherName)) return false;
		family = merge(family, otherName.family);
		first = merge(first, otherName.first);
		prefix = merge(prefix, otherName.prefix);
		suffix = merge(suffix, otherName.suffix);
		middle = merge(middle, otherName.middle);
		return true;
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();
	}

	public String title() {
		return prefix;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("N:");
		if (family != null) sb.append(family);
		sb.append(';');
		if (first != null) sb.append(first);
		sb.append(';');
		if (middle != null) sb.append(middle);
		sb.append(";");
		if (prefix != null) sb.append(prefix);
		sb.append(";");
		if (suffix != null) sb.append(suffix);
		return sb.toString();
	}

	private String ascii(String s) {
		return s.replace("Ä", "Ae").replace("ä", "ae").replace("Ö", "Oe").replace("ö", "oe").replace("Ü", "Ue").replace("ü", "ue").replace("ß", "ss").replace("é", "e");
	}

	private void setFamily(String string) {
		if (string.isEmpty()) return;
		family = string;
	}

	private void setFirst(String string) {
		if (string.isEmpty()) return;
		first = string;
	}

	private void setMiddle(String string) {
		if (string.isEmpty()) return;
		middle = string;
	}

	private void setPrefix(String string) {
		if (string.isEmpty()) return;
		prefix = string;
	}

	private void setSuffix(String string) {
		if (string.isEmpty()) return;
		suffix = string;
	}

	private void update() {
		prefix = prefBox.getText();
		first = firstBox.getText();
		middle = middleBox.getText();
		family = familyBox.getText();
		suffix = sufBox.getText();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(Color.green);
		}
	}

	protected Object clone() throws CloneNotSupportedException {
		try {
			return new Name(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
}
