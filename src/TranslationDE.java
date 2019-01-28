import java.util.TreeMap;


public class TranslationDE extends Translation{
	TreeMap<String, String> getTranslations(){
		TreeMap<String,String> trans=new TreeMap<String, String>();
		trans.put("Address book may be located on a WebDAV-Server or in a local directory.", "Adressbuch kann auf einem WebDAV-Server oder in einem lokalen Ordner liegen.");
		trans.put("Address book settings", "Adressbuch-Einstellungen");
		trans.put("Backup settings", "Sicherungseinstellungen");
		trans.put("Fix field syntax, if broken", "Syntax von beschädigten Feldern korrigieren");
		trans.put("Location of addressbook:", "Pfad zum Adressbuch:");
		trans.put("Mozilla Thunderbird with default address book", "Mozilla Thunderbird mit Standard-Adressbuch");
		trans.put("No Backup defined.", "Keine Sicherung geplant.");
		trans.put("Optional settings", "Optionale Einstellungen");
		trans.put("Ready.", "Bereit.");
		trans.put("Remove empty contacts", "Leere Kontakte löschen");
		trans.put("Remove empty fields from contacts", "Leere Felder aus Kontakten löschen");
		trans.put("Select Backup Location", "Ort für Sicheurng auswählen");
		trans.put("Select directory", "Ordner wählen");
		trans.put("<html>Some programs cannot handle all fields defined by the vCard standard.<br>To apply workarounds, select programs you use from the follwing list:", "<html>Manche Programme unterstützen nicht alle Felder des vCard-Standards.<br>Zum Aktivieren von Anpassungen, markieren Sie von Ihnen genutzte Programme in der Liste:");
		trans.put("start", "starten");
		return trans;
	}
}
