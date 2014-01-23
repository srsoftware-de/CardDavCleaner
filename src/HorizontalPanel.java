
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * ein Abkömmling von JPanel, der eine automatische Anordnung der Unterlemente nebeneinander ermöglicht
 * @author Stephan Richter
 *
 */
public class HorizontalPanel extends JPanel {
	/**
   * 
   */
  private static final long serialVersionUID = -3763921236213613770L;
	private int offset=5; // der Absatnd zwischen den Elementen
	private int width=0; // die Breite des Panels, anfänglich null
	private int height=0; // die Höhe des Panels, anfänglich null
	private boolean caption=false;
	/**
	 * erzeugt ein neues, leeres Panel
	 */
	public HorizontalPanel(){
		super(); // leeres Panel ezuegen
		init(); // java-eigenes automatisches Layout abschalten
	}

	/**
	 * erzeugt ein neues, leeres Panel mit Beschriftung
	 * @param string die Beschriftung, die im Rahmen um das Panel erscheinen soll
	 */
	public HorizontalPanel(String string) {
		super(); // leeres Panel erzuegen
		caption=true;
		init(); // Java-internes automatisches Layout abschalten
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),string)); // Rahmen um Feld Erzeugen
	}

	/**
	 * schaltet das Java-eigene automatische Layout ab
	 */
	private void init() {
		this.setLayout(null);
		width=5;
		offset=5;
		if (caption){
			System.out.println("setting offset to "+(offset+25));
			offset+=15;
		}
	}
	
	/**
	 * fügt eine grafische Komponente zum Panel hinzu. die Komponente wird rechts neben der zuletzt hinzugefügten angeordnet
	 * @param c die zuzufügende Komponente
	 */
	public void add(JComponent c){
		c.setSize(c.getPreferredSize()); // skaliert die zuzufügende Komponente auf ihre bevorzugte Größe		
		c.setLocation(width, offset); // ordnet die Komponente an
		width+=c.getWidth(); // speichert die aktuelle Position, bis zu der Komponenten gehen, damit auch die nächste Komponente angeordnert werden kann
		height=Math.max(height, c.getHeight()); // bestimmt die minimalgröße des Panels nach Addition der grafischen Komponente
		super.add(c); // fügt die grafische Komponente dem Panel hinzu
	}
	
	/**
	 * skaliert das gesamte Panel so, dass alle hinzugefügten Komponenten sichtbar bleiben
	 */
	public void scale(){
		setPreferredSize(new Dimension(width+offset+offset,height+offset+5));
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
	
	public void rescale(){
		insertCompoundBefore(null, null);
	}
}
