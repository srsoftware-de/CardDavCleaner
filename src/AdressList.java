import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class AdressList implements SortedSet<Adress> {

	private TreeSet<Adress> adresses=new TreeSet<Adress>();
	@Override
	public boolean add(Adress newAdr) {
		for (Adress adr:adresses){
			if (adr.isCompatibleWith(newAdr)){
				return adr.mergeWith(newAdr);				 
			}
		}
		return adresses.add(newAdr);
	}

	@Override
	public boolean addAll(Collection<? extends Adress> adresses) {
		boolean success=true;
		for (Adress a:adresses){
			add(a);
		}
		return success;
	}

	@Override
	public void clear() {
		adresses.clear();
	}

	@Override
	public boolean contains(Object obj) {
		return adresses.contains(obj);
	}

	@Override
	public boolean containsAll(Collection<?> objects) {
		for (Object o:objects){
			if (!contains(o)) return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return adresses.isEmpty();
	}

	@Override
	public Iterator<Adress> iterator() {
		return adresses.iterator();
	}

	@Override
	public boolean remove(Object obj) {
		return adresses.remove(obj);
	}

	@Override
	public boolean removeAll(Collection<?> objects) {
		return adresses.removeAll(objects);
	}

	@Override
	public boolean retainAll(Collection<?> objects) {
		return adresses.retainAll(objects);
	}

	@Override
	public int size() {
		return adresses.size();
	}

	@Override
	public Object[] toArray() {
		return adresses.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return adresses.toArray(arg0);
	}

	@Override
	public Comparator<? super Adress> comparator() {
		return adresses.comparator();
	}

	@Override
	public Adress first() {
		return adresses.first();
	}

	@Override
	public SortedSet<Adress> headSet(Adress arg0) {
		return adresses.headSet(arg0);
	}

	@Override
	public Adress last() {
		return adresses.last();
	}

	@Override
	public SortedSet<Adress> subSet(Adress fromElement, Adress toElement) {
		return adresses.subSet(fromElement, toElement);
	}

	@Override
	public SortedSet<Adress> tailSet(Adress fromElement) {
		return adresses.tailSet(fromElement);
	}

}
