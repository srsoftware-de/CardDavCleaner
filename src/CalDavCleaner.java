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
		Vector<Contact> contacts=new Vector<Contact>();
		int total=contactNamess.size();
		int counter=0;
		for (String contactName : contactNamess) {
			System.out.println(++counter+"/"+total);
			Contact contact = new Contact(new URL(host + contactName));
			if (contact.isEmpty()) {
				System.out.println("Waring: empty contact found ("+contactName+")");				
			} else contacts.add(contact);
		}
		
		
		TreeMap<String, Contact> names;
		boolean restart;
		do {
			restart=false;
			names = new TreeMap<String, Contact>(ObjectComparator.get());
			total = contacts.size();
			int index = 0;
			for (Contact contact : contacts) {
				System.out.println((++index) + "/" + total);
				System.out.println(contact);

				Name name = contact.name();
				if (name != null) {
					String name1 = name.first() + " " + name.last();
					String name2 = name.last() + " " + name.first();
					
					Contact existingContact = names.get(name1);
					if (existingContact!=null) {
						if (askForMege(name1, contact, existingContact)) {
							existingContact.merge(contact);
							contacts.remove(contact);
							restart = true;
							break;
						}
					}
					existingContact = names.get(name2);
					if (existingContact!=null) {
						if (askForMege(name2, contact, existingContact)) {
							existingContact.merge(contact);
							contacts.remove(contact);
							restart = true;
							break;
						}
					}
					names.put(name1, contact);
				}
			}
		} while (restart);
	}

	private boolean askForMege(String name, Contact contact, Contact contact2) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel("The name \"" + name + "\" is used by both following contacts:"));
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new JLabel("<html><br>" + contact.toString().replace("\n", "&nbsp<br>")));
		hp.add(new JLabel("<html><br>" + contact2.toString().replace("\n", "<br>")));
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
