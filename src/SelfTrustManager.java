import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

import sun.security.validator.ValidatorException;

/**
 * This Trust Manager is "naive" because it trusts everyone.
 **/
public class SelfTrustManager implements X509TrustManager {

	private X509TrustManager externalTrustManager;

	public SelfTrustManager(X509TrustManager externalTrustManager) {
		if (externalTrustManager == null) throw new IllegalArgumentException("No X509-Trust Manager found.");
		this.externalTrustManager = externalTrustManager;
	}

	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 * 
	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], String)
	 **/
	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		try {
			System.out.print("checkClientTrusted(");
			externalTrustManager.checkClientTrusted(certificates, authType);
			if (certificates == null || certificates.length == 0) throw new IllegalArgumentException("Empty certificate chain supplied!");
			if (authType == null || authType.isEmpty()) throw new IllegalArgumentException("No authType given!");

			for (X509Certificate certificate : certificates) {
				System.out.println(certificate);
				System.out.println("=======");
			}
			System.out.println("," + authType);
		} catch (ValidatorException ve) {
			ve.printStackTrace();
			System.err.println("catched!");
			System.exit(-1);
		}
	}

	/**
	 * Doesn't throw an exception, so this is how it approves a certificate.
	 * 
	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], String)
	 **/
	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		try {
			System.out.print("checkServerTrusted(");
			externalTrustManager.checkServerTrusted(certificates, authType);
			if (certificates == null || certificates.length == 0) throw new IllegalArgumentException("Empty certificate chain supplied!");
			if (authType == null || authType.isEmpty()) throw new IllegalArgumentException("No authType given!");
			for (X509Certificate certificate : certificates) {
				System.out.println(certificate);
				System.out.println("=======");
			}
			System.out.println("," + authType);
		} catch (ValidatorException ve) {
			ve.printStackTrace();
			System.err.println("catched!");
			System.exit(-1);
		}
	}

	/**
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 **/
	public X509Certificate[] getAcceptedIssuers() {
		return externalTrustManager.getAcceptedIssuers();
	}
}