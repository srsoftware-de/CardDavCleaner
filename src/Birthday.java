import java.awt.Color;
import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Birthday implements ChangeListener, Comparable<Birthday> {

	private String year;
	private String month;
	private String day;
	private String hour;
	private String minute;
	private String second;
	private boolean invalid = false;
	private InputField yearField, monthField, dayField, hourField, minuteField, secondField;

	public JPanel editForm() {
		VerticalPanel form = new VerticalPanel("Birthday");
		form.add(dateForm());
		form.add(timeForm());
		form.scale();
		return form;
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

	public boolean equals(Birthday b2) {
		if (b2 == null) return false;
		return (year.equals(b2.year) && month.equals(b2.month) && day.equals(b2.day));
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
			if (day != null && !day.isEmpty()) {
				if (month == null || month.isEmpty()) {
					sb.append("-");
				} else {
					sb.append(month);
				}
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

	public Birthday(String birthday) throws InvalidFormatException {
		String bday = birthday;
		if (bday.startsWith(";VALUE=DATE-TIME")) {
			bday = bday.substring(16);
		}
		if (!bday.startsWith(":")) {
			throw new InvalidFormatException("BDAY format invalid: BDAY" + birthday);
		}
		bday = bday.substring(1); // remove ':'

		if (!bday.startsWith("T")) {
			// date given
			if (!bday.startsWith("--")) {
				// year given
				year = bday.substring(0, 4);
				bday = bday.substring(4);

				if (bday.startsWith("-")) {
					bday = bday.substring(1);
				}
			} else {
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

	public boolean isInvalid() {
		return invalid;
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

	public int compareTo(Birthday otherBday) {
		return this.toString().compareTo(otherBday.toString());
	}
}
