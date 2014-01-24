import java.awt.Color;
import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Birthday extends Mergable<Birthday>implements ChangeListener, Comparable<Birthday> {

	public static void test() {
		try {
			System.out.print("Birthday creation test (null)...");
			String testCase=null;
			try{ 
				Birthday nb = new Birthday(testCase);
				System.err.println("failed: "+nb);
				System.exit(-1);
			} catch (InvalidFormatException ife){
				System.out.println("ok");				
			}			

			System.out.print("Birthday creation test (empty)...");
			testCase="BDAY:";
			Birthday eb = new Birthday(testCase);
			if (eb.toString().equals(testCase)){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+eb);
				System.exit(-1);
			}			
				
			System.out.print("Birthday creation test (valid year)...");
			testCase="BDAY:1234";
			Birthday vyb = new Birthday(testCase);
			if (vyb.toString().equals(testCase) && !vyb.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+vyb);
				System.exit(-1);
			}	
			
			System.out.print("Birthday creation test (invalid year)...");
			testCase="BDAY:XXXX";
			Birthday iyb = new Birthday(testCase);
			if (iyb.toString().equals(testCase) && iyb.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+iyb);
				System.exit(-1);
			}	

			System.out.print("Birthday creation test (valid month)...");
			testCase="BDAY:--01";
			Birthday vmb = new Birthday(testCase);
			if (vmb.toString().equals(testCase) && !vmb.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+vmb);
				System.exit(-1);
			}			

			System.out.print("Birthday creation test (invalid month 1)...");
			testCase="BDAY:--13";
			Birthday imb1 = new Birthday(testCase);
			if (imb1.toString().equals(testCase) && imb1.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+imb1);
				System.exit(-1);
			}
			
			System.out.print("Birthday creation test (invalid month 2)...");
			testCase="BDAY:--XX";
			Birthday imb2 = new Birthday(testCase);
			if (imb2.toString().equals(testCase) && imb2.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+imb2);
				System.exit(-1);
			}
			
			System.out.print("Birthday creation test (valid day)...");
			testCase="BDAY:---01";
			Birthday vdb = new Birthday(testCase);
			if (vdb.toString().equals(testCase) && !vdb.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+vdb);
				System.exit(-1);
			}			

			System.out.print("Birthday creation test (invalid day 1)...");
			testCase="BDAY:---32";
			Birthday idb1 = new Birthday(testCase);
			if (idb1.toString().equals(testCase) && idb1.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+idb1);
				System.exit(-1);
			}
			
			System.out.print("Birthday creation test (invalid day 2)...");
			testCase="BDAY:---XX";
			Birthday idb2 = new Birthday(testCase);
			if (idb2.toString().equals(testCase) && idb2.isInvalid()){
				System.out.println("ok");
			} else {
				System.err.println("failed: "+idb2);
				System.exit(-1);
			}	

			// TODO:
			System.out.println("weiter mit birthday.test");
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private String year;
	private String month;
	private String day;
	private String hour;
	private String minute;
	private String second;
	private boolean invalid = false;
	
	private InputField yearField, monthField, dayField, hourField, minuteField, secondField;
	public Birthday(String birthday) throws InvalidFormatException {		
		String bday = birthday;
		if (bday==null || !bday.startsWith("BDAY")) {
			throw new InvalidFormatException("Birthday entry does not start with BDAY: \"" + birthday+"\"");
		}
		bday=bday.substring(4);
		if (bday.startsWith(";VALUE=DATE-TIME")) {
			bday = bday.substring(16);
		}
		if (!bday.startsWith(":")) {
			throw new InvalidFormatException("BDAY format invalid: " + birthday);
		}
		bday = bday.substring(1); // remove ':'
		if (bday.isEmpty()) return;
		if (!bday.startsWith("T")) {
			// date given
			if (!bday.startsWith("--")) {
				// year given
				year = bday.substring(0, 4);
				bday = bday.substring(4);

				if (bday.startsWith("-")) {
					bday = bday.substring(1);
				}
			} else { // bday starts with --
				bday = bday.substring(2);
			}
			if (!bday.isEmpty()) {
				if (!bday.startsWith("-")) {
					month = bday.substring(0, 2);
					bday = bday.substring(2);
					if (bday.startsWith("-")) {
						bday = bday.substring(1);
					}
				} else {
					bday = bday.substring(1);
				}
			}
			if (!bday.isEmpty()) {
				day = bday.substring(0, 2);
				bday = bday.substring(2);
			}
		}
		if (!bday.isEmpty()) {
			if (!bday.startsWith("T")) {
				throw new InvalidFormatException("BDAY format invalid: BDAY" + birthday);
			} else {
				bday = bday.substring(1);
				if (!bday.startsWith("-")) {
					hour = bday.substring(0, 2);
					bday = bday.substring(2);
				} else {
					bday = bday.substring(1);
				}
				if (!bday.isEmpty()) {
					if (!bday.startsWith("-")) {
						minute = bday.substring(0, 2);
						bday = bday.substring(2);
					} else {
						bday = bday.substring(1);
					}
				}
				if (!bday.isEmpty()) {
					second = bday;
				}
			}
		}
		checkInvalidity();
	}

	public void checkInvalidity() {
		invalid = false;
		if (year != null) {
			if (yearField != null) yearField.setBackground(Color.red);
			try {
				int y = Integer.parseInt(year);
				if (y <= Calendar.getInstance().get(Calendar.YEAR)) {
					if (y < 100) year = "19" + y;
					if (y < 10) year = "190" + y;
					if (yearField != null) yearField.setBackground(Color.green);
				} else {
					invalid = true;
				}
			} catch (NumberFormatException nfe) {
				invalid = true;
			}
		}
		if (yearField != null && (year == null || year.isEmpty())) yearField.setBackground(Color.yellow);

		if (month != null) {
			if (monthField != null) monthField.setBackground(Color.red);
			try {
				int m = Integer.parseInt(month);
				if (m < 13) {
					if (m < 10) month = "0" + m;
					if (monthField != null) monthField.setBackground(Color.green);
				} else {
					invalid = true;
				}
			} catch (NumberFormatException nfe) {
				invalid = true;
			}
		}
		if (monthField != null && (month == null || month.isEmpty())) monthField.setBackground(Color.yellow);

		if (day != null) {
			if (dayField != null) dayField.setBackground(Color.red);
			try {
				int d = Integer.parseInt(day);
				if (d < 32) {
					if (d < 10) day = "0" + d;
					if (dayField != null) dayField.setBackground(Color.green);
				} else {
					invalid = true;
				}
			} catch (NumberFormatException nfe) {
				invalid = true;
			}
		}
		if (dayField != null && (day == null || day.isEmpty())) dayField.setBackground(Color.yellow);

		if (hour != null) {
			if (hourField != null) hourField.setBackground(Color.red);
			try {
				int h = Integer.parseInt(hour);
				if (h < 24) {
					if (h < 10) hour = "0" + h;
					if (hourField != null) hourField.setBackground(Color.green);
				} else {
					invalid = true;
				}
			} catch (NumberFormatException nfe) {
				invalid = true;
			}
		}
		if (hourField != null && (hour == null || hour.isEmpty())) hourField.setBackground(Color.yellow);

		if (minute != null) {
			if (minuteField != null) minuteField.setBackground(Color.red);
			try {
				int m = Integer.parseInt(minute);
				if (m < 60) {
					if (m < 10) minute = "0" + m;
					if (minuteField != null) minuteField.setBackground(Color.green);
				} else {
					invalid = true;
				}
			} catch (NumberFormatException nfe) {
				invalid = true;
			}
		}
		if (minuteField != null && (minute == null || minute.isEmpty())) minuteField.setBackground(Color.yellow);

		if (second != null) {
			if (secondField != null) secondField.setBackground(Color.red);
			try {
				int m = Integer.parseInt(second);
				if (m < 60) {
					if (m < 10) second = "0" + m;
					if (secondField != null) secondField.setBackground(Color.green);
				} else {
					invalid = true;
				}
			} catch (NumberFormatException nfe) {
				invalid = true;
			}
		}
		if (secondField != null && (second == null || second.isEmpty())) secondField.setBackground(Color.yellow);
	}

	public int compareTo(Birthday otherBday) {
		return this.toString().compareTo(otherBday.toString());
	}

	public JPanel editForm() {
		VerticalPanel form = new VerticalPanel("Birthday");
		form.add(dateForm());
		form.add(timeForm());
		form.scale();
		return form;
	}

	public boolean equals(Birthday b2) {
		if (b2 == null) return false;
		return (year.equals(b2.year) && month.equals(b2.month) && day.equals(b2.day));
	}

	@Override
  public boolean isCompatibleWith(Birthday other) {
		if (different(year,other.year)) return false;
		if (different(month,other.month)) return false;
		if (different(day,other.day)) return false;
		if (different(hour, other.hour)) return false;
		if (different(minute, other.minute)) return false;
		if (different(second, other.second)) return false;
	  return true;
  }

	@Override
  public boolean isEmpty() {
		if (year!=null) return false;
		if (month!=null) return false;
		if (day!=null) return false;
		if (hour!=null) return false;
		if (minute!=null) return false;
		if (second!=null) return false;
	  return true;
  }

	public boolean isInvalid() {
		return invalid;
	}

	@Override
  public boolean mergeWith(Birthday other) {
		year=merge(year,other.year);
		year=merge(month,other.month);
		year=merge(day,other.day);
		year=merge(hour,other.hour);
		year=merge(minute,other.minute);
		year=merge(second,other.second);
		if (other.invalid) invalid=true;
	  return true;
  }

	public void stateChanged(ChangeEvent evt) {
		Object source = evt.getSource();
		if (source == yearField) {
			year = yearField.getText().trim();
			if (year.isEmpty()) year = null;
		}
		if (source == monthField) {
			month = monthField.getText().trim();
			if (month.isEmpty()) month = null;
		}
		if (source == dayField) {
			day = dayField.getText().trim();
			if (day.isEmpty()) day = null;
		}
		if (source == hourField) {
			hour = hourField.getText().trim();
			if (hour.isEmpty()) hour = null;
		}
		if (source == minuteField) {
			minute = minuteField.getText().trim();
			if (minute.isEmpty()) minute = null;
		}
		if (source == secondField) {
			second = secondField.getText().trim();
			if (second.isEmpty()) second = null;
		}
		checkInvalidity();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BDAY");
		if ((hour == null || hour.isEmpty()) && (minute == null || minute.isEmpty()) && (second == null || second.isEmpty())) {
			// no time given
		} else {
			// time given
			sb.append(";VALUE=DATE-TIME");
		}
		sb.append(':');
		if (year == null || year.isEmpty()) {
			// no year given
			if (month == null || month.isEmpty()) {
				if (day!=null && !day.isEmpty()){
					sb.append("---");
				}				
			} else {
				sb.append("--"+month);
			}

			if (day != null && !day.isEmpty()) {
				sb.append(day);
			}
		} else {
			// year given
			sb.append(year);
			if (month != null && !month.isEmpty()) {
				if (day == null || day.isEmpty()) {
					sb.append("-");
					sb.append(month);
				} else {
					sb.append(month);
					sb.append(day);
				}
			}
		}
		if ((hour == null || hour.isEmpty()) && (minute == null || minute.isEmpty()) && (second == null || second.isEmpty())) {
			// no time given
		} else {
			sb.append("T");
			if (hour == null || hour.isEmpty()) {
				sb.append("-");
			} else {
				sb.append(hour);
			}
			if (minute == null || minute.isEmpty()) {
				sb.append("-");
			} else {
				sb.append(minute);
			}
			if (second != null && !second.isEmpty()) {
				sb.append(second);
			}
		}
		return sb.toString();
	}

	private JComponent dateForm() {
		HorizontalPanel dateForm = new HorizontalPanel();
		if (invalid) dateForm.setBackground(Color.red);
		dateForm.add(yearField = new InputField("Year of birth", year));
		yearField.addEditListener(this);
		dateForm.add(monthField = new InputField("Month of birth", month));
		monthField.addEditListener(this);
		dateForm.add(dayField = new InputField("Day of birth", day));
		dayField.addEditListener(this);
		dateForm.scale();
		return dateForm;
	}

	private JComponent timeForm() {
		HorizontalPanel form = new HorizontalPanel();
		form.add(hourField = new InputField("Hour", hour));
		hourField.addEditListener(this);
		form.add(minuteField = new InputField("Minute", minute));
		minuteField.addEditListener(this);
		form.add(secondField = new InputField("Second", second));
		secondField.addEditListener(this);
		form.scale();
		return form;
	}

}
