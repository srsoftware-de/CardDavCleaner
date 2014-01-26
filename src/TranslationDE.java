import java.util.TreeMap;


public class TranslationDE extends Translation{
	TreeMap<String, String> getTranslations(){
		TreeMap<String,String> trans=new TreeMap<String, String>();
		trans.put("<html>I use Thunderbird with this address book.<br>(This is important, as thunderbird only allows a limited number of phone numbers, email addresses, etc.)","<html>Ich benutze Thunderbird mit diesem Adressbuch.<br>(Das ist wichtig, da Thunderbird nur eine begrenzte Anzahl an Telefonnummern, Email-Adressen, etc. erlaubt.)");
		trans.put("Deleting #", "# wird gelöscht");
		trans.put("Password:","Passwort:");
		trans.put("Server responded with CODE #", "Server hat mit CODE # geantwortet");
		trans.put("Server settings", "Servereinstellungen");
		trans.put("Server + Path to addressbook:","Server und Pfad zum Adressbuch:");
		trans.put("start", "Start");
		trans.put("User:","Benutzer:");		
		return trans;
	}
}
