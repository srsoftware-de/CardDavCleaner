import java.awt.Color;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Birthday {

	private String year;
	private String month;
	private String day;
	private String hour;
	private String minute;
	private String second;
	private boolean invalid=false;
	
	public HorizontalPanel editForm() {
		HorizontalPanel form=new HorizontalPanel("Birthday");
		if (invalid) form.setBackground(Color.red);
		form.add(new InputField("Year of birth",year));
		form.add(new InputField("Month of birth",month));
		form.add(new InputField("Day of birth",day));
		form.add(new InputField("Hour",hour));
		form.add(new InputField("Minute",minute));
		form.add(new InputField("Second",second));
		form.scale();
		return form;
	}
	
	public boolean equals(Birthday b2){
		return (year.equals(b2.year) && month.equals(b2.month) && day.equals(b2.day));
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("BDAY");
		if ((hour==null || hour.isEmpty()) && (minute==null||minute.isEmpty()) && (second==null||second.isEmpty())){
			// no time given
		} else {
			// time given
			sb.append(";VALUE=DATE-TIME");
		}
		sb.append(':');
		if (year==null || year.isEmpty()){
			// no year given
			if (day!=null && !day.isEmpty()){
				if (month==null || month.isEmpty()){
					sb.append("-");
				} else {
					sb.append(month);
				}
				sb.append(day);
			}
		} else {
			// year given
			sb.append(year);
			if (month!=null && !month.isEmpty()){
				if (day==null||day.isEmpty()){
					sb.append("-");
					sb.append(month);
				} else {
					sb.append(month);
					sb.append(day);
				}
			}
		}
		if ((hour==null || hour.isEmpty()) && (minute==null||minute.isEmpty()) && (second==null||second.isEmpty())){
			// no time given
		} else {
			sb.append("T");
			if (hour==null||hour.isEmpty()){
				sb.append("-");
			} else {
				sb.append(hour);
			}
			if (minute==null||minute.isEmpty()){
				sb.append("-");
			} else {
				sb.append(minute);
			}
			if (second!=null && !second.isEmpty()){
				sb.append(second);
			}
		}
		return sb.toString();
	}

	public Birthday(String birthday) throws InvalidFormatException {
		String bday=birthday;
		if (bday.startsWith(";VALUE=DATE-TIME")) {
			bday = bday.substring(16);
		}
		if (!bday.startsWith(":")) {
			throw new InvalidFormatException("BDAY format invalid: BDAY"+birthday);
		}
		bday=bday.substring(1); // remove ':'

		if (!bday.startsWith("T")){
			// date given
			if (!bday.startsWith("--")){
				// year given
				year=bday.substring(0, 4);
				bday=bday.substring(4);
				
				if (bday.startsWith("-")){
					bday=bday.substring(1);
				}				
			} else {
				bday=bday.substring(2);
			}
			if (!bday.isEmpty()){
				if (!bday.startsWith("-")){
					month=bday.substring(0,2);
					bday=bday.substring(2);
				} else {
					bday=bday.substring(1);
				}
			}
			if (!bday.isEmpty()){
				day=bday.substring(0,2);
				bday=bday.substring(2);
			}
		}
		if (!bday.isEmpty()){
			if (!bday.startsWith("T")){
				throw new InvalidFormatException("BDAY format invalid: BDAY"+birthday);
			} else {
				bday=bday.substring(1);
				if (!bday.startsWith("-")){
					hour=bday.substring(0,2);
					bday=bday.substring(2);
				} else {
					bday=bday.substring(1);
				}
				if (!bday.isEmpty()){
					if (!bday.startsWith("-")){
						minute=bday.substring(0,2);
						bday=bday.substring(2);
					} else {
						bday=bday.substring(1);
					}
				}
				if (!bday.isEmpty()){
					second=bday;
				}
			}
		}
	}

	public boolean isInvalid() {
		return invalid;
	}
}
