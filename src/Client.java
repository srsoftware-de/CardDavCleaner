import java.util.TreeMap;
import java.util.TreeSet;


public class Client {
	TreeMap<String, Integer> maxAddresses, maxPhones, maxMails, maxUrls,maxMessengers;
	int maxOrgs;
	int maxNicknames;
	int maxLabels;
	public String name;

	public Client(String name,
								TreeMap<String,Integer> addresses,
								TreeMap<String,Integer> phones,
								TreeMap<String,Integer> mails,
								TreeMap<String,Integer> urls,
								TreeMap<String,Integer> messengers,
								Integer orgs,
								Integer nicknames,
								Integer categories,
								Integer labels) {
		this.name=name;
		maxAddresses=addresses;
		maxPhones=phones;
		maxMails=mails;
		maxUrls=urls;
		maxOrgs=(orgs==null)?0:orgs;
		maxMessengers=messengers;
		maxNicknames=nicknames;
		maxLabels=labels;
	}
	
	public TreeSet<Problem.Type> problemsWith(Contact c){
		return new TreeSet<Problem.Type>();
	}
}
