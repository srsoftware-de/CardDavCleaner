import java.util.Locale;
import java.util.TreeMap;


public class Translations {
	
	private static TreeMap<String, String> trans=loadTranslations().getTranslations();
	
	public static String get(String key){		
		if (trans.containsKey(key)) return trans.get(key);
		return key;
	}

	private static Translation loadTranslations() {		
		String locale=Locale.getDefault().getLanguage().toUpperCase();
		System.out.print("Loading translation for "+locale+"...");
		Translation trans;
		try {
			trans = (Translation) Translation.class.getClassLoader().loadClass("Translation"+locale).newInstance();
			System.out.println("success.");
			return trans;
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
		}
		System.out.println("Failed. Using en.");
		return new Translation();
	}


}
