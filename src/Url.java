import java.awt.Color;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Url extends Mergable<Url> implements ChangeListener, Comparable<Url> {
	public static void test() {
		try {
			System.out.print(_("Url creation test (null)..."));
			String testCase = null;
			try {
				Url nM = new Url(testCase);
				System.err.println(_("failed: #", nM));
				System.exit(-1);
			} catch (InvalidFormatException e) {
				System.out.println(_("ok"));
      }

			System.out.print(_("Url creation test (empty)..."));
			testCase = "URL:";
			Url emptyN = new Url(testCase);
			if (emptyN.toString().equals(testCase) && !emptyN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", emptyN));
				System.exit(-1);
			}

			System.out.print(_("Url creation test (simple)..."));
			testCase = "URL:http://www.example.org";
			Url simplN = new Url(testCase);
			if (simplN.toString().equals(testCase) && !simplN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", simplN));
				System.exit(-1);
			}

			System.out.print(_("Url creation test (work/invalid)..."));
			testCase = "URL;TYPE=WORK:internet";
			Url workN = new Url(testCase);
			if (workN.toString().equals(testCase) && workN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", workN));
				System.exit(-1);
			}

			System.out.print(_("Url creation test (home/valid)..."));
			testCase = "URL;TYPE=HOME:http://example.com";
			Url homeN = new Url(testCase);
			if (homeN.toString().equals(testCase) && !homeN.isInvalid()) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", homeN));
				System.exit(-1);
			}

			Url[] urlnames = { emptyN,simplN,workN,homeN };

			System.out.print(_("Url isEmpty test..."));
			int comp = 0;
			int num = 0;
			for (Url m : urlnames) {
				comp++;
				if (!m.isEmpty()) {
					num++;
				}
				if (m == emptyN) {
					comp--;
				}
			}
			if (num == comp) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Url compare test..."));
			comp = 0;
			num = 0;
			for (Url m : urlnames) {
				comp++;
				if (m.compareTo(workN) != 0 && m.compareTo(workN) == -workN.compareTo(m)) {
					num++;
				} else {
					if (workN==m){
						num++;
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Url compatibility test..."));
			comp = 0;
			num = 0;
			for (Url a : urlnames) {
				for (Url b : urlnames) {
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat = (a + "" + b).replace("URL", "").replace(";TYPE=WORK", "").replace(";TYPE=HOME", "").replaceFirst(":", "");
						if (concat.equals("http://www.example.org:internet") ||
								concat.equals("http://www.example.org:http://example.com") ||
								concat.equals("internet:http://www.example.org") ||
								concat.equals("internet:http://example.com") ||
								concat.equals("http://example.com:http://www.example.org") ||
								concat.equals("http://example.com:internet")) {
							comp++;
						} else {
							System.err.println(a + " <=> " + b);
						}
					}
				}
			}
			if (comp == num) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
			
			System.out.print(_("Url clone test..."));
			comp=0;
			num=0;
			for (Url m:urlnames){
				comp++;
				try {
					if (m.toString().equals(m.clone().toString())){
						num++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			if (comp==num){
				System.out.println(_("ok"));
			} else {				
								System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

			System.out.print(_("Url merge test..."));
			comp=0;
			num=0;
			for (Url m:urlnames){
				try {
					comp+=2;
					Url clone1=(Url) m.clone();
					Url clone2=(Url) workN.clone();
					
					if (clone1.mergeWith(workN) && clone1.toString().equals(workN.toString())) num++;
					if (clone2.mergeWith(m) && clone2.toString().equals(workN.toString())) num++;
					if (clone1.toString().equals("NICKNAME;TYPE=WORK;TYPE=INTERNET:Edward Snowden")) num++;
					if (clone2.toString().equals("NICKNAME;TYPE=WORK;TYPE=INTERNET:Edward Snowden")) num++;if (comp>num){
						if ((m.url!=null && !m.url.isEmpty()) && (workN.url!=null && !workN.url.isEmpty()) && !m.url.equals(workN.url)){
							num+=2;
						}
					}
					if (comp>num){
						System.out.println();
						System.out.println("fb: "+workN);
						System.out.println(" b: "+m);
						System.out.println("merged:");
						System.out.println("fb: "+clone2);
						System.out.println(" b: "+clone1);
					}
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println(_("ok"));
			} else {				
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
		} catch (UnknownObjectException e) {
      e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();

		}

	}	
	
	public static enum Category {
		HOME {
			@Override
			public String toString() {
				return "HOME";
			}
		},
		WORK {
			@Override
			public String toString() {
				return "WORK";
			}
		};

		public abstract String toString();

	};
	
	private static String _(String text) { 
		return Translations.get(text);
	}
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}	
	private boolean invalid=false;
	private String url;
	private InputField urlField;
	private JCheckBox homeBox;
	private JCheckBox workBox;
	private VerticalPanel form;
	private Pattern ptr = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
	private TreeSet<Category> categories=new TreeSet<Url.Category>();
	
	public Url(String content) throws UnknownObjectException, InvalidFormatException {
		if (content==null||!content.startsWith("URL")) throw new InvalidFormatException(_("Url does not start with \"URL\": #",content));
		String line = content.substring(3);
		while(!line.startsWith(":")){
			if (line.startsWith(";")){
				line=line.substring(1);
				continue;
			}
			if (line.toUpperCase().startsWith("TYPE=HOME")){
				categories.add(Category.HOME);
				line=line.substring(9);
				continue;
			} 
			if (line.toUpperCase().startsWith("TYPE=WORK")){
				categories.add(Category.WORK);
				line=line.substring(9);
				continue;
			} 
			if (line.toUpperCase().startsWith("WORK=")){
				categories.add(Category.WORK);
				line=line.substring(5);
				continue;
			} 
			throw new UnknownObjectException(line+" in "+content);
		}
		readUrl(line.substring(1));		
	}

	public int compareTo(Url otherUrl) {
		return this.toString().compareTo(otherUrl.toString());
	}
	
	public VerticalPanel editForm() {
		form=new VerticalPanel(_("Web Adress"));
		if (invalid) form.setBackground(Color.red);
		if (isEmpty()) form.setBackground(Color.yellow);
		form.add(urlField=new InputField(_("URL"),url));
		urlField.addEditListener(this);
		form.add(homeBox=new JCheckBox(_("Home"),categories.contains(Category.HOME)));
		homeBox.addChangeListener(this);
		form.add(workBox=new JCheckBox(_("Work"),categories.contains(Category.WORK)));
		workBox.addChangeListener(this);
		form.scale();
		return form;
	}

	@Override
  public boolean isCompatibleWith(Url other) {
		if (different(url, other.url)) return false;
	  return true;
  }

	public boolean isEmpty() {
		return url==null || url.isEmpty();
	}

	@Override
  public boolean mergeWith(Url other) {
		if (!isCompatibleWith(other)) return false;
		url=merge(url, other.url);
		categories.addAll(other.categories);
	  return true;
  }

	public void stateChanged(ChangeEvent arg0) {
		update();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("URL");
		if (categories.contains(Category.HOME)) sb.append(";TYPE=HOME");
		if (categories.contains(Category.WORK)) sb.append(";TYPE=WORK");
		sb.append(":");
		if (url!=null)sb.append(url);
		return sb.toString();
	}
	
	public String address(){
		return url;
	}
	
	private void checkValidity() {
		invalid=false;
		if (url==null) return;
		if (ptr.matcher(url).matches()){
			invalid=false;
		} else {
			invalid=true;
		}
	}

	private boolean isInvalid() {
	  return invalid;
  }

	private void readUrl(String line) {
		line=line.trim();
		if (line.isEmpty()) {
			line=null;
		}
		url = line;
		checkValidity();
	}

	private void update(){
		readUrl(urlField.getText());
		if (homeBox.isSelected()){
			categories.add(Category.HOME);
		}
		if (workBox.isSelected()){
			categories.add(Category.WORK);
		}
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(invalid?Color.red:Color.green);
		}	
	}
	
	protected Url clone() throws CloneNotSupportedException {		
		try {
			return new Url(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
	
	/**
	 * @param cloneCategories if set to false, categories of the ancestor will not be copied to the new Url
	 * @return a clone of thins url
	 * @throws CloneNotSupportedException
	 */
	public Url clone(boolean cloneCategories) throws CloneNotSupportedException {
		Url result = clone();
		result.categories.clear();
		return result;
	}
	
	public void addCategory(Category category) {
		categories.add(category);
	}
	public void removeCategory(Category category) {
		categories.remove(category);
	}
	public TreeSet<Category> categories() {
		return new TreeSet<Url.Category>(categories);
	}
}
