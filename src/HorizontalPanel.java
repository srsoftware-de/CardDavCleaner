
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
	private static int versatz=5; // der Absatnd zwischen den Elementen
	private int breite=0; // die Breite des Panels, anfänglich null
	private int höhe=0; // die Höhe des Panels, anfänglich null
	
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
		init(); // Java-internes automatisches Layout abschalten
		höhe+=15; // höhe initialisieren
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),string)); // Rahmen um Feld Erzeugen
	}

	/**
	 * schaltet das Java-eigene automatische Layout ab
	 */
	private void init() {
		this.setLayout(null);
	}
	
	/**
	 * fügt eine grafische Komponente zum Panel hinzu. die Komponente wird rechts neben der zuletzt hinzugefügten angeordnet
	 * @param c die zuzufügende Komponente
	 */
	public void add(JComponent c){
		c.setSize(c.getPreferredSize()); // skaliert die zuzufügende Komponente auf ihre bevorzugte Größe		
		c.setLocation(breite, versatz/2); // ordnet die Komponente an
		breite+=c.getWidth(); // speichert die aktuelle Position, bis zu der Komponenten gehen, damit auch die nächste Komponente angeordnert werden kann
		höhe=Math.max(höhe, c.getHeight()); // bestimmt die minimalgröße des Panels nach Addition der grafischen Komponente
		super.add(c); // fügt die grafische Komponente dem Panel hinzu
	}
	
	/**
	 * skaliert das gesamte Panel so, dass alle hinzugefügten Komponenten sichtbar bleiben
	 */
	public void skalieren(){
		setPreferredSize(new Dimension(breite+versatz+versatz,höhe+versatz));
	}
}
