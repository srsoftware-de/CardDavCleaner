import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class Phone extends Mergable<Phone> implements DocumentListener, ChangeListener, Comparable<Phone> {
	
	private boolean fax=false;
	private boolean home=false;
	private boolean cell=false;
	private boolean work=false;
	private boolean voice=false;
	private String number;
	private boolean invalid = false;
	
	private InputField numField;
	VerticalPanel form;
	private JCheckBox homeBox,voiceBox,workBox,cellBox,faxBox;
	
	static TreeSet<String> numbers=new TreeSet<String>();
	
	public Phone(String content) throws UnknownObjectException, InvalidFormatException {
		if (!content.startsWith("TEL;")) throw new InvalidFormatException("Phone does not start with \"TEL;\"");
		String line=content.substring(4);
		while(!line.startsWith(":")){
			String upper=line.toUpperCase();
			if (upper.startsWith("TYPE=FAX")){
				fax=true;
				line=line.substring(8);
				continue;
			}
			if (upper.startsWith("TYPE=HOME")){
				home=true;
				line=line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,HOME")){
				home=true;
				line=line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=CELL")){
				cell=true;
				line=line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,CELL")){
				cell=true;
				line=line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=WORK")){
				work=true;
				line=line.substring(9);
				continue;
			}
			if (upper.startsWith("\\,WORK")){
				work=true;
				line=line.substring(6);
				continue;
			}
			if (upper.startsWith("TYPE=VOICE")){
				voice=true;
				line=line.substring(10);
				continue;
			}
			if (upper.startsWith("\\,VOICE")){
				voice=true;
				line=line.substring(7);
				continue;
			}
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			throw new UnknownObjectException(line+" in "+content);
		}
		readPhone(line.substring(1));		
	}
	
	public String category() {
		if (home) return "home";
		if (work) return "work";
		if (fax) return "fax";
		if (cell) return "cell";
		if (voice) return "voice";
		return "empty category";
	}

	public void changedUpdate(DocumentEvent arg0) {
		update();
	}

	public int compareTo(Phone otherPhone) {
		return this.toString().compareTo(otherPhone.toString());
	}

	public VerticalPanel editForm() {
		form=new VerticalPanel("Phone");
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);
		
		form.add(numField=new InputField("Number",number));
		numField.addChangeListener(this);
		
		form.add(homeBox=new JCheckBox("Home Phone",home));
		homeBox.addChangeListener(this);
		form.add(voiceBox=new JCheckBox("Voice Phone",voice));
		voiceBox.addChangeListener(this);
		form.add(workBox=new JCheckBox("Work Phone",work));
		workBox.addChangeListener(this);
		form.add(cellBox=new JCheckBox("Cell Phone",cell));
		cellBox.addChangeListener(this);
		form.add(faxBox=new JCheckBox("Fax",fax));
		faxBox.addChangeListener(this);
		form.scale();
		return form;
	}

	public void insertUpdate(DocumentEvent arg0) {
		update();
	}

	public boolean isCellPhone() {
		return cell;
	}

	@Override
  public boolean isCompatibleWith(Phone other) {
		if (different(simpleNumber(),other.simpleNumber())) return false;
	  return true;
  }

	public boolean isEmpty(){
		return number==null || number.trim().isEmpty();
	}

	public boolean isFax() {
		return fax;
	}

	public boolean isHomePhone() {
		return home;
	}
	
	public boolean isInvalid() {
		return invalid ;
	}

	public boolean isVoice(){
		return voice;
	}

	public boolean isWorkPhone() {
		return work;
	}

	public void merge(Phone phone) throws InvalidAssignmentException {
		if (!number.equals(phone.number)) throw new InvalidAssignmentException("Trying to unite two phone number entries with different numbers!");
		if (phone.home) home=true;
		if (phone.work) work=true;
		if (phone.cell)cell= true;
		if (phone.fax) fax=true;
		if (phone.voice)voice=true; 
	}

	@Override
  public boolean mergeWith(Phone other) {
		number=merge(simpleNumber(), other.simpleNumber());
	  return true;
  }
	
	public String number() {
		return number;
	}

	public void removeUpdate(DocumentEvent arg0) {
		update();
	}
	
	public void setCell() {
		home=false;
		work=false;
		fax=false;
		cell=true;
		voice=false;
	}

	public void setFax() {
		home=false;
		work=false;
		fax=true;
		cell=false;
		voice=false;
	}

	public void setHome() {
		home=true;
		work=false;
		fax=false;
		cell=false;
		voice=false;
	}

	public void setVoice() {
		home=false;
		work=false;
		fax=false;
		cell=false;
		voice=true;
	}

	public void setWork() {
		home=false;
		work=true;
		fax=false;
		cell=false;
		voice=false;
	}

	public String simpleNumber() {
		String number=this.number.trim();
		while (number.contains("(")){
			number=number.replace("(", "");
		}
		while (number.contains(")")){
			number=number.replace(")", "");
		}
		while (number.contains(" ")){
			number=number.replace(" ", "");
		}
		if (number.startsWith("+49")) number=0+number.substring(3);
		if (number.startsWith("0049")) number=0+number.substring(4);
		return number;
}	

	public void stateChanged(ChangeEvent arg0) {
		update();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("TEL");
		if (fax) sb.append(";TYPE=FAX");
		if (home) sb.append(";TYPE=HOME");
		if (cell) sb.append(";TYPE=CELL");
		if (work) sb.append(";TYPE=WORK");
		if (voice) sb.append(";TYPE=VOICE");
		sb.append(':');
		sb.append(number);
		return sb.toString();
	}

	private void readPhone(String line) {
		if (line.isEmpty()){
			number=null;
			return;
		}
		String phone=line.replace(" ", "").replace("/", "").replace("-", "");
		for (char c:phone.toCharArray()){
			if (!Character.isDigit(c) && c!='+' && c!='(' && c!=')') invalid=true;				
		}
		number = phone;
		numbers.add(number);
	}

	private void update() {
		invalid=false;
		readPhone(numField.getText());
		home=homeBox.isSelected();
		work=workBox.isSelected();
		voice=voiceBox.isSelected();
		cell=cellBox.isSelected();
		fax=faxBox.isSelected();
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.red:Color.green);
		}
	}
}
