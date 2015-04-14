import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Messenger extends Mergable<Messenger> implements ChangeListener, Comparable<Messenger> {
	public static void test() {
		try {
			System.out.print(_("Messenger creation test (null)..."));
			String testCase = null;
			try {
				Messenger nM = new Messenger(testCase);
				System.err.println(_("failed: #", nM));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok."));
			}

			System.out.print(_("Messenger creation test (empty)..."));
			testCase = "IMPP:";
			try {
				Messenger eM = new Messenger(testCase);
				System.err.println(_("failed: #", eM));
				System.exit(-1);
			} catch (UnknownObjectException e) {
				System.out.println(_("ok."));
			}

			System.out.print(_("Messenger creation test (empty ICQ)..."));
			testCase = "IMPP:icq:";
			Messenger eICQ = new Messenger(testCase);
			if (eICQ.toString().equals(testCase.toUpperCase()+"\nX-ICQ:") && !eICQ.isInvalid()) {
				System.out.println(_("ok."));				
			} else {
				System.err.println(_("failed: #", eICQ));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (empty Skype)..."));
			testCase = "IMPP:skype:";
			Messenger eSkype = new Messenger(testCase);
			if (eSkype.toString().equals(testCase.toUpperCase()+"\nX-SKYPE:") && !eSkype.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", eSkype));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (empty MSN)..."));
			testCase = "IMPP:msn:";
			Messenger eMSN = new Messenger(testCase);
			if (eMSN.toString().equals(testCase.toUpperCase()+"\nX-MSN:") && !eMSN.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", eMSN));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (empty AIM)..."));
			testCase = "IMPP:aim:";
			Messenger eAIM = new Messenger(testCase);
			if (eAIM.toString().equals(testCase.toUpperCase()+"\nX-AIM:") && !eAIM.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", eAIM));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (empty Facebook)..."));
			testCase = "IMPP:facebook:";
			Messenger eFB = new Messenger(testCase);
			if (eFB.toString().equals(testCase.toUpperCase()+"\nX-FACEBOOK:") && !eFB.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", eFB));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (ICQ)..."));
			testCase = "IMPP:icq:123456";
			Messenger icq = new Messenger(testCase);
			if (icq.toString().equals(testCase.toUpperCase()+"\nX-ICQ:123456") && !icq.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", icq));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (Skype)..."));
			testCase = "IMPP:skype:test";
			Messenger skype = new Messenger(testCase);
			if (skype.toString().equals("IMPP:SKYPE:test\nX-SKYPE:test") && !skype.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", skype));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (MSN)..."));
			testCase = "IMPP:msn:test@example.com";
			Messenger msn = new Messenger(testCase);
			if (msn.toString().equals("IMPP:MSN:test@example.com\nX-MSN:test@example.com") && !msn.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", msn));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (AIM)..."));
			testCase = "IMPP:aim:test@example.com";
			Messenger aim = new Messenger(testCase);
			if (aim.toString().equals("IMPP:AIM:test@example.com\nX-AIM:test@example.com") && !aim.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", aim));
				System.exit(-1);
			}

			System.out.print(_("Messenger creation test (Facebook)..."));
			testCase = "IMPP:facebook:toast@example.com";
			Messenger fb = new Messenger(testCase);
			if (fb.toString().equals("IMPP:FACEBOOK:toast@example.com\nX-FACEBOOK:toast@example.com") && !fb.isInvalid()) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #", fb));
				System.exit(-1);
			}

			Messenger[] messengers = { eICQ, eSkype, eMSN, eAIM, eFB, icq, skype, msn, aim, fb };

			System.out.print(_("Messenger isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Messenger m : messengers) {
				comp++;
				if (!m.isEmpty()) {
					num++;
				}
				if (m == eICQ || m == eSkype || m == eMSN || m == eAIM || m == eFB) {
					comp--;
				}
			}
			if (num == comp) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Messenger compare test..."));
			comp = 0;
			num = 0;
			for (Messenger m : messengers) {
				comp++;
				if (m.compareTo(icq) != 0 && m.compareTo(icq) == -icq.compareTo(m)) {
					num++;
				} else {
					if (icq == m) {
						num++;
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Messenger compatibility test..."));
			comp = 0;
			num = 0;
			for (Messenger a : messengers) {
				for (Messenger b : messengers) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("IMPP:", "").replace("ICQ", "").replace("SKYPE", "").replace("MSN", "").replace("AIM", "").replace("FACEBOOK", "").replaceFirst(":", "").replace("X-:", "").replace("\n", "").replace("123456123456", "123456").replace("testtest","test").replace("test@example.comtest@example.com", "test@example.com").replace("toast@example.comtoast@example.com", "toast@example.com");
						if (concat.equals("123456:test") || concat.equals("123456:test@example.com") || concat.equals("123456:toast@example.com") || concat.equals("test:123456") || concat.equals("test:test@example.com") || concat.equals("test:toast@example.com") || concat.equals("test@example.com:123456") || concat.equals("test@example.com:test") || concat.equals("test@example.com:toast@example.com") || concat.equals("toast@example.com:123456") || concat.equals("toast@example.com:test") || concat.equals("toast@example.com:test@example.com")) {
							comp++;
						} else {
							System.err.println(concat);
							// System.err.println(a + " <=> " + b);
						}
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { comp, num }));
				System.exit(-1);
			}

			System.out.print(_("Messenger clone test..."));
			comp = 0;
			num = 0;
			for (Messenger m : messengers) {
				comp++;
				try {
					if (m.toString().equals(m.clone().toString())) {
						num++;
					}
				} catch (CloneNotSupportedException e) {}
			}
			if (comp == num) {
				System.out.println(_("ok."));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Messenger merge test..."));
			comp = 0;
			num = 0;
			for (Messenger m : messengers) {
				try {
					comp += 2;
					Messenger clone1 = (Messenger) m.clone();
					Messenger clone2 = (Messenger) icq.clone();

					if (clone1.mergeWith(icq) && clone1.nick.equals(icq.nick) && clone1.categories.containsAll(m.categories) && clone1.categories.containsAll(icq.categories)) num++;
					if (clone2.mergeWith(m) && clone2.nick.equals(icq.nick) && clone2.categories.containsAll(m.categories) && clone2.categories.containsAll(icq.categories)) num++;
					if (comp > num) {
						if ((m.nick != null && !m.nick.isEmpty()) && (icq.nick != null && !icq.nick.isEmpty()) && !m.nick.equals(icq.nick)) {
							num += 2;
						}
					}
					if (comp > num) {
						System.out.println();
						System.out.println(comp + " : " + num);
						System.out.println("fb: <" + icq + ">");
						System.out.println(" b: <" + m + ">");
						System.out.println("merged:");
						System.out.println("fb: <" + clone2 + ">");
						System.out.println(" b: <" + clone1 + ">");
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
			/**/
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();

		}

	}

	public static enum Category {
		AIM {
			@Override
			public String toString() {
				return "AIM";
			}
		},
		ICQ {
			@Override
			public String toString() {
				return "ICQ";
			}
		},
		MSN {
			@Override
			public String toString() {
				return "MSN";
			}
		},
		SIP {
			@Override
			public String toString() {
				return "SIP";
			}
		},
		SKYPE {
			@Override
			public String toString() {
				return "SKYPE";
			}
		},
		FACEBOOK {
			@Override
			public String toString() {
				return "FACEBOOK";
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

	private String nick = null;
	private TreeSet<Category> categories = new TreeSet<Category>();
	private JCheckBox aimBox;
	private JCheckBox icqBox;
	private JCheckBox msnBox;
	private JCheckBox sipBox;
	private JCheckBox skypeBox;
	private JCheckBox facebookBox;
	private InputField nickField;

	private VerticalPanel form;

	public Messenger(String content) throws InvalidFormatException, UnknownObjectException {
		if (content == null || !content.startsWith("IMPP:")) throw new InvalidFormatException(_("Messenger adress does not start with \"IMPP:\": #", content));
		String line = content.substring(5);
		while (!line.startsWith(":")) {
			String upper = line.toUpperCase();
			if (upper.startsWith("ICQ")) {
				categories.add(Category.ICQ);
				line = line.substring(3);
				continue;
			}
			if (upper.startsWith("AIM")) {
				categories.add(Category.AIM);
				line = line.substring(3);
				continue;
			}
			if (upper.startsWith("SKYPE")) {
				categories.add(Category.SKYPE);
				line = line.substring(5);
				continue;
			}
			if (upper.startsWith("MSN")) {
				categories.add(Category.MSN);
				line = line.substring(3);
				continue;
			}
			if (upper.startsWith("SIP")) {
				categories.add(Category.SIP);
				line = line.substring(3);
				continue;
			}
			if (upper.startsWith("FACEBOOK")) {
				categories.add(Category.FACEBOOK);
				line = line.substring(8);
				continue;
			}
			throw new UnknownObjectException("\"" + line + "\" in " + content);
		}
		readAddr(line.substring(1));
	}

	public int compareTo(Messenger otherMessenger) {
		return this.toString().compareTo(otherMessenger.toString());
	}

	public VerticalPanel editForm() {
		form = new VerticalPanel(_("Messenger"));
		if (isInvalid()) form.setBackground(Color.orange);
		form.add(nickField = new InputField(_("Nickname"), nick));
		nickField.addEditListener(this);
		form.add(aimBox = new JCheckBox(_("AIM"), categories.contains(Category.AIM)));
		aimBox.addChangeListener(this);
		form.add(icqBox = new JCheckBox(_("ICQ"), categories.contains(Category.ICQ)));
		icqBox.addChangeListener(this);
		form.add(skypeBox = new JCheckBox(_("Skype"), categories.contains(Category.SKYPE)));
		skypeBox.addChangeListener(this);
		form.add(msnBox = new JCheckBox(_("MSN"), categories.contains(Category.MSN)));
		msnBox.addChangeListener(this);
		form.add(sipBox = new JCheckBox(_("SIP"), categories.contains(Category.SIP)));
		sipBox.addChangeListener(this);
		form.add(facebookBox = new JCheckBox(_("Facebook"), categories.contains(Category.FACEBOOK)));
		facebookBox.addChangeListener(this);
		form.scale();
		return form;
	}

	public String id() throws UnknownObjectException {
		if (categories.isEmpty()) throw new UnknownObjectException(_("Messenger \"#\" has no known type!", nick));
		String result = categories.toString().replace("[", "").replace("]", ":").replace(",", ":");
		if (nick == null || nick.isEmpty()) return result;
		return result + nick;

	}

	@Override
	public boolean isCompatibleWith(Messenger other) {
		if (different(nick, other.nick)) return false;
		return true;
	}

	public boolean isEmpty() {
		return nick == null || nick.isEmpty();
	}

	public boolean isInvalid() {
		return categories.isEmpty();
	}

	@Override
	public boolean mergeWith(Messenger other) {
		if (!isCompatibleWith(other)) return false;
		nick = merge(nick, other.nick);
		categories.addAll(other.categories);
		return true;
	}

	public String nick() {
		return nick;
	}

	public void stateChanged(ChangeEvent ce) {
		Object source = ce.getSource();
		if (source == nickField) {
			nick = nickField.getText().trim();
		}
		if (aimBox.isSelected()) {
			categories.add(Category.AIM);
		} else {
			categories.remove(Category.AIM);
		}
		if (icqBox.isSelected()) {
			categories.add(Category.ICQ);
		} else {
			categories.remove(Category.ICQ);
		}
		if (skypeBox.isSelected()) {
			categories.add(Category.SKYPE);
		} else {
			categories.remove(Category.SKYPE);
		}
		if (msnBox.isSelected()) {
			categories.add(Category.MSN);
		} else {
			categories.remove(Category.MSN);
		}
		if (sipBox.isSelected()) {
			categories.add(Category.SIP);
		} else {
			categories.remove(Category.SIP);
		}
		if (facebookBox.isSelected()) {
			categories.add(Category.FACEBOOK);
		} else {
			categories.remove(Category.FACEBOOK);
		}
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(isInvalid() ? Color.orange : UIManager.getColor ( "Panel.background" ));
		}
	}

	public String toString() {
		try {
			return "IMPP:" + id() + "\nX-" + id();
		} catch (UnknownObjectException e) {
			e.printStackTrace();
			return "null";
		}
	}

	private void readAddr(String line) {
		while (line.startsWith(":"))
			line = line.substring(1);
		if (line.trim().isEmpty()) return;
		nick = line.toLowerCase();
	}

	protected Messenger clone() throws CloneNotSupportedException {
		try {
			return new Messenger("IMPP:"+this.id());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	/**
	 * @param cloneCategories if set to false, categories of the ancestor will not be copied to the new messenger
	 * @return a clone of thins email
	 * @throws CloneNotSupportedException
	 */
	public Messenger clone(boolean cloneCategories) throws CloneNotSupportedException {
		Messenger result = clone();
		result.categories.clear();
		return result;
	}

	public boolean belongsTo(String messengerType) {
		return categories.contains(messengerType);
	}

	public void addCategory(Category category) {
		categories.add(category);
	}
	public void removeCategory(Category category) {
		categories.remove(category);
	}

	public TreeSet<Category> categories() {
		return categories;
	}
}
