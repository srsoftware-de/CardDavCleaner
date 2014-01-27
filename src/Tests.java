
public class Tests {
	public static String reversed(String inp){
		String[] outer = inp.split(":");
		String[] inner = outer[1].split(";");
		String result="";
		for (String in:inner){
			result=result+(new StringBuilder(in)).reverse()+";";
		}
		result=result.substring(0, result.length()-1);
		return outer[0]+":"+result;	
	}
	
	public static void test(){
		System.out.print(_("Test.reverse..."));
		String dummy="Test:;;;;abcd;;;";
		dummy=reversed(dummy);
		if (dummy.equals("Test:;;;;dcba")){
			System.out.println(_("ok"));
		} else {
			System.err.println(_("failed: #",dummy));
		}
	}
	
	private static String _(String text) { 
		return Translations.get(text);
	}	
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}
}
