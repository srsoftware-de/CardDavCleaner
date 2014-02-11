import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import com.sun.net.ssl.TrustManager;


public class SelfTrustManager implements X509TrustManager, TrustManager {

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub
		System.out.println("checkClientTrusted");
		for (X509Certificate cert:chain){
			System.out.println(cert);
		}
		System.out.println(authType);
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub
		System.out.println("checkServerTrusted");
		for (X509Certificate cert:chain){
			System.out.println(cert);
		}
		System.out.println(authType);
		
	}

	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		System.out.println("getAcceptedIssuers => null");
		return null;
	}

}
