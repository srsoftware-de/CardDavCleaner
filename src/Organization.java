import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Organization extends Mergable<Organization> implements ChangeListener, Comparable<Organization> {
	public static void test() {
		try {
			System.out.print("Organization creation (null)...");
			try {
				Organization nullO = new Organization(null);
				System.err.println("failed: " + nullO);
				System.exit(-1);
			} catch (InvalidFormatException ife) {				
				System.out.println("ok");
			}
			
			System.out.print("Organization creation (empty)...");
			String testcase="ORG:;;";
			Organization emptyA = new Organization(testcase.replace(";",""));
			Organization emptyB = new Organization(testcase);
			if (emptyA.toString().equals(testcase) && emptyB.toString().equals(testcase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + emptyA);
				System.exit(-1);
			}

			System.out.print("Organization creation (name)...");
			testcase="ORG:eman;;";
			Organization namA = new Organization(Tests.reversed(testcase));
			Organization namB = new Organization(testcase);
			if (namA.toString().equals(Tests.reversed(testcase)+";;")&&namB.toString().equals(testcase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + namA+"/"+namB);
				System.exit(-1);
			}

			System.out.print("Organization creation (extended)...");
			testcase="ORG:;dednetxe;";
			Organization extA = new Organization(Tests.reversed(testcase));
			Organization extB = new Organization(testcase);
			if (extA.toString().equals(Tests.reversed(testcase)+";")&&extB.toString().equals(testcase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + extA+"/"+extB);
				System.exit(-1);
			}

			System.out.print("Organization creation (complete)...");
			testcase="ORG:eman;dednetxe;";
			Organization fullA = new Organization(Tests.reversed(testcase));
			Organization fullB = new Organization(testcase);
			if (fullA.toString().equals(Tests.reversed(testcase)+";")&&fullB.toString().equals(testcase)) {
				System.out.println("ok");
			} else {
				System.err.println("failed: " + fullA+"/"+fullB);
				System.exit(-1);
			}

			Organization [] orgs1={emptyA,namA,extA,fullA};
			Organization [] orgs2={emptyB,namB,extB,fullB};
			
			System.out.print("Organization isEmpty test...");
			int comp=0;
			int num=0;
			for (Organization a:orgs1){
				comp++;
				if (!a.isEmpty()){
					num++;
				} else if (a==emptyA) {
					num++;
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {
				System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Organization compare test...");
			comp=0;
			num=0;
			for (Organization a:orgs1){
				num++;
				if (a.compareTo(fullB)!=0 && a.compareTo(fullB)==-fullB.compareTo(a)){
					comp++;
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {
				System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
				
			System.out.print("Organization compatibility test 1 (self)...");
			comp=0;
			num=0;
			for (Organization a:orgs1){
				for (Organization b:orgs1){
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						System.err.println(a+" <=> "+b);
					}
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}

		
			System.out.print("Organization compatibility test 2 (other)...");
			comp=0;
			num=0;
			for (Organization a:orgs1){
				for (Organization b:orgs2){
					num++;
					if (a.isCompatibleWith(b)) {
						comp++;
					} else {
						String concat=(a+""+b).replace("ORG:","").replace(";", "");
						if (concat.equals("nameeman") ||
							  concat.equals("nameemandednetxe") ||
							  concat.equals("extendeddednetxe") ||
							  concat.equals("extendedemandednetxe") ||
							  concat.equals("nameextendedeman") ||
							  concat.equals("nameextendeddednetxe") ||
							  concat.equals("nameextendedemandednetxe")){
							comp++;
						} else {
							System.err.println(a+" <=> "+b);
						}
					}
				}
				
			}
			if (comp==num){
				System.out.println("ok");
			} else {
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Organization clone test...");
			comp=0;
			num=0;
			for (Organization a:orgs1){
				num++;
				try {
					if (a.toString().equals(a.clone().toString())){
						comp++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			for (Organization b:orgs2){
				num++;
				try {
					if (b.toString().equals(b.clone().toString())){
						comp++;
					}
				} catch (CloneNotSupportedException e) {
				}
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			
			System.out.print("Organization merge test 1 (compatible)...");
			comp=0;
			num=0;
			for (Organization a:orgs1){
				try {
					comp+=2;
					Organization clone1=(Organization) a.clone();
					Organization clone2=(Organization) fullA.clone();
					
					if (clone1.mergeWith(fullA) && clone1.toString().equals(fullA.toString())) num++;
					if (clone2.mergeWith(a) && clone2.toString().equals(fullA.toString())) num++;
				
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			System.out.print("Organization merge test 2 (incompatible)...");
			comp=0;
			num=0;
			for (Organization b:orgs2){
				try {
					comp+=2;
					Organization clone1=(Organization) b.clone();
					Organization clone2=(Organization) fullA.clone();
					
					if (!clone1.mergeWith(fullA)) {
						num++;
					} else if (b==emptyB){
						num++;
					}
					if (!clone2.mergeWith(b)) {
						num++;
					} else if (b==emptyB){
						num++;
					}
				
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}				
			}
			if (comp==num){
				System.out.println("ok");
			} else {				
								System.err.println(num+"/"+comp+" => failed");
				System.exit(-1);
			}
			
			// continue tests here
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}	
	String name=null;
	String extended=null;
	private InputField nameField;
	private InputField extField;
	private VerticalPanel form;
	
	public Organization(String content) throws UnknownObjectException, InvalidFormatException {		
		if (content==null || !content.startsWith("ORG:")) throw new InvalidFormatException("Organization does not start with \"ORG:\"");
		String line=content.substring(4);
		if (line.contains(";")){
			String[] parts = line.split(";",0);
			if (parts.length>0 && !parts[0].isEmpty()) name=parts[0];
			if (parts.length>1 && !parts[1].isEmpty()) extended=parts[1];
		} else name=line; 
		
	}
	public int compareTo(Organization otherOrg) {
		return this.toString().compareTo(otherOrg.toString());
	}

	
	public VerticalPanel editForm() {
		form=new VerticalPanel("Organization");
		form.add(nameField=new InputField("Name",name));
		nameField.addEditListener(this);
		form.add(extField=new InputField("Extended",extended));
		extField.addEditListener(this);
		form.scale();
		return form;
	}
	@Override
  public boolean isCompatibleWith(Organization other) {
		if (different(name,other.name)) return false;
		if (different(extended, other.extended)) return false;
		return true;
  }
	
	public boolean isEmpty() {
		return (name==null || name.isEmpty()) && (extended==null || extended.isEmpty());
	}

	@Override
  public boolean mergeWith(Organization other) {
		if (!isCompatibleWith(other))return false;
		name=merge(name,other.name);
		extended=merge(extended, other.extended);
	  return true;
  }

	public void stateChanged(ChangeEvent arg0) {
		Object source = arg0.getSource();
		if (source==nameField){
			name=nameField.getText().trim();
		}
		if (source==extField){
			extended=extField.getText().trim();
		}
		if (isEmpty()) {
			form.setBackground(Color.yellow);
		} else {
			form.setBackground(Color.green);
		}	
	}
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("ORG:");
		if (name!=null) sb.append(name);
		sb.append(';');
		if (extended!=null)sb.append(extended);
		sb.append(';');
		return sb.toString();
	}

	protected Object clone() throws CloneNotSupportedException {		
		try {
			return new Organization(this.toString());
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
}
