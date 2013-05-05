import java.util.Comparator;


public class ObjectComparator implements Comparator<Object>{
	private static ObjectComparator staticComparator;
	
	public int compare(Object t1,Object t2){
		//System.out.println("Compared "+t1.getClass().getName()+" ("+t1.toString()+")\nwith "+t2.getClass().getName()+" ("+t2.toString()+").");
		if (t1==null){
			if (t2==null) return 0;
			return -1;
		}
		if (t2==null) return 1;
		return t1.toString().compareTo(t2.toString());
	}

	public static ObjectComparator get() {
	  if (staticComparator==null) staticComparator=new ObjectComparator();
	  return staticComparator;
  }
}