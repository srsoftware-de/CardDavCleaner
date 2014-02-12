import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import sun.security.validator.ValidatorException;

/**
 * This Trust Manager is "naive" because it trusts everyone.
 **/
public class SelfTrustManager implements X509TrustManager {

	private static final int CN = 0;
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
		} catch (ValidatorException ve) {
			if (certificates == null || certificates.length == 0) throw new IllegalArgumentException("Empty certificate chain supplied!");
			if (authType == null || authType.isEmpty()) throw new IllegalArgumentException("No authType given!");
			for (X509Certificate certificate : certificates) {
				certificate.checkValidity();
				System.out.println("ID:"+certificate.getIssuerUniqueID());
				System.out.println("Principal:"+certificate.getIssuerX500Principal());
				System.out.println("DN:"+certificate.getIssuerDN());
				System.out.println("alt:"+certificate.getIssuerAlternativeNames());
				System.out.println("from:"+certificate.getNotBefore());
				System.out.println("to:"+certificate.getNotAfter());
				System.out.println("serial:"+certificate.getSerialNumber());
				System.out.println("version:"+certificate.getVersion());
				System.out.println("=======");				
				JOptionPane.showConfirmDialog(null, formatCert(certificate), getCertPart(certificate,CN), JOptionPane.YES_NO_OPTION);
			}
			System.out.println("," + authType);

			System.exit(-1);
		}
	}
	
	String getCertPart(X509Certificate certificate, int part){
		System.out.println(certificate.getIssuerX500Principal().getName());
		return "Cert";
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
		} catch (ValidatorException ve) {
			if (certificates == null || certificates.length == 0) throw new IllegalArgumentException("Empty certificate chain supplied!");
			if (authType == null || authType.isEmpty()) throw new IllegalArgumentException("No authType given!");
			for (X509Certificate certificate : certificates) {
				certificate.checkValidity();
				System.out.println("ID:"+certificate.getIssuerUniqueID());
				System.out.println("Principal:"+certificate.getIssuerX500Principal());
				System.out.println("DN:"+certificate.getIssuerDN());
				System.out.println("alt:"+certificate.getIssuerAlternativeNames());
				System.out.println("from:"+certificate.getNotBefore());
				System.out.println("to:"+certificate.getNotAfter());
				System.out.println("serial:"+certificate.getSerialNumber());
				System.out.println("version:"+certificate.getVersion());
				System.out.println("=======");				
				JOptionPane.showConfirmDialog(null, formatCert(certificate), getCertPart(certificate,CN), JOptionPane.YES_NO_OPTION);
			}			
			System.exit(-1);
		}
	}

	private VerticalPanel formatCert(X509Certificate certificate) {
		VerticalPanel result=new VerticalPanel("Certificate Info");
		result.add(new JLabel("Certificate Information here"));
		result.scale();
		return result;
	}

	/**
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 **/
	public X509Certificate[] getAcceptedIssuers() {
		return externalTrustManager.getAcceptedIssuers();
	}
}