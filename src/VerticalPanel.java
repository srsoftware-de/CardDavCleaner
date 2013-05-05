
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;


public class VerticalPanel extends JPanel {
	/**
   * 
   */
  private static final long serialVersionUID = -3284460780727609981L;
	private static int versatz=5;
	private int breite=0;
	private int höhe=versatz;
	
	public VerticalPanel(){
		super();
		init();
	}

	public VerticalPanel(String string) {
		super();
		init();
		höhe+=15;
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),string)); // Rahmen um Feld Erzeugen
	}

	private void init() {
		this.setLayout(null);
	}
	
	public void add(JComponent c){
		c.setSize(c.getPreferredSize());
		c.setLocation(versatz, höhe);
		breite=Math.max(breite, c.getWidth());
		höhe+=c.getHeight();
		super.add(c);
	}
	
	public void skalieren(){
		setPreferredSize(new Dimension(breite+versatz+versatz,höhe+versatz));
	}
}
