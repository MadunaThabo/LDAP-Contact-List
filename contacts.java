// by Maduna Thabo and Simphiwe Ndlovu
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.NamingException;
import java.util.Scanner;




public class contacts{
    public InitialDirContext connection;

    public void newConnection(){
        Properties property = new Properties();
        property.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        property.put(Context.PROVIDER_URL,"ldap://contacts.localhost:389");
        property.put(Context.SECURITY_PRINCIPAL,"cn=admin,dc=contacts,dc=local");
        property.put(Context.SECURITY_CREDENTIALS,"password");
        // env.put(Context.SECURITY_AUTHENTICATION,"password");

         
          
        try{
            connection=new InitialDirContext(property);
            System.out.println("GOT connection "+ connection);
              // Create a subcontext.
            // Context childCtx = connection.createSubcontext("child");
        }
        catch(AuthenticationException error)
        {
            System.out.println(error.getMessage());
        }
        catch(NamingException ne)
        {
            ne.printStackTrace();
        }
    }

    public void deleteContactName(String nameC) throws NamingException{
        String category = "cn="+nameC+",ou=Friends,dc=contacts,dc=local";
        try{
            connection.destroySubcontext(category);
        }
        catch(NamingException x){
            System.out.println("Contact was not found");
        }
    }

    public void getContactsByName(String nameUser) throws NamingException{
        String category = "cn="+nameUser+",ou=Friends,dc=contacts,dc=local";
        String searchFilter = "(objectClass=inetOrgPerson)";
        String[] requestCategory = {"cn","sn","mobile"};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(requestCategory);
        try{
            NamingEnumeration users = connection.search(category, searchFilter, controls);
            SearchResult result = null;
            result = (SearchResult) users.next();
            Attributes attr = result.getAttributes();
            System.out.println(attr.get("cn")  +" "+ attr.get("sn") +" "+ attr.get("mobile")+"\n");
        }
        catch(NamingException x)
        {
            // x.printStackTrace();
            System.out.println("Contact not found");
        }

        
    }

    public void addContact(String name,String surname, String contactNum) {
		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		attribute.add("inetOrgPerson");

		attributes.put(attribute);
		// user details
        attributes.put("cn", name);
		attributes.put("sn", surname);
		attributes.put("mobile", contactNum);
        
		try {
			connection.createSubcontext("cn="+name+",ou=Friends,dc=contacts,dc=local", attributes);
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			System.out.println("Contact already exists");
		}

	}

    public void getAllContacts() throws NamingException{
        String searchFilter = "(objectClass=inetOrgPerson)";
        String[] reqAtt = {"cn","sn","mobile"};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);
        try{
            NamingEnumeration users = connection.search("ou=Friends,dc=contacts,dc=local", searchFilter, controls);
            SearchResult result = null;
            while(users.hasMore()){
                // System.out.println("Error here 2");
                result = (SearchResult) users.next();
                Attributes attr = result.getAttributes();
                System.out.println(attr.get("cn")  +" "+ attr.get("sn") +" "+ attr.get("mobile")+"\n");
            }
        }
        catch(NamingException x){
            System.out.println("idk what error this is though");
        }
    }
    
    public static void main(String[] args) throws NamingException{   
        contacts cl=new contacts();
        cl.newConnection();
        Scanner sc = new Scanner(System.in);
        System.out.println("Full Contact List View");
        cl.getAllContacts();
        // cl.addContact("Neo", "Seefane", "07612565567");
        cl.getAllContacts();
        while(true){
            System.out.println("To exit type \"Exit\".\t To view all contacts type \"All\".\t To delete type \"Del\".\t To search type \"Search\".\t To add type \"Add\"");
            String op=new String ("");
            op = sc.next();
            System.out.println("\n");
            if(op.matches("Exit")){
                sc.close();
                System.exit(1);
            }
            else if(op.matches("All")){
                cl.getAllContacts();
            }
            else if(op.matches("Del")){
                System.out.println("Please enter name of contact you want to delete");
                op = sc.next();
                cl.deleteContactName(op);
            }
            else if(op.matches("Search")){
                System.out.println("Please enter name of contact you want to search");
                op = sc.next();
                cl.getContactsByName(op);
            }
            else if(op.matches("Add")){
                String n,s,c;
                System.out.println("Please enter name of the contact");
                n = sc.next();
                System.out.println("Please enter surname of contact");
                s = sc.next();
                System.out.println("Please enter the contact number of "+n);
                c = sc.next();
                cl.addContact(n,s,c);
            }
            else{
                System.out.println("Try another option");
            }
        }
    }
}
