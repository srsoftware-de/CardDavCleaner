import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


public class MergableList<Type extends Mergable<Type>> implements SortedSet<Type> {

	public static void test() {
		class TestElement extends Mergable<TestElement> implements Comparable<TestElement>{
			private String content;
			public TestElement(String content) {
				this.content=content;
			}
			@Override
			public boolean isCompatibleWith(TestElement other) {
				if (different(content,other.content))	return false;
				return true;
			}

			@Override
			public boolean mergeWith(TestElement other) {
				if (this==other) return false;
				if (content==null){
					content=other.content;
					return false;
				} else if (different(content,other.content)){
					content+="#"+other.content;
					return true;
				} else {
					return true;
				}
			}

			@Override
			public boolean isEmpty() {
				return content==null||content.isEmpty();
			}
			
			public int compareTo(TestElement o) {
				if (content==null) return -1;
				if (o.content==null) return 1;
				return content.compareTo(o.content);
			}	
			
			public String toString() {
				String s=super.toString();				
				return content+s.substring(s.indexOf("@"));
			}
		}
	
		TestElement null1 = new TestElement(null);
		TestElement null2 = new TestElement(null);
		TestElement empty1 = new TestElement("");
		TestElement empty2=new TestElement("");
		TestElement string1=new TestElement("string");
		TestElement string2=new TestElement("string");
		TestElement other1=new TestElement("other1");
		TestElement other2=new TestElement("other2");
		
		System.out.print("MergableList creation...");
		MergableList<TestElement> testList=new MergableList<TestElement>();
		MergableList<TestElement> testList2 = new MergableList<TestElement>();
		if (testList.toString().equals("[]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed!");	
		}	
		
		System.out.print("MergableList.add null...");		
		if (!testList.add(null) && testList.toString().equals("[]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.add TestElement(null)...");		
		if (!testList.add(null1) && testList.toString().equals("[]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.add TestElement(empty)...");		
		if (!testList.add(empty1) && testList.toString().equals("[]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.add TestElement("+string1+")...");		
		if (testList.add(string1) && testList.toString().equals("["+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.add TestElement("+string1+") again...");		
		if (!testList.add(string1) && testList.toString().equals("["+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.add TestElement("+string2+")...");		
		if (testList.add(string2) && testList.toString().equals("["+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.add TestElement("+other1+")...");		
		if (testList.add(other1) && testList.toString().equals("["+other1+", "+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}

		System.out.print("MergableList.add TestElement("+other2+")...");		
		if (testList.add(other2) && testList.toString().equals("["+other1+", "+other2+", "+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.addAll null...");
		if (!testList2.addAll(null) && testList2.toString().equals("[]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList2);	
		}

		System.out.print("MergableList.addAll "+testList+"...");
		if (testList2.addAll(testList) && testList2.toString().equals(testList.toString())){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList2);	
		}		
		
		System.out.print("MergableList.remove null...");		
		if (!testList.remove(null) && testList.toString().equals("["+other1+", "+other2+", "+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.remove TestElement(null)...");		
		if (!testList.remove(null1) && testList.toString().equals("["+other1+", "+other2+", "+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.remove TestElement("+string2+")...");		
		if (testList.remove(string2) && testList.toString().equals("["+other1+", "+other2+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}

		System.out.print("MergableList.remove TestElement("+string1+")...");		
		if (!testList.remove(string1) && testList.toString().equals("["+other1+", "+other2+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.removeAll "+testList+"...");		
		if (testList2.removeAll(testList) && testList2.toString().equals("["+string1+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList2);	
		}		
		
		testList2.add(other1);
		System.out.print("MergableList.removeAll "+testList2+"...");		
		if (testList.removeAll(testList2) && testList.toString().equals("["+other2+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
		
		System.out.print("MergableList.size...");		
		if (testList.size()==1 && testList2.size()==2){
			System.out.println("ok.");
		} else {
			System.err.println("failed!");	
		}
		
		testList.set.add(null2);
		testList.set.add(empty2);
		System.out.print("MergableList.update...");
		testList.update();
		if (testList.toString().equals("["+other2+"]")){
			System.out.println("ok.");
		} else {
			System.err.println("failed: "+testList);	
		}
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
		if (entries==null) return false;
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
  	if (o==null) return false;
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
