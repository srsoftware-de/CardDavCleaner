package de.keawe.carddavcleaner;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import de.keawe.gui.HorizontalPanel;
import de.keawe.gui.Translations;
import de.keawe.gui.VerticalPanel;

public class ContactListPreview {

	static String _(String text) {
		return Translations.get(text);
	}
	
	public static void show(Vector<Contact> contacts, String title) {
		VerticalPanel vp = new VerticalPanel();
		JButton btn = null;
		for (final Contact c : contacts) {
			btn = new JButton(c.tags("FN"));
			btn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					HorizontalPanel cPanel = new HorizontalPanel();
					StringBuffer orig = new StringBuffer();
					int maxLen=75;
					for (String line : c.orig().replace("\n ", "").split("\n")) {
						if (line.length()>maxLen) line=line.substring(0, maxLen-3)+"...";
						orig.append(line.trim()+"<br/>\n");
					}
					StringBuffer altered = new StringBuffer();
					if (c.markedForRemoval()) {
						altered.append(_("This card has been merged into other contact:")+"<br/><br/>");
						Contact merged = c.merged();
						if (merged != null) {
							for (String line : merged.card().toString().split("\n")) {
								if (line.length()>maxLen) line=line.substring(0, maxLen-3)+"...";
								altered.append(line.trim()+"<br/>\n");
							}
						}
					} else {
						altered.append(_("Updated VCard:")+"<br/><br/>");
						for (String line : c.card().toString().split("\n")) {
							if (line.length()>maxLen) line=line.substring(0, maxLen-3)+"...";
							altered.append(line.trim()+"<br/>\n");
						}
					}

					cPanel.add(new JLabel("<html>"+_("Original VCard:")+"<br/><br/>"+orig));
					cPanel.add(new JLabel("<html>"+altered));
					cPanel.scale();
					JOptionPane.showMessageDialog(null, cPanel, c.filename(), JOptionPane.OK_OPTION);
				}
			});
			vp.add(btn);
		}
		if (contacts.size()==1) { // if there is only 1 button:
			btn.doClick(); // activate the button
		} else { // otherwise: show selection dialog
			vp.scale();
			JScrollPane scroller = new JScrollPane(vp);
			Dimension desiredDim = scroller.getPreferredSize();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (desiredDim.height > screenSize.height-200) desiredDim.height = screenSize.height-200;
			if (desiredDim.width > screenSize.width-200) desiredDim.width = screenSize.width-200;
			scroller.setPreferredSize(desiredDim);
			JOptionPane.showMessageDialog(null, scroller, title, JOptionPane.OK_OPTION);
		}
	}

}
