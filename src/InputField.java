import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class InputField extends HorizontalPanel implements DocumentListener {
	/**
	 * used to create non-password input fields for the server login form
	 * @param owner the panel, to which the component shall be added 
	 * @param text the label for the field
	 * @param password if set to ture, a password field will be created
	 * @return the input field component
	 */
	
	JTextField result;
	private TreeSet<ChangeListener> editListeners;
	
	public InputField(String caption,boolean password) {
		add(new JLabel(caption + " "));
		result = password?(new JPasswordField(50)):(new JTextField(50));		
		add(result);
		scale();		
	}

	public InputField(String caption,String defaultValue) {
		add(new JLabel(caption + " "));		
		result = (defaultValue==null || defaultValue.isEmpty())?new JTextField(20):new JTextField(defaultValue);		
		add(result);
		scale();	
	}

	public InputField(String caption) {
		this(caption,false);
	}

	public String getText() {
		return result.getText();
	}

	public void addChangeListener(DocumentListener listener) {
		result.getDocument().addDocumentListener(listener);
	}
	
	public void addEditListener(ChangeListener listener){
		if (editListeners==null){
			editListeners=new TreeSet<ChangeListener>(ObjectComparator.get());
			result.getDocument().addDocumentListener(this);
		}
		editListeners.add(listener);
	}

	public void changedUpdate(DocumentEvent arg0) {
		edit();
	}

	public void insertUpdate(DocumentEvent arg0) {
		edit();
	}

	public void removeUpdate(DocumentEvent arg0) {
		edit();
	}

	private void edit() {
		if (editListeners!=null){
			for (ChangeListener cl:editListeners){
				cl.stateChanged(new ChangeEvent(this));
			}
		}
	}
}
