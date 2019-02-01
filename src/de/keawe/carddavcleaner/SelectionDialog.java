package de.keawe.carddavcleaner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import de.keawe.gui.HorizontalPanel;
import de.keawe.gui.Translations;
import de.keawe.gui.VerticalPanel;

public class SelectionDialog extends JDialog {
	
	private Tag selectedTag = null;
	
	static String _(String text) {
		return Translations.get(text);
	}
	
	static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
	
	private JButton defaultButton(final Tag t) {
		return new JButton("<html>"+t.value().replace(";", "<br/>\n"));
	}
	
	private JButton nameButton(final Tag t) {
		String[] parts = t.value().split(";",5);
		StringBuffer sb = new StringBuffer("<html>");
		for (int i = 0; i<parts.length; i++) {
			if (parts[i].isEmpty()) continue;
			switch (i) {
				case 0: sb.append(_("Family name: #",parts[i])); break;
				case 1: sb.append(_("Given name: #",parts[i])); break;
				case 2: sb.append(_("Additional names: #",parts[i])); break;
				case 3: sb.append(_("Prefix: #",parts[i])); break;
				case 4: sb.append(_("Postfi: #",parts[i])); break;
			}
			sb.append("<br/>");
		}
		return new JButton(sb.toString());
	}
	
	public SelectionDialog(Conflict conflict) {
		this.setModal(true);
		this.setTitle(_("Conflicting properties in merged contact:"));
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel(_("No more than one # entry allowed in VCard.",conflict.param())));
		vp.add(new JLabel(_("Please select one:")));
		
		HorizontalPanel hp = new HorizontalPanel();
		for (final Tag t:conflict.tags()) {
			JButton btn = t.name("N") ? nameButton(t) : defaultButton(t);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedTag = t;
					dispose();
				}
			});
			hp.add(btn);
		}
		JButton cancel = new JButton(_("Cancel"));
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		hp.add(cancel);
		hp.scale();
		vp.add(hp);
		vp.scale();
		add(vp);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public Tag selectedTag() {
		return selectedTag;
	}
}
