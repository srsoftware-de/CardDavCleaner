import java.awt.Color;
import java.rmi.activation.UnknownObjectException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Organization extends Mergable<Organization> implements ChangeListener, Comparable<Organization> {
	public static void test() {
		try {
			System.out.print(_("Organization creation (null)..."));
			try {
				Organization nullO = new Organization(null);
				System.err.println(_("failed: #", nullO));
				System.exit(-1);
			} catch (InvalidFormatException ife) {				
				System.out.println(_("ok"));
			}
			
			System.out.print(_("Organization creation (empty)..."));
			String testcase="ORG:;";
			Organization emptyA = new Organization(testcase.replace(";",""));
			Organization emptyB = new Organization(testcase);
			if (emptyA.toString().equals(testcase) && emptyB.toString().equals(testcase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", emptyA));
				System.exit(-1);
			}

			System.out.print(_("Organization creation (name)..."));
			testcase="ORG:eman;";
			Organization namA = new Organization(Tests.reversed(testcase));
			Organization namB = new Organization(testcase);
			if (namA.toString().equals(Tests.reversed(testcase)+";")&&namB.toString().equals(testcase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", namA+"/"+namB));
				System.exit(-1);
			}

			System.out.print(_("Organization creation (extended)..."));
			testcase="ORG:;dednetxe";
			Organization extA = new Organization(Tests.reversed(testcase));
			Organization extB = new Organization(testcase);
			if (extA.toString().equals(Tests.reversed(testcase))&&extB.toString().equals(testcase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", extA+"/"+extB));
				System.exit(-1);
			}

			System.out.print(_("Organization creation (complete)..."));
			testcase="ORG:eman;dednetxe";
			Organization fullA = new Organization(Tests.reversed(testcase));
			Organization fullB = new Organization(testcase);
			if (fullA.toString().equals(Tests.reversed(testcase))&&fullB.toString().equals(testcase)) {
				System.out.println(_("ok"));
			} else {
				System.err.println(_("failed: #", fullA+"/"+fullB));
				System.exit(-1);
			}

			Organization [] orgs1={emptyA,namA,extA,fullA};
			Organization [] orgs2={emptyB,namB,extB,fullB};
			
			System.out.print(_("Organization isEmpty test..."));
			int comp=0;
			int num=0;
			for (Organization a:orgs1){
				comp++;
				if (!a.isEmpty()){
					num++;
				}
				if (a==emptyA) {
					comp--;
				}
			}
			if (comp==num){
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
			
			System.out.print(_("Organization compare test..."));
			comp=0;
			num=0;
			for (Organization a:orgs1){
				num++;
				if (a.compareTo(fullB)!=0 && a.compareTo(fullB)==-fullB.compareTo(a)){
					comp++;
				}
			}
			if (comp==num){
				System.out.println(_("ok"));
			} else {
				System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
				
			System.out.print(_("Organization compatibility test 1 (self)..."));
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
				System.out.println(_("ok"));
			} else {
								System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}

		
			System.out.print(_("Organization compatibility test 2 (other)..."));
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
				System.out.println(_("ok"));
			} else {
								System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
			
			System.out.print(_("Organization clone test..."));
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
				System.out.println(_("ok"));
			} else {				
								System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
			
			
			System.out.print(_("Organization merge test 1 (compatible)..."));
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
				System.out.println(_("ok"));
			} else {				
								System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
			
			System.out.print(_("Organization merge test 2 (incompatible)..."));
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
				System.out.println(_("ok"));
			} else {				
								System.err.println(_("#/# => failed",new Object[]{num,comp}));
				System.exit(-1);
			}
			
			// continue tests here
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}
	private static String _(String text) { 
		return Translations.get(text);
	}
	private static String _(String key, Object insert) {
		return Translations.get(key, insert);
	}	
	String name=null;
	String extended=null;
	private InputField nameField;
	private InputField extField;
	private VerticalPanel form;
	
	public Organization(String content) throws UnknownObjectException, InvalidFormatException {		
		if (content==null || !content.startsWith("ORG:")) throw new InvalidFormatException(_("Organization entry does not start with \"ORG:\": #",content));
		String line=content.substring(4);
		if (line.contains(";")){
			String[] parts = line.split(";",0);
			name=null;
			extended="";
			for (String part:parts){
				if (name==null){
					name=part;					
				} else {
					extended+=";"+part;
				}
			}
			if (name!=null && name.trim().isEmpty()) name=null;
			while (extended.startsWith(";")) extended=extended.substring(1);
			if (extended.isEmpty())extended=null;
		} else name=line; 
		
	}
	public int compareTo(Organization otherOrg) {
		return this.toString().compareTo(otherOrg.toString());
	}

	
	public VerticalPanel editForm() {
		form=new VerticalPanel(_("Organization"));
		form.add(nameField=new InputField(_("Name"),name));
		nameField.addEditListener(this);
		form.add(extField=new InputField(_("Extended"),extended));
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
