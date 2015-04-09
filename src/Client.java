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
	
	public static enum ProblemType{
		EMAIL{
			@Override
			public String toString() {				
				return "Too many emails!";
			}
		},
		PHONE{
			@Override
			public String toString() {				
				return "Too many phones!";
			}
		}, 
		ORGS{
			@Override
			public String toString() {				
				return "Too many organizations!";
			}
		}, 
		NICKNAMES{
			@Override
			public String toString() {				
				return "Too many nicknames!";
			}
		}, 
		LABELS{
			@Override
			public String toString() {				
				return "Too many labels!";
			}
		}, 
		FAX{
			@Override
			public String toString() {				
				return "Fax";
			}
		}, 
		VOICE{
			@Override
			public String toString() {				
				return "Voice";
			}
		}, 
		PAGER{
			@Override
			public String toString() {				
				return "Pager";
			}
		}, 
		PREFERED{
			@Override
			public String toString() {				
				return "Preferred Phone";
			}
		};
		
		public abstract String toString();

	}

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
	

	public TreeSet<Client.ProblemType> problemsWith(Contact c) {
		TreeSet<ProblemType> result = new TreeSet<Client.ProblemType>();
		result.addAll(mailProblemsWith(c));
		result.addAll(phoneProblemsWith(c));
		result.addAll(addressProblemsWith(c));
		result.addAll(messengerProblemsWith(c));
		result.addAll(urlProblemsWith(c));
		if (c.orgCount()>maxOrgs){
			result.add(ProblemType.ORGS);
		}
		if (c.nicknameCount()>maxNicknames){
			result.add(ProblemType.NICKNAMES);
		}
		if (c.labelCount()>maxLabels){
			result.add(ProblemType.LABELS);
		}
		return result;
	}

	private Collection<? extends ProblemType> urlProblemsWith(Contact c) {
	  TreeSet<ProblemType> result = new TreeSet<Client.ProblemType>();
	  TreeMap<Url.Category, Integer> counter = new TreeMap<Url.Category, Integer>(urlLimit);
	  for (Url url:c.urls()){
	  	for (Url.Category cat:url.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(ProblemType.PHONE);
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private Collection<? extends ProblemType> messengerProblemsWith(Contact c) {
	  TreeSet<ProblemType> result = new TreeSet<Client.ProblemType>();
	  TreeMap<Messenger.Category, Integer> counter = new TreeMap<Messenger.Category, Integer>(messengerLimit);
	  for (Messenger mess:c.messengers()){
	  	for (Messenger.Category cat:mess.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(ProblemType.PHONE);
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private Collection<? extends ProblemType> addressProblemsWith(Contact c) {
	  TreeSet<ProblemType> result = new TreeSet<Client.ProblemType>();
	  TreeMap<Adress.Category, Integer> counter = new TreeMap<Adress.Category, Integer>(addressLimit);
	  for (Adress address:c.addresses()){
	  	for (Adress.Category cat:address.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(ProblemType.PHONE);
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private Collection<? extends ProblemType> phoneProblemsWith(Contact c) {
	  TreeSet<ProblemType> result = new TreeSet<Client.ProblemType>();
	  TreeMap<Phone.Category, Integer> counter = new TreeMap<Phone.Category, Integer>(phonesLimit);
	  for (Phone phone:c.phones()){
	  	for (Phone.Category cat:phone.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(ProblemType.PHONE);
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }


	private TreeSet<ProblemType> mailProblemsWith(Contact c) {
	  TreeSet<ProblemType> result = new TreeSet<Client.ProblemType>();
	  TreeMap<Email.Category, Integer> counter = new TreeMap<Email.Category, Integer>(mailLimit);
	  for (Email mail:c.mails()){
	  	for (Email.Category cat:mail.categories()){
	  		Integer count = counter.get(cat);
	  		if (count==null||count==null) {
	  			result.add(ProblemType.EMAIL);
	  		} else {
	  			counter.put(cat, count-1);
	  		}
	  	}
	  }
	  return result;
  }
}
