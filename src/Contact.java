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
	private TreeSet<Adress> adresses=new TreeSet<Adress>(ObjectComparator.get());
	private TreeSet<Phone> phones=new TreeSet<Phone>(ObjectComparator.get());

	public Contact(URL url) throws IOException, UnknownObjectException {
		parse(url);
	}

	private void parse(URL url) throws IOException, UnknownObjectException {
		sb=new StringBuffer();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		while ((line = in.readLine()) != null) {
			boolean known=false;
			if (line.equals("BEGIN:VCARD")) known=true;
			if (line.startsWith("VERSION:")) known=true;
			if (line.startsWith("ADR") && (known=true)) readAdress(line);
			if (line.startsWith("TEL") && (known=true)) readPhone(line);
			
			if (!known) throw new UnknownObjectException("unknown entry/instruction found in vcard: "+line);
			sb.append(line + "\n");
		}
		in.close();
		content.close();
		connection.disconnect();		
	}
	
	private void readPhone(String line) throws InvalidFormatException, UnknownObjectException {		
		phones.add(new Phone(line));
	}

	private void readAdress(String line) throws UnknownObjectException, InvalidFormatException {		
		adresses.add(new Adress(line));
	}

	public String toString() {		
		return sb.toString();
	}

}
