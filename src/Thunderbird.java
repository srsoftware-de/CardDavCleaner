import java.util.TreeMap;


public class Thunderbird extends Client {
	
	private static TreeMap<String,Integer> home_work(){
		TreeMap<String, Integer> result=new TreeMap<String, Integer>(ObjectComparator.get());
		result.put("home", 1);
		result.put("work", 1);
		return result;
	}

	private static TreeMap<String,Integer> home_work_cell_fax_pager(){
		TreeMap<String, Integer> result=new TreeMap<String, Integer>(ObjectComparator.get());
		result.put("home", 1);
		result.put("work", 1);
		result.put("fax", 1);
		result.put("cell", 1);
		result.put("pager", 1);
		return result;
	}
	
	private static TreeMap<String, Integer> tb_messengers(){		
		TreeMap<String, Integer> result=new TreeMap<String, Integer>(ObjectComparator.get());
		result.put("X-AIM",1);
		return result;
	}

	public Thunderbird() {
		super("Thunderbird", home_work(), home_work_cell_fax_pager(), home_work(), home_work(), tb_messengers(), 1, 1, Integer.MAX_VALUE, 1);
	}
}
