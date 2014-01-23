import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class MergableList<Type extends Mergable<Type>> implements SortedSet<Type> {

	public static void test() {
		// TODO Auto-generated method stub
		
	}
	private TreeSet<Type> set=new TreeSet<Type>();
	
	
	public boolean add(Type newElement) {
		if (newElement==null || newElement.isEmpty()) return false;
		for (Type element:set){
			if (element.isCompatibleWith(newElement)){
				return element.mergeWith(newElement);				 
			}
		}
		return set.add(newElement);
	}

	
	public boolean addAll(Collection<? extends Type> entries) {
		boolean success=true;
		for (Type e:entries){
			add(e);
		}
		return success;
	}

	
  public void clear() {
		set.clear();
  }

	
  public Comparator<? super Type> comparator() {
	  return set.comparator();
  }

	
  public boolean contains(Object o) {
	  return set.contains(o);
  }

	
  public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
  }

	
  public Type first() {
	  return set.first();
  }

	
  public SortedSet<Type> headSet(Type toElement) {
	  return set.headSet(toElement);
  }

	
  public boolean isEmpty() {
	  return set.isEmpty();
  }

	
  public Iterator<Type> iterator() {
	  return set.iterator();
  }

	
  public Type last() {
	  return set.last();
  }

	
  public boolean remove(Object o) {
	  return set.remove(o);
  }

	
  public boolean removeAll(Collection<?> c) {
	  return set.removeAll(c);
  }

	
  public boolean retainAll(Collection<?> c) {
	  return set.retainAll(c);
  }

	
  public int size() {
	  return set.size();
  }

	
  public SortedSet<Type> subSet(Type fromElement, Type toElement) {
	  return set.subSet(fromElement, toElement);
  }

	
  public SortedSet<Type> tailSet(Type fromElement) {
	  return set.tailSet(fromElement);
  }

	
  public Object[] toArray() {
		return set.toArray();
  }

	
  public <T> T[] toArray(T[] a) {
		return set.toArray(a);
  }
	
	public String toString(){
		return set.toString();
	}

	public void update(){
		TreeSet<Type> newSet = new TreeSet<Type>();
		for (Type element:set){
			if (element!=null && !element.isEmpty()) newSet.add(element);
		}
		set.clear();
		set.addAll(newSet);
	}
}
