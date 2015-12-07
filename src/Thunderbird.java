import java.util.TreeMap;


public class Thunderbird extends Client {
	
	public Thunderbird() {
		super("Thunderbird", addressLimit(), phonesLimit(), mailsLimit(), urlsLimit(), messengersLimit(), orgsLimit(), nicknamesLimit(), categoriesLimit(), labelsLimit());
	}
	
	private static Integer orgsLimit() {
	  return 1;
  }

	private static Integer nicknamesLimit() {
	  return 1;
  }

	private static Integer categoriesLimit() {
	  return 100;
  }

	private static Integer labelsLimit() {
	  return 0;
  }

	private static TreeMap<Url.Category, Integer> urlsLimit() {
	  TreeMap<Url.Category, Integer> result=new TreeMap<Url.Category, Integer>();
	  result.put(Url.Category.HOME, 1);
	  result.put(Url.Category.WORK, 1);
	  return result;
  }

	private static TreeMap<Messenger.Category, Integer> messengersLimit() {
	  TreeMap<Messenger.Category, Integer> result=new TreeMap<Messenger.Category, Integer>();
	  result.put(Messenger.Category.AIM, 1);
	  result.put(Messenger.Category.FACEBOOK, 1);
	  result.put(Messenger.Category.ICQ, 1);
	  result.put(Messenger.Category.MSN, 1);
	  result.put(Messenger.Category.SIP, 1);
	  result.put(Messenger.Category.SKYPE, 1);
	  return result;
  }

	private static TreeMap<Email.Category, Integer> mailsLimit() {
	  TreeMap<Email.Category, Integer> result=new TreeMap<Email.Category, Integer>();
	  result.put(Email.Category.HOME, 1);
	  result.put(Email.Category.WORK, 1);
		return result;
  }

	private static TreeMap<Phone.Category, Integer> phonesLimit() {
		TreeMap<Phone.Category, Integer> result=new TreeMap<Phone.Category, Integer>();
		result.put(Phone.Category.HOME,1);
		result.put(Phone.Category.WORK,1);
		result.put(Phone.Category.CELL,1);
		result.put(Phone.Category.FAX,1);
		result.put(Phone.Category.PAGER,1);
		result.put(Phone.Category.PREF, 0);
	  return result;
  }

	private static TreeMap<Adress.Category, Integer> addressLimit() {
	  TreeMap<Adress.Category, Integer> result=new TreeMap<Adress.Category, Integer>();
	  result.put(Adress.Category.HOME, 1);
	  result.put(Adress.Category.WORK, 1);
		return result;
  }
}
