package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
//import command line parser (need to import jar files if you see errors here, see readme)
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import server.Identity;
import server.Identity.Level;

//import rmisslex2.server.Square;

public class IdClient
{
static int registryPort = 1099;
    private IdClient() {
    	
    }


    /**
     * @param args java IdClient --server <serverhost> [--numport <port#>] <query>
     */
    public static void main(String[] args) {
	    CommandLineParser parser = new DefaultParser();
	    Options options = new Options();
	    options.addRequiredOption("s", "server", 			true, 	"<serverhost> server host ip");
	    options.addOption("n", "numport", 			true, 	"<port#> port number");
	    Option create = Option.builder("c")
	    		.longOpt("create")
	    		.desc("<loginname>  [<real name>] With this option, the client contacts the server and attempts to create the new login name. The client optionally provides the real user name and password along with the request.")
	    		.numberOfArgs(2)
	    		.optionalArg(true)
	    		.build();
	    options.addOption(create);
	    options.addOption("l", "lookup", 			true, 	"<loginname> With this option, the client connects with the server and looks up the loginname and displays all information found associated with the login name (except for the encrypted password).");
	    options.addOption("r", "reverse-lookup", 	true, 	"<UUID> With this option, the client connects with the server and looks up the UUID and displays all information found associated with the UUID (except for the encrypted password).");
	    Option modify = Option.builder("m")
	    		.longOpt("modify")
	    		.desc("<oldloginname> <newloginname> The client contacts the server and requests a loginname change. If the new login name is available, the server changes the name (note that the UUID does not ever change, once it has been assigned). If the new login name is taken, then the server returns an error")
	    		.numberOfArgs(2)
	    		.build();
	    options.addOption(modify);
	    options.addOption("p", "delete", 			true, 	"<loginname> The client contacts the server and requests to delete their loginname. The client must supply the correct password for this operation to succeed.");
	    //when password is required, check that it exists, and then that it is correct before calling any methods.
	    options.addOption("d", "password", 			true, 	"<password> password, used with create, modify, and delete commands");
	    options.addOption("g", "get", 				true, 	"users|uuids|all  The client contacts the server and obtains either a list all login names, list of all UUIDs or a list of user, UUID and string description all accounts (don’t show encrypted passwords).");
	    CommandLine cmd = null;
	    try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + e1.getMessage() );
	        System.exit(1);
		}
		String host = cmd.getOptionValue("server");
		if(cmd.hasOption("numport")) {
			try{
				registryPort = Integer.parseInt(cmd.getOptionValue("numport"));
			}
			catch(NumberFormatException e) {
				System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
                System.exit(1);
			}
		}
		
	    System.setProperty("javax.net.ssl.trustStore", "../resources/Client_Truststore");
	    System.setProperty("java.security.policy", "../resources/mysecurity.policy");
	    /* System.setSecurityManager(new RMISecurityManager()); */
	
	    try {
	    Registry registry = LocateRegistry.getRegistry(host, registryPort);
	    Identity stub = (Identity) registry.lookup("IdServer");
	    
	    int result = 0;
		//result = stub.identity(value); can call functions from server like this
	    
	    System.out.println(stub.get(Level.uuids));
		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
    }
}
