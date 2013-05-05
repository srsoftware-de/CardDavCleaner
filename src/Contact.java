import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeSet;

public class Contact {
	private StringBuffer sb;
	private TreeSet<Adress> adresses=new TreeSet<Adress>();

	public Contact(URL url) throws IOException {
		parse(url);
	}

	private void parse(URL url) throws IOException {
		sb=new StringBuffer();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream content = (InputStream) connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("ADR")) readAdress(line);
			System.out.println(line);
			sb.append(line + "\n");
		}
		in.close();
		content.close();
		connection.disconnect();
System.exit(0);
	}
	
	private void readAdress(String line) {
		
	}

	public String toString() {		
		return sb.toString();
	}

}
