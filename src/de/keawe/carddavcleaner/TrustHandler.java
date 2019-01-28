package de.keawe.carddavcleaner;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.keawe.gui.Translations;

public class TrustHandler {

	private static SSLSocketFactory sslSocketFactory;
	private static X509TrustManager externalTrustManager; // this references the default java trust manager 

	public static final SSLSocketFactory getSocketFactory() throws NoSuchAlgorithmException, KeyStoreException {
		if (externalTrustManager == null){
			TrustManagerFactory tmf=TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init((KeyStore)null);
			// in the loop we search the default trust manager for X509 certificates
			for (TrustManager tm:tmf.getTrustManagers()){
				if (tm instanceof X509TrustManager){
					externalTrustManager=(X509TrustManager) tm;
					break;
				}
			}
		}		
		
		if (sslSocketFactory == null) {
			try {
				// in the next lines we create an instance of our own trust manager and pass it to the ssl socket factory
				TrustManager[] tm = new TrustManager[] { new SelfTrustManager(externalTrustManager) };
				SSLContext context = SSLContext.getInstance("SSL");
				context.init(new KeyManager[0], tm, new SecureRandom());
				sslSocketFactory = (SSLSocketFactory) context.getSocketFactory();
			} catch (KeyManagementException e) {
				System.err.println(_("No SSL algorithm support: #",e.getMessage()));
			} catch (NoSuchAlgorithmException e) {
				System.err.println(_("Exception when setting up the key trust management."));
			}
		}
		return sslSocketFactory;
	}
	
	private static String _(String text) { 
		return Translations.get(text);
	}
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
}
