import java.awt.Color;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Birthday {

	private String time;
	private String birtdate;
	private boolean invalid=false;
	
	public HorizontalPanel editForm() {
		HorizontalPanel form=new HorizontalPanel("Birthday");
		if (invalid) form.setBackground(Color.red);
		form.add(new InputField("Time",time));
		form.add(new InputField("Birthdate",birtdate));
		form.scale();
		return form;
	}
	
	public boolean equals(Birthday b2){
		return birtdate.equals(b2.birtdate);
	}
	
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
			bday = bday.substring(16);
		}
		if (!bday.startsWith(":")) {
			System.out.println(bday);
			throw new NotImplementedException();
		}
		bday=bday.substring(1);
		String year = bday.substring(0, 4);
		bday = bday.substring(4);
		if (bday.startsWith("-")) bday = bday.substring(1);
		String month = bday.substring(0, 2);
		bday = bday.substring(2);
		if (bday.startsWith("-")) bday = bday.substring(1);
		String day = bday.substring(0, 2);
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

	public boolean isInvalid() {
		return invalid;
	}
}
