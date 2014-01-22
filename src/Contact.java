import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.activation.UnknownObjectException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Contact implements ActionListener, DocumentListener, ChangeListener, Comparable<Contact> {
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd#HHmmss");
	//private String revision;
	//private String productId;
	private Name name;
	private String formattedName; // TODO: eine vcard kann auch mehrere haben!
	private TreeSet<String> titles=new TreeSet<String>();
	private Collection<Phone> phones = new TreeSet<Phone>();
	private TreeSet<Adress> adresses = new TreeSet<Adress>();
	private Collection<Email> mails = new TreeSet<Email>();
	private TreeSet<String> roles=new TreeSet<String>();
	private Birthday birthday;	
	private Label label;
	private boolean htmlMail;
	private TreeSet<Url> urls = new TreeSet<Url>();
	private String uid;
	private TreeSet<String> notes=new TreeSet<String>();
	private TreeSet<String> photos=new TreeSet<String>();
	private TreeSet<Organization> orgs=new TreeSet<Organization>();
	private String vcfName;
	private TreeSet<Messenger> messengers=new TreeSet<Messenger>();
	private TreeSet<String> categories=new TreeSet<String>();
	private Collection<Nickname> nicks=new TreeSet<Nickname>();
	
	/* form elements */
	private JScrollPane scroll;
	private InputField formattedField;
	private VerticalPanel form;
	private VerticalPanel titleForm;
	private VerticalPanel nickForm;
	private VerticalPanel roleForm;
	private JButton newMailButton;
	private JButton newPhoneButton;
	private JButton newTitleButton;
	private JButton newNickButton;
	private JButton newRoleButton;
	private JButton birthdayButton;
	private HorizontalPanel phoneForm;
	private HorizontalPanel adressForm;
	private JButton newAdressButton;
	private HorizontalPanel mailForm;
	private VerticalPanel urlForm;
	private JButton newUrlButton;
	private HorizontalPanel orgForm;
	private JButton newOrgButton;
	private HorizontalPanel messengerForm;
	private JButton newMessengerButton;
	private VerticalPanel categoryForm;
	private JButton newCategoryButton;
	private Vector<TitleField> titleFields;
	private Vector<CategoryField> categoryFields;
	private Vector<RoleField> roleFields;
	private Vector<NoteField> noteFields;
	private VerticalPanel noteForm;
	private JButton newNoteButton;
	
	private JComponent editForm() {
		form=new VerticalPanel();
		scroll=new JScrollPane(form);
		Dimension dim=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		dim.setSize(dim.getWidth()-100, dim.getHeight()-100);
		scroll.setPreferredSize(dim);
		scroll.setSize(scroll.getPreferredSize());
		
		/* Name */
		if (name==null) try {
			name=new Name("N:;;;;");
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		form.add(name.editForm());
		
		/* Formatted Name */
		form.add(formattedField=new InputField("Formatted name",formattedName));
		formattedField.addChangeListener(this);
		
		/* Titles */
		titleForm = new VerticalPanel("Titles");
		titleFields=new Vector<TitleField>();
		for (String t:titles){			
			TitleField titleField=new TitleField("Title", t);
			titleField.addEditListener(this);
			titleForm.add(titleField);
			titleFields.add(titleField);
		}
		titleForm.add(newTitleButton=new JButton("add title"));
		newTitleButton.addActionListener(this);		
		titleForm.scale();		
		form.add(titleForm);
		
		/* Nicknames */
		nickForm=new VerticalPanel("Nicknames");
		for (Nickname nick:nicks){
			nickForm.add(nick.editForm());
		}
		nickForm.add(newNickButton=new JButton("add nickname"));
		newNickButton.addActionListener(this);
		nickForm.scale();
		form.add(nickForm);
		
		/* Roles */
		roleForm = new VerticalPanel("Roles");
		roleFields=new Vector<RoleField>();
		for (String t:roles){			
			RoleField roleField=new RoleField("Role", t);
			roleField.addEditListener(this);
			roleForm.add(roleField);
			roleFields.add(roleField);
		}
		roleForm.add(newRoleButton=new JButton("add role"));
		newRoleButton.addActionListener(this);		
		roleForm.scale();		
		form.add(roleForm);
	
		/* Birthday */
		if (birthday!=null){
			form.add(birthday.editForm());
		} else {
			form.add(birthdayButton=new JButton("add Birthday"));
			birthdayButton.addActionListener(this);
		}
		
		/* Phones */
		phoneForm=new HorizontalPanel("Phones");
		for (Phone p:phones){
			phoneForm.add(p.editForm());
		}
		phoneForm.add(newPhoneButton = new JButton("Add Phone"));
		newPhoneButton.addActionListener(this);
		phoneForm.scale();
		form.add(phoneForm);
		
		/* Adresses */
		adressForm=new HorizontalPanel("Adresses");
		for (Adress a:adresses){
			adressForm.add(a.editForm());
		}
		adressForm.add(newAdressButton=new JButton("Add Address"));
		newAdressButton.addActionListener(this);
		adressForm.scale();
		form.add(adressForm);

		/* Emails */
		mailForm=new HorizontalPanel("Email Adresses");
		for (Email m:mails){
			mailForm.add(m.editForm());
		}
		mailForm.add(newMailButton=new JButton("Add Email"));
		newMailButton.addActionListener(this);
		mailForm.scale();
		form.add(mailForm);
		
		/* URLs */
		urlForm=new VerticalPanel("Websites");
		for (Url u:urls){
			urlForm.add(u.editForm());
		}
		urlForm.add(newUrlButton=new JButton("Add URL"));
		newUrlButton.addActionListener(this);
		urlForm.scale();
		form.add(urlForm);		
		
		/* Organizations */
		orgForm=new HorizontalPanel("Organizations");
		for (Organization o: orgs){
			orgForm.add(o.editForm());
		}
		orgForm.add(newOrgButton=new JButton("Add Organization"));
		newOrgButton.addActionListener(this);
		orgForm.scale();
		form.add(orgForm);
		
		/* Messengers */
		messengerForm=new HorizontalPanel("Messengers");
		for (Messenger m:messengers){
			messengerForm.add(m.editForm());
		}
		messengerForm.add(newMessengerButton=new JButton("Add Messenger"));
		newMessengerButton.addActionListener(this);
		messengerForm.scale();
		form.add(messengerForm);
		
		/* Categories */
		categoryForm = new VerticalPanel("Categories");
		categoryFields=new Vector<CategoryField>();
		for (String c:categories){			
			CategoryField categoryField=new CategoryField("Category", c);
			categoryField.addEditListener(this);
			categoryForm.add(categoryField);
			categoryFields.add(categoryField);
		}
		categoryForm.add(newCategoryButton=new JButton("add category"));
		newCategoryButton.addActionListener(this);		
		categoryForm.scale();		
		form.add(categoryForm);
		
		/* Notes */
		noteForm = new VerticalPanel("Notes");
		noteFields=new Vector<NoteField>();
		for (String n:notes){			
			NoteField noteField=new NoteField("Note", n);
			noteField.addEditListener(this);
			noteForm.add(noteField);
			noteFields.add(noteField);
		}
		noteForm.add(newNoteButton=new JButton("add note"));
		newNoteButton.addActionListener(this);		
		noteForm.scale();		
		form.add(noteForm);	
		
		form.scale();
		return scroll;
	}

	public boolean isInvalid() {
		for (Adress a:adresses){
			if (a.isInvalid()) return true;
		}
		for (Phone p:phones){
			if (p.isInvalid()) return true;
		}
		for (Email m:mails){
			if (m.isInvalid()) return true;
		}
		for (Nickname n:nicks){
			if (n.isInvalid()) return true;
		}
		if (name!=null && name.isInvalid()) return true;
		if (birthday!=null && birthday.isInvalid()) return true;
		if (label!=null && label.isInvalid()) return true;
		for (Organization o:orgs){
			if (o.isInvalid()) return true;	
		}
		for (Messenger m: messengers){
			if (m.isInvalid()) return true;
		}
		return false;
	}

	public boolean conflictsWith(Contact c2){
		if (name!=null && c2.name!=null && !name.canonical().equals(c2.name.canonical())) return true;
		if (birthday!=null && c2.birthday!=null && !birthday.equals(c2.birthday)) return true;
		if (!titles.isEmpty() && !c2.titles.isEmpty() && !titles.equals(c2.titles)) return true;
		if (!roles.isEmpty() && c2.roles.isEmpty() && !roles.equals(c2.roles)) return true;		
		if (!phones.isEmpty() && !c2.phones.isEmpty() && !getPhoneNumbers().equals(c2.getPhoneNumbers())) return true;
		if (!mails.isEmpty() && !c2.mails.isEmpty() && !getMailAdresses().equals(c2.getMailAdresses())) return true;
		if (!adresses.isEmpty() && !c2.adresses.isEmpty() && !getAdressData().equals(c2.getAdressData())) return true;
		if (!urls.isEmpty() && !c2.urls.isEmpty() && !urls.equals(c2.urls))	return true;
		if (!nicks.isEmpty() && !c2.nicks.isEmpty() && !nicks.equals(c2.nicks)) return true;
		if (!notes.isEmpty() && !c2.notes.isEmpty() && !notes.equals(c2.notes))return true;
		if (!orgs.isEmpty() && !c2.orgs.isEmpty() && !orgs.equals(c2.orgs)) return true;
		if (!photos.isEmpty() && !c2.photos.isEmpty() && !photos.equals(c2.photos))	return true;		
		return false;
	}

	private TreeSet<String> getMailAdresses() {
		TreeSet<String> result=new TreeSet<String>();
		for (Email mail:mails) result.add(mail.address());
		return result;
	}

	private TreeSet<String> getPhoneNumbers() {
		TreeSet<String> result=new TreeSet<String>();
		for (Phone phone:phones) result.add(phone.number());
		return result;
	}

	private TreeSet<String> getAdressData() {
		TreeSet<String> result=new TreeSet<String>();
		for (Adress adress:adresses) result.add(adress.canonical());
		return result;
	}
	
	

	public boolean isEmpty() {
		return adresses.isEmpty() &&
					phones.isEmpty() &&
					mails.isEmpty() && 
					titles.isEmpty() &&
					roles.isEmpty() && 
					birthday==null &&
					(categories==null || categories.isEmpty()) &&
					urls.isEmpty() &&
					notes.isEmpty() &&
					photos.isEmpty() &&
					orgs.isEmpty() &&
					nicks.isEmpty();
	}
	
	private void clearFields() {
		adresses.clear();
		phones.clear();
		mails.clear();
		titles.clear();
		roles.clear();
		birthday=null;
		categories.clear();
		urls.clear();
		notes.clear();
		photos.clear();
		orgs.clear();
		nicks.clear();
	}
	
	public void merge(Contact contact,boolean thunderbirdMerge) throws InvalidAssignmentException, ToMuchEntriesForThunderbirdException {
		adresses.addAll(contact.adresses);
		
		/* merging phones by numbers */
		TreeMap<String,Phone> phoneMap=new TreeMap<String, Phone>();
		
		/** phones **/
		/* add the current phones to the phone map */
		for (Phone phone:phones){
			Phone existingPhone = phoneMap.get(phone.number());
			if (existingPhone!=null){
				existingPhone.merge(phone);
			} else phoneMap.put(phone.number(), phone);
		}
		
		/* add the phone numbers of the second contact to the phone map */
		for (Phone phone:contact.phones){
			Phone existingPhone = phoneMap.get(phone.number());
			if (existingPhone!=null){
				existingPhone.merge(phone);
			} else phoneMap.put(phone.number(), phone);
		}
		
		if (thunderbirdMerge) {
			phones=thunderbirdMergePhone(phoneMap.values());
		} else phones=phoneMap.values();
		/** endof phones **/

		/** email adresses **/
		TreeMap<String,Email> mailMap=new TreeMap<String,Email>();
		
		for (Email mail:mails){
			Email existingMail=mailMap.get(mail.address());
			if (existingMail!=null){
				existingMail.merge(mail);
			} else mailMap.put(mail.address(), mail);
		}
		
		for (Email mail:contact.mails){
			Email existingMail=mailMap.get(mail.address());
			if (existingMail!=null){
				existingMail.merge(mail);
			} else mailMap.put(mail.address(), mail);
		}
		
		if (thunderbirdMerge) {
			mails=thunderbirdMergeMail(mailMap.values());
		} else mails=mailMap.values();
		
		TreeMap<String, Nickname> nickMap=new TreeMap<String, Nickname>();
		
		for (Nickname nick:nicks){
			Nickname existingNick=nickMap.get(nick.text());
			if (existingNick!=null){
				existingNick.merge(nick);
			} else nickMap.put(nick.text(), nick);
		}

		for (Nickname nick:contact.nicks){
			Nickname existingNick=nickMap.get(nick.text());
			if (existingNick!=null){
				existingNick.merge(nick);
			} else nickMap.put(nick.text(), nick);
		}
		
		nicks=nickMap.values();

		if (name!=null){
			if (contact.name!=null && !contact.name.equals(name)){
				name=(Name) selectOneOf("name",name,contact.name,contact);
			}
		} else name=contact.name;
		
		if (formattedName!=null){
			if (contact.formattedName!=null && !contact.formattedName.equals(formattedName)){
				formattedName=(String) selectOneOf("formated name", formattedName, contact.formattedName,contact);
			}
		} else formattedName=contact.formattedName;
		
		titles.addAll(contact.titles);
		roles.addAll(contact.roles);
		
		if (categories!=null){
			if (contact.categories!=null){
				categories.addAll(contact.categories);
			}
		} else categories=contact.categories;
		
		if (birthday!=null){
			if (contact.birthday!=null && !contact.birthday.equals(birthday)){
				birthday= (Birthday) selectOneOf("birtday", birthday, contact.birthday,contact);
			}
		} else birthday=contact.birthday;
		
		if (contact.htmlMail) htmlMail=true;
		urls.addAll(contact.urls);
		if (uid==null) uid=contact.uid;
		notes.addAll(contact.notes);
		photos.addAll(contact.photos);
		orgs.addAll(contact.orgs);		
	}
	
	private Collection<Email> thunderbirdMergeMail(Collection<Email> mails) throws ToMuchEntriesForThunderbirdException {
		TreeSet<Email> overloadedCategoryNumbers=new TreeSet<Email>();
		boolean home=false;
		boolean work=false;	

		for (Email mail:mails){
			if (mail.isWorkMail()){
				mail.setWork(); // if address is tagged both, home and work, then set to work only
				if (work) {
					overloadedCategoryNumbers.add(mail);
				} else work=true;
			}
			
			if (mail.isHomeMail()){
				mail.setHome();
				if (home) {
					overloadedCategoryNumbers.add(mail);
				} else home=true;
			}
		}
		for (Email email:overloadedCategoryNumbers){
			if (!work) {
				System.out.println("Using "+email.address()+" as home mail address, as '"+email.category()+"' is already used by another number.");
				email.setWork();
				work=true;
				continue;
			}
			if (!home) {
				System.out.println("Using "+email.address()+" as home mail address, as '"+email.category()+"' is already used by another number.");
				email.setHome();
				home=true;
				continue;
			}
			throw new ToMuchEntriesForThunderbirdException("There is no thunderbird slot left for the following email entry: "+email);			
		}
		return mails;
	}

	private Collection<Phone> thunderbirdMergePhone(Collection<Phone> phones) throws ToMuchEntriesForThunderbirdException {
		TreeSet<Phone> overloadedCategoryNumbers=new TreeSet<Phone>();
		boolean fax=false;
		boolean home=false;
		boolean cell=false;
		boolean work=false;	

		for (Phone phone:phones){
			if (phone.isWorkPhone()){
				phone.setWork();
				if (work) {
					overloadedCategoryNumbers.add(phone);
				} else work=true;
			}
			if (phone.isHomePhone() || phone.isVoice()){
				phone.setHome();
				if (home) {
					overloadedCategoryNumbers.add(phone);
				} else home=true;
			}
			if (phone.isCellPhone()){
				phone.setCell();
				if (cell) {
					overloadedCategoryNumbers.add(phone);
				} else cell=true;
			}
			if (phone.isFax()){
				phone.setFax();
				if (fax) {
					overloadedCategoryNumbers.add(phone);
				} else fax=true;
			}
		}
		for (Phone phone:overloadedCategoryNumbers){
			if (!home) {
				System.out.println("Using "+phone.simpleNumber()+" as home phone number, as '"+phone.category()+"' is already used by another number.");
				phone.setHome();
				home=true;
				continue;
			}
			if (!cell) {
				System.out.println("Using "+phone.simpleNumber()+" as cell phone number, as '"+phone.category()+"' is already used by another number.");
				phone.setCell();
				cell=true;
				continue;
			}
			if (!work) {
				System.out.println("Using "+phone.simpleNumber()+" as home work number, as '"+phone.category()+"' is already used by another number.");
				phone.setWork();
				work=true;
				continue;
			}
			if (!fax) {
				System.out.println("Using "+phone.simpleNumber()+" as home fax number, as '"+phone.category()+"' is already used by another number.");
				phone.setFax();
				fax=true;
				continue;
			}
			throw new ToMuchEntriesForThunderbirdException("There is no thunderbird slot left for the following number entry: "+phone);
		}
		return phones;
	}

	private Object selectOneOf(String title, Object o1, Object o2, Contact contact2) {
		VerticalPanel vp=new VerticalPanel();
		vp.add(new JLabel("<html>Merging the following two contacts:<br>&nbsp;"));
		HorizontalPanel hp=new HorizontalPanel();
		hp.add(new JLabel("<html>"+this.toString(true).replace("\n", "&nbsp;<br>")));
		hp.add(new JLabel("<html>"+contact2.toString(true).replace("\n", "<br>")));
		hp.scale();
		vp.add(hp);
		vp.add(new JLabel("<html><br>Which "+title+" shall be used?"));
		vp.scale();
		UIManager.put("OptionPane.yesButtonText", o1.toString().replace("\\,",","));
		UIManager.put("OptionPane.noButtonText", o2.toString().replace("\\,",","));
		int decision = JOptionPane.showConfirmDialog(null, vp, "Please select", JOptionPane.YES_NO_CANCEL_OPTION);
		UIManager.put("OptionPane.yesButtonText","Yes");
		UIManager.put("OptionPane.noButtonText", "No");
		switch (decision){
			case JOptionPane.YES_OPTION:	return o1;
			case JOptionPane.NO_OPTION:	return o2;
			case JOptionPane.CANCEL_OPTION: System.exit(0);
		}
		return null;
	}

	public Contact(String directory,String name) throws UnknownObjectException, IOException, AlreadyBoundException, InvalidFormatException  {
		vcfName=name;
		parse(new URL(directory+name));
	}
	
	public String toString() {
		return toString(false);
	}
	
	/**
	 * @param shorter if set to TRUE, the contact will be cut down (for display purposes).
	 * @return the code of that contact
	 */
	public String toString(boolean shorter) {
		StringBuffer sb=new StringBuffer();
		sb.append("BEGIN:VCARD\n");
		
		if (!shorter){
			sb.append("VERSION:3.0\n");
			sb.append("PRODID:-//SRSoftwae CalDavCleaner\n");
		}

		if (uid!=null) sb.append("UID:"+uid+"\n");
		
		if (!shorter){
			sb.append(newRevision()); sb.append("\n");			
		}

		sb.append("FN:"); if (formattedName!=null) sb.append(formattedName); // required for Version 3
		sb.append("\n");
		
		sb.append(name);// required for Version 3
		sb.append("\n");
		
		for (Nickname nick:nicks){
			sb.append(nick);
			sb.append("\n");
		}
		
		if (categories!=null && !categories.isEmpty()){
			sb.append("CATEGORIES:");
			for (Iterator<String> it = categories.iterator(); it.hasNext();){
				sb.append(it.next());
				if (it.hasNext()) {
					sb.append(",");
				}		
			}
			sb.append("\n");
		}
		
		for (String title:titles){
			sb.append("TITLE:"+title+"\n");
		}
		
		for (Organization org:orgs){
			sb.append(org);
			sb.append("\n");
		}
		
		for (Messenger messenger:messengers){
			sb.append(messenger);
			sb.append("\n");
		}
		
		for (String role:roles){
			sb.append("ROLE:"+role+"\n");
		}
		
		if (birthday!=null &&	!birthday.toString().equals("BDAY:")) {			
			sb.append(birthday);
			sb.append("\n");
		}
		
		for (Adress adress:adresses){
			sb.append(adress);
			sb.append("\n");
		}
		
		for(Phone phone:phones){
			sb.append(phone);
			sb.append("\n");			
		}
		for(Email mail:mails){
			sb.append(mail);
			sb.append("\n");
		}
		
		if (htmlMail) sb.append("X-MOZILLA-HTML:TRUE\n");
		
		for (Url url:urls){
			sb.append(url);
			sb.append("\n");
		}
		
		if (shorter){
			for (String note:notes){
				sb.append("NOTE:"+((note.length()>30)?(note.substring(0,28)+"..."):note)+"\n");	
			}
		} else {
			for (String note:notes){
				sb.append("NOTE:"+note+"\n");	
			}
		}
		

		if (shorter){
			for (String photo:photos) {
				sb.append(photo.substring(0,30)+"...\n");
			}
		} else {		
			for (String photo:photos) {
				sb.append(photo+"\n");
			}
		}

		sb.append("END:VCARD\n");
		if (!shorter){
			return sb.toString();
		}
		return sb.toString().replace("\\,",",");
	}
	private String newRevision() {
		String date=formatter.format(new Date()).replace('#','T');
		return "REV:"+date;
	}

	private void parse(URL url) throws IOException, UnknownObjectException, AlreadyBoundException, InvalidFormatException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		Vector<String> lines=new Vector<String>();
		String line;
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();
		content.close();
		connection.disconnect();
		for (int index = 0; index < lines.size(); index++) {
			line = lines.elementAt(index);
			while (index + 1 < lines.size() && (lines.elementAt(index + 1).startsWith(" ") || lines.elementAt(index + 1).startsWith("\\n"))) {
				index++;
				String dummy=lines.elementAt(index);
				if (dummy.startsWith(" ")) dummy=dummy.substring(1);
				line += dummy;
			}
			boolean known = false;
			if (line.equals("BEGIN:VCARD")) known = true;
			if (line.equals("END:VCARD")) known = true;
			if (line.startsWith("VERSION:")) known = true;
			if (line.startsWith("ADR") && (known = true)) readAdress(line);
			if (line.startsWith("UID:") && (known = true)) readUID(line.substring(4));
			if (line.startsWith("TEL;") && (known = true)) readPhone(line);
			if (line.startsWith("TEL:") && (known = true)) readPhone(line.replace(":", ";TYPE=home:"));
			if (line.startsWith("EMAIL") && (known = true)) readMail(line);
			if (line.startsWith("NICKNAME") && (known = true)) readNick(line);
			if (line.startsWith("IMPP:") && (known = true)) readIMPP(line);
			if (line.startsWith("X-ICQ:") && (known = true)) readIMPP(line.replace("X-", "IMPP:"));
			if (line.startsWith("X-AIM:") && (known = true)) readIMPP(line.replace("X-", "IMPP:"));
			if (line.startsWith("X-SKYPE:") && (known = true)) readIMPP(line.replace("X-", "IMPP:"));
			if (line.startsWith("REV:")) known = true;// readRevision(line.substring(4));
			if (line.startsWith("NOTE:") && (known = true)) readNote(line.substring(5));
			if (line.startsWith("LABEL") && (known = true)) readLabel(line);
			if (line.startsWith("BDAY") && (known = true)) readBirthday(line.substring(4));
			if (line.startsWith("ROLE:") && (known = true)) readRole(line.substring(5));
			if (line.startsWith("URL") && (known = true)) readUrl(line);
			if (line.startsWith("PRODID:")) known = true; // readProductId(line.substring(7));
			if (line.startsWith("N:") && (known = true)) readName(line);
			if (line.startsWith("FN:") && (known = true)) readFormattedName(line.substring(3));
			if (line.startsWith("ORG:") && (known = true)) readOrg(line);
			if (line.startsWith("TITLE:") && (known = true)) readTitle(line.substring(6));
			if (line.startsWith("PHOTO;") && (known = true)) readPhoto(line);
			if (line.startsWith("CATEGORIES:") && (known = true)) readCategories(line.substring(11));
			if (line.startsWith("X-MOZILLA-HTML:") && (known = true)) readMailFormat(line.substring(15));
			if (line.startsWith(" \\n") && line.trim().equals("\\n")) known = true;

			if (!known) {
				throw new UnknownObjectException("unknown entry/instruction found in vcard "+vcfName+": '" + line+"'");
			}
		}
		changed();
	}

	private void readIMPP(String line) throws UnknownObjectException, InvalidFormatException {
			Messenger messenger = new Messenger(line);
			if (!messenger.isEmpty()) messengers.add(messenger);
		}

	private void readBirthday(String bday) throws InvalidFormatException {
		birthday=new Birthday(bday);
	}
	
	private void readLabel(String line) throws InvalidFormatException {
		label=new Label(line);
	}

	private void readPhoto(String line) {
		photos.add(line);
	}

	private void readMailFormat(String line) {
		htmlMail=line.toUpperCase().equals("TRUE");
	}

	private void readTitle(String line) throws AlreadyBoundException {
		if (line.isEmpty()) return;
		titles.add(line);
	}
	
	private void readRole(String line) throws AlreadyBoundException {
		if (line.isEmpty()) return;
		roles.add(line.replace("\\n", "\n"));
	}
	
	private void readOrg(String line) throws InvalidFormatException, UnknownObjectException, AlreadyBoundException {
		Organization org = new Organization(line);		
		if (!org.isEmpty()) orgs.add(org);
	}

	private void readUID(String uid) {
		if (uid.isEmpty()) return;
		this.uid=uid;
	}

	private void readFormattedName(String fn) {
		if (fn.isEmpty()) return;
		formattedName=fn;
	}

	private void readName(String line) throws InvalidFormatException, UnknownObjectException {
		Name n = new Name(line);
		if (!n.isEmpty())	name=n;

	}

	/*private void readProductId(String line) {
		if (line.isEmpty()) return;
		productId = line;
	}*/

	private void readUrl(String line) throws InvalidFormatException, UnknownObjectException {
		Url url=new Url(line);
		if (!url.isEmpty()) urls.add(url);
	}

	private void readNote(String line) throws AlreadyBoundException {
		if (line.isEmpty()) return;
		notes.add(line);
	}

	
	private void readCategories(String line) throws AlreadyBoundException {
		if (line.isEmpty()) return;
		if (categories==null) categories=new TreeSet<String>();
		String[] cats = line.split(",");
		for (String category:cats){
			categories.add(category.trim());
		}
	}

/*	private void readRevision(String line) {
		if (line.isEmpty()) return;
		revision = line;
	}*/

	private void readPhone(String line) throws InvalidFormatException, UnknownObjectException {
		Phone phone = new Phone(line);
		if (!phone.isEmpty()) phones.add(phone);
	}

	private void readAdress(String line) throws UnknownObjectException, InvalidFormatException {
		Adress adress = new Adress(line);
		if (!adress.isEmpty()) adresses.add(adress);
	}
	
	private void readMail(String line) throws UnknownObjectException, InvalidFormatException {
		Email mail = new Email(line);
		if (!mail.isEmpty()) mails.add(mail);
	}

	private void readNick(String line) throws UnknownObjectException, InvalidFormatException {
		Nickname nick = new Nickname(line);
		if (!nick.isEmpty()) nicks.add(nick);
	}
	
	public Name name() {
		return name;
	}

	public TreeSet<String> phoneNumbers() {
		TreeSet<String> numbers=new TreeSet<String>();
		for (Phone p:phones)	numbers.add(p.number());
		return numbers;
	}
	
	public TreeSet<String> simpleNumbers(){
		TreeSet<String> numbers=new TreeSet<String>();
		for (Phone p:phones) numbers.add(p.simpleNumber());
		return numbers;
	}

	public TreeSet<String> mailAdresses() {
		TreeSet<String> mails=new TreeSet<String>();
		for (Email e:this.mails){
			mails.add(e.address());
		}
		return mails;
	}
	
	public String vcfName(){
		return vcfName;
	}

	public byte[] getBytes() {
		return toString().getBytes();
	}

	public void generateName() {
		try {
			vcfName=(new MD5Hash(this))+".vcf";
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public File writeToFile() throws IOException {
		File f=new File(vcfName());
		BufferedWriter bw=new BufferedWriter(new FileWriter(f));
		bw.write(toString());
		bw.close();
		return f;
	}

	public TreeSet<String> messengers() throws UnknownObjectException {
		TreeSet<String> messengers=new TreeSet<String>();
		for (Messenger m:this.messengers){
			messengers.add(m.id());
		}
		return messengers;
	}

	public boolean edit() {
		String before=this.toString();
		String [] options={"Ok", "Delete this contact"};
		int choice=JOptionPane.showOptionDialog(null, editForm(),													 "Edit contact"		, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null, options, options[0]);
		switch (choice){
		case 1:
				clearFields();
				break;
			default:
				changed();
		}
		return !this.equals(before);
	}

	private void updateNicks(){
		TreeSet<Nickname> newNicks = new TreeSet<Nickname>();
		for (Nickname n:nicks){
			if (!n.isEmpty()){
				newNicks.add(n);
			}
		}
		nicks=newNicks;
	}
	
	private void updatePhones(){
		TreeSet<Phone> newPhones=new TreeSet<Phone>();
		for (Phone p:phones){
			if (!p.isEmpty()) {
				newPhones.add(p);
			}
		}
		phones=newPhones;
	}
	
	private void updateUrls(){
		TreeSet<Url> newUrls=new TreeSet<Url>();
		for (Url p:urls){
			if (!p.isEmpty()) {
				newUrls.add(p);
			}
		}
		urls=newUrls;
	}
	
	private void updateOrgs(){
		TreeSet<Organization> newOrgs=new TreeSet<Organization>();
		for (Organization p:orgs){
			if (!p.isEmpty()) {
				newOrgs.add(p);
			}
		}
		orgs=newOrgs;
	}
	
	private void updateAdresses(){
		TreeSet<Adress> newAdresses=new TreeSet<Adress>();
		for (Adress p:adresses){
			if (!p.isEmpty()) {
				newAdresses.add(p);
			}
		}
		adresses=newAdresses;
	}
	
	private void updateEmails(){
		TreeSet<Email> newMails=new TreeSet<Email>();
		for (Email e:mails){
			if (!e.isEmpty()) {
				newMails.add(e);
			}
		}
		mails=newMails;
	}
	
	private void updateMessengers(){
		TreeSet<Messenger> newMessengers=new TreeSet<Messenger>();
		for (Messenger m:messengers){
			if (!m.isEmpty()) {
				newMessengers.add(m);
			}
		}
		messengers=newMessengers;
	}

	private void changed() {
		updateNicks();
		updatePhones();
		updateAdresses();
		updateEmails();
		updateUrls();
		updateOrgs();
		updateMessengers();
	}

	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source==newTitleButton){
			TitleField titleField=new TitleField("Title");
			titleField.addEditListener(this);
			titleFields.add(titleField);
			titleForm.insertCompoundBefore(newTitleButton, titleField);
			form.rescale();
		}
		
		if (source==newNickButton){
			try {
				Nickname newNick=new Nickname("NICKNAME:");
				VerticalPanel newNickForm = newNick.editForm();
				nickForm.insertCompoundBefore(newNickButton, newNickForm);
				nicks.add(newNick);
				form.rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newRoleButton){
			RoleField roleField=new RoleField("Role");
			roleField.addEditListener(this);
			roleFields.add(roleField);
			roleForm.insertCompoundBefore(newRoleButton, roleField);
			form.rescale();
		}
		if (source==birthdayButton){
			try {
				birthday=new Birthday(":"+Calendar.getInstance().get(Calendar.YEAR));
				form.replace(birthdayButton,birthday.editForm());
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newPhoneButton){
			try {
				Phone newPhone=new Phone("TEL;:");
				VerticalPanel newPhoneForm = newPhone.editForm();
				phoneForm.insertCompoundBefore(newPhoneButton,newPhoneForm);
				phones.add(newPhone);
				form.rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newAdressButton){
			try {
				Adress newAdress=new Adress("ADR;:");
				VerticalPanel newAdressForm = newAdress.editForm();
				adressForm.insertCompoundBefore(newAdressButton,newAdressForm);
				adresses.add(newAdress);
				form.rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newMailButton){
			try {
				Email newMail=new Email("EMAIL:");
				VerticalPanel newMailForm = newMail.editForm();
				mailForm.insertCompoundBefore(newMailButton,newMailForm);
				mails.add(newMail);
				form.rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newUrlButton){
			try {
				Url newUrl=new Url("URL:");
				VerticalPanel newUrlForm = newUrl.editForm();
				urlForm.insertCompoundBefore(newUrlButton,newUrlForm);
				urls.add(newUrl);
				form.rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newOrgButton){
			try {
				Organization newOrg=new Organization("ORG:");
				VerticalPanel newOrgForm = newOrg.editForm();
				orgForm.insertCompoundBefore(newOrgButton,newOrgForm);
				orgs.add(newOrg);
				form.rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newMessengerButton){
			try {
				Messenger newMessenger=new Messenger("IMPP::");
				VerticalPanel newMessengerForm = newMessenger.editForm();
				messengerForm.insertCompoundBefore(newMessengerButton,newMessengerForm);
				messengers.add(newMessenger);
				form.rescale();
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
		}
		if (source==newCategoryButton){
			CategoryField categoryField=new CategoryField("Category");
			categoryField.addEditListener(this);
			categoryFields.add(categoryField);
			categoryForm.insertCompoundBefore(newCategoryButton, categoryField);
			form.rescale();
		}
		if (source==newNoteButton){
			NoteField newNoteField=new NoteField("Note");
			newNoteField.addEditListener(this);
			noteFields.add(newNoteField);
			noteForm.insertCompoundBefore(newNoteButton, newNoteField);
			form.rescale();
		}
	}

	public void changedUpdate(DocumentEvent e) {
		update();
	}

	public void insertUpdate(DocumentEvent e) {
		update();
	}

	public void removeUpdate(DocumentEvent e) {
		update();
	}
	
	public void stateChanged(ChangeEvent e) {
		update(e.getSource());
	}

	private void update(Object source) {
		if (source instanceof TitleField)	updateTitles();
		if (source instanceof RoleField) updateRoles();
		if (source instanceof CategoryField) updateCategories();
		if (source instanceof NoteField) updateNotefields();
	}

	private void updateNotefields() {
		notes.clear();
		for (NoteField nf:noteFields){
			String note=nf.getText().trim();
			if (note!=null && !note.isEmpty()){
				notes.add(note);
				nf.setBackground(Color.green);
			} else {
				nf.setBackground(Color.yellow);
			}
		}		
	}

	private void updateCategories() {
		categories.clear();
		for (CategoryField cf:categoryFields){
			String cat=cf.getText().trim();
			if (cat!=null && !cat.isEmpty()){
				categories.add(cat);
				cf.setBackground(Color.green);
			} else {
				cf.setBackground(Color.yellow);
			}
		}		
	}

	private void updateTitles() {
		titles.clear();
		for (TitleField tf:titleFields){
			String title=tf.getText().trim();
			if (title!=null && !title.isEmpty()){
				titles.add(title);
				tf.setBackground(Color.green);
			} else {
				tf.setBackground(Color.yellow);
			}		
		}		
	}
	
	private void updateRoles() {
		roles.clear();
		for (RoleField rf:roleFields){
			String role=rf.getText().trim();
			if (role!=null && !role.isEmpty()){
				roles.add(role);
				rf.setBackground(Color.green);
			} else {
				rf.setBackground(Color.yellow);
			}
		}		
	}

	private void update() {
		formattedName=formattedField.getText();
	}

	public int compareTo(Contact o) {
		return vcfName().compareTo(o.vcfName());
	}	
}
