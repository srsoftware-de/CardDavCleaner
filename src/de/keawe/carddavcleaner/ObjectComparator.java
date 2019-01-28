package de.keawe.carddavcleaner;

import java.util.Comparator;


public class ObjectComparator implements Comparator<Object>{
	private static ObjectComparator staticComparator;
	
	public static ObjectComparator get() {
	  if (staticComparator==null) staticComparator=new ObjectComparator();
	  return staticComparator;
  }

	public int compare(Object t1,Object t2){
		//System.out.println("Compared "+t1.getClass().getName()+" ("+t1.toString()+")\nwith "+t2.getClass().getName()+" ("+t2.toString()+").");
		return t1.toString().compareTo(t2.toString());
	}
}