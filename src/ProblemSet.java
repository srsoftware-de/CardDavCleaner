import java.util.TreeSet;


public class ProblemSet extends TreeSet<Problem> {

  private static final long serialVersionUID = -4760514549652427552L;

  public ProblemSet() {
	  super(ObjectComparator.get());
  }
  
	public boolean contains(Problem p) {
	  return super.contains(p);
	}
	
	public boolean contains(Problem.Type t) {
		for (Problem p:this){
			if (p.type() == t){
				return true;
			}
		}
		return false;
	}
}
