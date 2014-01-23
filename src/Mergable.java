import java.security.InvalidParameterException;


public abstract class Mergable<T> {
	public abstract boolean isCompatibleWith(T other);	
	public abstract boolean mergeWith(T other);
	public abstract boolean isEmpty();
	
	public static void test(){
		System.out.println("test() not implemented for "+Mergable.class.getSimpleName());
	}
	
	protected boolean different(String s1,String s2){
		if (s1==null || s1.isEmpty()) return false;
		if (s2==null || s2.isEmpty()) return false;
		return !s1.toLowerCase().equals(s2.toLowerCase());
	}
	
	protected String merge(String s1, String s2) {
		if (different(s1,s2)) throw new InvalidParameterException("Trying to merge \""+s1+"\" with \""+s2+"\"!");
		if (s1==null || s1.isEmpty()){
			return s2;
		}
	  return s1;
  }	
}
