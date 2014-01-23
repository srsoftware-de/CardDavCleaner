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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> obj) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<? super Adress> comparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Adress first() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Adress> headSet(Adress arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Adress last() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Adress> subSet(Adress arg0, Adress arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Adress> tailSet(Adress arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
