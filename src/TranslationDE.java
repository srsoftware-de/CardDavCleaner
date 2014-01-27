import java.util.TreeMap;


public class TranslationDE extends Translation{
	TreeMap<String, String> getTranslations(){
		TreeMap<String,String> trans=new TreeMap<String, String>();
		trans.put("Abort program", "Programmausführung abbrechen");
		trans.put("Adress", "Adresse");
		trans.put("City", "Ort");
		trans.put("Country", "Land");
		trans.put("Deleting #", "# wird gelöscht");
		trans.put("e-mail", "E-Mail");
		trans.put("Edit manually", "bearbeiten");
		trans.put("Extended Adress", "Adresszusatz");
		trans.put("found # @# in #", "# @# in # gefunden");
		trans.put("# has an invalid format", "Das Format von # ist ungültig");
		trans.put("Home Adress", "Adresse Zuhause");
		trans.put("Invalid Contact", "ungültiger/beschädigter Kontakt");
		trans.put("<html>I use Thunderbird with this address book.<br>(This is important, as thunderbird only allows a limited number of phone numbers, email addresses, etc.)","<html>Ich benutze Thunderbird mit diesem Adressbuch.<br>(Das ist wichtig, da Thunderbird nur eine begrenzte Anzahl an Telefonnummern, Email-Adressen, etc. erlaubt.)");
		trans.put("<html>Merging and cleaning aborted! Goodbye!","<html>Zusammenführen und Bereinigen der Kontakte abgebrochen! Auf Wiedersehen!");
		trans.put("messenger", "Chat-/Mitteilungsprogramm");
		trans.put("name", "Name");
		trans.put("...not successful (# / #). Trying to remove first...","...nicht erfolgreich (# / #). Versuche erst zu löschen...");
		trans.put("<html>No data has been modified on the server <b>until now</b>. Continue?", "<html><b>Bis jetzt</b> wurden keine Daten auf dem Server verändert. Fortfahren?");
		trans.put("<html>Nothing to do. Your adress book is either empty or well sorted!!", "<html>Keine anstehenden Aufgaben. Ihr Adressbuch ist entweder leer, oder gut sortiert!!");
		trans.put("Password:","Passwort:");
		trans.put("Please confirm", "Bitte bestätigen");
		trans.put("Please decide!","Bitte entscheiden!");
		trans.put("phone number","Telefonnummer");
		trans.put("Post Office Box", "Postfach");
		trans.put("reading contact #/#: #", "lese Kontakt #/#: #");
		trans.put("Region", "Region/Bundesland");
		trans.put("<html>Scanning, merging and cleaning <i>successfully</i> done! Goodbye!", "<html>Prüfen, Zusammenführen und Bereinigen <i>erfolgreich</i> durchgeführt! Auf Wiedersehen!");
		trans.put("Server responded with CODE #", "Server hat mit CODE # geantwortet");
		trans.put("Server settings", "Servereinstellungen");
		trans.put("Server + Path to addressbook:","Server und Pfad zum Adressbuch:");
		trans.put("<html><br>Shall those contacts be <i>merged</i>?","<html><br>Sollen diese beiden Kontakte <i>zusammengeführt</i> werden?");
		trans.put("Skip", "Überspringen");
		trans.put("start", "Start");
		trans.put("Street Adress", "Straße");
		trans.put("<html>Sorry! Unfortunateley, i was not able to write a file to the WebDAV server.<br>But don't worry, i created a <b>Backup</b> of the file at #", "<html>Verzeihung! Leider kontte ich eine Datei nicht zum WebDAV server senden.<br>Aber keine Angst, ich habe ein <b>Backup</b> der Datei unter # gespeichert!");
		trans.put("...success!", "...erfolgreich!");
		trans.put("<html>The # \"<b>#</b>\" is used by both following contacts:","<html># # wird von beiden folgenden Kontakten verwendet");
		trans.put("<html>The following contacts will be <b>deleted</b>:", "<html>Die folgenden Kontakte werden <b>gelöscht</b>:");
		trans.put("<html>The following <b>merged contacts</b> will be written to the server:", "<html>Die folgenden <b>zusammengeführten Kontakte</b> werden zum server übertragen:");
		trans.put("Uploading #", "Lade # hoch");
		trans.put("User:","Benutzer:");
		trans.put("Warning: skipping empty contact # (Contains nothing but a name)", "Warnung: überspringe leeren Kontakt # (Enthält außer dem Namen keine Daten)");		
		trans.put("<html><font color=\"red\">Warning! Those contacts contain unequal birth dates!","<html><font color=\"red\">Achtung! Diese kontakte enthalten unterschiedliche Geburtstags-Einträge!");
		trans.put("<html>#<br>Will abort operation now.", "<html>#<br>Aktion wird nun abgebrochen.");
		trans.put("Work Adress", "Adresse Arbeitsplatz");
		trans.put("Zip", "Postleitzahl");
		return trans;
	}
}
