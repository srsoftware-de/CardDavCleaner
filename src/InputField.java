import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class InputField extends HorizontalPanel {
	/**
	 * used to create non-password input fields for the server login form
	 * @param owner the panel, to which the component shall be added 
	 * @param text the label for the field
	 * @param password if set to ture, a password field will be created
	 * @return the input field component
	 */
	
	JTextField result;
	
	public InputField(String caption,boolean password) {
		add(new JLabel(caption + " "));
		result = password?(new JPasswordField(50)):(new JTextField(50));		
		add(result);
		scale();		
	}

	public InputField(String caption,String defaultValue) {
		this(caption);
		result.setText(defaultValue);
	}

	public InputField(String caption) {
		this(caption,false);
	}

	public String getText() {
		// TODO Auto-generated method stub
		return result.getText();
	}
}
