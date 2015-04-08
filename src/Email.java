import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Email extends Mergable<Email> implements DocumentListener, ChangeListener, Comparable<Email> {
	public static void test() {
		try {
			System.out.print(_("Email creation test (null)..."));
			String testCase = null;
			try {
				Email nM = new Email(testCase);
				System.err.println(_("failed: #", nM));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok"));
			}

			System.out.print(_("Email creation test (empty)..."));
			testCase = "EMAIL:";
			Email eM = new Email(testCase);
			if (eM.toString().equals(testCase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", eM));
				System.exit(-1);
			}

			System.out.print(_("Email creation test (valid)..."));
			testCase = "EMAIL:test.test-24+a@test.example.com";
			Email vM = new Email(testCase);
			if (vM.toString().equals(testCase) && !vM.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", vM));
				System.exit(-1);
			}

			System.out.print(_("Email creation test (invalid)..."));
			testCase = "EMAIL:steinlaus";
			Email iM = new Email(testCase);
			if (iM.toString().equals(testCase) && iM.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", iM));
				System.exit(-1);
			}

			System.out.print(_("Email creation test (valid work)..."));
			testCase = "EMAIL;TYPE=WORK:work@example.com";
			Email workM = new Email(testCase);
			if (workM.toString().equals(testCase) && !workM.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", workM));
				System.exit(-1);
			}

			System.out.print(_("Email creation test (valid home)..."));
			testCase = "EMAIL;TYPE=HOME:home@example.com";
			Email homeM = new Email(testCase);
			if (homeM.toString().equals(testCase) && !homeM.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", homeM));
				System.exit(-1);
			}

			System.out.print(_("Email creation test (valid internet)..."));
			testCase = "EMAIL;TYPE=INTERNET:net@example.com";
			Email netM = new Email(testCase);
			if (netM.toString().equals(testCase) && !netM.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", netM));
				System.exit(-1);
			}

			Email[] mails = { eM, vM, iM, workM, homeM, netM };

			System.out.print(_("Email isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Email m : mails) {
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

			System.out.print(_("Email compare test..."));
			comp = 0;
			num = 0;
			for (Email m : mails) {
				comp++;
				if (m.compareTo(netM) != 0 && m.compareTo(netM) == -netM.compareTo(m)) {
					num++;
				} else {
					if (netM == m) {
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

			System.out.print(_("Email compatibility test..."));
			comp = 0;
			num = 0;
			for (Email a : mails) {
				for (Email b : mails) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("EMAIL", "").replace(";TYPE=INTERNET", "").replace(";TYPE=WORK", "").replace(";TYPE=HOME", "").replaceFirst(":", "");
						if (concat.equals("net@example.com:test.test-24+a@test.example.com") || concat.equals("net@example.com:steinlaus") || concat.equals("net@example.com:work@example.com") || concat.equals("net@example.com:home@example.com") || concat.equals("steinlaus:home@example.com") || concat.equals("steinlaus:net@example.com") || concat.equals("steinlaus:test.test-24+a@test.example.com") || concat.equals("steinlaus:work@example.com") || concat.equals("test.test-24+a@test.example.com:home@example.com") || concat.equals("test.test-24+a@test.example.com:net@example.com") || concat.equals("test.test-24+a@test.example.com:steinlaus") || concat.equals("test.test-24+a@test.example.com:work@example.com") || concat.equals("work@example.com:home@example.com") || concat.equals("work@example.com:net@example.com") || concat.equals("work@example.com:steinlaus") || concat.equals("work@example.com:test.test-24+a@test.example.com") || concat.equals("home@example.com:steinlaus") || concat.equals("home@example.com:net@example.com") || concat.equals("home@example.com:test.test-24+a@test.example.com") || concat.equals("home@example.com:work@example.com")) {
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
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Email clone test..."));
			comp = 0;
			num = 0;
			for (Email m : mails) {
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

			System.out.print(_("Email merge test..."));
			comp = 0;
			num = 0;
			for (Email m : mails) {
				try {
					comp += 2;
					Email clone1 = (Email) m.clone();
					Email clone2 = (Email) netM.clone();

					if (clone1.mergeWith(netM) && clone1.toString().equals(netM.toString())) num++;
					if (clone2.mergeWith(m) && clone2.toString().equals(netM.toString())) num++;
					if (comp > num) {
						if ((m.adress != null && !m.adress.isEmpty()) && (netM.adress != null && !netM.adress.isEmpty()) && !m.adress.equals(netM.adress)) {
							num += 2;
						}
					}
					if (comp > num) {
						System.out.println();
						System.out.println("fb: " + netM);
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
		HOME {
			@Override
			public String toString() {
				return "Home";
			}
		},
		WORK {
			@Override
			public String toString() {
				return "Work";
			}
		},
		MOBILE {
			@Override
			public String toString() {
				return "Mobile";
			}
		},
		INTERNET {
			@Override
			public String toString() {
				return "Internet";
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

	private TreeSet<Category> categories = new TreeSet<Category>();
	private String adress = null;

	private boolean invalid = false;
	private InputField adressBox;
	private JCheckBox homeBox;
	private JCheckBox workBox;
	private JCheckBox mobileBox;
	private JCheckBox internetBox;
	private VerticalPanel form;

	Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");

	public Email(String content) throws UnknownObjectException, InvalidFormatException {
		if (content == null || !content.startsWith("EMAIL")) throw new InvalidFormatException("Mail adress does not start with \"EMAIL\"");
		String line = content.substring(5);
		while (!line.startsWith(":")) {
			String upper = line.toUpperCase();
			if (upper.startsWith("TYPE=WORK")) {
				categories.add(Category.WORK);
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("TYPE=HOME")) {
				categories.add(Category.HOME);
				line = line.substring(9);
				continue;
			}
			if (upper.startsWith("TYPE=INTERNET")) {
				categories.add(Category.INTERNET);
				line = line.substring(13);
				continue;
			}
			if (upper.startsWith("TYPE=X-INTERNET")) {
				categories.add(Category.INTERNET);
				line = line.substring(15);
				continue;
			}
			if (upper.startsWith("TYPE=MOBILE")) {
				categories.add(Category.MOBILE);
				line = line.substring(11);
				continue;
			}
			if (upper.startsWith("TYPE=X-MOBILE")) {
				categories.add(Category.MOBILE);
				line = line.substring(13);
				continue;
			}

			if (line.startsWith(";")) {
				line = line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line + " in " + content);
		}
		readAddr(line.substring(1));
	}

	public String address() {
		return adress;
	}

	public TreeSet<Category> categories() {
		return new TreeSet<Email.Category>(categories);
	}

	public void changedUpdate(DocumentEvent e) {
		update();
	}

	public int compareTo(Email otherEmail) {
		return this.toString().compareTo(otherEmail.toString());
	}

	public VerticalPanel editForm() {
		form = new VerticalPanel(_("Email"));

		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);

		form.add(adressBox = new InputField(_("Adress"), adress));
		adressBox.addChangeListener(this);

		form.add(homeBox = new JCheckBox(_("Home"), isHomeMail()));
		homeBox.addChangeListener(this);
		form.add(workBox = new JCheckBox(_("Work"), isWorkMail()));
		workBox.addChangeListener(this);
		form.add(mobileBox = new JCheckBox(_("Mobile"), isMobileMail()));
		mobileBox.addChangeListener(this);
		form.add(internetBox = new JCheckBox(_("Internet"), isInternetMail()));
		internetBox.addChangeListener(this);
		form.scale();
		return form;
	}

	public void insertUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public boolean isCompatibleWith(Email other) {
		if (different(adress, other.adress)) return false;
		return true;
	}

	public boolean isEmpty() {
		return adress == null || adress.trim().isEmpty();
	}

	private boolean isMobileMail() {
		return categories.contains(Category.MOBILE);
	}

	public boolean isHomeMail() {
		return categories.contains(Category.HOME);
	}

	public boolean isInternetMail() {
		return categories.contains(Category.INTERNET);
	}

	public boolean isWorkMail() {
		return categories.contains(Category.WORK);
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void merge(Email mail) throws InvalidAssignmentException {
		if (!adress.equals(mail.adress)) throw new InvalidAssignmentException(_("Trying to merge two mails with different adresses!"));
		categories.addAll(mail.categories);
	}

	@Override
	public boolean mergeWith(Email other) {
		if (!isCompatibleWith(other)) return false;
		adress = merge(adress, other.adress);
		categories.addAll(other.categories);
		return true;
	}

	public void removeUpdate(DocumentEvent e) {
		update();
	}

	/*
	 * public void setHome() { work=false; home=true; internet=false; }
	 * 
	 * public void setInternet(){ work=false; home=false; internet=true; }
	 * 
	 * public void setWork() { work=true; home=false; internet=false; }
	 */

	public void stateChanged(ChangeEvent arg0) {
		update();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("EMAIL");
		if (categories.contains(Category.HOME)) sb.append(";TYPE=HOME");
		if (categories.contains(Category.WORK)) sb.append(";TYPE=WORK");
		if (categories.contains(Category.MOBILE)) sb.append(";TYPE=MOBILE");
		if (categories.contains(Category.INTERNET)) sb.append(";TYPE=INTERNET");
		sb.append(":");
		if (adress != null) sb.append(adress);
		return sb.toString();
	}

	private void checkValidity() {
		invalid = false;
		if (adress != null) {
			invalid = !ptr.matcher(adress).matches();
		}
	}

	private void readAddr(String line) {
		line = line.trim();
		if (line.isEmpty()) {
			adress = null;
		} else {
			adress = line.toLowerCase();
		}
		checkValidity();
	}

	private void update() {
		readAddr(adressBox.getText());

		if (homeBox.isSelected()) {
			categories.add(Category.HOME);
		}
		if (workBox.isSelected()) {
			categories.add(Category.WORK);
		}
		if (mobileBox.isSelected()) {
			categories.add(Category.MOBILE);
		}
		if (internetBox.isSelected()) {
			categories.add(Category.INTERNET);
		}
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid ? Color.red : Color.green);
		}
	}

	protected Email clone() throws CloneNotSupportedException {
		try {
			return new Email(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	/**
	 * @param cloneCategories if set to false, categories of the ancestor will not be copied to the new email
	 * @return a clone of thins email
	 * @throws CloneNotSupportedException
	 */
	public Email clone(boolean cloneCategories) throws CloneNotSupportedException {
		Email result = clone();
		result.categories.clear();
		return result;
	}

	public boolean is(Category c) {
		return categories.contains(c);
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public void removeCategory(Category category) {
		categories.remove(category);
	}

}
