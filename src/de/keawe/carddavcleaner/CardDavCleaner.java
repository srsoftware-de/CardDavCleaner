package de.keawe.carddavcleaner;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.sun.corba.se.pept.transport.ContactInfo;

import de.keawe.gui.HorizontalPanel;
import de.keawe.gui.InputField;
import de.keawe.gui.Translations;
import de.keawe.gui.VerticalPanel;

public class CardDavCleaner extends JFrame {
	
	private static String _(String text) {
		return Translations.get(text);
	}
	
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}

	private InputField addressField;
	private InputField userField;
	private InputField passwordField;
	private File backupPath = null;
	private JProgressBar progressBar;
	private JCheckBox fixSyntaxOption;
	private JCheckBox dropEmptyFieldsOption;
	private JCheckBox dropEmptyContactsOption;
	
	private boolean askForCommit(AddressBook addressBook) {
		// TODO Auto-generated method stub
		System.out.println("CardDavCleaner.askForCommit not implemented");
		return false;
	}
	
	private HorizontalPanel backupPanel() {
		HorizontalPanel backupPanel = new HorizontalPanel(_("Backup settings"));
		final JLabel backupPathLabel = new JLabel(" " + _("No Backup defined.") + "                                                                 ");
		JButton backupPathButton = new JButton(_("Select Backup Location"));
		backupPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectBackupPath(backupPathLabel);
			}
		});
		backupPanel.add(backupPathButton);
		backupPanel.add(backupPathLabel);
		return backupPanel.scale();
	}
	
	public CardDavCleaner() {
		super(_("Keawe CardDAV cleaner"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createComponents();
		setVisible(true);
	}
	
	/**
	 * creates all the components for the server login form
	 */
	private void createComponents() {
		VerticalPanel mainPanel = new VerticalPanel();

		mainPanel.add(backupPanel());
		mainPanel.add(serverPanel());
		mainPanel.add(optionsPanel());
		mainPanel.add(progressPanel());
		mainPanel.scale();
		
		add(mainPanel);
		pack();
		setVisible(true);
	}
	
	public void enterPressed() {
		startCleaning();
	}
	
	private JComponent locationPanel() {
		HorizontalPanel locationPanel = new HorizontalPanel();
		
		JLabel infoText = new JLabel(_("Address book may be located on a WebDAV-Server or in a local directory.")+"  ");
		JButton locationButton = new JButton(_("Select directory"));
		locationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSource();
			}
		});
		locationPanel.add(infoText);
		locationPanel.add(locationButton);
		locationPanel.scale();
		return locationPanel;
	}

	public static void main(String[] args) {
		//System.setProperty("jsse.enableSNIExtension", "false");
		if (args.length > 0 && args[0].equals("--test")) {
			test();
		} else {
			CardDavCleaner cleaner = new CardDavCleaner();
			cleaner.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
	}
	
	private JComponent optionsPanel() {
		VerticalPanel optionsPanel = new VerticalPanel(_("Optional settings"));
		
		fixSyntaxOption = new JCheckBox(_("Fix field syntax, if broken"));
		fixSyntaxOption.setSelected(true);
		optionsPanel.add(fixSyntaxOption);

		dropEmptyFieldsOption = new JCheckBox(_("Remove empty fields from contacts"));
		dropEmptyFieldsOption.setSelected(true);
		optionsPanel.add(dropEmptyFieldsOption);
		
		dropEmptyContactsOption = new JCheckBox(_("Remove empty contacts"));
		dropEmptyContactsOption.setSelected(true);
		optionsPanel.add(dropEmptyContactsOption);
		
		optionsPanel.add(new JLabel(_("<html>Some programs cannot handle all fields defined by the vCard standard.<br>To apply workarounds, select programs you use from the follwing list:")));
		
		JCheckBox thunderbirdBox = new JCheckBox(_("Mozilla Thunderbird with default address book"));
		optionsPanel.add(thunderbirdBox);

		return optionsPanel.scale();
	}

	private JComponent progressPanel() {
		HorizontalPanel bar = new HorizontalPanel();
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(800, 32));
		progressBar.setStringPainted(true);
		progressBar.setString(_("Ready."));
		bar.add(progressBar);
		
		JButton startButton = new JButton(_("start"));
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startCleaning();
			}
		});
		bar.add(startButton);
		
		bar.scale();
		return bar;
	}
	
	private void proposeMerge(MergeCandidate candidate) {
		// TODO Auto-generated method stub
		System.out.println("CardDavCleaner.proposeMerge not implemented");
	}

	protected void selectBackupPath(JLabel label) {
		JFileChooser j = new JFileChooser();
		j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		Integer opt = j.showSaveDialog(this);
		if (opt == JFileChooser.APPROVE_OPTION) {
			backupPath  = j.getSelectedFile();
			label.setText(" " + _("Backup wil be written to #", backupPath));
		}
	}
	
	protected void selectSource() {
		JFileChooser j = new JFileChooser();
		j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		Integer opt = j.showSaveDialog(this);
		if (opt == JFileChooser.APPROVE_OPTION) {
			File source = j.getSelectedFile();
			addressField.setText("file://"+source.toString());
		}
	}
	
	private VerticalPanel serverPanel() {
		VerticalPanel serverPanel = new VerticalPanel(_("Address book settings"));
		serverPanel.add(locationPanel());
		
		serverPanel.add(addressField = new InputField(_("Location of addressbook:")));
		addressField.setText("https://example.com/cardDAV");
		
		serverPanel.add(userField = new InputField(_("User:"), false));
		serverPanel.add(passwordField = new InputField(_("Password:"), true));
		
		addressField.setEnterListener(this);
		userField.setEnterListener(this);
		passwordField.setEnterListener(this);
		return serverPanel.scale();
	}
	
	protected void startCleaning() {
		progressBar.setString(_("Connecting to address book..."));
		AddressBook addressBook = new AddressBook(addressField.getText(),userField.getText(),passwordField.getText());
		if (fixSyntaxOption.isSelected()) addressBook.enableSytaxFixing();
		if (dropEmptyFieldsOption.isSelected()) addressBook.enableDropEmptyFields();
		if (dropEmptyContactsOption.isSelected()) addressBook.enableDropEmptyContacts();
		
		String problem = addressBook.problem();
		if (problem == null) {
			addressBook.loadContacts(backupPath);
			MergeCandidate candidate;
			while ((candidate = addressBook.getMergeCandidate()) != null) proposeMerge(candidate);
			if (askForCommit(addressBook)) addressBook.commit();
		} else progressBar.setString(_("There is a problem with your settings: #",_(problem)));
	}

	private static void test() {
		try {
			System.out.println("CardDavCleaner.proposeMerge not implemented");
			System.out.println("#===================================#");
			System.out.println(_("| All tests successfully completed! |"));
			System.out.println("#===================================#");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}