import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.UnexpectedException;
import java.rmi.activation.UnknownObjectException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class CardDavCleaner extends JFrame implements ActionListener {

	InputField serverField, userField, passwordField;
	private JCheckBox thunderbirdBox;
	private static final long serialVersionUID = -2875331857455588061L;

	public CardDavCleaner() {
		super("SRSoftware CardDAV cleaner");
		createComponents();
		setVisible(true);
	}

	/**
	 * creates all the components for the server login form
	 */
	private void createComponents() {

		VerticalPanel mainPanel = new VerticalPanel("Server settings");

		mainPanel.add(serverField = new InputField("Server + Path to addressbook:", false));
		mainPanel.add(userField = new InputField("User:", false));
		mainPanel.add(passwordField = new InputField("Password:", true));
		thunderbirdBox = new JCheckBox("<html>I use Thunderbird with this address book.<br>(This is important, as thunderbird only allows a limited number of phone numbers, email addresses, etc.)");
		mainPanel.add(thunderbirdBox);
		JButton startButton = new JButton("start");
		startButton.addActionListener(this);
		mainPanel.add(startButton);

		mainPanel.scale();
		add(mainPanel);
		pack();
		setVisible(true);
	}

	/**
	 * tries to log in to the server using the given credentials and scans the contacts
	 * 
	 * @param host the server hostname
	 * @param user the username used to log in
	 * @param password the password corrosponding to the username
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws UnknownObjectException
	 * @throws AlreadyBoundException
	 * @throws InvalidAssignmentException
	 */
	private void startCleaning(String host, final String user, final String password) throws IOException, InterruptedException, UnknownObjectException, AlreadyBoundException, InvalidAssignmentException, InvalidFormatException {
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
		TreeSet<String> contacts = new TreeSet<String>();
		while ((line = in.readLine()) != null) {
			if (line.contains(".vcf")) contacts.add(extractContactName(line));
		}
		in.close();
		content.close();
		connection.disconnect();

		try {
			cleanContacts(host, contacts);
		} catch (ToMuchEntriesForThunderbirdException e) {
			JOptionPane.showMessageDialog(this, "<html>" + e.getMessage() + "<br>Will abort operation now.");
			System.exit(-1);
		}
	}

	/**
	 * starts the actual scanning of contacts upon server login
	 * 
	 * @param host the hostname
	 * @param contactNamess the list of contact file names
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws UnknownObjectException
	 * @throws AlreadyBoundException
	 * @throws InvalidAssignmentException
	 * @throws ToMuchEntriesForThunderbirdException
	 */
	private void cleanContacts(String host, Set<String> contactNamess) throws IOException, InterruptedException, UnknownObjectException, AlreadyBoundException, InvalidAssignmentException, InvalidFormatException, ToMuchEntriesForThunderbirdException {

		Vector<Contact> contacts = readContacts(host, contactNamess);

		// next: find and merge related contacts
		TreeMap<Contact, TreeSet<Contact>> blackLists = new TreeMap<Contact, TreeSet<Contact>>();
		cleanByPhone(contacts, blackLists);
		cleanByEmail(contacts, blackLists);
		cleanByMessenger(contacts, blackLists);
		cleanByName(contacts, blackLists);

		// next: display changes to be made, ask for confirmation
		writeContacts(host, contacts);
	}

	private void cleanByName(Vector<Contact> contacts, TreeMap<Contact, TreeSet<Contact>> blackLists) throws InterruptedException, InvalidAssignmentException, ToMuchEntriesForThunderbirdException {
		TreeMap<String, TreeSet<Contact>> phoneMap = new TreeMap<String, TreeSet<Contact>>();
		for (Contact contact : contacts) {
			TreeSet<Contact> blacklistForContact = blackLists.get(contact);
			TreeSet<String> names = new TreeSet<String>();
			names.add(contact.name().canonical());
			names.addAll(contact.nicknames());
			for (String name : names) {
				TreeSet<Contact> contactsForName = phoneMap.get(name);
				if (contactsForName == null) {
					contactsForName = new TreeSet<Contact>();
					contactsForName.add(contact);
					phoneMap.put(name, contactsForName);
				} else { // we already have one or more contact with this mail address
					for (Contact existingContact : contactsForName) {
						if (blacklistForContact != null && blacklistForContact.contains(existingContact)) {
							continue;// this contact pair is blacklisted, go on to next contact
						}
						// if this contact pair is not blacklisted:
						if (askForMege("name", name, contact, existingContact)) {
							contact.mergeWith(existingContact, thunderbirdBox.isSelected());
							contacts.remove(existingContact);
							existingContact.markForDeletion();
							cleanByName(contacts, blackLists);
							return;
						} else { // if merging was denied: add contact pair to blacklist
							if (blacklistForContact == null) {
								blacklistForContact = new TreeSet<Contact>();
								blackLists.put(contact, blacklistForContact);
							}
							blacklistForContact.add(existingContact);
						}
					}
				}
			}
		}
	}

	private void cleanByPhone(Vector<Contact> contacts, TreeMap<Contact, TreeSet<Contact>> blackLists) throws InterruptedException, InvalidAssignmentException, ToMuchEntriesForThunderbirdException {
			TreeMap<String, TreeSet<Contact>> phoneMap = new TreeMap<String, TreeSet<Contact>>();
			for (Contact contact : contacts) {
				TreeSet<Contact> blacklistForContact = blackLists.get(contact);
				TreeSet<String> numbers = contact.simpleNumbers();
				for (String number : numbers) {
					TreeSet<Contact> contactsForNumber = phoneMap.get(number);
					if (contactsForNumber == null) {
						contactsForNumber = new TreeSet<Contact>();
						contactsForNumber.add(contact);
						phoneMap.put(number, contactsForNumber);
					} else { // we already have one or more contact with this mail address
						for (Contact existingContact : contactsForNumber) {
							if (blacklistForContact != null && blacklistForContact.contains(existingContact)) {
								continue;// this contact pair is blacklisted, go on to next contact
							}
							// if this contact pair is not blacklisted:
							if (askForMege("phone number", number, contact, existingContact)) {
								contact.mergeWith(existingContact, thunderbirdBox.isSelected());
								contacts.remove(existingContact);
								existingContact.markForDeletion();
								cleanByPhone(contacts, blackLists);
								return;
							} else { // if merging was denied: add contact pair to blacklist
								if (blacklistForContact == null) {
									blacklistForContact = new TreeSet<Contact>();
									blackLists.put(contact, blacklistForContact);
								}
								blacklistForContact.add(existingContact);
							}
						}
					}
				}
			}
	}

	private void cleanByEmail(Vector<Contact> contacts, TreeMap<Contact, TreeSet<Contact>> blackLists) throws InterruptedException, InvalidAssignmentException, ToMuchEntriesForThunderbirdException {
			TreeMap<String, TreeSet<Contact>> mailMap = new TreeMap<String, TreeSet<Contact>>();
			for (Contact contact : contacts) {
				TreeSet<Contact> blacklistForContact = blackLists.get(contact);
				TreeSet<String> emails = contact.mailAdresses();
				for (String email : emails) {
					TreeSet<Contact> contactsForEmail = mailMap.get(email);
					if (contactsForEmail == null) {
						contactsForEmail = new TreeSet<Contact>();
						contactsForEmail.add(contact);
						mailMap.put(email, contactsForEmail);
					} else { // we already have one or more contact with this mail address
						for (Contact existingContact : contactsForEmail) {
							if (blacklistForContact != null && blacklistForContact.contains(existingContact)) {
								continue;// this contact pair is blacklisted, go on to next contact
							}
							// if this contact pair is not blacklisted:
							if (askForMege("e-mail", email, contact, existingContact)) {
								contact.mergeWith(existingContact, thunderbirdBox.isSelected());
								contacts.remove(existingContact);
								existingContact.markForDeletion();
								cleanByEmail(contacts, blackLists);
								return;
							} else { // if merging was denied: add contact pair to blacklist
								if (blacklistForContact == null) {
									blacklistForContact = new TreeSet<Contact>();
									blackLists.put(contact, blacklistForContact);
								}
								blacklistForContact.add(existingContact);
							}
						}
					}
				}
			}

	}

	private void cleanByMessenger(Vector<Contact> contacts, TreeMap<Contact, TreeSet<Contact>> blackLists) throws InterruptedException, InvalidAssignmentException, ToMuchEntriesForThunderbirdException, UnknownObjectException {
			TreeMap<String, TreeSet<Contact>> messengerMap = new TreeMap<String, TreeSet<Contact>>();
			for (Contact contact : contacts) {
				TreeSet<Contact> blacklistForContact = blackLists.get(contact);
				TreeSet<String> mIDs = contact.messengerNicks();
				for (String messengerID : mIDs) {
					TreeSet<Contact> contactsForMessengerID = messengerMap.get(messengerID);
					if (contactsForMessengerID == null) {
						contactsForMessengerID = new TreeSet<Contact>();
						contactsForMessengerID.add(contact);
						messengerMap.put(messengerID, contactsForMessengerID);
					} else { // we already have one or more contact with this messenger
						for (Contact existingContact : contactsForMessengerID) {
							if (blacklistForContact != null && blacklistForContact.contains(existingContact)) {
								continue;// this contact pair is blacklisted, go on to next contact
							}
							// if this contact pair is not blacklisted:
							if (askForMege("messenger", messengerID, contact, existingContact)) {
								contact.mergeWith(existingContact, thunderbirdBox.isSelected());
								contacts.remove(existingContact);
								existingContact.markForDeletion();
								cleanByMessenger(contacts, blackLists);
								return;
							} else { // if merging was denied: add contact pair to blacklist
								if (blacklistForContact == null) {
									blacklistForContact = new TreeSet<Contact>();
									blackLists.put(contact, blacklistForContact);
								}
								blacklistForContact.add(existingContact);
							}
						}
					}
				}
			}
	}

	private void writeContacts(String host, Vector<Contact> contacts) throws IOException {
		TreeSet<Contact> deleteList = getDeletionList(contacts);
		TreeSet<Contact> writeList = getWriteList(contacts);
		if (!(writeList.isEmpty() && deleteList.isEmpty())) {
			if (confirmLists(writeList, deleteList)) {
				JOptionPane.showMessageDialog(null, "<html>Scanning, merging and cleaning <i>successfully</i> done! Goodbye!");
			} else {
				JOptionPane.showMessageDialog(null, "<html>Merging and cleaning aborted! Goodbye!");
			}
		} else {
			JOptionPane.showMessageDialog(null, "<html>Nothing to do. Your adress book is either empty or well sorted!!");
		}
	}

	private Vector<Contact> readContacts(String host, Set<String> contactNamess) throws IOException, AlreadyBoundException {
		int total = contactNamess.size();
		int counter = 0;
		Vector<Contact> contacts = new Vector<Contact>();
		// Next: read all contacts, remember contacts that contain nothing but a name
		for (String contactName : contactNamess) {
			System.out.println("reading contact " + (++counter) + "/" + total + ": " + contactName);
			try {
				Contact contact = new Contact(host, contactName);
				if (skipInvalidContact(contact, contactName)) continue;
				if (contact.isEmpty()) {
					contact.markForDeletion();
					System.out.println("Warning: skipping empty contact " + contactName + " (Contains nothing but a name)");
				} else {
					contacts.add(contact);
				}
			} catch (UnknownObjectException uoe) {
				uoe.printStackTrace();
				JOptionPane.showMessageDialog(null, uoe.getMessage());
			} catch (InvalidFormatException ife) {
				ife.printStackTrace();
				JOptionPane.showMessageDialog(null, ife.getMessage());
			}
		}
		return contacts;
	}

	private TreeSet<Contact> getWriteList(Vector<Contact> contacts) {
		TreeSet<Contact> result = new TreeSet<Contact>();
		for (Contact contact : contacts) {
			if (contact.shallBeRewritten()) {
				result.add(contact);
			}
		}
		return result;
	}

	private TreeSet<Contact> getDeletionList(Collection<Contact> contacts) {
		TreeSet<Contact> result = new TreeSet<Contact>();
		for (Contact contact : contacts) {
			if (contact.shallBeDeleted()) {
				result.add(contact);
			}
		}
		return result;
	}

	private boolean skipInvalidContact(Contact contact, String contactName) {
		while (contact.isInvalid()) {
			String[] options = { "Edit manually", "Skip", "Abort program" };
			int opt = JOptionPane.showOptionDialog(null, contactName + " has an invalid format", "Invalid Contact", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			switch (opt) {
			case 0:
				if (contact.edited()) {
					contact.markForRewrite();
				}
				break;
			case 1:
				return true;
			default:
				System.exit(-1);
			}
		}
		return false;
	}

	/**
	 * subsumes changes to be performed and asks usert to confirm to apply those changes
	 * 
	 * @param writeList list of contacts to be written to the server
	 * @param deleteList list of contacts to be DELETED from the server
	 * @return true, only if the user has confirmed to propagate the suggested changes
	 */
	private boolean confirmLists(TreeSet<Contact> writeList, TreeSet<Contact> deleteList) {
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel listsPanel = new HorizontalPanel();

		VerticalPanel deleteListPanel = new VerticalPanel();
		deleteListPanel.add(new JLabel("<html>The following contacts would be <b>deleted</b> by the full version:"));

		VerticalPanel delList = new VerticalPanel();
		for (Contact c:deleteList) delList.add(new JLabel("<html><br>"+c.toString(true).replace("\n","<br>")));
		delList.scale();

		JScrollPane sp = new JScrollPane(delList);
		sp.setPreferredSize(new Dimension(300, 300));
		sp.setSize(sp.getPreferredSize());
		deleteListPanel.add(sp);
		deleteListPanel.scale();

		VerticalPanel writeListPanel = new VerticalPanel();
		writeListPanel.add(new JLabel("<html>The following <b>merged contacts</b> would be written to the server by the full version:"));

		VerticalPanel wrList = new VerticalPanel();
		for (Contact c:writeList) wrList.add(new JLabel("<html><br>"+c.toString(true).replace("\n","<br>")));
		wrList.scale();

		JScrollPane sp2 = new JScrollPane(wrList);
		sp2.setPreferredSize(new Dimension(300, 300));
		sp2.setSize(sp2.getPreferredSize());
		writeListPanel.add(sp2);
		writeListPanel.scale();

		listsPanel.add(deleteListPanel);
		listsPanel.add(writeListPanel);
		listsPanel.scale();

		vp.add(listsPanel);
		vp.add(new JLabel("<html>No data has been modified on the server <b>until now</b>. Continue?"));
		vp.scale();
		int decision = JOptionPane.showConfirmDialog(null, vp, "Please confirm", JOptionPane.YES_NO_OPTION);
		return decision == JOptionPane.YES_OPTION;
	}

	/**
	 * asks, whether the given contacts shall be merged
	 * 
	 * @param identifier as string to clarify, which contacts may be merged
	 * @param name the name of the person
	 * @param contact the first contact to be merged with the second
	 * @param contact2 the second contact to be merged with the first
	 * @return true, only if, the user confirms to the merging
	 * @throws InterruptedException
	 */
	private boolean askForMege(String identifier, String name, Contact contact, Contact contact2) throws InterruptedException {
		if (!contact.conflictsWith(contact2)) return true;
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel("<html>The " + identifier + " \"<b>" + name + "</b>\" is used by both following contacts:"));
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel("<html><br>" + contact.toString(true).replace("\n", "&nbsp<br>")));
		hp.add(new JLabel("<html><br>" + contact2.toString(true).replace("\n", "<br>")));
		hp.scale();
		vp.add(hp);
		vp.add(new JLabel("<html><br>Shall those contacts be <i>merged</i>?"));
		if (contact.birthday()!=null && contact2.birthday()!=null && !contact.birthday().equals(contact2.birthday())){
			vp.add(new JLabel("<html><font color=\"red\">Warning! Those contacts contain unequal birth dates!"));
		}
		vp.scale();
		int decision = JOptionPane.showConfirmDialog(null, vp, "Please decide!", JOptionPane.YES_NO_CANCEL_OPTION);
		if (decision == JOptionPane.CANCEL_OPTION) System.exit(0);
		return decision == JOptionPane.YES_OPTION;
	}

	/**
	 * extracts a person's name from a vcard line
	 * 
	 * @param line the vcard text containing a name
	 * @return the extracted name
	 */
	private String extractContactName(String line) {
		String[] parts = line.split("/|\"");
		for (String part : parts) {
			if (part.contains("vcf")) return part;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
			startCleaning(serverField.getText(), userField.getText(), new String(passwordField.getText()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setVisible(false);
		System.exit(0);
	}

	public static void main(String[] args) {
		CardDavCleaner cleaner = new CardDavCleaner();
		cleaner.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
