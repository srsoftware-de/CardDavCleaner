package de.keawe.carddavcleaner;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VCard {
	
	private StringBuffer buf;
	private String filename=null;

	public StringBuffer buffer() {
		return buf;
	}
	
	public String filename() {
		return filename;
	}
	
	public void setBuffer(StringBuffer code) {
		buf.setLength(0);
		buf.append(code);
	}
	
	@Override
	public String toString() {
		return buf.toString();
	}

	public VCard(String source, String filename) throws IOException {
		this.filename = filename;
		buf = new StringBuffer();
		
		InputStreamReader reader;
		if (source.startsWith("file://")) {
			reader = new FileReader(source.substring(7)+filename);
		} else {
			URL url = new URL(source+filename);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream content = connection.getInputStream();
			
			reader = new InputStreamReader(content);
		}
		
		int character = 0;		
		while ((character = reader.read()) > -1) buf.append((char)character);
		reader.close();
	}



}
