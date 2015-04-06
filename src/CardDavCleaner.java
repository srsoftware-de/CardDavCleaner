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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CardDavCleaner extends JFrame implements ActionListener {

	private static final long serialVersionUID = -2875331857455588061L;

	public static void main(String[] args) {
		System.setProperty("jsse.enableSNIExtension", "false");
		if (args.length > 0 && args[0].equals("--test")) {
			test();
		} else {
			CardDavCleaner cleaner = new CardDavCleaner();
			cleaner.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
	}

	private static String _(String text) {
		return Translations.get(text);
	}

	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}

	/**
	 * interactively merges the given two contacts
	 * 
	 * @param contact the first contact to be merged with the second
	 * @param contact2 the second contact to be merged with the first
	 * @param association clarifies the type of the association (key / value)
	 * @return true if contacts have been merged
	 * @throws InvalidAssignmentException the assignment variable does not contain 2 values
	 */
	private boolean mergeInteractively(Contact contact, Contact contact2,String[] association) throws InvalidAssignmentException {
		if (contact.conflictsWith(contact2)){
			if (blackListed(contact,contact2)){
				return false;
			}
			if (association.length != 2) {
				System.err.println(association);
				throw new InvalidAssignmentException("Invalid association: "+association);
			}
			VerticalPanel vp = new VerticalPanel();
			vp.add(new JLabel(_("<html>The # \"<b>#</b>\" is used by both following contacts:", new Object[] { association[0], association[1] })));
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(new JLabel("<html><br>" + contact.toString(true).replace("\n", "&nbsp<br>")));
			hp.add(new JLabel("<html><br>" + contact2.toString(true).replace("\n", "<br>")));
			hp.scale();
			vp.add(hp);
			vp.add(new JLabel(_("<html><br>Shall those contacts be <i>merged</i>?")));
			if (contact.birthday() != null && contact2.birthday() != null && !contact.birthday().equals(contact2.birthday())) {
				vp.add(new JLabel(_("<html><font color=\"red\">Warning! Those contacts contain unequal birth dates!")));
			}
			vp.scale();
			int decision = JOptionPane.showConfirmDialog(null, vp, _("Please decide!"), JOptionPane.YES_NO_CANCEL_OPTION);
			if (decision == JOptionPane.CANCEL_OPTION) System.exit(0);
			if (decision == JOptionPane.NO_OPTION){
				addToBlackList(contact,contact2);
				return false;
			}
		} else {
			System.out.println(_("auto merge # with #",new Object[]{ contact.uid(), contact2.uid()}));
		}
		return contact.mergeWith(contact2);
	}

	private boolean blackListed(Contact contact, Contact contact2) {
		TreeSet<Contact> listForContact = blackList.get(contact);
		if (listForContact==null) return false;
		return listForContact.contains(contact2);
	}

	TreeMap<Contact, TreeSet<Contact>> blackList = new TreeMap<Contact, TreeSet<Contact>>();	
	private void addToBlackList(Contact contact, Contact contact2) {
		// mapping contact => contact2
		TreeSet<Contact> listForContact = blackList.get(contact);
		if (listForContact==null){
			listForContact=new TreeSet<Contact>();
			blackList.put(contact, listForContact);
		}
		listForContact.add(contact2);
		// mapping contact2 => contact
		listForContact = blackList.get(contact2);
		if (listForContact==null){
			listForContact=new TreeSet<Contact>();
			blackList.put(contact2, listForContact);
		}
		listForContact.add(contact);
	}

	private static void test() {
		MD5Hash.test();
		Tests.test();
		MergableList.test();
		Adress.test();
		Birthday.test();
		Email.test();
		Label.test();
		Messenger.test();
		Name.test();
		Nickname.test();
		Organization.test();
		Phone.test();
		Url.test();
		Contact.test();
		try {
			System.out.println("#===================================#");
			System.out.println(_("| All tests successfully completed! |"));
			System.out.println("#===================================#");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	InputField serverField, userField, passwordField;

	private JCheckBox thunderbirdBox;

	private TreeSet<Contact> deleteList = new TreeSet<Contact>();

	public CardDavCleaner() {
		super(_("SRSoftware CardDAV cleaner"));
		createComponents();
		setVisible(true);
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
			JOptionPane.showMessageDialog(this, _("Error during server communication!"));
		}
		setVisible(false);
		System.exit(0);
	}

	private String _(String key, int response) {
		return Translations.get(key, response);
	}

	/**
	 * starts the actual scanning of contacts upon server login
	 * 
	 * @param host the hostname
	 * @param contactNames the list of contact file names
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws UnknownObjectException
	 * @throws AlreadyBoundException
	 * @throws InvalidAssignmentException
	 * @throws ToMuchEntriesForThunderbirdException
	 */
	private void cleanContacts(String host, Set<String> contactNames) throws IOException, InterruptedException, UnknownObjectException, AlreadyBoundException, InvalidAssignmentException, InvalidFormatException, ToMuchEntriesForThunderbirdException {

		Vector<Contact> contacts = readContacts(host, contactNames);

		//TreeMap<Contact, TreeSet<Contact>> blackLists = new TreeMap<Contact, TreeSet<Contact>>();
		
		// next: mark duplicates for removal
		margDuplicatesForRemoval(contacts);
		
		// next: find associations between contacts and do an interactive merge 
		mergeAssociated(contacts);
		
		if (thunderbirdBox.isSelected()) {
			thunderbirdDistibute(contacts);
		}

		// next: display changes to be made, ask for confirmation
		writeContacts(host, contacts);
	}

	private void mergeAssociated(Vector<Contact> contacts) throws InvalidAssignmentException {
		boolean repeat;
		do {
			repeat = false;
			for (Contact contact1:contacts){
				for (Contact contact2:contacts){
					if (contact1 == contact2) {
						continue;
					}
					String[] association = contact1.getAssociationWith(contact2);
					if (association != null && mergeInteractively(contact1,contact2,association)){
						contact2.setCustom(1, _("Merged into other contact"));
						deleteList.add(contact2);
						contacts.remove(contact2);
						repeat=true;
					}
					if (repeat){
						break;
					}
				}
				if (repeat){
					break;
				}
			}
			System.out.println(_("# contacts...",contacts.size()));
		} while (repeat);
	}

	private void margDuplicatesForRemoval(Vector<Contact> contacts) {
		boolean repeat;
		do {
			repeat = false;
			int num=1+(contacts.size()/100);
			int prog=0;
			for (Contact contact1:contacts){
				if (++prog % num == 0){
					System.out.print('□');
				}
				for (Contact contact2:contacts){
					
					if (contact1 == contact2){
						continue;
					}
					if (contact1.isSameAs(contact2)){
						System.out.println("\n"+_("Marked # for removal: duplicate of #.",new Object[] { contact2.uid(), contact1.uid() }));
						contact2.setCustom(1, _("Duplicate"));
						deleteList.add(contact2);
						contacts.remove(contact2);
						repeat=true;
					}
				
					if (repeat){
						break;
					}
				}
				if (repeat){
					break;
				}
			}
		} while (repeat);
		System.out.println();
	}

	private void thunderbirdDistibute(Vector<Contact> contacts) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
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
		deleteListPanel.add(new JLabel(_("<html>The following contacts will be <b>deleted</b>:")));

		VerticalPanel delList = new VerticalPanel();
		for (Contact c : deleteList)
			delList.add(new JLabel("<html><br>" + c.toString(true).replace("\n", "<br>")));
		delList.scale();

		JScrollPane sp = new JScrollPane(delList);
		sp.setPreferredSize(new Dimension(300, 300));
		sp.setSize(sp.getPreferredSize());
		deleteListPanel.add(sp);
		deleteListPanel.scale();

		VerticalPanel writeListPanel = new VerticalPanel();
		writeListPanel.add(new JLabel(_("<html>The following <b>merged contacts</b> will be written to the server:")));

		VerticalPanel wrList = new VerticalPanel();
		for (Contact c : writeList)
			wrList.add(new JLabel("<html><br>" + c.toString(true).replace("\n", "<br>")));
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
		vp.add(new JLabel(_("<html>No data has been modified on the server <b>until now</b>. Continue?")));
		vp.scale();
		int decision = JOptionPane.showConfirmDialog(null, vp, _("Please confirm"), JOptionPane.YES_NO_OPTION);
		return decision == JOptionPane.YES_OPTION;
	}

	/**
	 * creates all the components for the server login form
	 */
	private void createComponents() {

		VerticalPanel mainPanel = new VerticalPanel(_("Server settings"));

		mainPanel.add(serverField = new InputField(_("Server + Path to addressbook:"), false));
		mainPanel.add(userField = new InputField(_("User:"), false));
		mainPanel.add(passwordField = new InputField(_("Password:"), true));
		thunderbirdBox = new JCheckBox(_("<html>I use the program Thunderbird with this address book.<br>(This is important, as thunderbird only allows a limited number of phone numbers, email addresses, etc.)"));
		mainPanel.add(thunderbirdBox);
		JButton startButton = new JButton(_("start"));
		startButton.addActionListener(this);
		mainPanel.add(startButton);

		mainPanel.scale();
		add(mainPanel);
		pack();
		setVisible(true);
	}

	/**
	 * actually removes a contact from the server
	 * 
	 * @param u the url of the contact to be erased
	 * @throws IOException
	 */
	private void deleteContact(URL u) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("DELETE");
		conn.setDoOutput(true);
		conn.connect();
		int response = conn.getResponseCode();
		conn.disconnect();
		if (response != 204) {
			throw new UnexpectedException(_("Server responded with CODE #", response));
		}
	}

	/**
	 * removes the contacts in the given list from the server
	 * 
	 * @param host the hostname and path to the server contact list
	 * @param deleteList the set of contacts to be deleted
	 * @throws IOException
	 */
	private void deleteUselessContacts(String host, TreeSet<Contact> deleteList) throws IOException {
		for (Contact c : deleteList) {
			System.out.println(_("Deleting #", c.vcfName()));
			deleteContact(new URL(host + "/" + c.vcfName()));
		}
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

	private TreeSet<Contact> getWriteList(Vector<Contact> contacts) {
		TreeSet<Contact> result = new TreeSet<Contact>();
		for (Contact contact : contacts) {
			if (contact.shallBeRewritten()) {
				result.add(contact);
			}
		}
		return result;
	}

	/**
	 * writes back the modified contacts to the server
	 * 
	 * @param host the hostname and path to write to
	 * @param writeList the set of contacts to upload
	 * @throws IOException
	 */
	private void putMergedContacts(String host, TreeSet<Contact> writeList) throws IOException {
		for (Contact c : writeList) {

			System.out.println(_("Uploading #", c.vcfName()));

			byte[] data = c.getBytes();
			URL putUrl = new URL(host + "/" + c.vcfName());
			HttpURLConnection conn = (HttpURLConnection) putUrl.openConnection();
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/vcard");
			conn.connect();
			OutputStream out = conn.getOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			int read = -1;

			while ((read = in.read()) != -1)
				out.write(read);
			out.close();
			int response = conn.getResponseCode();
			conn.disconnect();

			if (response < 200 || response > 299) {
				System.out.println(_("...not successful (# / #). Trying to remove first...", new Object[] { response, conn.getResponseMessage() }));

				/* the next two lines have been added to circumvent the problem, that on some caldav servers, entries can not simply be overwritten */
				deleteContact(new URL(host + "/" + c.vcfName()));
				c.generateName();

				data = c.getBytes();
				putUrl = new URL(host + "/" + c.vcfName());
				conn = (HttpURLConnection) putUrl.openConnection();
				conn.setRequestMethod("PUT");
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "text/vcard");
				conn.connect();
				out = conn.getOutputStream();
				in = new ByteArrayInputStream(data);
				read = -1;

				while ((read = in.read()) != -1)
					out.write(read);
				out.close();
				response = conn.getResponseCode();
				conn.disconnect();
				if (response < 200 || response > 299) {
					File f = c.writeToFile();
					JOptionPane.showMessageDialog(this, _("<html>Sorry! Unfortunateley, i was not able to write a file to the WebDAV server.<br>But don't worry, i created a <b>Backup</b> of the file at #", f.getAbsolutePath()));
					throw new UnexpectedException(_("Server responded with CODE ", response));
				}
			}
			System.out.println(_("...success!"));
		}
	}

	private Vector<Contact> readContacts(String host, Set<String> contactNamess) throws IOException, AlreadyBoundException {
		int total = contactNamess.size();
		int counter = 0;
		Vector<Contact> contacts = new Vector<Contact>();
		// Next: read all contacts, remember contacts that contain nothing but a name
		for (String contactName : contactNamess) {
			System.out.println(_("reading contact #/#: #", new Object[] { ++counter, total, contactName }));
			try {
				Contact contact = new Contact(host, contactName);
				do {
					if (skipInvalidContact(contact)) break;
					;
					if (contact.isEmpty()) {
						contact.clearFields();
						deleteList.add(contact);
						contact.setCustom(1, _("empty contact"));
						System.out.println(_("Warning: skipping empty contact # (Contains nothing but a name)", contact.vcfName()));
					} else {
						contacts.add(contact);
					}
					contact = contact.getClonedContactIfExists();
				} while (contact != null);
			} catch (UnknownObjectException uoe) {
				uoe.printStackTrace();
				JOptionPane.showMessageDialog(null, _("Found unknown object in vCard (" + contactName + "): ") + uoe.getMessage());
			} catch (InvalidFormatException ife) {
				ife.printStackTrace();
				JOptionPane.showMessageDialog(null, ife.getMessage());
			}
		}
		return contacts;
	}

	private boolean skipInvalidContact(Contact contact) {
		while (contact.isInvalid()) {
			String[] options = { _("Edit manually"), _("Skip"), _("Abort program") };
			int opt = JOptionPane.showOptionDialog(null, _("# has an invalid format", contact.vcfName()), _("Invalid Contact"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			switch (opt) {
			case 0:
				if (contact.edited()) {
					if (contact.isEmpty()) {
						contact.setCustom(1, _("emtpy contact"));
						deleteList.add(contact);
					} else {
						contact.markForRewrite();
					}
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
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 */
	private void startCleaning(String host, final String user, final String password) throws IOException, InterruptedException, UnknownObjectException, AlreadyBoundException, InvalidAssignmentException, InvalidFormatException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});

		if (!host.endsWith("/")) host += "/";
		URL url = new URL(host);

		HttpURLConnection connection = null;
		if (host.startsWith("https")) {
			// here we set a socket factory, which uses our own trust manager
			HttpsURLConnection.setDefaultSSLSocketFactory(TrustHandler.getSocketFactory());
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
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
			cleanContacts(host, contacts);
		} catch (SSLHandshakeException ve) {
			JOptionPane.showMessageDialog(this, _("Sorry, i was not able to establish a secure connection to this server. I will quit now."));
		} catch (ToMuchEntriesForThunderbirdException e) {
			JOptionPane.showMessageDialog(this, _("<html>#<br>Will abort operation now.", e.getMessage()));
			System.exit(-1);
		}
	}

	private void writeContacts(String host, Vector<Contact> contacts) throws IOException {
		TreeSet<Contact> writeList = getWriteList(contacts);
		if (!(writeList.isEmpty() && deleteList.isEmpty())) {
			if (confirmLists(writeList, deleteList)) {
				//putMergedContacts(host, writeList);
				//deleteUselessContacts(host, deleteList);
				JOptionPane.showMessageDialog(null, _("<html>Scanning, merging and cleaning <i>successfully</i> done! Goodbye!"));
			} else {
				JOptionPane.showMessageDialog(null, _("<html>Merging and cleaning aborted! Goodbye!"));
			}
		} else {
			JOptionPane.showMessageDialog(null, _("<html>Nothing to do. Your adress book is either empty or well sorted!!"));
		}
	}
}
