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

public class TrustHandler {

	private static SSLSocketFactory sslSocketFactory;
	private static X509TrustManager externalTrustManager;

	public static final SSLSocketFactory getSocketFactory() throws NoSuchAlgorithmException, KeyStoreException {
		if (externalTrustManager == null){
			TrustManagerFactory tmf= TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init((KeyStore)null);
			for (TrustManager tm:tmf.getTrustManagers()){
				if (tm instanceof X509TrustManager){
					externalTrustManager=(X509TrustManager) tm;
					break;
				}
			}
		}
		
		
		if (sslSocketFactory == null) {
			try {
				TrustManager[] tm = new TrustManager[] { new SelfTrustManager(externalTrustManager) };
				SSLContext context = SSLContext.getInstance("SSL");
				context.init(new KeyManager[0], tm, new SecureRandom());

				sslSocketFactory = (SSLSocketFactory) context.getSocketFactory();

			} catch (KeyManagementException e) {
				System.err.println("No SSL algorithm support: " + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				System.err.println("Exception when setting up the Naive key management.");
			}
		}
		return sslSocketFactory;
	}
}
