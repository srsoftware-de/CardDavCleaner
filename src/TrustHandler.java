import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


public class TrustHandler {
	
	private static SSLSocketFactory sslSocketFactory;

	public static final SSLSocketFactory getSocketFactory()
	{
	  if ( sslSocketFactory == null ) {
	    try {
	      TrustManager[] tm = new TrustManager[] { new SelfTrustManager() };
	      SSLContext context = SSLContext.getInstance ("SSL");
	      context.init( new KeyManager[0], tm, new SecureRandom( ) );

	      sslSocketFactory = (SSLSocketFactory) context.getSocketFactory ();

	    } catch (KeyManagementException e) {
	      System.err.println("No SSL algorithm support: " + e.getMessage()); 
	    } catch (NoSuchAlgorithmException e) {
	    	System.err.println("Exception when setting up the Naive key management.");
	    }
	  }
	  return sslSocketFactory;
	}
}
