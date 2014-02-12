import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * This Trust Manager is "naive" because it trusts everyone.
 **/
public class SelfTrustManager implements X509TrustManager {
	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 * 
	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], String)
	 **/
	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		System.out.print("checkClientTrusted(");
		for (X509Certificate certificate:certificates){
			System.out.println(certificate);
		}
		System.out.println(","+authType);
	}

	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 * 
	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], String)
	 **/
	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		System.out.print("checkServerTrusted(");
		for (X509Certificate certificate:certificates){
			System.out.println(certificate);
		}
		System.out.println(","+authType);

	}

	/**
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 **/
	public X509Certificate[] getAcceptedIssuers() {
		System.out.println("getAcceptedIssuers");
		return null; // I've seen someone return new X509Certificate[ 0 ];
	}
}