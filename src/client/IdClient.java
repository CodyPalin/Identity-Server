package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

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

/**
 * Client Method for interacting with IdServer. Uses the terminal and the client reads the arguments. 
 * Then the client will interact with the server to perform the commands or queries once connected 
 * @author Cody Palin, Omar Gonzalez
 *
 */
public class IdClient
{
static int registryPort = 1099;
    private IdClient() {
    	
    }


    /**
     * Main method, depending on arguments used, can perform actions and interact with the IdServer
     * @param args java IdClient --server <serverhost> [--numport <port#>] <query> //Arguments and based on <query> will perform different commands such as creating an account, looking up a username or more.
     */
    public static void main(String[] args) {
	    CommandLineParser parser = new DefaultParser();
	    Options options = new Options();
	    options.addRequiredOption("s", "server", 			true, 	"<serverhost> server host ip or list of IPs");
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
	    options.addOption("d", "delete", 			true, 	"<loginname> The client contacts the server and requests to delete their loginname. The client must supply the correct password for this operation to succeed.");
	    //when password is required, check that it exists, and then that it is correct before calling any methods.
	    options.addOption("p", "password", 			true, 	"<password> password, used with create, modify, and delete commands");
	    options.addOption("g", "get", 				true, 	"users|uuids|all  The client contacts the server and obtains either a list all login names, list of all UUIDs or a list of user, UUID and string description all accounts (don't show encrypted passwords).");
	    CommandLine cmd = null;
	    try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + e1.getMessage() );
	        System.exit(1);
		}
		String hosts = cmd.getOptionValue("server");
		ArrayList<String> hostlist = new ArrayList<String>();
		if(hosts.contains(".txt")) {
			File f = new File(hosts);
		    Scanner fileScanner = null;
		    try {
				fileScanner = new Scanner(f);
			} catch (FileNotFoundException e) {
				System.err.println("File not Found");
				System.exit(1);
			}
		    while(fileScanner.hasNextLine()) {
		    	hostlist.add(fileScanner.nextLine());
		    }
		}
		else {
			hostlist.add(hosts);
		}
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
	    Registry registry = null;
	    Identity stub = null;
    	for(String host: hostlist) {
		    registry = LocateRegistry.getRegistry(host, registryPort);
    		try {
    			stub = (Identity) registry.lookup("IdServer");
    			break;
    		}
    		catch(RemoteException e) {
    			
    		}
    	}
    	if(stub == null) {
    		System.err.println("All servers offline.");
    		System.exit(1);
    	}
	    //int result = 0;
		//result = stub.identity(value); can call functions from server like this
	    String passwordInput = "";
	    boolean hasPassword = false;
	    if(cmd.hasOption('p'))
	    {
	    	passwordInput = cmd.getOptionValue("p");
	    	hasPassword = true;
	    }
	    if(cmd.hasOption('c'))
	    {
	    	if(!hasPassword)
	    	{
	    		System.err.println("password is required for creating a login");
	    		System.exit(1);
	    	}
	    	String[] v = cmd.getOptionValues('c');
	    	String loginname = v[0];
	    	String realname;
	    	if(v.length == 2) 
	    		realname = v[1];
	    	else
	    		realname = System.getProperty("user.name");
	    	long val = stub.Create(loginname, realname, passwordInput);
	    	if(val != 0)
	    		System.out.println("Successfully created a new UUID for this login: "+ val);
	    	else
	    		System.out.println("failed to create a new UUID");
	    }
	    else if(cmd.hasOption('l'))
	    {
	    	String loginname = cmd.getOptionValue('l');
	    	System.out.println(stub.Lookup(loginname));
	    }
	    else if(cmd.hasOption('r'))
	    {
	    	String uuidString = cmd.getOptionValue('r');
	    	long uuid = 0;
	    	try{
	    		uuid = Long.parseLong(uuidString);
	    	}
	    	catch(NumberFormatException e) {
	    		System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
                System.exit(1);
	    	}
	    	System.out.println(stub.reverseLookup(uuid));
	    }
	    else if(cmd.hasOption('m'))
	    {
	    	if(!hasPassword)
	    	{
	    		System.err.println("password is required for modifying a login");
	    		System.exit(1);
	    	}

	    	String[] v = cmd.getOptionValues('m');
	    	if(stub.Modify(v[0], v[1], passwordInput))
	    	{
	    		System.out.println("Login name modified successfully.");
	    	}
	    	else {
	    		System.err.println("Login name modification failed.");
	    	}
	    	
	    }
	    else if(cmd.hasOption('d'))
	    {
	    	if(!hasPassword)
	    	{
	    		System.err.println("password is required for deleting a login");
	    		System.exit(1);
	    	}

	    	String loginname = cmd.getOptionValue('d');
	    	if(stub.Delete(loginname, passwordInput))
	    	{
	    		System.out.println("Login name deleted successfully.");
	    	}
	    	else {
	    		System.err.println("Login name deletion failed.");
	    	}
	    	
	    }
	    else if(cmd.hasOption('g'))
	    {
	    	Level l = Level.valueOf(cmd.getOptionValue('g'));
	    	System.out.println(stub.get(l));
	    }
		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
    }
}
