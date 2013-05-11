import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * implements MD5-hashing for arbitrary objects
 * @author Stephan Richter
 *
 */
public class MD5Hash {
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return hashtext;
	}
	
}