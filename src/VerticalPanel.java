
import java.awt.Dimension;

import javax.swing.BorderFactory;
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
}
