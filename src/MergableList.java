import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class MergableList<Type extends Mergable<Type>> implements SortedSet<Type> {

	private TreeSet<Type> adresses=new TreeSet<Type>();
	@Override
	public boolean add(Type newAdr) {
		for (Type adr:adresses){
			if (adr.isCompatibleWith(newAdr)){
				return adr.mergeWith(newAdr);				 
			}
		}
		return adresses.add(newAdr);
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
		adresses.clear();
  }

	@Override
  public Comparator<? super Type> comparator() {
	  return adresses.comparator();
  }

	@Override
  public boolean contains(Object o) {
	  return adresses.contains(o);
  }

	@Override
  public boolean containsAll(Collection<?> c) {
		return adresses.containsAll(c);
  }

	@Override
  public Type first() {
	  return adresses.first();
  }

	@Override
  public SortedSet<Type> headSet(Type toElement) {
	  return adresses.headSet(toElement);
  }

	@Override
  public boolean isEmpty() {
	  return adresses.isEmpty();
  }

	@Override
  public Iterator<Type> iterator() {
	  return adresses.iterator();
  }

	@Override
  public Type last() {
	  return adresses.last();
  }

	@Override
  public boolean remove(Object o) {
	  return adresses.remove(o);
  }

	@Override
  public boolean removeAll(Collection<?> c) {
	  return adresses.removeAll(c);
  }

	@Override
  public boolean retainAll(Collection<?> c) {
	  return adresses.retainAll(c);
  }

	@Override
  public int size() {
	  return adresses.size();
  }

	@Override
  public SortedSet<Type> subSet(Type fromElement, Type toElement) {
	  return adresses.subSet(fromElement, toElement);
  }

	@Override
  public SortedSet<Type> tailSet(Type fromElement) {
	  return adresses.tailSet(fromElement);
  }

	@Override
  public Object[] toArray() {
		return adresses.toArray();
  }

	@Override
  public <T> T[] toArray(T[] a) {
		return adresses.toArray(a);
  }
}
