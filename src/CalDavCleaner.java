import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class CalDavCleaner extends JFrame implements ActionListener {

	public CalDavCleaner() {
		super();
		createComponents();
		setVisible(true);
	}

	private void createComponents() {
		VerticalPanel mainPanel=new VerticalPanel("Server settings");
				
		SuggestField serverField=addInput(mainPanel,"Server:");
		SuggestField userfield=addInput(mainPanel, "User:");
		JPasswordField passwordField=addPassword(mainPanel, "Password:");
		
		JButton startButton = new JButton("start");
		startButton.addActionListener(this);
		mainPanel.add(startButton);
		
		
		mainPanel.skalieren();
		add(mainPanel);
		pack();
		setVisible(true);
	}

	private JPasswordField addPassword(VerticalPanel mainPanel, String text) {
		HorizontalPanel hp=new HorizontalPanel();
		hp.add(new JLabel(text+" "));
		JPasswordField result=new JPasswordField(50);
		hp.add(result);
		hp.skalieren();
		mainPanel.add(hp);
		return result;
	}

	private SuggestField addInput(VerticalPanel mainPanel, String text) {
		HorizontalPanel hp=new HorizontalPanel();
		hp.add(new JLabel(text+" "));
		SuggestField result=new SuggestField(50, false);
		hp.add(result);
		hp.skalieren();
		mainPanel.add(hp);
		return result;
	}

	public static void main(String[] args) {
		CalDavCleaner cleaner = new CalDavCleaner();
		cleaner.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("start pressed");
	}

}
