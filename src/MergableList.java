import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class MergableList<Type extends Mergable<Type>> implements SortedSet<Type> {

	private TreeSet<Type> set=new TreeSet<Type>();
	@Override
	public boolean add(Type newElment) {
		for (Type element:set){
			if (element.isCompatibleWith(newElment)){
				return element.mergeWith(newElment);				 
			}
		}
		return set.add(newElment);
	}

	@Override
	public boolean addAll(Collection<? extends Type> entries) {
		boolean success=true;
		for (Type e:entries){
			add(e);
		}
		return success;
	}

	@Override
  public void clear() {
		set.clear();
  }

	@Override
  public Comparator<? super Type> comparator() {
	  return set.comparator();
  }

	@Override
  public boolean contains(Object o) {
	  return set.contains(o);
  }

	@Override
  public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
  }

	@Override
  public Type first() {
	  return set.first();
  }

	@Override
  public SortedSet<Type> headSet(Type toElement) {
	  return set.headSet(toElement);
  }

	@Override
  public boolean isEmpty() {
	  return set.isEmpty();
  }

	@Override
  public Iterator<Type> iterator() {
	  return set.iterator();
  }

	@Override
  public Type last() {
	  return set.last();
  }

	@Override
  public boolean remove(Object o) {
	  return set.remove(o);
  }

	@Override
  public boolean removeAll(Collection<?> c) {
	  return set.removeAll(c);
  }

	@Override
  public boolean retainAll(Collection<?> c) {
	  return set.retainAll(c);
  }

	@Override
  public int size() {
	  return set.size();
  }

	@Override
  public SortedSet<Type> subSet(Type fromElement, Type toElement) {
	  return set.subSet(fromElement, toElement);
  }

	@Override
  public SortedSet<Type> tailSet(Type fromElement) {
	  return set.tailSet(fromElement);
  }

	@Override
  public Object[] toArray() {
		return set.toArray();
  }

	@Override
  public <T> T[] toArray(T[] a) {
		return set.toArray(a);
  }
	
	public String toString(){
		return set.toString();
	}
}
