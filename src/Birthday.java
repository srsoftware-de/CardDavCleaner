import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Birthday {

	private String time;
	private String birtdate;
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("BDAY");
		if (time!=null) sb.append(";VALUE=DATE-TIME");
		sb.append(':');
		sb.append(birtdate);
		if (time!=null) sb.append(time);
		return sb.toString();
	}

	public Birthday(String bday) {
		if (bday.startsWith(";VALUE=DATE-TIME")) {
			bday = bday.substring(17);
		}
		if (!bday.startsWith(":")) {
			System.out.println(bday);
			throw new NotImplementedException();
		}
		bday=bday.substring(1);
		String year = bday.substring(0, 3);
		bday = bday.substring(4);
		if (bday.startsWith("-")) bday = bday.substring(1);
		String month = bday.substring(0, 1);
		bday = bday.substring(2);
		if (bday.startsWith("-")) bday = bday.substring(1);
		String day = bday.substring(0, 1);
		bday = bday.substring(2);

		birtdate = year + month + day;

		if (!bday.isEmpty()) {
			if (bday.startsWith("T")) {
				time = bday;
			} else {
				System.err.println(bday);
				throw new NotImplementedException();
			}
		}
	}

}
