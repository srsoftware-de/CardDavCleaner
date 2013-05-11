import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class CardDavCleaner extends JFrame implements ActionListener {

	private JTextField serverField;
	private JTextField userField;
	private JPasswordField passwordField;

	public CardDavCleaner() {
		super();
		createComponents();
		setVisible(true);
	}

	private void createComponents() {
		VerticalPanel mainPanel = new VerticalPanel("Server settings");

		serverField = addInput(mainPanel, "Server:");
		userField = addInput(mainPanel, "User:");
		passwordField = addPassword(mainPanel, "Password:");

		JButton startButton = new JButton("start");
		startButton.addActionListener(this);
		mainPanel.add(startButton);

		mainPanel.scale();
		add(mainPanel);
		pack();
		setVisible(true);
	}

	private JPasswordField addPassword(VerticalPanel mainPanel, String text) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel(text + " "));
		JPasswordField result = new JPasswordField(50);
		hp.add(result);
		hp.scale();
		mainPanel.add(hp);
		return result;
	}

	private JTextField addInput(VerticalPanel mainPanel, String text) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel(text + " "));
		JTextField result = new JTextField(50);
		hp.add(result);
		hp.scale();
		mainPanel.add(hp);
		return result;
	}

	public static void main(String[] args) {
		CardDavCleaner cleaner = new CardDavCleaner();
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
		

		
		//putTestFile(host);
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
		TreeSet<Contact> writeList=new TreeSet<Contact>(ObjectComparator.get());
		TreeSet<Contact> deleteList=new TreeSet<Contact>(ObjectComparator.get());
		Vector<Contact> contacts = new Vector<Contact>();
		int total = contactNamess.size();
		int counter = 0;
		for (String contactName : contactNamess) {
			System.out.println("reading contact "+(++counter) + "/" + total+": "+contactName);
			Contact contact = new Contact(host,contactName);
			if (contact.isEmpty()) {
				deleteList.add(contact);
				System.out.println("Warning: skipping empty contact " + contactName);
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
			for (Contact contact : contacts) {
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
								writeList.add(contact);
								writeList.remove(existingContact);
								contactsForName.remove(existingContact);
								deleteList.add(existingContact);
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
								writeList.add(contact);
								writeList.remove(existingContact);
								contactsForNumber.remove(existingContact);
								deleteList.add(existingContact);
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
							writeList.add(contact);
							writeList.remove(existingContact);
							deleteList.add(existingContact);
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
		
		if (!(writeList.isEmpty() && deleteList.isEmpty()) && confirmLists(writeList,deleteList)){
			putMergedContacts(host,writeList);
			deleteUselessContacts(host,deleteList);
		}
		JOptionPane.showMessageDialog(null, "Scanning, merging and cleaning successfully done! Godbye!");
		setVisible(false);
		System.exit(0);
		
		
	}

	private boolean confirmLists(TreeSet<Contact> writeList, TreeSet<Contact> deleteList) {
		VerticalPanel vp=new VerticalPanel();
		HorizontalPanel listsPanel=new HorizontalPanel();
		
		VerticalPanel deleteListPanel=new VerticalPanel();
		deleteListPanel.add(new JLabel("The following contacts will be deleted:"));
		
		VerticalPanel delList=new VerticalPanel();
		for (Contact c:deleteList) delList.add(new JLabel("<html><br>"+c.toString(true).replace("\n","<br>")));
		delList.scale();
		
		JScrollPane sp=new JScrollPane(delList);
		sp.setPreferredSize(new Dimension(300,300));
		sp.setSize(sp.getPreferredSize());
		deleteListPanel.add(sp);
		deleteListPanel.scale();
		
		
		
		
		VerticalPanel writeListPanel=new VerticalPanel();
		writeListPanel.add(new JLabel("The following merged contacts will be written to the server:"));
		
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
		vp.add(new JLabel("Please confirm those changes."));
		vp.scale();
		int decision=JOptionPane.showConfirmDialog(null, vp, "Please confirm", JOptionPane.YES_NO_OPTION);
		return decision==JOptionPane.YES_OPTION;
	}

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
		    if (response<200 || response>299) throw new UnexpectedException("Server responded with CODE "+response);
	    }
		}
	}

	private void deleteUselessContacts(String host,TreeSet<Contact> deleteList) throws IOException {	
		for (Contact c:deleteList) {
			System.out.println("Deleting "+c.vcfName());			
			deleteContact(new URL(host+"/"+c.vcfName()));
			System.out.println("Deleting "+c.vcfName());
			
			URL putUrl=new URL(host+"/"+c.vcfName());
			HttpURLConnection conn = ( HttpURLConnection ) putUrl.openConnection();
			conn.setRequestMethod( "DELETE" );  
	    conn.setDoOutput( true );  
	    conn.connect();  
	    int response=conn.getResponseCode();
	    conn.disconnect();
	    if (response!=204){
	    	throw new UnexpectedException("Server responded with CODE "+response);
	    }
		}
	}
	
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

	private boolean askForMege(String identifier, String name, Contact contact, Contact contact2) throws InterruptedException {
		if (!contact.conflictsWith(contact2)) return true;
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel("The " + identifier + " \"" + name + "\" is used by both following contacts:"));
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel("<html><br>" + contact.toString(true).replace("\n", "&nbsp<br>")));
		hp.add(new JLabel("<html><br>" + contact2.toString(true).replace("\n", "<br>")));
		hp.scale();
		vp.add(hp);
		vp.add(new JLabel("<html><br>Shall those contacts be merged?"));
		vp.scale();
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
