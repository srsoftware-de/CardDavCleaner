import java.util.TreeMap;


public class TranslationDE extends Translation{
	TreeMap<String, String> getTranslations(){
		TreeMap<String,String> trans=new TreeMap<String, String>();
		trans.put("Password:","Passwort:");
		trans.put("Server settings", "Servereinstellungen");
		trans.put("Server + Path to addressbook:","Server und Pfad zum Adressbuch:");
		trans.put("User:","Benutzer:");		
		return trans;
	}
}
