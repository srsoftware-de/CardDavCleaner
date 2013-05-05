import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;
import java.util.Vector;

import com.sun.media.sound.InvalidFormatException;

public class Contact {
	private StringBuffer sb;
	private TreeSet<Adress> adresses = new TreeSet<Adress>(ObjectComparator.get());
	private TreeSet<Phone> phones = new TreeSet<Phone>(ObjectComparator.get());
	private TreeSet<Email> mails = new TreeSet<Email>(ObjectComparator.get());
	private Name name;
	private String formattedName;
	private String title;

	private String revision;
	private String note;
	private TreeSet<Url> urls = new TreeSet<Url>(ObjectComparator.get());
	private String productId;
	private String uid;
	private boolean htmlMail;
	private String photo;
	private String role;
	private Birthday birthday;	

	public Contact(URL url) throws UnknownObjectException, IOException  {
		parse(url);
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("BEGIN:VCARD\n");

		sb.append("FN:"); if (formattedName!=null) sb.append(formattedName); // required for Version 3
		sb.append("\n");
		
		sb.append(name);// required for Version 3
		sb.append("\n");
		
		if (title!=null) sb.append("TITLE:"+title+"\n");
		if (birthday!=null) sb.append(birthday);
		
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
		//TODO: verbleibende Felder einfügen
		sb.append("END:VCARD\n");
		return sb.toString();
	}


	private void parse(URL url) throws IOException, UnknownObjectException {
		sb = new StringBuffer();

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
		for (int index=0; index<lines.size(); index++){
			line=lines.elementAt(index);
			while (index+1<lines.size() && lines.elementAt(index+1).startsWith(" ")){
				index++;
				line+=lines.elementAt(index).substring(2);
			}			
			sb.append(line + "\n");
			boolean known = false;
			if (line.equals("BEGIN:VCARD")) known = true;
			if (line.equals("END:VCARD")) known = true;
			if (line.startsWith("VERSION:")) known = true;
			if (line.startsWith("ADR;") && (known = true)) readAdress(line);
			if (line.startsWith("UID:") && (known = true)) readUID(line.substring(4));
			if (line.startsWith("TEL;") && (known = true)) readPhone(line);
			if (line.startsWith("EMAIL;") && (known = true)) readMail(line);
			if (line.startsWith("REV:") && (known = true)) readRevision(line.substring(4));
			if (line.startsWith("NOTE:") && (known = true)) readNote(line.substring(5));
			if (line.startsWith("BDAY") && (known = true)) readBirthday(line.substring(4));
			if (line.startsWith("ROLE:") && (known = true)) readRole(line.substring(5));
			if (line.startsWith("URL;") && (known = true)) readUrl(line);
			if (line.startsWith("PRODID:") && (known = true)) readProductId(line.substring(7));
			if (line.startsWith("N:") && (known = true)) readName(line);
			if (line.startsWith("FN:") && (known=true)) readFormattedName(line.substring(3));
			if (line.startsWith("ORG:") && (known = true)) readOrg(line);			
			if (line.startsWith("TITLE:") && (known = true)) readTitle(line.substring(6));
			if (line.startsWith("PHOTO;") && (known = true)) readPhoto(line);
			if (line.startsWith("X-MOZILLA-HTML:") && (known=true)) readMailFormat(line.substring(15));
			if (line.startsWith(" \\n") && line.trim().equals("\\n")) known=true;
			
			
			if (!known) {
				System.err.println(sb.toString());
				throw new UnknownObjectException("unknown entry/instruction found in vcard: " + line);
			}
		}
	}

	private void readBirthday(String bday) {
		birthday=new Birthday(bday);
	}

	private void readPhoto(String line) {		
		photo=line;
	}

	private void readMailFormat(String line) {
		htmlMail=line.toUpperCase().equals("TRUE");
	}

	private void readTitle(String line) {
		if (line.isEmpty()) return;
		title = line;
	}

	private void readOrg(String line) throws InvalidFormatException, UnknownObjectException {
		Organization org = new Organization(line);
		
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

	private void readProductId(String line) {
		if (line.isEmpty()) return;
		productId = line;
	}

	private void readUrl(String line) throws InvalidFormatException, UnknownObjectException {
		urls.add(new Url(line));
	}

	private void readNote(String line) {
		if (line.isEmpty()) return;
		note = line.replace("\\n", "\n");
	}
	
	private void readRole(String line) {
		if (line.isEmpty()) return;
		role = line.replace("\\n", "\n");
	}

	private void readRevision(String line) {
		if (line.isEmpty()) return;
		revision = line;
	}

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
		if (!mail.isEmpty()) mails.add(new Email(line));
	}
}
