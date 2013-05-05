import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.rmi.activation.UnknownObjectException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

	private void startCleaning(String host, final String user, final String password) throws IOException, InterruptedException, UnknownObjectException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});

		if (!host.endsWith("/")) host+="/";
		URL url = new URL(host);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		Vector<String> contacts=new Vector<String>();
		while ((line = in.readLine()) != null) {
			if (line.contains(".vcf")) contacts.add(extractContactName(line));			
		}
		in.close();
		content.close();
		connection.disconnect();
		
		scanContacts(host,contacts);
	}

	private void scanContacts(String host, Vector<String> contacts) throws IOException, InterruptedException, UnknownObjectException {
		int total=contacts.size();
		for (int index=0; index<total; index++){
			String contactName=contacts.elementAt(index);
			System.out.println(index+"/"+total);
			Contact contact=new Contact(new URL(host+contactName));
		}
	}

	private String extractContactName(String line) {
		String[] parts=line.split("/|\"");
		for (String part:parts){
			if (part.contains("vcf")){
				return part;
			}
		}
		return null;
	}
}
