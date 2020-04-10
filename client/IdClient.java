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

//import rmisslex2.server.Square;

public class IdClient
{

    private IdClient() {
    }


    /**
     * @param args java IdClient --server <serverhost> [--numport <port#>] <query>
     */
    public static void main(String[] args) {
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();
    options.addRequiredOption("s", "server", 			true, 	"<serverhost> server host ip");
    options.addOption("n", "numport", 			true, 	"<password> port number");
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
    options.addOption("d", "password", 			true, 	"<password> password, used with create, modify, and delete commands");
    options.addOption("g", "get", 				true, 	"users|uuids|all  The client contacts the server and obtains either a list all login names, list of all UUIDs or a list of user, UUID and string description all accounts (don’t show encrypted passwords).");
    try {
		CommandLine cmd = parser.parse(options, args);
		//do stuff with commands here.
	} catch (ParseException e1) {
        // oops, something went wrong
        System.err.println( "Parsing failed.  Reason: " + e1.getMessage() );
        System.exit(1);
	}
	/*
	 * if (args.length < 3) { System.err.
	 * println("Usage: java IdClient --server <serverhost> [--numport <port#>] <query>"
	 * ); System.exit(1); }
	 */
	String host = null;
	int registryPort = 5121;
	if (args.length == 3) {
	    host = args[0];
	} else {
	    host = args[0];
	    registryPort = Integer.parseInt(args[3]);
	}

	
	    System.setProperty("javax.net.ssl.trustStore", "../resources/Client_Truststore");
	    System.setProperty("java.security.policy", "../resources/mysecurity.policy");
	    /* System.setSecurityManager(new RMISecurityManager()); */

	    try {
	    Registry registry = LocateRegistry.getRegistry(host, registryPort);
	    Identity stub = (Identity) registry.lookup("IdentityServer");

	    int result = 0;
		//result = stub.identity(value); can call functions from server like this
	    System.out.println(result);
		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
    }

    /**
     * 
     * @param loginname
     * @param realname
     * @param password
     */
    public static void create(String loginname, String realname, String password){
        //Server's UUID would set username to Loginname, and if provided Name to realname. Probably need to overload 3? times. one without realname, one with realname. We have to option to make password optional but I will make it so it's required.
        //Another option would be to have the Server class handle the arguments provided and have this be the only create. That's a little risky and I think overloading is a little better.


    }

    public static void lookup(String loginname){
        //Request server for matching login name. if true, return all info except password
    }

    public static void reverselookup(UUID id){
        //Request server info for matching uuid. if true returns all info except password.
    }

    public static void modify(String oldusername, String newusername, String password){
        //Changes to new username if the password is the correct one. Password is optional, but i think for this case we should make it required
    }

    public static void delete(String loginname, String password){
        //deletes login/uuid?
    }

    public static void get(String option){
        //returns either list of all usernames, list of all uuids, or both if specified such as usernames, uuids, all.
    }

}
