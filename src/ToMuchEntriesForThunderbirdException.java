
public class ToMuchEntriesForThunderbirdException extends Exception {

  private static final long serialVersionUID = -8331710867790660847L;

	public ToMuchEntriesForThunderbirdException(Object o) {
		super(o.toString());
	}
}
