
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;


public class VerticalPanel extends JPanel {
	/**
   * 
   */
  private static final long serialVersionUID = -3284460780727609981L;
	private int offset=5;
	private int width=0;
	private int height=offset;
	private boolean hasTitle=false;
	
	public VerticalPanel(){
		this(null);
	}

	public VerticalPanel(String title) {
		super();
		if (title!=null){
			hasTitle=true;
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),title)); // Rahmen um Feld Erzeugen
		}
		init();
	}

	private void init() {
		this.setLayout(null);
		width=0;
		height=offset;
		if (hasTitle){
			height+=15;
		}
	}
	
	public void add(JComponent c){
		c.setSize(c.getPreferredSize());
		c.setLocation(offset, height);
		width=Math.max(width, c.getWidth());
		height+=c.getHeight();
		super.add(c);
	}
	
	public VerticalPanel scale(){
		setPreferredSize(new Dimension(width+offset+offset,height+offset));
		return this;
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
