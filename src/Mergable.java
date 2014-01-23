
public interface Mergable<T> {
	public boolean isCompatibleWith(T other);
	
	public boolean mergeWith(T other);
}
