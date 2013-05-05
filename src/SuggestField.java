
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;


public class SuggestField extends JTextField implements KeyListener, ActionListener{
	
  private static final long serialVersionUID = 4183066803864491291L;
	private static PrefixTree dictionary=null;
	private static File dictionaryFile=new File(System.getProperty("user.home")+"/.config/dictionary");
	private static Charset charset =Charset.forName("UTF-8");
	private JPopupMenu suggestionList;
	private int selectionIndex=-1;
	
	public SuggestField() {
		this(true);
	}
	
	public SuggestField(int cols,boolean ignoreCase) {
		super(cols);
		try {
			if (dictionary==null) loadSuggestions(ignoreCase);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addKeyListener(this);
		suggestionList=new JPopupMenu();
	}

	public SuggestField(boolean ignoreCase) {
		super();
		try {
			if (dictionary==null) loadSuggestions(ignoreCase);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addKeyListener(this);
		suggestionList=new JPopupMenu();
	}

	public SuggestField(int i) {
		this(i,true);
	}

	private void loadSuggestions(boolean ignoreCase) throws IOException {
		dictionary=new PrefixTree(ignoreCase);
		if (dictionaryFile.exists()){
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFile), charset));
	    String line = null;
	    while ((line = br.readLine()) != null) dictionary.add(line);
	    br.close();
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		//System.out.println("keyReleased in "+e.getSource().getClass().getSimpleName());
		int c = e.getKeyCode();
		switch (c){
		case 38:
			suggestionList.requestFocus();
			selectionIndex--;
			if (selectionIndex<0) selectionIndex=suggestions.size()-1;
			//System.out.println(selectionIndex);
			return;
		case 40:
			suggestionList.requestFocus();
			selectionIndex++;
			if (selectionIndex==suggestions.size()) selectionIndex=0;
			//System.out.println(selectionIndex);
			return;
		case ' ':
		case '-':
		case ',':
		case '.':
			useSuggestion((char)c);
			break;
		}
		String text=getText();
		if (text!=null && text.length()>0){
			if (text.endsWith(" ")) {
				hidePopup();
				String newWord=trim(lastWord(text));
				dictionary.add(newWord);
			} else {
				suggestFor(lastWord(text));		
			}
		}
	}

	private String trim(String lastWord) {
		boolean changed=true;
		while (changed){
			changed=false;
			if (lastWord.endsWith("\\n")) lastWord=lastWord.substring(0,lastWord.length()-2);
			if (lastWord.endsWith("?") ||
					lastWord.endsWith("!") ||
					lastWord.endsWith(".") ||
					lastWord.endsWith(",") ||
					lastWord.endsWith(";") ||
					lastWord.endsWith(":") ||
					lastWord.endsWith(")") ||
					lastWord.endsWith("]") ||
					lastWord.endsWith("}")) {
				lastWord=lastWord.substring(0, lastWord.length()-1);
				changed=true;
			}
			lastWord=lastWord.trim();
		}
		return lastWord;
	}

	private void useSuggestion(char c) {
		if (!suggestionList.isVisible()) return;
		if (selectionIndex>-1){
			String text=getText();
			text=text.substring(0, text.length()-1);
			int len=lastWord(text).length();
			setText(text+suggestions.get(selectionIndex).substring(len)+c);
			hidePopup();
		}
	}

	private String lastWord(String text) {
		int pos=text.trim().lastIndexOf(' ');
		if (pos>0) return text.substring(pos).trim();
		return text.trim();
	}
	
	private Point pos=null;
	private Vector<String> suggestions;
	
	private void hidePopup(){
		suggestionList.setVisible(false);		
	}

	private void suggestFor(String text) {
		int len=text.length();
		suggestions = dictionary.get(text);
		for (int i=0; i<suggestions.size(); i++){
			String suggestion = suggestions.get(i).trim();
			suggestions.remove(i);
			if (suggestion.length()<len+4 || i>10) {
				i--;
			} else {
				suggestions.insertElementAt(suggestion.trim(), i);
			}
		}
		if (suggestions.isEmpty()){
			hidePopup();
		} else {
			suggestionList.removeAll();
			for (String suggestion:suggestions)	{
				JMenuItem item = new JMenuItem(text+suggestion.substring(text.length()));
				item.addActionListener(this);
				item.addKeyListener(this);
				suggestionList.add(item);
			}
			selectionIndex=-1;
			pos = getCaret().getMagicCaretPosition();
			suggestionList.show(this, pos.x-10, pos.y+20);
		}
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public static void save() throws IOException {
		if (dictionary==null) return;
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictionaryFile), charset));
		for (String line:dictionary.getAll()){
			bw.write(line+"\n");
		}
		bw.close();
		//System.out.println("Suggestsions saved.");
	}

	public void actionPerformed(ActionEvent e) {
		//System.out.println("action in "+e.getSource().getClass().getSimpleName());
		if (e.getSource() instanceof JMenuItem){
			JMenuItem item=(JMenuItem) e.getSource();
			String text=getText();
			int len=lastWord(text).length();
			setText(text.substring(0,text.length()-len)+item.getText());
			hidePopup();
		}
	}
}
