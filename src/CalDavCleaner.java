import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.activation.UnknownObjectException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CalDavCleaner extends JFrame implements ActionListener {

	private JTextField serverField;
	private JTextField userField;
	private JPasswordField passwordField;

	public CalDavCleaner() {
		super();
		createComponents();
		setVisible(true);
	}

	private void createComponents() {
		VerticalPanel mainPanel = new VerticalPanel("Server settings");

		serverField = addInput(mainPanel, "Server:");
		serverField.setText("http://kommune10.dyndns.info:815/cloud/remote.php/carddav/addressbooks/srichter/standard");
		userField = addInput(mainPanel, "User:");
		userField.setText("srichter");
		passwordField = addPassword(mainPanel, "Password:");

		JButton startButton = new JButton("start");
		startButton.addActionListener(this);
		mainPanel.add(startButton);

		mainPanel.skalieren();
		add(mainPanel);
		pack();
		setVisible(true);
	}

	private JPasswordField addPassword(VerticalPanel mainPanel, String text) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel(text + " "));
		JPasswordField result = new JPasswordField(50);
		hp.add(result);
		hp.skalieren();
		mainPanel.add(hp);
		return result;
	}

	private JTextField addInput(VerticalPanel mainPanel, String text) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel(text + " "));
		JTextField result = new JTextField(50);
		hp.add(result);
		hp.skalieren();
		mainPanel.add(hp);
		return result;
	}

	public static void main(String[] args) {
		CalDavCleaner cleaner = new CalDavCleaner();
		cleaner.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent arg0) {
		try {
			startCleaning(serverField.getText(), userField.getText(), new String(passwordField.getPassword()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void startCleaning(String host, final String user, final String password) throws IOException, InterruptedException, UnknownObjectException, AlreadyBoundException, InvalidAssignmentException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});

		if (!host.endsWith("/")) host += "/";
		URL url = new URL(host);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		TreeSet<String> contacts = new TreeSet<String>(ObjectComparator.get());
		while ((line = in.readLine()) != null) {
			if (line.contains(".vcf")) contacts.add(extractContactName(line));
		}
		in.close();
		content.close();
		connection.disconnect();

		scanContacts(host, contacts);
	}

	private void scanContacts(String host, Set<String> contactNamess) throws IOException, InterruptedException, UnknownObjectException, AlreadyBoundException, InvalidAssignmentException {
		Vector<Contact> contacts = new Vector<Contact>();
		int total = contactNamess.size();
		int counter = 0;
		for (String contactName : contactNamess) {
			System.out.println(++counter + "/" + total);
			Contact contact = new Contact(new URL(host + contactName));
			if (contact.isEmpty()) {
				System.out.println("Waring: skipping empty contact " + contactName);
			} else
				contacts.add(contact);
		}

		TreeMap<Contact, TreeSet<Contact>> blackLists = new TreeMap<Contact, TreeSet<Contact>>(ObjectComparator.get());
		TreeMap<String, TreeSet<Contact>> nameMap; // one name may map to multiple contacts, as multiple persons may have the same name
		TreeMap<String, TreeSet<Contact>> numberMap; // on number can be used by multiple persons, as people living together may share a landline number
		TreeMap<String, Contact> mailMap; 
		boolean restart;
		do {

			restart = false;
			nameMap = new TreeMap<String, TreeSet<Contact>>(ObjectComparator.get());
			numberMap = new TreeMap<String, TreeSet<Contact>>(ObjectComparator.get());
			mailMap = new TreeMap<String, Contact>(ObjectComparator.get());
			total = contacts.size();
			int index = 0;
			for (Contact contact : contacts) {
				System.out.println((++index) + "/" + total);
				System.out.println(contact);

				TreeSet<Contact> blacklist = blackLists.get(contact);

				/************* name *****************/
				Name name = contact.name();
				if (name != null) { // we can only do name comparison for contacts with name...
					String canonicalName = name.canonical();
					TreeSet<Contact> contactsForName = nameMap.get(canonicalName);

					if (contactsForName == null) { // if we didn't have contacts with this name before, we can't compare.
						contactsForName = new TreeSet<Contact>(ObjectComparator.get());
						contactsForName.add(contact); // add a mapping for this contacts name
						nameMap.put(canonicalName, contactsForName);
					} else { // this name appeared before:
						for (Contact existingContact : contactsForName) {
							if (blacklist != null && blacklist.contains(existingContact)) continue;

							// if this contact pair is not blacklisted:
							if (askForMege("name", canonicalName, contact, existingContact)) {
								contact.merge(existingContact);
								contactsForName.remove(existingContact);
								contacts.remove(existingContact);
								restart = true;
								break; // this has to be done, as contactsForName changed
							} else { // if merging was denied: add contact pair to blacklist
								if (blacklist == null) {
									blacklist = new TreeSet<Contact>(ObjectComparator.get());
									blackLists.put(contact, blacklist);
								}
								blacklist.add(existingContact);
							}
						} // ---> for (Contact existingContact : contactsForName)
						if (restart) break; // this has to be done, as contacts changed
						
					}
				} // ---> if (name != null)
				/************* name *****************/
				/************* phone ****************/
				TreeSet<String> numbers = contact.phoneNumbers();
				for (String number:numbers){
					TreeSet<Contact> contactsForNumber = numberMap.get(number);
					if (contactsForNumber==null){
						contactsForNumber=new TreeSet<Contact>(ObjectComparator.get());
						contactsForNumber.add(contact);
						numberMap.put(number, contactsForNumber);
					} else {
						for (Contact existingContact:contactsForNumber){
							if (blacklist != null && blacklist.contains(existingContact)) continue;

							// if this contact pair is not blacklisted:
							if (askForMege("phone number", number, contact, existingContact)) {
								contact.merge(existingContact);
								contactsForNumber.remove(existingContact);
								contacts.remove(existingContact);
								restart = true;
								break; // this has to be done, as contactsForName changed
							} else { // if merging was denied: add contact pair to blacklist
								if (blacklist == null) {
									blacklist = new TreeSet<Contact>(ObjectComparator.get());
									blackLists.put(contact, blacklist);
								}
								blacklist.add(existingContact);
							}
						}
						if (restart) break;
					}
				}
				if (restart) break;				
				/************* phone ****************/
				/************* email ****************/
				TreeSet<String> mails = contact.mailAdresses();
				for (String mail:mails){
					Contact existingContact = mailMap.get(mail);
					if (existingContact==null){
						existingContact=contact;
						mailMap.put(mail, contact);
					} else {
						if (blacklist != null && blacklist.contains(existingContact)) continue;

							// if this contact pair is not blacklisted:
						if (askForMege("e-mail", mail, contact, existingContact)) {
							contact.merge(existingContact);
							contacts.remove(existingContact);
							restart = true;
							break; // this has to be done, as contactsForName changed
						} else { // if merging was denied: add contact pair to blacklist
							if (blacklist == null) {
								blacklist = new TreeSet<Contact>(ObjectComparator.get());
								blackLists.put(contact, blacklist);
							}
							blacklist.add(existingContact);							
						}
						if (restart) break;
					}
				}
				if (restart) break;				
				/************* email ****************/
				
			} // for
		} while (restart);
	}

	private boolean askForMege(String identifier, String name, Contact contact, Contact contact2) throws InterruptedException {
		if (!contact.conflictsWith(contact2)) return true;
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel("The " + identifier + " \"" + name + "\" is used by both following contacts:"));
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel("<html><br>" + contact.toString(true).replace("\n", "&nbsp<br>")));
		hp.add(new JLabel("<html><br>" + contact2.toString(true).replace("\n", "<br>")));
		hp.skalieren();
		vp.add(hp);
		vp.add(new JLabel("<html><br>Shall those contacts be merged?"));
		vp.skalieren();
		int decision = JOptionPane.showConfirmDialog(null, vp, "Please decide!", JOptionPane.YES_NO_CANCEL_OPTION);
		if (decision == JOptionPane.CANCEL_OPTION) System.exit(0);
		return decision == JOptionPane.YES_OPTION;
	}

	private String extractContactName(String line) {
		String[] parts = line.split("/|\"");
		for (String part : parts) {
			if (part.contains("vcf")) {
				return part;
			}
		}
		return null;
	}
}
