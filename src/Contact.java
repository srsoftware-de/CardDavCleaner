import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.activation.UnknownObjectException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Contact extends Mergable<Contact> implements ActionListener, DocumentListener, ChangeListener, Comparable<Contact> {
	
	public static void test() {
		try {
			System.out.print(_("Contact creation test (null)..."));
			String testCase = null;
			try {
				Contact nullC = new Contact(testCase);
				System.err.println(_("failed: #", nullC));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok"));
			}

			System.out.print(_("Contact creation test (empty)..."));
			testCase = "";
			try {
				Contact emptyC = new Contact(testCase);
				System.err.println(_("failed: #", emptyC));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok"));
			}
			
			System.out.print(_("Contact parser test (linebreak without carriage return)..."));
			testCase = "This text\ncontains no\ncarriage returns";
			
			StringWriter writer = new StringWriter();
			StringReader reader = new StringReader(testCase);
			readVCardLines(reader, writer);
			if (writer.toString().equals("This text\r\ncontains no\r\ncarriage returns")) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", _("No carriage returns added after newlines!")));
				System.exit(-1);
			}
			
			System.out.print(_("Contact parser test (folded lines)..."));
			testCase = "Short line\n\rThis is a rather\r\n  long line with\r\n  two breaks.\r\nSecond short line";
			
			writer = new StringWriter();
			reader = new StringReader(testCase);
			Vector<String> lines = readVCardLines(reader, writer);
			
			if (lines.get(0).equals("Short line") && lines.get(1).equals("This is a rather long line with two breaks.") && lines.get(2).equals("Second short line")) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", _("Foldes lines not parsed appropriately!")));
				System.exit(-1);
			}
			
			System.out.print(_("Contact parser test (folded lines with missing returns)..."));
			testCase = "Short line\nThis is a rather\n  long line with\n  two breaks.\nSecond short line";
			
			writer = new StringWriter();
			reader = new StringReader(testCase);
			lines = readVCardLines(reader, writer);
			
			if (lines.get(0).equals("Short line") && lines.get(1).equals("This is a rather long line with two breaks.") && lines.get(2).equals("Second short line")) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", _("Foldes lines not parsed appropriately!")));
				System.exit(-1);
			}
			
			System.out.print(_("Contact creation test (name only)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nEND:VCARD\n";
			Contact name = new Contact(testCase);
			if (name.toString(true).equals(testCase) && !name.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", name.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + formatted name)..."));
			testCase = "BEGIN:VCARD\nFN:Testcard\nN:Test;Kontakt;;;\nEND:VCARD\n";
			Contact formatted = new Contact(testCase);
			if (formatted.toString(true).equals(testCase) && !formatted.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", formatted.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + number)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nTEL;TYPE=WORK:0123456789\nEND:VCARD\n";
			Contact number = new Contact(testCase);
			if (number.toString(true).equals(testCase) && !number.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", number.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + nick)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nNICKNAME;TYPE=HOME:Egon\nEND:VCARD\n";
			Contact nick = new Contact(testCase);
			if (nick.toString(true).equals(testCase) && !nick.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", nick.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + email)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nEMAIL;TYPE=INTERNET:test@example.com\nEND:VCARD\n";
			Contact mail = new Contact(testCase);
			if (mail.toString(true).equals(testCase) && !mail.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", mail.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + messenger)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nIMPP:icq:123456\nEND:VCARD\n";
			Contact messenger = new Contact(testCase);
			if (messenger.toString(true).equals("BEGIN:VCARD\nN:Test;Contact;;;\nIMPP:ICQ:123456\nX-ICQ:123456\nEND:VCARD\n") && !messenger.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", messenger.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + note)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nNOTE:this is a note\nEND:VCARD\n";
			Contact note = new Contact(testCase);
			if (note.toString(true).equals(testCase) && !note.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", note.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + label)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nLABEL:a label\nEND:VCARD\n";
			Contact label = new Contact(testCase);
			if (label.toString(true).equals(testCase) && !label.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", label.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + birthday [year only])..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nBDAY:1992\nEND:VCARD\n";
			Contact bdayyear = new Contact(testCase);
			if (bdayyear.toString(true).equals(testCase) && !bdayyear.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", bdayyear.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + birthday [month only])..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nBDAY:--04\nEND:VCARD\n";
			Contact bdaymonth = new Contact(testCase);
			if (bdaymonth.toString(true).equals(testCase) && !bdaymonth.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", bdaymonth.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + birthday [day only])..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nBDAY:---17\nEND:VCARD\n";
			Contact bdayday = new Contact(testCase);
			if (bdayday.toString(true).equals(testCase) && !bdayday.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", bdayday.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + birthday [time only])..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nBDAY;VALUE=DATE-TIME:T123456\nEND:VCARD\n";
			Contact bdaytime = new Contact(testCase);
			if (bdaytime.toString(true).equals(testCase) && !bdaytime.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", bdaytime.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + birthday [full])..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nBDAY;VALUE=DATE-TIME:19910417T123456\nEND:VCARD\n";
			Contact bday = new Contact(testCase);
			if (bday.toString(true).equals(testCase) && !bday.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", bday.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + role)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nROLE:contact for testing\nEND:VCARD\n";
			Contact role = new Contact(testCase);
			if (role.toString(true).equals(testCase) && !role.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", role.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + url)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nURL:www.srsoftware.de\nEND:VCARD\n";
			Contact url = new Contact(testCase);
			if (url.toString(true).equals(testCase) && !url.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", url.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + organization)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nORG:SRSoftware GbR;Gera;Thüringen\nEND:VCARD\n";
			Contact org = new Contact(testCase);
			if (org.toString(true).equals(testCase) && !org.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", org.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + title)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nTITLE:Prof. Dr. rer. nat.\nEND:VCARD\n";
			Contact title = new Contact(testCase);
			if (title.toString(true).equals(testCase) && !title.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", title.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + categories)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nCATEGORIES:Family,Work\nEND:VCARD\n";
			Contact categories = new Contact(testCase);
			if (categories.toString(true).equals(testCase) && !categories.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", categories.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (name + adress)..."));
			testCase = "BEGIN:VCARD\nN:Test;Contact;;;\nADR;TYPE=HOME:postbox;extended;street;city;region;zip;country\nEND:VCARD\n";
			Contact adress = new Contact(testCase);
			if (adress.toString(true).equals(testCase) && !adress.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", adress.toString(true)));
				System.exit(-1);
			}

			System.out.print(_("Contact creation test (full)..."));
			testCase = "BEGIN:VCARD\nFN:Testcard\nN:Test;Contact;;;\nNICKNAME;TYPE=HOME:0perat0r\nCATEGORIES:Family,Work\nTITLE:Prof. Dr. rer. nat.\nORG:SRSoftware GbR;\nIMPP:icq:123456\nROLE:contact for testing\nBDAY;VALUE=DATE-TIME:19910417T123456\nLABEL:a label\nADR;TYPE=HOME:postbox;extended;street;city;region;zip;country\nTEL;TYPE=WORK:9876543210\nEMAIL;TYPE=INTERNET:test@example.com\nURL:www.srsoftware.de\nNOTE:this is a note\nEND:VCARD\n";
			Contact full = new Contact(testCase);
			if (full.toString(true).equals("BEGIN:VCARD\nFN:Testcard\nN:Test;Contact;;;\nNICKNAME;TYPE=HOME:0perat0r\nCATEGORIES:Family,Work\nTITLE:Prof. Dr. rer. nat.\nORG:SRSoftware GbR;\nIMPP:ICQ:123456\nX-ICQ:123456\nROLE:contact for testing\nBDAY;VALUE=DATE-TIME:19910417T123456\nLABEL:a label\nADR;TYPE=HOME:postbox;extended;street;city;region;zip;country\nTEL;TYPE=WORK:9876543210\nEMAIL;TYPE=INTERNET:test@example.com\nURL:www.srsoftware.de\nNOTE:this is a note\nEND:VCARD\n") && !full.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", full.toString(true)));
				System.exit(-1);
			}

			Contact[] contacts = { name, formatted, number, nick, mail, messenger, note, label, bdayyear, bdaymonth, bdayday, bdaytime, bday, role, url, org, title, categories, adress, full };

			System.out.print(_("Contact isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Contact c : contacts) {
				comp++;
				if (!c.isEmpty()) {
					num++;
				}
				if (c == name || c == formatted || c == label) {
					comp--;
				}
			}
			if (num == comp) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Contact compare test..."));
			comp = 0;
			num = 0;
			for (Contact c : contacts) {
				comp++;
				if (c.compareTo(full) != 0 && c.compareTo(full) == -full.compareTo(c)) {
					num++;
				}
				if (full == c) {
					num++;
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Contact compatibility test..."));
			comp = 0;
			num = 0;
			for (Contact a : contacts) {
				for (Contact b : contacts) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						if ((a == formatted && b != formatted) || (a != formatted && b == formatted)) {
							num--;
						} else if ((a == bdayyear && b != bdayyear) || (a != bdayyear && b == bdayyear)) {
							num--;
						} else if ((a == number && b == full)||(b == number && a == full)){
							num--;
						} else if ((a == nick && b == full)||(b == nick && a == full)){
							num--;
						} else if ((a == org && b == full)||(b == org && a == full)){
							num--;
						}

						if (comp != num) {
							System.err.println(a + " <=> " + b);
							System.exit(-1);
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

			System.out.print(_("Contact clone test..."));
			comp = 0;
			num = 0;
			for (Contact c : contacts) {
				comp++;
				if (c.toString().equals(c.clone().toString())) num++;
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}

			System.out.print(_("Contact merge test..."));
			comp = 0;
			num = 0;
			for (Contact c : contacts) {

				Contact clone1 = (Contact) c.clone();
				Contact clone2 = (Contact) full.clone();

				if (c.isCompatibleWith(full)) {
					comp += 2;

					if (c == number || c == nick || c == org) num += 2;
					clone1.mergeWith(full);
					clone2.mergeWith(c);

					if (clone1.toString().equals(full.toString())) num++;
					if (clone2.toString().equals(full.toString())) num++;

				}
				if (comp != num) {
					String s1 = clone1.toString();
					String s2 = full.toString();
					int p = 0;
					while (s1.charAt(p) == s2.charAt(p)) {
						System.out.print(s1.charAt(p));
						p++;
					}
					System.out.println("\nDiff:");
					System.out.println(s1.substring(p));
					System.out.println(s2.substring(p));
					System.exit(-1);
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed", new Object[] { num, comp }));
				System.exit(-1);
			}/**/
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

	private static String _(Object text) {
		return Translations.get(text.toString());
	}

	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}

	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd#HHmmss");

	private Name name;
	private String formattedName; // TODO: eine vcard kann auch mehrere haben!
	private Birthday birthday;
	private String uid;
	private String vcfName;
	private String anniversary;
	private boolean htmlMail;
	private boolean rewrite = false;
	private TreeSet<String> titles = new TreeSet<String>();
	private TreeSet<String> roles = new TreeSet<String>();
	private TreeSet<String> notes = new TreeSet<String>();
	private TreeSet<String> photos = new TreeSet<String>();
	private TreeSet<String> categories = new TreeSet<String>();
	private TreeMap<Integer, String> customContent = new TreeMap<Integer, String>();
	private MergableList<Label> labels = new MergableList<Label>();
	private MergableList<Phone> phones = new MergableList<Phone>();
	private MergableList<Adress> adresses = new MergableList<Adress>();
	private MergableList<Email> mails = new MergableList<Email>();
	private MergableList<Url> urls = new MergableList<Url>();
	private MergableList<Organization> orgs = new MergableList<Organization>();
	private MergableList<Messenger> messengers = new MergableList<Messenger>();
	private MergableList<Nickname> nicks = new MergableList<Nickname>();
	private TreeSet<String> ignoredLines = new TreeSet<String>();
	
	private Contact clonedContact;

	/* form elements */
	private JScrollPane scroll;
	private InputField formattedField, anniversaryField;
	private VerticalPanel form, titleForm, nickForm, roleForm, customForm;
	private JButton newMailButton, newPhoneButton, newTitleButton, newNickButton, newRoleButton, newCustomButton;
	private HorizontalPanel mailForm, phoneForm, adressForm;
	private JButton birthdayButton;
	private JButton newAdressButton;

	private VerticalPanel urlForm;
	private JButton newUrlButton;
	private HorizontalPanel orgForm;
	private JButton newOrgButton;
	private HorizontalPanel messengerForm;
	private JButton newMessengerButton;
	private VerticalPanel categoryForm;
	private JButton newCategoryButton;
	private Vector<CustomField> titleFields, customFields;
	private Vector<CategoryField> categoryFields;
	private Vector<RoleField> roleFields;
	private Vector<NoteField> noteFields;
	private VerticalPanel noteForm;
	private JButton newNoteButton;
	private VerticalPanel nameForm;
	private HorizontalPanel outerForm;
	private JButton cloneButton;
	private JButton resetCloneButton;
	private HorizontalPanel clonePanel;

	public Contact(String data) throws UnknownObjectException, InvalidFormatException {
		parse(data);
	}

	public Contact(String directory, String name, File backupPath) throws UnknownObjectException, IOException, InvalidFormatException {
		vcfName = name;
		readFromUrl(new URL(directory + name), backupPath);
	}

	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source == newTitleButton) {
			CustomField titleField = new CustomField(_("Title"));
			titleField.addEditListener(this);
			titleFields.add(titleField);
			titleForm.insertCompoundBefore(newTitleButton, titleField);
			rescale();
		}

		if (source == newNickButton) {
			try {
				Nickname newNick = new Nickname("NICKNAME:");
				VerticalPanel newNickForm = newNick.editForm();
				nickForm.insertCompoundBefore(newNickButton, newNickForm);
				nicks.add(newNick);
				rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newRoleButton) {
			RoleField roleField = new RoleField(_("Role"));
			roleField.addEditListener(this);
			roleFields.add(roleField);
			roleForm.insertCompoundBefore(newRoleButton, roleField);
			rescale();
		}
		if (source == birthdayButton) {
			try {
				birthday = new Birthday(":" + Calendar.getInstance().get(Calendar.YEAR));
				form.replace(birthdayButton, birthday.editForm());
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newPhoneButton) {
			try {
				Phone newPhone = new Phone("TEL:000");
				VerticalPanel newPhoneForm = newPhone.editForm();
				phoneForm.insertCompoundBefore(newPhoneButton, newPhoneForm);
				phones.add(newPhone);
				rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newAdressButton) {
			try {
				Adress newAdress = new Adress("ADR;:");
				VerticalPanel newAdressForm = newAdress.editForm();
				adressForm.insertCompoundBefore(newAdressButton, newAdressForm);
				adresses.add(newAdress);
				rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newMailButton) {
			try {
				Email newMail = new Email("EMAIL:");
				VerticalPanel newMailForm = newMail.editForm();
				mailForm.insertCompoundBefore(newMailButton, newMailForm);
				mails.add(newMail);
				rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newUrlButton) {
			try {
				Url newUrl = new Url("URL:http://www.srsoftware.de");
				VerticalPanel newUrlForm = newUrl.editForm();
				urlForm.insertCompoundBefore(newUrlButton, newUrlForm);
				urls.add(newUrl);
				rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newOrgButton) {
			try {
				Organization newOrg = new Organization("ORG:Organization");
				VerticalPanel newOrgForm = newOrg.editForm();
				orgForm.insertCompoundBefore(newOrgButton, newOrgForm);
				orgs.add(newOrg);
				rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newMessengerButton) {
			try {
				System.out.println("newMessengerButton");
				Messenger newMessenger = new Messenger("IMPP:ICQ:nickname");
				VerticalPanel newMessengerForm = newMessenger.editForm();
				messengerForm.insertCompoundBefore(newMessengerButton, newMessengerForm);
				messengers.add(newMessenger);
				rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source == newCategoryButton) {
			CategoryField categoryField = new CategoryField(_("Category"));
			categoryField.addEditListener(this);
			categoryFields.add(categoryField);
			categoryForm.insertCompoundBefore(newCategoryButton, categoryField);
			rescale();
		}
		if (source == newNoteButton) {
			NoteField newNoteField = new NoteField(_("Note"));
			newNoteField.addEditListener(this);
			noteFields.add(newNoteField);
			noteForm.insertCompoundBefore(newNoteButton, newNoteField);
			rescale();
		}
		if (source == cloneButton) {
			clonedContact = clone();
			if (clonedContact != null) {
				clonePanel = new HorizontalPanel(_("cloned contact"));
				clonePanel.add(resetCloneButton = new JButton(_("drop clone")));
				resetCloneButton.addActionListener(this);
				clonePanel.add(clonedContact.baseForm());
				clonePanel.scale();
				outerForm.replace(cloneButton, clonePanel);
			}
		}
		if (source == resetCloneButton) {
			clonedContact.clearFields();
			outerForm.replace(clonePanel, cloneButton);
			clonedContact = null;
		}
	}

	public Birthday birthday() {
		return birthday;
	}

	public void changedUpdate(DocumentEvent e) {
		update();
	}

	public int compareTo(Contact o) {
		return vcfName().compareTo(o.vcfName());
	}

	public boolean conflictsWith(Contact c2) {
		if (name != null && !name.isCompatibleWith(c2.name)) {
			//System.out.println("name conflict (" + name + " - " + c2.name + ")");
			return true;
		}
		if (birthday != null && !birthday.isCompatibleWith(c2.birthday)) {
			//System.out.println("bday conflict (" + birthday + " - " + c2.birthday + ")");
			return true;
		}
		if (different(formattedName, c2.formattedName)) {
			//System.out.println("formatted name conflict (" + formattedName + " - " + c2.formattedName + ")");
			return true;
		}
		if (different(anniversary, c2.anniversary)) {
			//System.out.println("anniversary conflict (" + anniversary + " - " + c2.anniversary + ")");
			return true;
		}
		if (!labels.isEmpty() && !c2.labels.isEmpty() && !labels.equals(c2.labels)) {
			//System.out.println("labels conflict (" + labels + " - " + c2.labels + ")");
			return true;
		}
		if (!titles.isEmpty() && !c2.titles.isEmpty() && !titles.equals(c2.titles)) {
			//System.out.println("titles conflict (" + titles + " - " + c2.titles + ")");
			return true;
		}
		if (!roles.isEmpty() && !c2.roles.isEmpty() && !roles.equals(c2.roles)) {
			//System.out.println("roles conflict (" + roles + " - " + c2.roles + ")");
			return true;
		}
		if (!phones.isEmpty() && !c2.phones.isEmpty() && !getSimplePhoneNumbers().equals(c2.getSimplePhoneNumbers())) {
			TreeSet<String> nums1 = getSimplePhoneNumbers();
			TreeSet<String> num2 = c2.getSimplePhoneNumbers();
			if (!nums1.containsAll(num2) && !num2.containsAll(nums1)) {
				//System.out.println("phones conflict (" + getSimplePhoneNumbers() + " - " + c2.getSimplePhoneNumbers() + ")");
				return true;
			}
		}
		if (!mails.isEmpty() && !c2.mails.isEmpty() && !getMailAdresses().equals(c2.getMailAdresses())) {
			//System.out.println("mails conflict (" + getMailAdresses() + " - " + c2.getMailAdresses() + ")");
			return true;
		}
		if (!adresses.isEmpty() && !c2.adresses.isEmpty() && !getAdressData().equals(c2.getAdressData())) {
			//System.out.println("addresses conflict (" + getAdressData() + " - " + c2.getAdressData() + ")");
			return true;
		}
		if (!urls.isEmpty() && !c2.urls.isEmpty() && !urls.equals(c2.urls)) {
			//System.out.println("urls conflict (" + urls + " - " + c2.urls + ")");
			return true;
		}
		if (!nicks.isEmpty() && !c2.nicks.isEmpty() && !nicks.equals(c2.nicks)) {
			//System.out.println("nicknames conflict (" + nicks + " - " + c2.nicks + ")");
			return true;
		}
		if (!notes.isEmpty() && !c2.notes.isEmpty() && !notes.equals(c2.notes)) {
			//System.out.println("notes conflict (" + notes + " - " + c2.notes + ")");
			return true;
		}
		if (!orgs.isEmpty() && !c2.orgs.isEmpty() && !orgs.equals(c2.orgs)) {
			//System.out.println("organizations conflict (" + orgs + " - " + c2.orgs + ")");
			return true;
		}
		if (!photos.isEmpty() && !c2.photos.isEmpty() && !photos.equals(c2.photos)) {
			//System.out.println("photo conflict (" + photos + " - " + c2.photos + ")");
			return true;
		}
		for (Entry<Integer, String> cc : customContent.entrySet()) {
			Integer key = cc.getKey();
			String val = c2.customContent.get(key);
			if (val != null && !val.equals(cc.getValue())) {
				//System.out.println("Custom content conflict (" + cc.getValue() + " - " + val + ")");
				return true;
			}
		}
		return false;
	}

	public boolean edited() {
		String before = this.toString();
		String[] options = { _("Ok"), _("Delete this contact") };
		int choice = JOptionPane.showOptionDialog(null, editForm(), _("Edit contact"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		switch (choice) {
		case 1:
			clearFields();
			break;
		default:
			changed();
		}
		return !this.equals(before);
	}

	public void generateName() {
		try {
			vcfName = (new MD5Hash(this)) + ".vcf";
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public byte[] getBytes() {
		return toString().getBytes();
	}

	public Contact getClonedContactIfExists() {
		if (clonedContact != null) clonedContact.changed();
		return clonedContact;
	}

	public void insertUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public boolean isCompatibleWith(Contact other) {
		return !conflictsWith(other);
	}

	public boolean isEmpty() {
		return adresses.isEmpty() && phones.isEmpty() && mails.isEmpty() && titles.isEmpty() && roles.isEmpty() && birthday == null && (categories == null || categories.isEmpty()) && urls.isEmpty() && notes.isEmpty() && photos.isEmpty() && orgs.isEmpty() && messengers.isEmpty() && nicks.isEmpty();
	}

	public boolean isInvalid() {
		for (Phone p : phones) {
			if (p.isInvalid()) return true;
		}
		for (Email m : mails) {
			if (m.isInvalid()) return true;
		}
		for (Nickname n : nicks) {
			if (n.isInvalid()) return true;
		}
		if (birthday != null && birthday.isInvalid()) return true;
		for (Messenger m : messengers) {
			if (m.isInvalid()) return true;
		}
		return false;
	}

	public TreeSet<String> mailAdresses() {
		TreeSet<String> mails = new TreeSet<String>();
		for (Email e : this.mails) {
			mails.add(e.address());
		}
		return mails;
	}

	public void markForRewrite() {
		rewrite = true;
	}

	@Override
	public boolean mergeWith(Contact other) {
		try {
			mergeWith(other, false);
			return true;
		} catch (InvalidAssignmentException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void mergeWith(Contact contact, boolean skipAsk) throws InvalidAssignmentException {
		phones.addAll(contact.phones);
		mails.addAll(contact.mails);
		adresses.addAll(contact.adresses);
		nicks.addAll(contact.nicks);
		mergeNames(contact, skipAsk);
		titles.addAll(contact.titles);
		roles.addAll(contact.roles);
		categories.addAll(contact.categories);
		mergeBirthday(contact);
		urls.addAll(contact.urls);
		notes.addAll(contact.notes);
		photos.addAll(contact.photos);
		orgs.addAll(contact.orgs);
		messengers.addAll(contact.messengers);
		labels.addAll(contact.labels);
		ignoredLines.addAll(contact.ignoredLines);
		if (contact.htmlMail) htmlMail = true;

		if (uid == null) uid = contact.uid;
		markForRewrite();
	}

	public TreeSet<String> messengerNicks() {
		TreeSet<String> ids = new TreeSet<String>();
		for (Messenger m : this.messengers) {
			ids.add(m.nick());
		}
		return ids;
	}

	public Name name() {
		return name;
	}

	public TreeSet<String> nicknames() {
		TreeSet<String> result = new TreeSet<String>();
		for (Nickname nick : nicks) {
			if (nick != null && !nick.isEmpty()) {
				result.add(nick.name());
			}
		}
		return result;
	}
	
	public int nicknameCount(){
		return nicks.size();
	}

	public void removeUpdate(DocumentEvent e) {
		update();
	}

	public boolean shallBeRewritten() {
		return rewrite; // only rewrite if it is not marked for deletion
	}

	public TreeSet<String> simpleNumbers() {
		TreeSet<String> numbers = new TreeSet<String>();
		for (Phone p : phones)
			numbers.add(p.simpleNumber());
		return numbers;
	}

	public void stateChanged(ChangeEvent e) {
		update(e.getSource());
	}

	public String toString() {
		return toString(false);
	}

	/**
	 * @param shorter if set to TRUE, the contact will be cut down (for display purposes).
	 * @return the code of that contact
	 */
	public String toString(boolean shorter) {
		StringBuffer sb = new StringBuffer();
		sb.append("BEGIN:VCARD\n");

		addContent(sb, shorter);
		if (uid != null) sb.append("UID:" + uid + "\n");
		sb.append("END:VCARD\n");
		if (!shorter) {
			return sb.toString();
		}
		return sb.toString().replace("\\,", ",");
	}

	public String getContent() {
		StringBuffer sb = new StringBuffer();
		addContent(sb, false);
		return sb.toString();
	}

	public void addContent(StringBuffer sb, boolean shorter) {
		int cutLength = 60;

		if (!shorter) {
			sb.append("VERSION:3.0\n");
			sb.append("PRODID:-//SRSoftware CalDavCleaner\n");
		}

		if (!shorter) sb.append(newRevision()+"\n");

		if (formattedName != null) sb.append("FN:"+formattedName+"\n"); // required for Version 3

		sb.append(name+"\n");// required for Version 3

		for (Nickname nick : nicks) sb.append(nick+"\n");

		if (categories != null && !categories.isEmpty()) {
			sb.append("CATEGORIES:");
			for (Iterator<String> it = categories.iterator(); it.hasNext();) {
				sb.append(it.next());
				if (it.hasNext()) {
					sb.append(",");
				}
			}
			sb.append("\n");
		}

		for (String title : titles) sb.append("TITLE:" + title + "\n");

		for (Organization org : orgs) sb.append(org+"\n");

		for (Messenger messenger : messengers) sb.append(messenger+"\n");

		for (String role : roles) sb.append("ROLE:" + role + "\n");

		if (birthday != null && !birthday.toString().equals("BDAY:")) sb.append(birthday+"\n");

		if (anniversary != null) {
			sb.append("ANNIVERSARY:");
			sb.append(anniversary); // required for Version 3
			sb.append("\n");
		}

		if (shorter) {
			for (Label label : labels) {
				String labelString = label.toString();
				sb.append(((labelString.length() > cutLength + 2) ? (labelString.substring(0, cutLength) + "...") : labelString) + "\n");
			}
		} else {
			for (Label label : labels) {
				sb.append(label);
				sb.append("\n");
			}
		}

		for (Adress adress : adresses) {
			sb.append(adress);
			sb.append("\n");
		}

		for (Phone phone : phones) {
			sb.append(phone);
			sb.append("\n");
		}
		for (Email mail : mails) {
			sb.append(mail);
			sb.append("\n");
		}

		if (htmlMail) sb.append("X-MOZILLA-HTML:TRUE\n");

		for (Url url : urls) {
			sb.append(url);
			sb.append("\n");
		}

		if (shorter) {
			for (String note : notes) sb.append("NOTE:" + ((note.length() > cutLength + 2) ? (note.substring(0, cutLength) + "...") : note) + "\n");
		} else {
			for (String note : notes) sb.append("NOTE:" + note + "\n");
		}

		if (shorter) {
			for (String photo : photos) sb.append(photo.substring(0, 30) + "...\n");
		} else {
			for (String photo : photos) sb.append(photo + "\n");
			
		}

		for (Entry<Integer, String> c : customContent.entrySet()) sb.append("CUSTOM" + c.getKey() + ":" + c.getValue() + "\n");
		
		if (!shorter) {
			for (String ignoredLine : ignoredLines) sb.append(ignoredLine+"\n");
		}
	}

	public String vcfName() {
		if (vcfName == null || vcfName.isEmpty()) generateName();
		return vcfName;
	}

	public File writeToFile() throws IOException {
		File f = new File(vcfName());
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(toString());
		bw.close();
		return f;
	}

	private VerticalPanel baseForm() {
		VerticalPanel form = new VerticalPanel();

		/* Name */
		nameForm = new VerticalPanel("Name");
		if (name == null) try {
			name = new Name("N:;;;;");
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		nameForm.add(name.editForm(null));

		/* Formatted Name */
		nameForm.add(formattedField = new InputField(_("Formatted name"), formattedName));
		formattedField.addChangeListener(this);
		nameForm.scale();
		form.add(nameForm);

		/* Titles */
		titleForm = new VerticalPanel(_("Titles"));
		titleFields = new Vector<CustomField>();
		for (String t : titles) {
			CustomField titleField = new CustomField(_("Title"), t);
			titleField.addEditListener(this);
			titleForm.add(titleField);
			titleFields.add(titleField);
		}
		titleForm.add(newTitleButton = new JButton(_("add title")));
		newTitleButton.addActionListener(this);
		titleForm.scale();
		form.add(titleForm);

		/* Nicknames */
		nickForm = new VerticalPanel(_("Nicknames"));
		for (Nickname nick : nicks) {
			nickForm.add(nick.editForm());
		}
		nickForm.add(newNickButton = new JButton(_("add nickname")));
		newNickButton.addActionListener(this);
		nickForm.scale();
		form.add(nickForm);

		/* Roles */
		roleForm = new VerticalPanel(_("Roles"));
		roleFields = new Vector<RoleField>();
		for (String t : roles) {
			RoleField roleField = new RoleField(_("Role"), t);
			roleField.addEditListener(this);
			roleForm.add(roleField);
			roleFields.add(roleField);
		}
		roleForm.add(newRoleButton = new JButton(_("add role")));
		newRoleButton.addActionListener(this);
		roleForm.scale();
		form.add(roleForm);

		/* Birthday */
		if (birthday != null) {
			form.add(birthday.editForm());
		} else {
			form.add(birthdayButton = new JButton(_("add birthday")));
			birthdayButton.addActionListener(this);
		}

		anniversaryField = new InputField(_("Anniversary"), anniversary);
		anniversaryField.addChangeListener(this);
		form.add(anniversaryField);

		/* Phones */
		phoneForm = new HorizontalPanel(_("Phones"));
		for (Phone p : phones) {
			phoneForm.add(p.editForm());
		}
		phoneForm.add(newPhoneButton = new JButton(_("add phone")));
		newPhoneButton.addActionListener(this);
		phoneForm.scale();
		form.add(phoneForm);

		/* Adresses */
		adressForm = new HorizontalPanel(_("Adresses"));
		for (Adress a : adresses) {
			adressForm.add(a.editForm());
		}
		adressForm.add(newAdressButton = new JButton(_("add adress")));
		newAdressButton.addActionListener(this);
		adressForm.scale();
		form.add(adressForm);

		/* Emails */
		mailForm = new HorizontalPanel(_("Email Adresses"));
		for (Email m : mails) {
			mailForm.add(m.editForm());
		}
		mailForm.add(newMailButton = new JButton(_("add email")));
		newMailButton.addActionListener(this);
		mailForm.scale();
		form.add(mailForm);

		/* URLs */
		urlForm = new VerticalPanel(_("Websites"));
		for (Url u : urls) {
			urlForm.add(u.editForm());
		}
		urlForm.add(newUrlButton = new JButton(_("add URL")));
		newUrlButton.addActionListener(this);
		urlForm.scale();
		form.add(urlForm);

		/* Organizations */
		orgForm = new HorizontalPanel(_("Organizations"));
		for (Organization o : orgs) {
			orgForm.add(o.editForm());
		}
		orgForm.add(newOrgButton = new JButton(_("add organization")));
		newOrgButton.addActionListener(this);
		orgForm.scale();
		form.add(orgForm);

		/* Messengers */
		messengerForm = new HorizontalPanel(_("Messengers"));
		for (Messenger m : messengers) {
			messengerForm.add(m.editForm());
		}
		messengerForm.add(newMessengerButton = new JButton(_("add messenger")));
		newMessengerButton.addActionListener(this);
		messengerForm.scale();
		form.add(messengerForm);

		/* Categories */
		categoryForm = new VerticalPanel(_("Categories"));
		categoryFields = new Vector<CategoryField>();
		for (String c : categories) {
			CategoryField categoryField = new CategoryField(_("Category"), c);
			categoryField.addEditListener(this);
			categoryForm.add(categoryField);
			categoryFields.add(categoryField);
		}
		categoryForm.add(newCategoryButton = new JButton(_("add category")));
		newCategoryButton.addActionListener(this);
		categoryForm.scale();
		form.add(categoryForm);

		/* Notes */
		noteForm = new VerticalPanel(_("Notes"));
		noteFields = new Vector<NoteField>();
		for (String n : notes) {
			NoteField noteField = new NoteField(_("Note"), n);
			noteField.addEditListener(this);
			noteForm.add(noteField);
			noteFields.add(noteField);
		}
		noteForm.add(newNoteButton = new JButton(_("add note")));
		newNoteButton.addActionListener(this);
		noteForm.scale();
		form.add(noteForm);

		/* Custom */
		customForm = new VerticalPanel(_("Custom"));
		customFields = new Vector<CustomField>();
		for (Entry<Integer, String> cc : customContent.entrySet()) {
			CustomField customField = new CustomField(_("Custom") + cc.getKey(), cc.getValue());
			customField.addEditListener(this);
			customForm.add(customField);
			customFields.add(customField);
		}
		customForm.add(newCustomButton = new JButton(_("add custom field")));
		newCustomButton.addActionListener(this);
		customForm.scale();
		form.add(customForm);

		form.scale();
		return form;
	}

	private void changed() {
		phones.update();
		adresses.update();
		labels.update();
		mails.update();
		urls.update();
		orgs.update();
		messengers.update();
		nicks.update();
	}

	private JComponent editForm() {
		outerForm = new HorizontalPanel();

		outerForm.add(form = baseForm());
		outerForm.add(cloneButton = new JButton(_(">> clone this contact >>")));
		cloneButton.addActionListener(this);

		outerForm.scale();

		int height=Math.min(outerForm.getPreferredSize().height+20, screenDim.height-100);
		int width=Math.min(outerForm.getPreferredSize().width+20, screenDim.width-50);
		

		//screenDim.setSize(screenDim.getWidth() - 100, screenDim.getHeight() - 100);
		scroll = new JScrollPane(outerForm);

		scroll.setPreferredSize(new Dimension(width,height));
		scroll.setSize(scroll.getPreferredSize());

		return scroll;
	}

	private TreeSet<String> getAdressData() {
		TreeSet<String> result = new TreeSet<String>();
		for (Adress adress : adresses)
			result.add(adress.canonical());
		return result;
	}

	private TreeSet<String> getMailAdresses() {
		TreeSet<String> result = new TreeSet<String>();
		for (Email mail : mails)
			result.add(mail.address());
		return result;
	}

	private TreeSet<String> getSimplePhoneNumbers() {
		TreeSet<String> result = new TreeSet<String>();
		for (Phone phone : phones)
			result.add(phone.simpleNumber());
		return result;
	}

	private void mergeBirthday(Contact contact) {
		if (contact.birthday == null) {
			// nothing to do
		} else if (birthday == null) {
			birthday = contact.birthday;
		} else if (birthday.isCompatibleWith(contact.birthday)) {
			birthday.mergeWith(contact.birthday);
		} else {
			birthday = (Birthday) selectOneOf(_("birtday"), birthday, contact.birthday, contact);
		}
	}

	private void mergeNames(Contact contact, boolean skipAsk) {
		if (name == null) {
			name = contact.name;
		} else if (name.isCompatibleWith(contact.name)) {
			name.mergeWith(contact.name);
		} else {
			if (skipAsk) {
				name = contact.name;
			} else {
				name = (Name) selectOneOf(_("name (last name, first name)"), name, contact.name, contact);
			}
		}

		if (different(formattedName, contact.formattedName)) {
			if (skipAsk) {
				formattedName = contact.formattedName;
			} else {
				formattedName = (String) selectOneOf(_("formatted name"), formattedName, contact.formattedName, contact);
			}
		} else {
			formattedName = merge(formattedName, contact.formattedName);
		}
	}

	private String newRevision() {
		String date = formatter.format(new Date()).replace('#', 'T');
		return "REV:" + date;
	}

	/*
	 * private void readRevision(String line) { if (line.isEmpty()) return; revision = line; }
	 */

	private void parse(String data) throws UnknownObjectException, InvalidFormatException {
		if (data == null || data.isEmpty()) throw new InvalidFormatException(_("No data given"));
		String[] lineArray = data.split("\n");
		parse(new Vector<String>(Arrays.asList(lineArray)));
	}
	
	/**
	 * Reads lines from a reader handle and writes a copy writer handle.
	 * In doing so, missing carriage returns are added.
	 * @param reader the source for reading the lines
	 * @param writer the destination to which the content shall be cloned
	 * @return a collection of lines read from the source
	 * @throws IOException
	 */
	private static Vector<String> readVCardLines(Reader reader, Writer writer) throws IOException {
		Vector<String> lines = new Vector<String>();

		StringBuffer buf = new StringBuffer();
		int last=0;
		int nextToLast=0;
		int character=0;
		while ((character = reader.read())!= -1) {
			if (character=='\n' && last != '\r') { // NL without CR found! Insert CR to comply with RFC 6350
				if (writer!=null) writer.write('\r');
				buf.append('\r');
				nextToLast=last; // shift newline 
				last='\r'; // inserted carriage return
			}
			
			if (writer!=null) writer.write(character);
			
			if (nextToLast == '\r' && last=='\n') {
				if (character == ' ') { // folded line
					buf.setLength(buf.length()-2);
					last=0;
					nextToLast=0;
					continue;
				} else { // regular line end
					String line = buf.toString().trim();
					if (!line.isEmpty()) lines.add(line);
					buf.setLength(0);
				}
			}
			
			
			buf.append((char)character);
			nextToLast = last;
			last = character;
		}
		String line = buf.toString().trim();
		if (!line.isEmpty()) lines.add(line);
		
		return lines;
	}

	/**
	 * Loads a contact from an url, reads its contents into lines
	 * @param url the location of the vcard
	 * @param backupPath File handle of folder to which a backup shall be written
	 * @throws IOException
	 * @throws UnknownObjectException
	 * @throws InvalidFormatException
	 */
	private void readFromUrl(URL url, File backupPath) throws IOException, UnknownObjectException, InvalidFormatException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = connection.getInputStream();
		
		InputStreamReader reader = new InputStreamReader(content);
		FileWriter file = (backupPath == null)?null:new FileWriter(new File(backupPath, vcfName()));

		Vector<String> lines = readVCardLines(reader,file);
				
		reader.close();
		if (file != null) file.close();
		
		content.close();
		connection.disconnect();
		parse(lines);
	}

	private void parse(Vector<String> lines) throws UnknownObjectException, InvalidFormatException {
		for (String line:lines) {
			boolean known = false;
			if (line.equals("BEGIN:VCARD")) known = true;
			if (line.equals("END:VCARD")) known = true;
			if (line.startsWith("DAVDROID1.")) line = line.substring(10); // groups are not supported, yet!
			if (line.startsWith("DAVDROID2.")) line = line.substring(10); // groups are not supported, yet!
			if (line.startsWith("ITEM1.")) line = line.substring(6); // groups are not supported, yet!
			
			if (line.startsWith("ADR") && (known = true)) readAdress(line);
			if (line.startsWith("ANNIVERSARY:") && (known = true)) readAnniversary(line.substring(12));
			if (line.startsWith("BDAY") && (known = true)) readBirthday(line);
			if (line.startsWith("CATEGORIES:") && (known = true)) readCategories(line.substring(11));
			if (line.startsWith("CLASS:") && (known = true)) ignoredLines.add(line);
			if (line.startsWith("CUSTOM") && (known = true)) readCustom(line.substring(6));
			if (line.startsWith("EMAIL") && (known = true)) readMail(line);
			if (line.startsWith("FN:") && (known = true)) readFormattedName(line.substring(3));
			if (line.startsWith("IMPP") && (known = true)) readIMPP(line);
			if (line.startsWith("LABEL") && (known = true)) readLabel(line);
			if (line.startsWith("MAILER:") && (known = true)) ignoredLines.add(line);
			if (line.startsWith("N:") && (known = true)) readName(line);
			if (line.startsWith("NICKNAME") && (known = true)) readNick(line);
			if (line.startsWith("NOTE:") && (known = true)) readNote(line.substring(5));
			if (line.startsWith("ORG:") && (known = true)) readOrg(line);
			if (line.startsWith("PHOTO") && (known = true)) readPhoto(line);
			if (line.startsWith("PRODID:")) known = true;
			if (line.startsWith("PROFILE:") && (known = true)) ignoredLines.add(line);
			if (line.startsWith("REV:")) known = true;
			if (line.startsWith("ROLE:") && (known = true)) readRole(line.substring(5));
			if (line.startsWith("TEL") && (known = true)) readPhone(line);
			if (line.startsWith("TITLE:") && (known = true)) readTitle(line.substring(6));
			if (line.startsWith("UID:") && (known = true)) readUID(line.substring(4));
			if (line.startsWith("URL") && (known = true)) readUrl(line);
			if (line.startsWith("VERSION:")) known = true;
			if (line.startsWith("X-AIM:") && (known = true)) readIMPP(line.replace("X-", "IMPP:"));
			if (line.startsWith("X-ABLABEL") && (known = true)) ignoredLines.add(line);
			if (line.startsWith("X-EVOLUTION-FILE-AS") && (known = true)) ignoredLines.add(line);
			if (line.startsWith("X-ICQ:") && (known = true)) readIMPP(line.replace("X-", "IMPP:"));
			if (line.startsWith("X-MOZILLA-HTML:") && (known = true)) readMailFormat(line.substring(15));
			if (line.startsWith("X-MS-IMADDRESS:") && (known = true)) readIMPP(line.replace("X-", "IMPP:"));
			if (line.startsWith("X-PHONETIC-LAST-NAME") && (known = true)) ignoredLines.add(line);
			if (line.startsWith("X-SKYPE:") && (known = true)) readIMPP(line.replace("X-", "IMPP:"));
			if (line.startsWith("X-THUNDERBIRD-ETAG") && (known = true)) ignoredLines.add(line);
			if (!known) {
				for (String s:lines) System.err.println(s);
				throw new UnknownObjectException(_("unknown entry/instruction found in vcard #: '#'", new Object[] { vcfName, line.length()>200 ? line.substring(0, 200):line }));
			}
		}
		changed();
	}

	private void readCustom(String s) {
		Integer index = Integer.parseInt("" + s.charAt(0));
		customContent.put(index, s.substring(1));
	}

	public void setCustom(int i, String text) {
		customContent.put(i, text);
	}

	private void readAdress(String line) throws UnknownObjectException, InvalidFormatException {
		Adress adress = new Adress(line);
		if (!adress.isEmpty()) adresses.add(adress);
	}

	private void readBirthday(String bday) throws InvalidFormatException {
		birthday = new Birthday(bday);
	}

	private void readCategories(String line) {
		if (line.isEmpty()) return;
		if (categories == null) categories = new TreeSet<String>();
		String[] cats = line.split(",");
		for (String category : cats) {
			categories.add(category.trim());
		}
	}

	private void readFormattedName(String fn) {
		if (fn.isEmpty()) return;
		formattedName = fn;
	}

	private void readIMPP(String line) throws UnknownObjectException, InvalidFormatException {
		Messenger messenger = new Messenger(line);
		if (!messenger.isEmpty()) messengers.add(messenger);
	}

	private void readLabel(String line) throws InvalidFormatException {
		Label label = new Label(line);
		if (!label.isEmpty()) labels.add(label);
	}

	private void readMail(String line) throws UnknownObjectException, InvalidFormatException {
		Email mail = new Email(line);
		if (!mail.isEmpty()) mails.add(mail);
	}

	private void readMailFormat(String line) {
		htmlMail = line.toUpperCase().equals("TRUE");
	}

	private void readName(String line) throws InvalidFormatException, UnknownObjectException {
		Name n = new Name(line);
		if (!n.isEmpty()) name = n;

	}

	private void readNick(String line) throws UnknownObjectException, InvalidFormatException {
		Nickname nick = new Nickname(line);
		if (!nick.isEmpty()) nicks.add(nick);
	}

	private void readNote(String line) {
		if (line.isEmpty()) return;
		notes.add(line);
	}

	private void readOrg(String line) throws InvalidFormatException, UnknownObjectException {
		Organization org = new Organization(line);
		if (!org.isEmpty()) orgs.add(org);
	}

	private void readPhone(String line) throws InvalidFormatException, UnknownObjectException {
		Phone phone = new Phone(line);
		phones.add(phone);
	}

	private void readPhoto(String line) {
		photos.add(line);
	}

	private void readRole(String line) {
		if (line.isEmpty()) return;
		roles.add(line.replace("\\n", "\n"));
	}

	private void readTitle(String line) {
		if (line.isEmpty()) return;
		titles.add(line);
	}

	private void readAnniversary(String line) {
		if (line.isEmpty()) return;
		anniversary = line;
	}

	private void readUID(String uid) {
		if (uid.isEmpty()) return;
		this.uid = uid;
	}

	private void readUrl(String line) throws InvalidFormatException, UnknownObjectException {
		Url url = new Url(line);
		if (!url.isEmpty()) urls.add(url);
	}

	private void rescale() {
		if (form != null) form.rescale();
		if (outerForm != null) outerForm.rescale();
		if (scroll != null) scroll.revalidate();
	}

	private Object selectOneOf(String title, Object o1, Object o2, Contact contact2) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel(_("<html>Merging the following two contacts:<br>&nbsp;")));
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel("<html>" + this.toString(true).replace("\n", "&nbsp;<br>")));
		hp.add(new JLabel("<html>" + contact2.toString(true).replace("\n", "<br>")));
		hp.scale();
		vp.add(hp);
		vp.add(new JLabel(_("<html><br>Which # shall be used?", title)));
		vp.scale();
		UIManager.put("OptionPane.yesButtonText", o1.toString().replace("\\,", ","));
		UIManager.put("OptionPane.noButtonText", o2.toString().replace("\\,", ","));
		int decision = JOptionPane.showConfirmDialog(null, vp, _("Please select"), JOptionPane.YES_NO_CANCEL_OPTION);
		UIManager.put("OptionPane.yesButtonText", _("Yes"));
		UIManager.put("OptionPane.noButtonText", _("No"));
		switch (decision) {
		case JOptionPane.YES_OPTION:
			return o1;
		case JOptionPane.NO_OPTION:
			return o2;
		case JOptionPane.CANCEL_OPTION:
			System.exit(0);
		}
		return null;
	}

	private void update() {
		formattedName = formattedField.getText();
		anniversary = anniversaryField.getText();
	}

	private void update(Object source) {
		if (source instanceof CustomField) updateTitles();
		if (source instanceof RoleField) updateRoles();
		if (source instanceof CategoryField) updateCategories();
		if (source instanceof NoteField) updateNotes();
	}

	private void updateCategories() {
		categories.clear();
		for (CategoryField cf : categoryFields) {
			String cat = cf.getText().trim();
			if (cat != null && !cat.isEmpty()) {
				categories.add(cat);
				cf.setBackground(UIManager.getColor ( "Panel.background" ));
			} else {
				cf.setBackground(Color.yellow);
			}
		}
	}

	private void updateNotes() {
		notes.clear();
		for (NoteField nf : noteFields) {
			String note = nf.getText().trim();
			if (note != null && !note.isEmpty()) {
				notes.add(note);
				nf.setBackground(UIManager.getColor ( "Panel.background" ));
			} else {
				nf.setBackground(Color.yellow);
			}
		}
	}

	private void updateRoles() {
		roles.clear();
		for (RoleField rf : roleFields) {
			String role = rf.getText().trim();
			if (role != null && !role.isEmpty()) {
				roles.add(role);
				rf.setBackground(UIManager.getColor ( "Panel.background" ));
			} else {
				rf.setBackground(Color.yellow);
			}
		}
	}

	private void updateTitles() {
		titles.clear();
		for (CustomField tf : titleFields) {
			String title = tf.getText().trim();
			if (title != null && !title.isEmpty()) {
				titles.add(title);
				tf.setBackground(UIManager.getColor ( "Panel.background" ));
			} else {
				tf.setBackground(Color.yellow);
			}
		}
	}

	protected Contact clone() {
		try {
			changed();
			return new Contact(toString());
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	void clearFields() {
		adresses.clear();
		phones.clear();
		mails.clear();
		titles.clear();
		roles.clear();
		birthday = null;
		categories.clear();
		urls.clear();
		notes.clear();
		photos.clear();
		orgs.clear();
		nicks.clear();
		changed();
	}

	public boolean isSameAs(Contact c2) {
		return getContent().equals(c2.getContent());
	}

	public String[] getAssociationWith(Contact contact) {
		String phoneAssociation = getPhoneAssociation(contact);
		if (phoneAssociation != null) {
			return new String[] { _("Phone"), phoneAssociation };
		}
		String mailAssociation = getMailAssociation(contact);
		if (mailAssociation != null) {
			return new String[] { _("Email"), mailAssociation };
		}
		String nameAssociation = getNameAssociation(contact);
		if (nameAssociation != null) {
			return new String[] { _("Name"), nameAssociation };
		}
		String messengerAssociation = getMessengerAssociation(contact);
		if (messengerAssociation != null) {
			return new String[] { _("Messenger"), messengerAssociation };
		}
		return null;
	}

	private String getMessengerAssociation(Contact contact) {
		TreeSet<String> nicks1 = this.messengerNicks();
		TreeSet<String> nicks2 = contact.messengerNicks();
		for (String nick : nicks1) {
			if (nicks2.contains(nick)) return nick;
		}
		return null;
	}

	private String getNameAssociation(Contact contact) {
		if (this.name == null || contact.name == null) return null;
		if (this.name.canonical().equals(contact.name.canonical())) {
			return name.main();
		}
		return null;
	}

	private String getMailAssociation(Contact contact) {
		TreeSet<String> mails1 = this.mailAdresses();
		TreeSet<String> mails2 = contact.mailAdresses();
		for (String mail : mails1) {
			if (mails2.contains(mail)) return mail;
		}
		return null;
	}

	private String getPhoneAssociation(Contact contact) {
		TreeSet<String> numbers1 = this.simpleNumbers();
		TreeSet<String> numbers2 = contact.simpleNumbers();
		for (String num : numbers1) {
			if (numbers2.contains(num)) return num;
		}
		return null;
	}

	public String uid() {
		return uid;
	}

	public int showResolveDialog(TreeSet<Contact> additionalContacts, Client client, ProblemSet problems) throws UnknownObjectException, InvalidFormatException {
		final Contact backupContact = this.clone();
		if (additionalContacts.isEmpty()) {
			additionalContacts.add(new Contact(name.toString()));
		}

		int count = additionalContacts.size();
		VerticalPanel grid = new VerticalPanel();
		for (Problem problem:problems){
			JLabel label = new JLabel(_(problem.toString()));
			label.setForeground(Color.red);
			grid.add(label);
		}
		HorizontalPanel overview=new HorizontalPanel();
		for (int i = 0; i < count; i++) {
			overview.add(new JLabel(_("<html>New<br>Contact #", i + 1)+" "));
		}
		overview.add(new JLabel("   "));		
		overview.add(new JLabel(_("<html>Original<br>Contact")));
		grid.add(overview.scale());
		
		addNameSelectors(count, grid, backupContact, additionalContacts,client,problems);
		addBDaySelector(count, grid, backupContact, additionalContacts,client,problems);
		addAnniversarySelector(count, grid, backupContact, additionalContacts,client,problems);
		addTitlesSelector(count, grid, backupContact, additionalContacts,client,problems);
		addRolesSelector(count, grid, backupContact, additionalContacts,client,problems);
		addNotesSelector(count, grid, backupContact, additionalContacts,client,problems);
		addPhotosSelector(count, grid, backupContact, additionalContacts,client,problems);
		addCategoriesSelector(count, grid, backupContact, additionalContacts,client,problems);
		addLabelsSelector(count, grid, backupContact, additionalContacts,client,problems);
		addOrgsSelector(count, grid, backupContact, additionalContacts,client,problems);
		addAddressSelectors(count, grid, backupContact, additionalContacts,client,problems);
		addNicknamesSelector(count, grid, backupContact, additionalContacts,client,problems);
		addPhoneSelectors(count, grid, backupContact, additionalContacts,client,problems);
		addMailSelectors(count,grid,backupContact,additionalContacts,client,problems);
		addMessengerSelectors(count,grid,backupContact,additionalContacts,client,problems);
		addUrlSelectors(count,grid,backupContact,additionalContacts,client,problems);

		// TODO: implement this for private TreeMap<Integer,String> customContent = new TreeMap<Integer,String>();
		JScrollPane scrollableGrid=new JScrollPane(grid.scale());
		int height=Math.min(grid.getPreferredSize().height+20, screenDim.height-100);
		int width=Math.min(grid.getPreferredSize().width+20, screenDim.width-50);
		scrollableGrid.setPreferredSize(new Dimension(width,height));
		
		String []options = new String[] {_("Update!"),_("Add another clone"),_("Cancel")};
		return JOptionPane.showOptionDialog(null, scrollableGrid, _("Distribute Fields"), 0, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private void addMessengerSelectors(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.messengers != null && !backup.messengers.isEmpty()) {
			final VerticalPanel messengersPanel=new VerticalPanel();
			for (final Messenger messenger : backup.messengers) {
				final VerticalPanel messengerPanel=new VerticalPanel(_("Messenger: #",messenger.nick()));
				HorizontalPanel panel;
				for (final Messenger.Category category : Messenger.Category.values()) {
					panel=new HorizontalPanel();
					for (final Contact additionalContact : additionalContacts) {
						panel.add(activeBox(new Action() {

							@Override
							public void change(JCheckBox box) {
								Messenger additionalContactMessenger = additionalContact.getMessenger(messenger.nick());
								if (box.isSelected()) {
									if (additionalContactMessenger == null) {
										try {
											additionalContactMessenger = messenger.clone(false);
											additionalContact.addMessenger(additionalContactMessenger);
										} catch (CloneNotSupportedException e) {
											e.printStackTrace();
										}
									}
									additionalContactMessenger.addCategory(category);
								} else {
									if (additionalContactMessenger != null) {
										additionalContactMessenger.removeCategory(category);
										if (additionalContactMessenger.categories().isEmpty()) {
											additionalContact.removeMessenger(additionalContactMessenger);
										}
									}
								}
								checkValidity(additionalContacts,client,Problem.Type.MESSENGER,messengersPanel);
							}
						}));
					}
					panel.add(activeBox(_(category), messenger.categories().contains(category), new Action() {

						@Override
						public void change(JCheckBox box) {
							Messenger baseMessenger = getMessenger(messenger.nick());
							if (box.isSelected()) {
								if (baseMessenger == null) {
									try {
										baseMessenger = messenger.clone(false);
										addMessenger(baseMessenger);
									} catch (CloneNotSupportedException e) {
										e.printStackTrace();
									}
								}
								baseMessenger.addCategory(category);
							} else {
								if (baseMessenger != null) {
									baseMessenger.removeCategory(category);
									if (baseMessenger.categories().isEmpty()) {
										removeMessenger(baseMessenger);
									}
								}
							}
							checkValidity(additionalContacts,client,Problem.Type.MESSENGER,messengersPanel);
						}
					}));
					messengerPanel.add(panel.scale());
				}
				messengersPanel.add(messengerPanel.scale());
			}
			if (problems.contains(Problem.Type.MESSENGER)){
				messengersPanel.setBackground(Color.ORANGE);
			}
			grid.add(messengersPanel.scale());
		}
	}

	private void addTitlesSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		HorizontalPanel panel;
		if (backup.titles != null && !backup.titles.isEmpty()) {
			final VerticalPanel titlePanel = new VerticalPanel(_("Titles"));			
			for (final String title : backup.titles) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								additionalContact.titles.add(title);
							} else {
								additionalContact.titles.remove(title);
							}
							checkValidity(additionalContacts,client,Problem.Type.TITLE,titlePanel);
						}
					}));
				}
				panel.add(activeBox(title, new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							titles.add(title);
						} else {
							titles.remove(title);
						}
						checkValidity(additionalContacts,client,Problem.Type.TITLE,titlePanel);
					}
				}));
				titlePanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.TITLE)){
				titlePanel.setBackground(Color.ORANGE);
			}
			grid.add(titlePanel.scale());
		}
  }
	
	private void addCategoriesSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.categories != null && !backup.categories.isEmpty()) {
			final VerticalPanel categoriePanel = new VerticalPanel(_("Categories"));			
			HorizontalPanel panel;
			for (final String category : backup.categories) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								additionalContact.categories.add(category);
							} else {
								additionalContact.categories.remove(category);
							}
							checkValidity(additionalContacts, client, Problem.Type.CATEGORIES, categoriePanel);
						}
					}));
				}
				panel.add(activeBox(category, new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							categories.add(category);
						} else {
							categories.remove(category);
						}
						checkValidity(additionalContacts, client, Problem.Type.CATEGORIES, categoriePanel);
					}
				}));
				categoriePanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.CATEGORIES)){
				categoriePanel.setBackground(Color.ORANGE);
			}
			grid.add(categoriePanel.scale());
		}
  }
	
	private void addNicknamesSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		HorizontalPanel panel;
		if (backup.nicks != null && !backup.nicks.isEmpty()) {
			final VerticalPanel nicknamePanel = new VerticalPanel(_("Nicknames"));			
			for (final Nickname nickname : backup.nicks) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								try {
	                additionalContact.nicks.add(nickname.clone());
                } catch (CloneNotSupportedException e) {
	                e.printStackTrace();
                }
							} else {
								additionalContact.nicks.remove(nickname);
							}
							checkValidity(additionalContacts,client,Problem.Type.NICKNAMES,nicknamePanel);
						}
					}));
				}
				panel.add(activeBox(nickname, new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							try {
                nicks.add(nickname.clone());
              } catch (CloneNotSupportedException e) {
                e.printStackTrace();
              }
						} else {
							nicks.remove(nickname);
						}
						checkValidity(additionalContacts,client,Problem.Type.NICKNAMES,nicknamePanel);
					}
				}));
				nicknamePanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.NICKNAMES)){
				nicknamePanel.setBackground(Color.ORANGE);
			}
			grid.add(nicknamePanel.scale());
		}
  }
	
	// TODO: this method has not been tested
	private void addLabelsSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (labels != null && !labels.isEmpty()) {
			final VerticalPanel labelPanel = new VerticalPanel(_("Labels"));
			HorizontalPanel panel;
			for (final Label label : backup.labels) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								try {
	                additionalContact.labels.add(label.clone());
                } catch (CloneNotSupportedException e) {
	                e.printStackTrace();
                }
							} else {
								additionalContact.labels.remove(label);
							}
							checkValidity(additionalContacts,client,Problem.Type.LABELS,labelPanel);
						}
					}));
				}
				panel.add(activeBox(label, new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							try {
                labels.add(label.clone());
              } catch (CloneNotSupportedException e) {
                e.printStackTrace();
              }
						} else {
							labels.remove(label);
						}
						checkValidity(additionalContacts,client,Problem.Type.LABELS,labelPanel);
					}
				}));
				labelPanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.LABELS)){
				labelPanel.setBackground(Color.ORANGE);
			}
			grid.add(labelPanel.scale());
		}
  }
	
	protected void checkValidity(TreeSet<Contact> additionalContacts, Client client,Problem.Type problemType, JPanel panel) {
		ProblemSet problems = client.problemsWith(this);
		for (Contact c:additionalContacts){
			problems.addAll(client.problemsWith(c));
		}
		if (problems.contains(problemType)){
			panel.setBackground(Color.orange);
		} else {
			panel.setBackground(UIManager.getColor ( "Panel.background" ));
		}
  }

	private void addOrgsSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		HorizontalPanel panel;
		if (backup.orgs != null && !backup.orgs.isEmpty()) {
			final VerticalPanel orgPanel = new VerticalPanel(_("Orgs"));			
			for (final Organization org : backup.orgs) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								try {
	                additionalContact.orgs.add(org.clone());
                } catch (CloneNotSupportedException e) {
	                e.printStackTrace();
                }
							} else {
								additionalContact.orgs.remove(org);
							}
							checkValidity(additionalContacts,client,Problem.Type.ORGS,orgPanel);
						}
					}));
				}
				panel.add(activeBox(org, new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							try {
                orgs.add(org.clone());
              } catch (CloneNotSupportedException e) {
                e.printStackTrace();
              }
						} else {
							orgs.remove(org);
						}
						checkValidity(additionalContacts,client,Problem.Type.ORGS,orgPanel);
					}
				}));
				orgPanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.ORGS)){
				orgPanel.setBackground(Color.ORANGE);
			}
			grid.add(orgPanel.scale());
		}
  }
	
	private void addNotesSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.notes != null && !backup.notes.isEmpty()) {
			final VerticalPanel notePanel = new VerticalPanel(_("Notes"));			
			HorizontalPanel panel;
			for (final String note : backup.notes) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								additionalContact.notes.add(note);
							} else {
								additionalContact.notes.remove(note);
							}
							checkValidity(additionalContacts,client,Problem.Type.ROLES,notePanel);
						}
					}));
				}
				panel.add(activeBox(note, new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							notes.add(note);
						} else {
							notes.remove(note);
						}
						checkValidity(additionalContacts,client,Problem.Type.ROLES,notePanel);
					}
				}));
				notePanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.NOTES)){
				notePanel.setBackground(Color.ORANGE);
			}
			grid.add(notePanel.scale());
		}
  }

	private void addPhotosSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.photos != null && !backup.photos.isEmpty()) {
			final VerticalPanel photoPanel = new VerticalPanel(_("Photos"));			
			HorizontalPanel panel;
			for (final String photo : backup.photos) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								additionalContact.photos.add(photo);
							} else {
								additionalContact.photos.remove(photo);
							}
							checkValidity(additionalContacts, client, Problem.Type.PHOTO, photoPanel);
						}
					}));
				}
				panel.add(activeBox(_("Photo"), new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							photos.add(photo);
						} else {
							photos.remove(photo);
						}
						checkValidity(additionalContacts, client, Problem.Type.PHOTO, photoPanel);
					}
				}));
				photoPanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.NOTES)){
				photoPanel.setBackground(Color.ORANGE);
			}
			grid.add(photoPanel.scale());
		}
  }
	
	private void addRolesSelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.roles != null && !backup.roles.isEmpty()) {
			final VerticalPanel rolePanel = new VerticalPanel(_("Roles"));			
			HorizontalPanel panel;
			for (final String role : backup.roles) {
				panel = new HorizontalPanel();
				for (final Contact additionalContact:additionalContacts) {
					panel.add(activeBox(new Action() {

						@Override
						public void change(JCheckBox box) {
							if (box.isSelected()) {
								additionalContact.roles.add(role);
							} else {
								additionalContact.roles.remove(role);
							}
							checkValidity(additionalContacts,client,Problem.Type.ROLES,rolePanel);
						}
					}));
				}
				panel.add(activeBox(role, new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							roles.add(role);
						} else {
							roles.remove(role);
						}
						checkValidity(additionalContacts,client,Problem.Type.ROLES,rolePanel);
					}
				}));
				rolePanel.add(panel.scale());
			}
			if (problems.contains(Problem.Type.ROLES)){
				rolePanel.setBackground(Color.ORANGE);
			}
			grid.add(rolePanel.scale());
		}
  }
	
	private void addAnniversarySelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		final HorizontalPanel panel;
		if (backup.anniversary != null) {
			panel=new HorizontalPanel(_("anniversary"));
			for (final Contact additionalContact:additionalContacts) {
				panel.add(activeBox(new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							additionalContact.anniversary=backup.anniversary;
						} else {
							additionalContact.anniversary=null;;
						}
						checkValidity(additionalContacts,client,Problem.Type.ANNIVERSARY,panel);
					}
				}));
			}
			panel.add(activeBox(anniversary, new Action() {

				@Override
				public void change(JCheckBox box) {
					if (box.isSelected()) {
						anniversary=backup.anniversary;
					} else {
						anniversary=null;
					}
					checkValidity(additionalContacts,client,Problem.Type.ANNIVERSARY,panel);
				}
			}));
			if (problems.contains(Problem.Type.ANNIVERSARY)){
				panel.setBackground(Color.ORANGE);
			}
			grid.add(panel.scale());
		}
  }

	private void addBDaySelector(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		final HorizontalPanel panel;
		if (backup.birthday != null) {
			panel=new HorizontalPanel(_("birthday"));
			for (final Contact additionalContact:additionalContacts) {
				panel.add(activeBox(new Action() {

					@Override
					public void change(JCheckBox box) {
						if (box.isSelected()) {
							try {
								additionalContact.setBirthday(backup.birthday.clone());
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
						} else {
							additionalContact.birthday=null;
						}
						checkValidity(additionalContacts,client,Problem.Type.BIRTHDAY,panel);
					}
				}));
			}
			panel.add(activeBox(birthday.format("Y/m/d"), new Action() {

				@Override
				public void change(JCheckBox box) {
					if (box.isSelected()) {
						if (birthday == null) {
							try {
								birthday=backup.birthday.clone();
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
						}
					} else {
						birthday=null;
					}
					checkValidity(additionalContacts,client,Problem.Type.BIRTHDAY,panel);
				}
			}));
			if (problems.contains(Problem.Type.BIRTHDAY)){
				panel.setBackground(Color.ORANGE);
			}
			grid.add(panel.scale());
		}
  }

	protected void unsetBirthday() {
		birthday=null;
  }

	protected void setBirthday(Birthday b) {
	  birthday=b;	  
  }

	private void addNameSelectors(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		HorizontalPanel panel;
		if (backup.name != null) {
			VerticalPanel namePanel=new VerticalPanel(_("Name"));
			String part=backup.name.prefix();
			if (part != null && !part.isEmpty()) {
				panel=new HorizontalPanel();
					for (int i = 0; i < count; i++) {
					panel.add(activeCheckBox());
				}
				panel.add(activeCheckBox(part));
				namePanel.add(panel.scale());
			}
			
			part=backup.name.first();
			if (part != null && !part.isEmpty()) {
				panel=new HorizontalPanel();
				for (int i = 0; i < count; i++) {
					panel.add(activeCheckBox());
				}
				panel.add(activeCheckBox(part));
				namePanel.add(panel.scale());
			}
			
			part=backup.name.middle();
			if (part != null && !part.isEmpty()) {
				panel=new HorizontalPanel();
				for (int i = 0; i < count; i++) {
					panel.add(activeCheckBox());
				}
				panel.add(activeCheckBox(part));
				namePanel.add(panel.scale());
			}
			
			part=backup.name.last();
			if (part != null && !part.isEmpty()) {
				panel=new HorizontalPanel();
				for (int i = 0; i < count; i++) {
					panel.add(activeCheckBox());
				}
				panel.add(activeCheckBox(part));
				namePanel.add(panel.scale());
			}
			
			part=backup.name.suffix();
			if (part != null && !part.isEmpty()) {
				panel=new HorizontalPanel();
				for (int i = 0; i < count; i++) {
					panel.add(activeCheckBox());
				}
				panel.add(activeCheckBox(part));
				namePanel.add(panel.scale());
			}
			grid.add(namePanel.scale());
		}

		if (backup.formattedName != null) {
			final HorizontalPanel fNamePanel=new HorizontalPanel(_("formatted name"));
			for (final Contact additionalContact : additionalContacts) {
				fNamePanel.add(activeBox(new Action() {
					@Override
					public void change(JCheckBox origin) {
						additionalContact.formattedName = origin.isSelected() ? backup.formattedName : null;
						checkValidity(additionalContacts,client,Problem.Type.FORMATTEDNAME,fNamePanel);
					}
				}));
			}
			fNamePanel.add(activeBox(formattedName, new Action() {
				@Override
				public void change(JCheckBox origin) {
					formattedName = origin.isSelected() ? backup.formattedName : null;
					checkValidity(additionalContacts,client,Problem.Type.FORMATTEDNAME,fNamePanel);
				}
			}));
			if (problems.contains(Problem.Type.FORMATTEDNAME)){
				fNamePanel.setBackground(Color.ORANGE);
			}
			grid.add(fNamePanel.scale());
		}
  }

	private void addUrlSelectors(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.urls != null && !backup.urls.isEmpty()) {
			final VerticalPanel urlsPanel=new VerticalPanel();
			for (final Url url : backup.urls) {
				final VerticalPanel urlPanel=new VerticalPanel(_("Url"));
				urlPanel.add(new JLabel(url.address()));
				HorizontalPanel panel;
				for (final Url.Category category : Url.Category.values()) {
					panel=new HorizontalPanel();
					for (final Contact additionalContact : additionalContacts) {
						panel.add(activeBox(new Action() {

							@Override
							public void change(JCheckBox box) {
								Url additionalContactUrl = additionalContact.getUrl(url.address());
								if (box.isSelected()) {
									if (additionalContactUrl == null) {
										try {
											additionalContactUrl = url.clone(false);
											additionalContact.addUrl(additionalContactUrl);
										} catch (CloneNotSupportedException e) {
											e.printStackTrace();
										}
									}
									additionalContactUrl.addCategory(category);
								} else {
									if (additionalContactUrl != null) {
										additionalContactUrl.removeCategory(category);
										if (additionalContactUrl.categories().isEmpty()) {
											additionalContact.removeUrl(additionalContactUrl);
										}
									}
								}
								checkValidity(additionalContacts,client,Problem.Type.URLS,urlsPanel);
							}
						}));
					}
					panel.add(activeBox(_(category), url.categories().contains(category), new Action() {

						@Override
						public void change(JCheckBox box) {
							Url baseUrl = getUrl(url.address());
							if (box.isSelected()) {
								if (baseUrl == null) {
									try {
										baseUrl = url.clone(false);
										addUrl(baseUrl);
									} catch (CloneNotSupportedException e) {
										e.printStackTrace();
									}
								}
								baseUrl.addCategory(category);
							} else {
								if (baseUrl != null) {
									baseUrl.removeCategory(category);
									if (baseUrl.categories().isEmpty()) {
										removeUrl(baseUrl);
									}
								}
							}
							checkValidity(additionalContacts,client,Problem.Type.URLS,urlsPanel);
						}
					}));
					urlPanel.add(panel.scale());
				}
				urlsPanel.add(urlPanel.scale());
			}
			if (problems.contains(Problem.Type.URLS)){
				urlsPanel.setBackground(Color.ORANGE);
			}
			grid.add(urlsPanel.scale());
		}
	}
	





	private void addMailSelectors(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.mails != null && !backup.mails.isEmpty()) {
			final VerticalPanel mailsPanel=new VerticalPanel();
			HorizontalPanel panel;
			for (final Email mail : backup.mails) {
				final VerticalPanel mailPanel=new VerticalPanel(_("Mail"));
				mailPanel.add(new JLabel(mail.address()));
				for (final Email.Category category : Email.Category.values()) {
					panel=new HorizontalPanel();
					for (final Contact additionalContact : additionalContacts) {
						panel.add(activeBox(new Action() {

							@Override
							public void change(JCheckBox box) {
								Email additionalContactEmail = additionalContact.getMail(mail.address());
								if (box.isSelected()) {
									if (additionalContactEmail == null) {
										try {
											additionalContactEmail = mail.clone(false);
											additionalContact.addMail(additionalContactEmail);
										} catch (CloneNotSupportedException e) {
											e.printStackTrace();
										}
									}
									additionalContactEmail.addCategory(category);
								} else {
									if (additionalContactEmail != null) {
										additionalContactEmail.removeCategory(category);
										if (additionalContactEmail.categories().isEmpty()) {
											additionalContact.removeMail(additionalContactEmail);
										}
									}
								}
								checkValidity(additionalContacts,client,Problem.Type.EMAIL,mailsPanel);
							}
						}));
					}
					panel.add(activeBox(_(category), mail.categories().contains(category), new Action() {

						@Override
						public void change(JCheckBox box) {
							Email baseMail = getMail(mail.address());
							if (box.isSelected()) {
								if (baseMail == null) {
									try {
										baseMail = mail.clone(false);
										addMail(baseMail);
									} catch (CloneNotSupportedException e) {
										e.printStackTrace();
									}
								}
								baseMail.addCategory(category);
							} else {
								if (baseMail != null) {
									baseMail.removeCategory(category);
									if (baseMail.categories().isEmpty()) {
										removeMail(baseMail);
									}
								}
							}
							checkValidity(additionalContacts,client,Problem.Type.EMAIL,mailsPanel);
						}
					}));
					mailPanel.add(panel.scale());
				}
				mailsPanel.add(mailPanel.scale());
			}
			if (problems.contains(Problem.Type.EMAIL)){
				mailsPanel.setBackground(Color.ORANGE);
			}
			grid.add(mailsPanel.scale());
		}
	}




	private void addPhoneSelectors(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.phones != null && !backup.phones.isEmpty()) {
			final VerticalPanel phonesPanel=new VerticalPanel();
			for (final Phone phone : backup.phones) {
				final VerticalPanel phonePanel=new VerticalPanel(_("Phone: #",phone.simpleNumber()));
				HorizontalPanel panel;
				for (final Phone.Category category : Phone.Category.values()) {
					panel=new HorizontalPanel();
					for (final Contact additionalContact : additionalContacts) {
						panel.add(activeBox(new Action() {

							@Override
							public void change(JCheckBox box) {
								Phone additionalContactPhone = additionalContact.getPhone(phone.simpleNumber());
								if (box.isSelected()) {
									if (additionalContactPhone == null) {
										try {
											additionalContactPhone = phone.clone(false);
											additionalContact.addPhone(additionalContactPhone);
										} catch (CloneNotSupportedException e) {
											e.printStackTrace();
										}
									}
									additionalContactPhone.addCategory(category);
								} else {
									if (additionalContactPhone != null) {
										additionalContactPhone.removeCategory(category);
										if (additionalContactPhone.categories().isEmpty()) {
											additionalContact.removePhone(additionalContactPhone);
										}
									}
								}
								checkValidity(additionalContacts,client,Problem.Type.PHONE,phonesPanel);
							}
						}));
					}
					panel.add(activeBox(_(category), phone.categories().contains(category), new Action() {

						@Override
						public void change(JCheckBox box) {
							Phone basePhone = getPhone(phone.simpleNumber());
							if (box.isSelected()) {
								if (basePhone == null) {
									try {
										basePhone = phone.clone(false);
										addPhone(basePhone);
									} catch (CloneNotSupportedException e) {
										e.printStackTrace();
									}
								}
								basePhone.addCategory(category);
							} else {
								if (basePhone != null) {
									basePhone.removeCategory(category);
									if (basePhone.categories().isEmpty()) {
										removePhone(basePhone);
									}
								}
							}
							checkValidity(additionalContacts,client,Problem.Type.PHONE,phonesPanel);
						}
					}));
					phonePanel.add(panel.scale());
				}
				phonesPanel.add(phonePanel.scale());
			}
			if (problems.contains(Problem.Type.PHONE)){
				phonesPanel.setBackground(Color.ORANGE);
			}
			grid.add(phonesPanel.scale());
		}
	}
	
	private void addAddressSelectors(int count, VerticalPanel grid, final Contact backup, final TreeSet<Contact> additionalContacts, final Client client, ProblemSet problems) {
		if (backup.adresses != null && !backup.adresses.isEmpty()) {
			final VerticalPanel addressesPanel=new VerticalPanel();
			for (final Adress address : backup.adresses) {
				VerticalPanel addressPanel=new VerticalPanel(_("Address"));
				HorizontalPanel panel;
				addressPanel.add(new JLabel(address.canonical()));
				for (final Adress.Category category : Adress.Category.values()) {
					panel=new HorizontalPanel();
					for (final Contact additionalContact : additionalContacts) {
						panel.add(activeBox(new Action() {

							@Override
							public void change(JCheckBox box) {
								Adress additionalContactAddress = additionalContact.getAddress(address.canonical());
								if (box.isSelected()) {
									if (additionalContactAddress == null) {
										try {
											additionalContactAddress = address.clone(false);
											additionalContact.addAddress(additionalContactAddress);
										} catch (CloneNotSupportedException e) {
											e.printStackTrace();
										}
									}
									additionalContactAddress.addCategory(category);
								} else {
									if (additionalContactAddress != null) {
										additionalContactAddress.removeCategory(category);
										if (additionalContactAddress.categories().isEmpty()) {
											additionalContact.removeAddress(additionalContactAddress);
										}
									}
								}
								checkValidity(additionalContacts,client,Problem.Type.ADDRESSES,addressesPanel);
							}
						}));
					}
					panel.add(activeBox(_(category), address.categories().contains(category), new Action() {

						@Override
						public void change(JCheckBox box) {
							Adress baseAddress = getAddress(address.canonical());
							if (box.isSelected()) {
								if (baseAddress == null) {
									try {
										baseAddress = address.clone(false);
										addAddress(baseAddress);
									} catch (CloneNotSupportedException e) {
										e.printStackTrace();
									}
								}
								baseAddress.addCategory(category);
							} else {
								if (baseAddress != null) {
									baseAddress.removeCategory(category);
									if (baseAddress.categories().isEmpty()) {
										removeAddress(baseAddress);
									}
								}
							}
							checkValidity(additionalContacts,client,Problem.Type.ADDRESSES,addressesPanel);
						}
					}));
					addressPanel.add(panel.scale());
				}
				addressesPanel.add(addressPanel.scale());
			}
			if (problems.contains(Problem.Type.ADDRESSES)){
				addressesPanel.setBackground(Color.ORANGE);
			}
			grid.add(addressesPanel.scale());
		}
	}





	

	protected void removePhone(Phone phone) {
		phones.remove(phone);
	}
	
	protected void removeMessenger(Messenger m) {
		messengers.remove(m);
	}
	
	protected void removeMail(Email mail) {
		mails.remove(mail);
	}

	protected void removeUrl(Url url) {
		urls.remove(url);
	}
	
	protected void removeAddress(Adress a) {
		adresses.remove(a);	  
  }

	protected void addAddress(Adress a) {
		adresses.add(a);	  
  }

	protected void addMail(Email email) {
		mails.add(email);
	}
	
	protected void addUrl(Url url) {
		urls.add(url);
	}

	protected void addPhone(Phone phone) {
		phones.add(phone);
	}
	
	protected void addMessenger(Messenger m) {
		messengers.add(m);
	}

	protected Phone getPhone(String simpleNumber) {
		for (Phone p : phones) {
			if (p.simpleNumber().equals(simpleNumber)) {
				return p;
			}
		}
		return null;
	}
	
	protected Adress getAddress(String canonical) {
		for (Adress a:adresses){
			if (a.canonical().equals(canonical)){
				return a;
			}
		}
	  return null;
  }
	
	protected Email getMail(String address) {
		for (Email e : mails) {
			if (e.address().equals(address)) {
				return e;
			}
		}
		return null;
	}
	
	protected Url getUrl(String address) {
		for (Url u : urls) {
			if (u.address().equals(address)) {
				return u;
			}
		}
		return null;
	}

	
	protected Messenger getMessenger(String nick) {
		for (Messenger m : messengers) {
			if (m.nick().equals(nick)) {
				return m;
			}
		}
		return null;
	}

	private JCheckBox activeBox(Object text, final Action action) {
		return activeBox(text, true, action);
	}

	private JCheckBox activeBox(Object text, boolean selected, final Action action) {
		final JCheckBox box = (text == null) ? new JCheckBox() : new JCheckBox(text.toString(), selected);
		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.change(box);
			}
		});
		return box;
	}

	private JCheckBox activeBox(Action action) {
		return activeBox(null, action);
	}

	private static JCheckBox activeCheckBox(String suffix) {
		JCheckBox result = (suffix == null) ? (new JCheckBox()) : (new JCheckBox(suffix));
		result.setSelected(true);
		result.setEnabled(false);
		return result;
	}

	private static JCheckBox activeCheckBox() {
		return activeCheckBox(null);
	}

	public TreeSet<Email> mails() {
	  return new TreeSet<Email>(mails);
  }

	public TreeSet<Phone> phones() {
	  return new TreeSet<Phone>(phones);
  }

	public TreeSet<Adress> addresses() {
	  return new TreeSet<Adress>(adresses);
  }

	public TreeSet<Messenger> messengers() {
	  return new TreeSet<Messenger>(messengers);
  }

	public TreeSet<Url> urls() {
	  return new TreeSet<Url>(urls);
  }

	public int orgCount() {
	  return orgs.size();
  }

	public int labelCount() {
	  return labels.size();
  }
}
