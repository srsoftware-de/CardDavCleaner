import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.activation.UnknownObjectException;
import java.util.TreeSet;

import com.sun.media.sound.InvalidFormatException;

public class Contact {
	private StringBuffer sb;
	private TreeSet<Adress> adresses = new TreeSet<Adress>(ObjectComparator.get());
	private TreeSet<Phone> phones = new TreeSet<Phone>(ObjectComparator.get());
	private TreeSet<Email> mails = new TreeSet<Email>(ObjectComparator.get());
	private String revision;
	private String note;
	private TreeSet<Url> urls = new TreeSet<Url>(ObjectComparator.get());
	private String productId;
	private String display;
	private Name name;
	private String formattedName;
	private String uid;	

	public Contact(URL url) throws IOException, UnknownObjectException {
		parse(url);
	}

	private void parse(URL url) throws IOException, UnknownObjectException {
		sb = new StringBuffer();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line + "\n");
			boolean known = false;
			if (line.equals("BEGIN:VCARD")) known = true;
			if (line.startsWith("VERSION:")) known = true;
			if (line.startsWith("ADR;") && (known = true)) readAdress(line);
			if (line.startsWith("UID:") && (known = true)) readUID(line.substring(4));
			if (line.startsWith("TEL;") && (known = true)) readPhone(line);
			if (line.startsWith("EMAIL;") && (known = true)) readMail(line);
			if (line.startsWith("REV:") && (known = true)) readRevision(line.substring(4));
			if (line.startsWith("NOTE:") && (known = true)) readNote(line.substring(5));
			if (line.startsWith("URL;") && (known = true)) readUrl(line);
			if (line.startsWith("PRODID:") && (known = true)) readProductId(line.substring(7));
			if (line.startsWith("N:") && (known = true)) readName(line);
			if (line.startsWith("FN:") && (known=true)) readFormattedName(line.substring(3));
			if (line.startsWith("ORG:") && (known = true)) readOrg(line);
			

			if (!known) {
				System.err.println(sb.toString());
				throw new UnknownObjectException("unknown entry/instruction found in vcard: " + line);
			}
		}
		in.close();
		content.close();
		connection.disconnect();
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
		name=new Name(line);

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
		note = line;
	}

	private void readRevision(String line) {
		if (line.isEmpty()) return;
		revision = line;
	}

	private void readPhone(String line) throws InvalidFormatException, UnknownObjectException {
		phones.add(new Phone(line));
	}

	private void readAdress(String line) throws UnknownObjectException, InvalidFormatException {
		adresses.add(new Adress(line));
	}

	private void readMail(String line) throws UnknownObjectException, InvalidFormatException {
		mails.add(new Email(line));
	}

	public String toString() {
		return sb.toString();
	}

}
