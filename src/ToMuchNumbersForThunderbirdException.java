
public class ToMuchNumbersForThunderbirdException extends Exception {

	public ToMuchNumbersForThunderbirdException(Phone phone) {
		super(phone.toString());
	}
}
