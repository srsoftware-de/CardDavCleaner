package de.keawe.carddavcleaner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.keawe.gui.Translations;

public class AddressBook {

	private String source;
	private String problem = "Not initialized";
	private String username = null;
	private String password = null;
	private boolean fixFieldSyntax = false;
	private boolean dropEmptyFields = false;
	private boolean dropEmptyContacts = false;
	private JProgressBar progressBar = null;
	private boolean fixLineBreaks;
	private Vector<Contact> contacts;
	private Vector<MergeCandidate> candidateList = null;
	
	private static String _(String text) {
		return Translations.get(text);
	}

	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}

	public AddressBook(String source, String username, String password) {
		if (source == null) {
			problem = "Location of addressbook is null";
			return;
		}
		source=source.trim();
		if (source.isEmpty()) {
			problem = "Location of addressbook is empty";
			return;
		}
		if (source.startsWith("file://")) {
			if (!source.endsWith(File.separator)) source+=File.separator;
		} else {
			if (!source.endsWith("/")) source+="/";
			this.username = username;
			this.password = password;
		}
		this.source = source;
		problem = null;
	}
	
	public void commit() {
		// TODO Auto-generated method stub
		System.out.println("AddressBook.commit not implemented");
	}
	
	private Vector<MergeCandidate> createCandidateList() {
		if (progressBar != null) {
			progressBar.setValue(0);
			progressBar.setMaximum(contacts.size());
			progressBar.setString(_("Searching for similar contacts..."));
		}
		Vector<MergeCandidate> list = new Vector<MergeCandidate>();
		for (int i = 0; i < contacts.size(); i++) {
			if (progressBar != null) progressBar.setValue(i);
			for (int j=i+1; j<contacts.size(); j++) {
				Contact a = contacts.get(i);
				Contact b = contacts.get(j);
				if (a.similarTo(b)) list.add(new MergeCandidate(contacts.get(i),contacts.get(j)));
			}
		}
		if (progressBar != null) {
			progressBar.setValue(0);
			progressBar.setMaximum(list.size());
			progressBar.setString(_("Processing merge candidates..."));
		}
		return list;
	}
	
	private void enableAuthenticator() {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password.toCharArray());
			}
		});
	}
	
	public void enableDropEmptyContacts() {
		dropEmptyContacts   = true;
	}

	public void enableDropEmptyFields() {
		dropEmptyFields  = true;
	}
	
	public void enableLineBreakFixing() {
		fixLineBreaks = true;
	}
	
	public void enableProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}



	public void enableSytaxFixing() {
		fixFieldSyntax = true;
	}
	
	private String extractContactName(String line) {
		String[] parts = line.split("/|\"");
		for (String part : parts) {
			if (part.contains("vcf")) return part;
		}
		return null;
	}
	
	public MergeCandidate getMergeCandidate() {
		if (candidateList  == null) candidateList = createCandidateList();
		if (candidateList.isEmpty()) return null;
		return candidateList.remove(0);
	}

	public void loadContacts(File backupPath) throws NoSuchAlgorithmException, KeyStoreException, IOException {
		progressBar.setString(CardDavCleaner._("reading list of contacts..."));
		TreeSet<String> contactList = getContactList();
		
		contacts = new Vector<Contact>();
		int index = 0;
		int num = contactList.size();
		progressBar.setMaximum(num);
		for (String fileName : contactList) {
			index++;
			progressBar.setString(_("Reading contact #...",index+"/"+num));
			
			progressBar.setValue(index);
			VCard card = new VCard(source,fileName);
			if (backupPath != null) storeBackup(backupPath,card);
			Contact contact = new Contact(card);
			contacts.add(contact);
		}
	}

	private TreeSet<String> getContactList() throws IOException, NoSuchAlgorithmException, KeyStoreException {
		TreeSet<String> contacts = new TreeSet<String>();
		if (source.startsWith("file://")) {
			File dir = new File(source.substring(7));
			if (!dir.isDirectory()) throw new InvalidParameterException(_("# is not a directory!",dir.toString()));
			for (File file: dir.listFiles()) {
				if (file.isDirectory()) continue;
				String filename = file.getName();
				if (filename.toLowerCase().endsWith(".vcf")) contacts.add(filename);
			}
		} else {
			if (username != null && !username.isEmpty()) enableAuthenticator();
			if (!source.endsWith("/")) source += "/";
			if (source.startsWith("https")) HttpsURLConnection.setDefaultSSLSocketFactory(TrustHandler.getSocketFactory());// here we set a socket factory, which uses our own trust manager
			
			try {
				URL url = new URL(source);
				
				HttpURLConnection connection = null;
				connection = (HttpURLConnection) url.openConnection();
				setRequestMethodUsingWorkaroundForJREBug(connection, "REPORT");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
				connection.setRequestProperty("Depth", "1");
				connection.connect();
				OutputStream out = connection.getOutputStream();
				String request="<card:addressbook-query xmlns:d=\"DAV:\" xmlns:card=\"urn:ietf:params:xml:ns:carddav\" />";
				ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
				int read = -1;
	
				while ((read = in.read()) != -1) out.write(read);
				out.close();
				
				InputStream content = (InputStream) connection.getInputStream();			
				
				DocumentBuilder dBuilder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = dBuilder.parse(content);
				NodeList nList = doc.getElementsByTagName("d:href");
				
				for (int index=0; index<nList.getLength(); index++){
					Node node=nList.item(index);
					String href=node.getTextContent();
					String name = extractContactName(href);
					contacts.add(name);
				}
				in.close();
				content.close();
				connection.disconnect();
			} catch (SSLHandshakeException ve) {
				ve.printStackTrace();
				JOptionPane.showMessageDialog(null, _("Sorry, i was not able to establish a secure connection to this server. I will quit now."));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e){
				e.printStackTrace();
			}
		}
		return contacts;
	}

	public String problem() {
		return problem;
	}

	public boolean ready() {
		if (problem == null) return true;
		if (progressBar != null) progressBar.setString(CardDavCleaner._("There is a problem with your settings: #",CardDavCleaner._(problem)));
		return false;
	}

	private static final void setRequestMethodUsingWorkaroundForJREBug(final HttpURLConnection httpURLConnection, final String method) {
		try {
			httpURLConnection.setRequestMethod(method);
			// Check whether we are running on a buggy JRE
		} catch (final ProtocolException pe) {
			Class<?> connectionClass = httpURLConnection.getClass();
			Field delegateField = null;
			try {
				delegateField = connectionClass.getDeclaredField("delegate");
				delegateField.setAccessible(true);
				HttpURLConnection delegateConnection = (HttpURLConnection) delegateField.get(httpURLConnection);
				setRequestMethodUsingWorkaroundForJREBug(delegateConnection, method);
			} catch (NoSuchFieldException e) {
				// Ignore for now, keep going
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			try {
				Field methodField;
				while (connectionClass != null) {
					try {
						methodField = connectionClass.getDeclaredField("method");
					} catch (NoSuchFieldException e) {
						connectionClass = connectionClass.getSuperclass();
						continue;
					}
					methodField.setAccessible(true);
					methodField.set(httpURLConnection, method);
					break;
				}
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void storeBackup(File backupPath, VCard card) throws IOException {
		File file = new File(backupPath+File.separator+card.filename());
		FileWriter backup = new FileWriter(file);
		backup.write(card.toString());
		backup.close();
	}





}
