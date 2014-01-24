
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
		System.out.print("Test.reverse...");
		String dummy="Test:;;;;abcd;;;";
		dummy=reversed(dummy);
		if (dummy.equals("Test:;;;;dcba")){
			System.out.println("ok");
		} else {
			System.err.println("failed: "+dummy);
		}
	}
}
