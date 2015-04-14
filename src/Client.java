import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;


public class Client {
	TreeMap<Adress.Category, Integer> addressLimit;
	TreeMap<Email.Category, Integer> mailLimit;
	TreeMap<Phone.Category, Integer> phonesLimit;
	TreeMap<Messenger.Category, Integer> messengerLimit;
	TreeMap<Url.Category, Integer> urlLimit;
	int maxOrgs;
	int maxNicknames;
	int maxLabels;
	public String name;
	


	public Client(String name,
								TreeMap<Adress.Category,Integer> addresses,
								TreeMap<Phone.Category,Integer> phones,
								TreeMap<Email.Category,Integer> mails,
								TreeMap<Url.Category,Integer> urls,
								TreeMap<Messenger.Category,Integer> messengers,
								Integer orgs,
								Integer nicknames,
								Integer categories,
								Integer labels) {
		this.name=name;
		addressLimit=addresses;
		phonesLimit=phones;
		mailLimit=mails;
		urlLimit=urls;
		messengerLimit=messengers;
		maxOrgs=(orgs==null)?0:orgs;
		maxNicknames=(nicknames==null)?0:nicknames;
		maxLabels=(labels==null)?0:labels;
	}
	

	public ProblemSet problemsWith(Contact c) {
		ProblemSet result = new ProblemSet();
		result.addAll(mailProblemsWith(c));
		result.addAll(phoneProblemsWith(c));
		result.addAll(addressProblemsWith(c));
		result.addAll(messengerProblemsWith(c));
		result.addAll(urlProblemsWith(c));
		if (c.orgCount()>maxOrgs){
			result.add(new Problem(Problem.Type.ORGS,_("# can manage only have # organizations.", new Object[] { name,maxOrgs } )));
		}
		if (c.nicknameCount()>maxNicknames){
			result.add(new Problem(Problem.Type.NICKNAMES,_("# can manage only have # nicknames.", new Object[] { name,maxNicknames } )));
		}
		if (c.labelCount()>maxLabels){
			result.add(new Problem(Problem.Type.LABELS,_("# can manage only # labels.", new Object[] { name,maxLabels } )));
		}
		return result;
	}

	private Collection<Problem> urlProblemsWith(Contact c) {
	  TreeSet<Problem> result = new TreeSet<Problem>();
	  TreeMap<Url.Category, Integer> counter = new TreeMap<Url.Category, Integer>(urlLimit);
	  for (Url url:c.urls()){
	  	for (Url.Category cat:url.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(new Problem(Problem.Type.URLS,_("# can manage only # urls with category \"#\"", new Object[] { name,urlLimit.get(cat),cat} )));
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private Collection<Problem> messengerProblemsWith(Contact c) {
	  TreeSet<Problem> result = new TreeSet<Problem>();
	  TreeMap<Messenger.Category, Integer> counter = new TreeMap<Messenger.Category, Integer>(messengerLimit);
	  for (Messenger mess:c.messengers()){
	  	for (Messenger.Category cat:mess.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(new Problem(Problem.Type.MESSENGER,_("# can manage only # messengers with category \"#\"", new Object[] { name,messengerLimit.get(cat),cat} )));
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private Collection<Problem> addressProblemsWith(Contact c) {
	  TreeSet<Problem> result = new TreeSet<Problem>();
	  TreeMap<Adress.Category, Integer> counter = new TreeMap<Adress.Category, Integer>(addressLimit);
	  for (Adress address:c.addresses()){
	  	for (Adress.Category cat:address.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(new Problem(Problem.Type.ADDRESSES,_("# can manage only # addresses with category \"#\"", new Object[] { name,addressLimit.get(cat),cat} )));
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private Collection<Problem> phoneProblemsWith(Contact c) {
	  TreeSet<Problem> result = new TreeSet<Problem>();
	  TreeMap<Phone.Category, Integer> counter = new TreeMap<Phone.Category, Integer>(phonesLimit);
	  for (Phone phone:c.phones()){
	  	for (Phone.Category cat:phone.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(new Problem(Problem.Type.PHONE,_("# can manage only # phone numbers with category \"#\"", new Object[] { name,phonesLimit.get(cat),cat} )));
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private TreeSet<Problem> mailProblemsWith(Contact c) {
	  TreeSet<Problem> result = new TreeSet<Problem>();
	  TreeMap<Email.Category, Integer> counter = new TreeMap<Email.Category, Integer>(mailLimit);
	  for (Email mail:c.mails()){
	  	for (Email.Category cat:mail.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(new Problem(Problem.Type.EMAIL,_("# can manage only # email adresses with category \"#\"", new Object[] { name,mailLimit.get(cat),cat} )));
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }
	
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
}
