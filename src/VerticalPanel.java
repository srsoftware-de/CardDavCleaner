
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;


public class VerticalPanel extends JPanel {
	/**
   * 
   */
  private static final long serialVersionUID = -3284460780727609981L;
	private static int offset=5;
	private int width=0;
	private int height=offset;
	
	public VerticalPanel(){
		super();
		init();
	}

	public VerticalPanel(String string) {
		super();
		init();
		height+=15;
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),string)); // Rahmen um Feld Erzeugen
	}

	private void init() {
		width=0;
		height=offset;
		this.setLayout(null);
	}
	
	public void add(JComponent c){
		c.setSize(c.getPreferredSize());
		c.setLocation(offset, height);
		width=Math.max(width, c.getWidth());
		height+=c.getHeight();
		super.add(c);
	}
	
	public void scale(){
		setPreferredSize(new Dimension(width+offset+offset,height+offset));
	}
	
	public void rescale(){
		insertCompoundBefore(null, null);
	}

	public void insertCompoundBefore(JComponent givenComponent, JComponent newComponent) {
		Component[] oldComps = super.getComponents();
		super.removeAll();
		init();
		for (Component c:oldComps){
			if (c==givenComponent) {
				add(newComponent);
			}
			add((JComponent)c);
		}
		scale();
		this.repaint();
	}

	public void replace(JComponent old, JComponent replacement) {
		Component[] oldComps = super.getComponents();
		super.removeAll();
		init();
		for (Component c:oldComps){
			if (c==old) {
				add(replacement);
			} else {
				add((JComponent)c);
			}
		}
		scale();
		this.repaint();
	}
}
