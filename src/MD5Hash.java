import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * implements MD5-hashing for arbitrary objects
 * @author Stephan Richter
 *
 */
public class MD5Hash implements Comparable<MD5Hash>{
	public static void test() {
		String text="Dies ist ein Test";
		System.out.print(_("MD5 hashing..."));
		try {
			MD5Hash hash = new MD5Hash(text);
			if (hash.toString().equals("6cddeb6a2f0582c82dee9a38e3f035d7")){
				System.out.println(_("ok."));
			} else {
				System.err.println(_("failed: #",hash));
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println(_("failed!"));
		}		
	}
	private static String _(String text) { 
		return Translations.get(text);
	}
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
	
	String hashtext;
	/**
	 * create new hash value for given object
	 * @param o the object to be hashed
	 * @throws NoSuchAlgorithmException
	 */
	public MD5Hash(Object o) throws NoSuchAlgorithmException {
		String description=o.toString();
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.reset();
		m.update(description.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1,digest);
		hashtext = bigInt.toString(16);
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
  }
	public int compareTo(MD5Hash o) {
		return hashtext.compareTo(o.hashtext);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return hashtext;
	}
	
}