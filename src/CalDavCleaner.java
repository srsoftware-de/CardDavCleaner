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
		userField = addInput(mainPanel, "User:");
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startCleaning(String host, final String user, final String password) throws IOException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password.toCharArray());
			}
		});

		URL url = new URL(host);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.contains(".vcf")) addContact(extractContactUrl(line)));
			
		}
		in.close();
		content.close();
		connection.disconnect();
	}

}
