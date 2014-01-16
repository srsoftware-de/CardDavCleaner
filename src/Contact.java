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

public class Contact implements ActionListener, DocumentListener, ChangeListener {
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd#HHmmss");
	//private String revision;
	//private String productId;
	private Name name;
	private String formattedName; // TODO: eine vcard kann auch mehrere haben!
	private TreeSet<String> titles=new TreeSet<String>(ObjectComparator.get());
	private Collection<Phone> phones = new TreeSet<Phone>(ObjectComparator.get());
	private TreeSet<Adress> adresses = new TreeSet<Adress>(ObjectComparator.get());
	private Collection<Email> mails = new TreeSet<Email>(ObjectComparator.get());
	private TreeSet<String> roles=new TreeSet<String>(ObjectComparator.get());
	private Birthday birthday;	
	private Label label;
	private boolean htmlMail;
	private TreeSet<Url> urls = new TreeSet<Url>(ObjectComparator.get());
	private String uid;
	private TreeSet<String> notes=new TreeSet<String>(ObjectComparator.get());
	private TreeSet<String> photos=new TreeSet<String>(ObjectComparator.get());
	private TreeSet<Organization> orgs=new TreeSet<Organization>(ObjectComparator.get());
	private String vcfName;
	private TreeSet<Messenger> messengers=new TreeSet<Messenger>(ObjectComparator.get());
	private TreeSet<String> categories;
	private Collection<Nickname> nicks=new TreeSet<Nickname>(ObjectComparator.get());
	
	/* form elements */
	private JScrollPane scroll;
	private InputField formattedField;
	private TreeSet<TitleField> titleFields;
	private VerticalPanel form;
	private VerticalPanel titleForm;
	private VerticalPanel nickForm;
	private VerticalPanel roleForm;
	private JButton newMailButton;
	private JButton newPhoneButton;
	private JButton newTitleButton;
	private JButton newNickButton;
	private JButton newRoleButton;
	private TreeSet<RoleField> roleFields;
	
	private JComponent editForm() {
		form=new VerticalPanel();
		scroll=new JScrollPane(form);
		Dimension dim=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		dim.setSize(dim.getWidth()/2-100, dim.getHeight()/2-100);
		scroll.setPreferredSize(dim);
		scroll.setSize(scroll.getPreferredSize());
		
		/* Name */
		form.add(name.editForm());
		
		/* Formatted Name */
		form.add(formattedField=new InputField("Formatted name",formattedName));
		formattedField.addChangeListener(this);
		
		/* Titles */
		titleForm = new VerticalPanel();
		titleFields=new TreeSet<TitleField>(ObjectComparator.get());
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
		roleForm = new VerticalPanel();
		roleFields=new TreeSet<RoleField>(ObjectComparator.get());
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
		}
		// TODO: add/remove
		
		/* Phones */
		for (Phone p:phones){
			form.add(p.editForm());
		}
		newPhoneButton = new JButton("Add Phone");
		newPhoneButton.addActionListener(this);
		form.add(newPhoneButton);
		// TODO: add/remove
		
		/* Adresses */
		for (Adress a:adresses){
			form.add(a.editForm());
		}	
		// TODO: add/remove

		/* Emails */
		for (Email m:mails){
			form.add(m.editForm());
		}
		newMailButton=new JButton("Add Email");
		newMailButton.addActionListener(this);
		form.add(newMailButton);
		// TODO: add/remove
		
		/* URLs */
		for (Url u:urls){
			form.add(u.editForm());
		}
		// TODO: add/remove
		
		/* Organizations */
		for (Organization o: orgs){
			form.add(o.editForm());
		}
		// TODO: add/remove
		
		/* Messengers */
		for (Messenger m:messengers){
			form.add(m.editForm());
		}
		// TODO: add/remove
		
		/* Categories */
		if (categories!=null && !categories.isEmpty()){
			HorizontalPanel cats=new HorizontalPanel();
			for (String c:categories){
				cats.add(new InputField("Category", c));
			}
			cats.scale();
			form.add(cats);
		}
		// TODO: add/remove
		
		/* Notes */
		// TODO: Notes
		
		
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
		if (name.isInvalid()) return true;
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
		TreeSet<String> result=new TreeSet<String>(ObjectComparator.get());
		for (Email mail:mails) result.add(mail.address());
		return result;
	}

	private TreeSet<String> getPhoneNumbers() {
		TreeSet<String> result=new TreeSet<String>(ObjectComparator.get());
		for (Phone phone:phones) result.add(phone.number());
		return result;
	}

	private TreeSet<String> getAdressData() {
		TreeSet<String> result=new TreeSet<String>(ObjectComparator.get());
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
					categories==null &&
					urls.isEmpty() &&
					notes.isEmpty() &&
					photos.isEmpty() &&
					orgs.isEmpty() &&
					nicks.isEmpty();
	}
	
	public void merge(Contact contact,boolean thunderbirdMerge) throws InvalidAssignmentException, ToMuchEntriesForThunderbirdException {
		adresses.addAll(contact.adresses);
		
		/* merging phones by numbers */
		TreeMap<String,Phone> phoneMap=new TreeMap<String, Phone>(ObjectComparator.get());
		
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
		TreeMap<String,Email> mailMap=new TreeMap<String,Email>(ObjectComparator.get());
		
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
		
		TreeMap<String, Nickname> nickMap=new TreeMap<String, Nickname>(ObjectComparator.get());
		
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
		TreeSet<Email> overloadedCategoryNumbers=new TreeSet<Email>(ObjectComparator.get());
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
		TreeSet<Phone> overloadedCategoryNumbers=new TreeSet<Phone>(ObjectComparator.get());
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
		
		if (categories!=null){
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
		
		for (String role:roles){
			sb.append("ROLE:"+role+"\n");
		}
		
		if (birthday!=null) {
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

	private void readBirthday(String bday) {
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
		if (categories==null) categories=new TreeSet<String>(ObjectComparator.get());
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
		TreeSet<String> numbers=new TreeSet<String>(ObjectComparator.get());
		for (Phone p:phones)	numbers.add(p.number());
		return numbers;
	}
	
	public TreeSet<String> simpleNumbers(){
		TreeSet<String> numbers=new TreeSet<String>(ObjectComparator.get());
		for (Phone p:phones) numbers.add(p.simpleNumber());
		return numbers;
	}

	public TreeSet<String> mailAdresses() {
		TreeSet<String> mails=new TreeSet<String>(ObjectComparator.get());
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
			System.out.println(vcfName);
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
		TreeSet<String> messengers=new TreeSet<String>(ObjectComparator.get());
		for (Messenger m:this.messengers){
			messengers.add(m.id());
		}
		return messengers;
	}

	public void edit() {
		System.out.println(this);
		JOptionPane.showConfirmDialog(null, editForm(), "Edit contact", JOptionPane.OK_CANCEL_OPTION);
		changed();
		System.out.println(this);
	}
	
	private void updateNicks(){
		TreeSet<Nickname> newNicks = new TreeSet<Nickname>(ObjectComparator.get());
		for (Nickname n:nicks){
			if (!n.isEmpty()){
				newNicks.add(n);
			}
		}
		nicks=newNicks;
	}
	
	private void updatePhones(){
		TreeSet<Phone> newPhones=new TreeSet<Phone>(ObjectComparator.get());
		for (Phone p:phones){
			if (!p.isEmpty()) {
				newPhones.add(p);
			}
		}
		phones=newPhones;
	}

	private void changed() {
		updateNicks();
		updatePhones();
	}

	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source==newTitleButton){
			TitleField titleField=new TitleField("Title");
			titleField.addEditListener(this);
			titleFields.add(titleField);
			titleForm.insertCompoundBefore(newTitleButton, titleField);
			form.rescale();
			System.out.println("inserted title field");
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
			System.out.println("inserted role field");
		}
		if (source==newPhoneButton){
			try {
				Phone newPhone=new Phone("TEL;:");
				VerticalPanel newPhoneForm = newPhone.editForm();
				form.insertCompoundBefore(newPhoneButton,newPhoneForm);
				phones.add(newPhone);
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
				form.insertCompoundBefore(newMailButton,newMailForm);
				mails.add(newMail);
			} catch (UnknownObjectException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
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
		System.out.println(this);
	}

	private void updateTitles() {
		titles.clear();
		for (TitleField tf:titleFields){
			String title=tf.getText();
			if (title!=null && !title.trim().isEmpty()){
				titles.add(title.trim());
			}
		}		
	}
	
	private void updateRoles() {
		roles.clear();
		for (RoleField tf:roleFields){
			String role=tf.getText();
			if (role!=null && !role.trim().isEmpty()){
				roles.add(role.trim());
			}
		}		
	}

	private void update() {
		formattedName=formattedField.getText();
	}	
}
