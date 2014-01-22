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

	InputField serverField,userField, passwordField;
	private JCheckBox thunderbirdBox;
  private static final long serialVersionUID = -2875331857455588061L;

  public CardDavCleaner() {
		super();
		createComponents();
		setVisible(true);
	}

	/**
	 * creates all the components for the server login form
	 */
	private void createComponents() {
		
		VerticalPanel mainPanel = new VerticalPanel("Server settings");

		mainPanel.add(serverField = new InputField("Server + Path to addressbook:",false));
		mainPanel.add(userField = new InputField("User:",false));
		mainPanel.add(passwordField = new InputField("Password:",true));
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
			scanContacts(host, contacts);
		} catch (ToMuchEntriesForThunderbirdException e) {
			JOptionPane.showMessageDialog(this, "<html>"+e.getMessage()+"<br>Will abort operation now.");
			System.exit(-1);
		}
	}

	/**
	 * starts the actual scanning of contacts upon server login
	 * @param host the hostname
	 * @param contactNamess the list of contact file names
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws UnknownObjectException
	 * @throws AlreadyBoundException
	 * @throws InvalidAssignmentException
	 * @throws ToMuchEntriesForThunderbirdException 
	 */
	private void scanContacts(String host, Set<String> contactNamess) throws IOException, InterruptedException, UnknownObjectException, AlreadyBoundException, InvalidAssignmentException, InvalidFormatException, ToMuchEntriesForThunderbirdException {
		TreeSet<Contact> writeList=new TreeSet<Contact>();
		TreeSet<Contact> deleteList=new TreeSet<Contact>();
		Vector<Contact> contacts = new Vector<Contact>();
		int total = contactNamess.size();
		int counter = 0;
		
		// Next: read all contacts, remember contacts that contain nothing but a name
		for (String contactName : contactNamess) {
			System.out.println("reading contact "+(++counter) + "/" + total+": "+contactName);
			try {
				Contact contact = new Contact(host,contactName);				
				if (skipInvalidContact(contact,contactName,writeList)) continue;
				if (contact.isEmpty()) {
					deleteList.add(contact);
					System.out.println("Warning: skipping empty contact " + contactName+ " (Contains nothing but a name)");
				} else {
					contacts.add(contact);
				}
			} catch (UnknownObjectException uoe){
				uoe.printStackTrace();
				JOptionPane.showMessageDialog(null, uoe.getMessage());
			} catch (InvalidFormatException ife){
				ife.printStackTrace();
				JOptionPane.showMessageDialog(null, ife.getMessage());
			}
		}
	
		// next: find and merge related contacts
		TreeMap<Contact, TreeSet<Contact>> blackLists = new TreeMap<Contact, TreeSet<Contact>>();
		TreeMap<String, TreeSet<Contact>> nameMap; // one name may map to multiple contacts, as multiple persons may have the same name
		TreeMap<String, TreeSet<Contact>> numberMap; // on number can be used by multiple persons, as people living together may share a landline number
		TreeMap<String, Contact> mailMap;
		TreeMap<String, Contact> messengerMap;
		boolean restart;
		do {
			restart = false;
			nameMap = new TreeMap<String, TreeSet<Contact>>();
			numberMap = new TreeMap<String, TreeSet<Contact>>();
			mailMap = new TreeMap<String, Contact>();
			messengerMap = new TreeMap<String, Contact>();
			total = contacts.size();
			for (Contact contact : contacts) {
				TreeSet<Contact> blacklist = blackLists.get(contact);

				/************* name *****************/
				Name name = contact.name();
				if (name != null) { // we can only do name comparison for contacts with name...
					String canonicalName = name.canonical();
					TreeSet<Contact> contactsForName = nameMap.get(canonicalName);

					if (contactsForName == null) { // if we didn't have contacts with this name before, we can't compare.
						contactsForName = new TreeSet<Contact>();
						contactsForName.add(contact); // add a mapping for this contacts name
						nameMap.put(canonicalName, contactsForName);
					} else { // this name appeared before:
						for (Contact existingContact : contactsForName) {
							if (blacklist != null && blacklist.contains(existingContact)) continue;

							// if this contact pair is not blacklisted:
							if (askForMege("name", canonicalName, contact, existingContact)) {
								contact.merge(existingContact,thunderbirdBox.isSelected());
								writeList.add(contact);
								writeList.remove(existingContact);
								contactsForName.remove(existingContact);
								deleteList.add(existingContact);
								contacts.remove(existingContact);
								restart = true;
								break; // this has to be done, as contactsForName changed
							} else { // if merging was denied: add contact pair to blacklist
								if (blacklist == null) {
									blacklist = new TreeSet<Contact>();
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
				TreeSet<String> numbers = contact.simpleNumbers();
				for (String number:numbers){
					TreeSet<Contact> contactsForNumber = numberMap.get(number);
					if (contactsForNumber==null){
						contactsForNumber=new TreeSet<Contact>();
						contactsForNumber.add(contact);						
						numberMap.put(number, contactsForNumber);
					} else {
						for (Contact existingContact:contactsForNumber){
							if (blacklist != null && blacklist.contains(existingContact)) continue;

							// if this contact pair is not blacklisted:
							if (askForMege("phone number", number, contact, existingContact)) {
								contact.merge(existingContact,thunderbirdBox.isSelected());
								writeList.add(contact);
								writeList.remove(existingContact);
								contactsForNumber.remove(existingContact);
								deleteList.add(existingContact);
								contacts.remove(existingContact);
								restart = true;
								break; // this has to be done, as contactsForName changed
							} else { // if merging was denied: add contact pair to blacklist
								if (blacklist == null) {
									blacklist = new TreeSet<Contact>();
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
							contact.merge(existingContact,thunderbirdBox.isSelected());
							contacts.remove(existingContact);
							writeList.add(contact);
							writeList.remove(existingContact);
							deleteList.add(existingContact);
							restart = true;
							break; // this has to be done, as contactsForName changed
						} else { // if merging was denied: add contact pair to blacklist
							if (blacklist == null) {
								blacklist = new TreeSet<Contact>();
								blackLists.put(contact, blacklist);
							}
							blacklist.add(existingContact);							
						}
						if (restart) break;
					}
				}
				if (restart) break;				
				/************* email ****************/
				
				/************* messenger *****************/
				TreeSet<String> messsengers = contact.messengers();
				for (String messenger:messsengers){
					Contact existingMessenger = messengerMap.get(messenger);
					if (existingMessenger==null){
						existingMessenger=contact;
						mailMap.put(messenger, contact);
					} else {
						if (blacklist != null && blacklist.contains(existingMessenger)) continue;

							// if this contact pair is not blacklisted:
						if (askForMege("messenger", messenger, contact, existingMessenger)) {
							contact.merge(existingMessenger,thunderbirdBox.isSelected());
							contacts.remove(existingMessenger);
							writeList.add(contact);
							writeList.remove(existingMessenger);
							deleteList.add(existingMessenger);
							restart = true;
							break;
						} else { // if merging was denied: add contact pair to blacklist
							if (blacklist == null) {
								blacklist = new TreeSet<Contact>();
								blackLists.put(contact, blacklist);
							}
							blacklist.add(existingMessenger);							
						}
						if (restart) break;
					}
				}
				if (restart) break;
				/************* messenger *****************/
			} // for
		} while (restart);
		
		// next: display changes to be made, ask for confirmation		
		if (!(writeList.isEmpty() && deleteList.isEmpty())){ 
			if (confirmLists(writeList,deleteList)){
				putMergedContacts(host,writeList);
				deleteUselessContacts(host,deleteList);
				JOptionPane.showMessageDialog(null, "<html>Scanning, merging and cleaning <i>successfully</i> done! Goodbye!");
			} else {
				JOptionPane.showMessageDialog(null, "<html>Merging and cleaning aborted! Goodbye!");
			}
		} else {
			JOptionPane.showMessageDialog(null, "<html>Nothing to do. Your adress book is either empty or well sorted!!");

		}
		setVisible(false);
		System.exit(0);
		
		
	}

	private boolean skipInvalidContact(Contact contact,String contactName, TreeSet<Contact> writeList) {
		while (contact.isInvalid()){
			String [] options={"Edit manually","Skip","Abort program"};
			int opt=JOptionPane.showOptionDialog(null, contactName+" has an invalid format", "Invalid Contact", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			switch (opt) {
				case 0:
					if (contact.edit()){
						writeList.add(contact);
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
	 * @param writeList list of contacts to be written to the server
	 * @param deleteList list of contacts to be DELETED from the server
	 * @return true, only if the user has confirmed to propagate the suggested changes
	 */
	private boolean confirmLists(TreeSet<Contact> writeList, TreeSet<Contact> deleteList) {
		VerticalPanel vp=new VerticalPanel();
		HorizontalPanel listsPanel=new HorizontalPanel();
		
		VerticalPanel deleteListPanel=new VerticalPanel();
		deleteListPanel.add(new JLabel("<html>The following contacts will be <b>deleted</b>:"));
		
		VerticalPanel delList=new VerticalPanel();
		for (Contact c:deleteList) delList.add(new JLabel("<html><br>"+c.toString(true).replace("\n","<br>")));
		delList.scale();
		
		JScrollPane sp=new JScrollPane(delList);
		sp.setPreferredSize(new Dimension(300,300));
		sp.setSize(sp.getPreferredSize());
		deleteListPanel.add(sp);
		deleteListPanel.scale();
		
		
		
		
		VerticalPanel writeListPanel=new VerticalPanel();
		writeListPanel.add(new JLabel("<html>The following <b>merged contacts</b> will be written to the server:"));
		
		VerticalPanel wrList=new VerticalPanel();
		for (Contact c:writeList) wrList.add(new JLabel("<html><br>"+c.toString(true).replace("\n","<br>")));
		wrList.scale();
		
		JScrollPane sp2=new JScrollPane(wrList);
		sp2.setPreferredSize(new Dimension(300,300));
		sp2.setSize(sp2.getPreferredSize());
		writeListPanel.add(sp2);
		writeListPanel.scale();

		listsPanel.add(deleteListPanel);
		listsPanel.add(writeListPanel);
		listsPanel.scale();
		
		vp.add(listsPanel);
		vp.add(new JLabel("<html>No data has been modified on the server <b>until now</b>. Continue?"));
		vp.scale();
		int decision=JOptionPane.showConfirmDialog(null, vp, "Please confirm", JOptionPane.YES_NO_OPTION);
		return decision==JOptionPane.YES_OPTION;
	}

	/**
	 * writes back the modified contacts to the server
	 * @param host the hostname and path to write to
	 * @param writeList the set of contacts to upload
	 * @throws IOException
	 */
	private void putMergedContacts(String host,TreeSet<Contact> writeList) throws IOException {
		for (Contact c:writeList) {			
			
			System.out.println("Uploading "+c.vcfName());
			
			byte[] data=c.getBytes();
			URL putUrl=new URL(host+"/"+c.vcfName());
			HttpURLConnection conn = ( HttpURLConnection ) putUrl.openConnection();
			conn.setRequestMethod( "PUT" );  
	    conn.setDoOutput( true );  
	    conn.setRequestProperty( "Content-Type", "text/vcard" );  
	    conn.connect();  
	    OutputStream out = conn.getOutputStream();  
	    ByteArrayInputStream in = new ByteArrayInputStream( data );  
	    int read = -1;  
	  
	    while ((read=in.read()) != -1 ) out.write( read );
	    out.close();
	    int response=conn.getResponseCode();
	    conn.disconnect();
	    

	    if (response<200 || response>299){
	    	System.out.println("...not successful ("+response+" / "+conn.getResponseMessage()+"). Trying to remove first...");
	    	
				/* the next two lines have been added to circumvent the problem, that on some caldav servers, entries can not simply be overwritten */
				deleteContact(new URL(host+"/"+c.vcfName()));
				c.generateName();

				data=c.getBytes();
				putUrl=new URL(host+"/"+c.vcfName());
				conn = ( HttpURLConnection ) putUrl.openConnection();
				conn.setRequestMethod( "PUT" );  
		    conn.setDoOutput( true );  
		    conn.setRequestProperty( "Content-Type", "text/vcard" );  
		    conn.connect();  
		    out = conn.getOutputStream();  
		    in = new ByteArrayInputStream( data );  
		    read = -1;  
		  
		    while ((read=in.read()) != -1 ) out.write( read );
		    out.close();
		    response=conn.getResponseCode();
		    conn.disconnect();
		    if (response<200 || response>299){
		    	File f=c.writeToFile();
		    	JOptionPane.showMessageDialog(this, "<html>Sorry! Unfortunateley, i was not able to write a file to the WebDAV server.<br>But don't worry, i created a <b>Backup</b> of the file at "+f.getAbsolutePath());
		    	throw new UnexpectedException("Server responded with CODE "+response);
		    }
	    }
	    System.out.println("...success!");
		}
	}

	/**
	 * removes the contacts in the given list from the server
	 * @param host the hostname and path to the server contact list
	 * @param deleteList the set of contacts to be deleted
	 * @throws IOException
	 */
	private void deleteUselessContacts(String host,TreeSet<Contact> deleteList) throws IOException {	
		for (Contact c:deleteList) {
			System.out.println("Deleting "+c.vcfName());
			deleteContact(new URL(host+"/"+c.vcfName()));
		}
	}
	
	/**
	 * actually removes a contact from the server
	 * @param u the url of the contact to be erased
	 * @throws IOException
	 */
	private void deleteContact(URL u) throws IOException{
		HttpURLConnection conn = ( HttpURLConnection ) u.openConnection();
		conn.setRequestMethod( "DELETE" );  
    conn.setDoOutput( true );  
    conn.connect();  
    int response=conn.getResponseCode();
    conn.disconnect();
    if (response!=204){
    	throw new UnexpectedException("Server responded with CODE "+response);
    }
	}

	/**
	 * asks, whether the given contacts shall be merged
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
		vp.scale();
		int decision = JOptionPane.showConfirmDialog(null, vp, "Please decide!", JOptionPane.YES_NO_CANCEL_OPTION);
		if (decision == JOptionPane.CANCEL_OPTION) System.exit(0);
		return decision == JOptionPane.YES_OPTION;
	}

	/**
	 * extracts a person's name from a vcard line
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

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
			startCleaning(serverField.getText(), userField.getText(), new String(passwordField.getText()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		CardDavCleaner cleaner = new CardDavCleaner();
		cleaner.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
