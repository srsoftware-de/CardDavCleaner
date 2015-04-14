


public class Problem {
	public static enum Type{
		EMAIL, PHONE, ORGS, NICKNAMES, LABELS, FAX, VOICE, PAGER, PREFERED, ADDRESSES,MESSENGER,URLS, FORMATTEDNAME, BIRTHDAY, ANNIVERSARY;
	}

	private Type type;
	private String txt;
	
	public Problem(Type type,String txt) {
		this.type=type;
		this.txt=txt;
  }
	
	public Type type(){
		return type;
	}
	
	@Override
	public String toString() {
		String result="Problem with #:";
		switch (type) {
		case EMAIL:
			result=_(result,"EMAIL");
			break;
		case PHONE:
			result=_(result,"PHONE");
			break;
		case ORGS:
			result=_(result,"ORGS");
			break;
		case NICKNAMES:
			result=_(result,"NICKNAMES");
			break;
		case LABELS:
			result=_(result,"LABELS");
			break;
		case FAX:
			result=_(result,"FAX");
			break;
		case VOICE:
			result=_(result,"VOICE");
			break;
		case PAGER:
			result=_(result,"PAGER");
			break;
		case PREFERED:
			result=_(result,"PREFERED");
			break;

		default:
			result=_("Uncategorized problem:");
			break;
		}
	  return result+txt;
	}
	
	private static String _(Object text) {
		return Translations.get(text.toString());
	}
	
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
}
