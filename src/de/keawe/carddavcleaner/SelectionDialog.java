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
	
	public SelectionDialog(Conflict conflict) {
		this.setModal(true);
		this.setTitle(_("Conflicting properties in merged contact:"));
		VerticalPanel vp = new VerticalPanel();
		vp.add(new JLabel(_("No more than one # entry allowed in VCard.",conflict.param())));
		vp.add(new JLabel(_("Please select one:")));
		
		HorizontalPanel hp = new HorizontalPanel();
		for (final Tag t:conflict.tags()) {
			JButton btn = new JButton("<html>"+t.value().replace(";", "<br/>\n"));
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
