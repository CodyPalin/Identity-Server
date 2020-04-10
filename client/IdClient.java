package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
//import command line parser (need to import jar files if you see errors here, see readme)
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import server.Identity;

//import rmisslex2.server.Square;

public class IdClient
{

    private IdClient() {
    }


    public static void main(String[] args) {
    CommandLineParser parser = new DefaultParser();
	if (args.length < 3) {
	    System.err.println("Usage: java IdClient --server <serverhost>");
	    System.exit(1);
	}
	String host = null;
	int value;
	int count;
	int registryPort = 5121;
	if (args.length == 3) {
	    host = args[0];
	    value = Integer.parseInt(args[1]);
	    count = Integer.parseInt(args[2]);
	} else {
	    host = args[0];
	    value = Integer.parseInt(args[1]);
	    count = Integer.parseInt(args[2]);
	    registryPort = Integer.parseInt(args[3]);
	}

	try {
	    System.setProperty("javax.net.ssl.trustStore", "../resources/Client_Truststore");
	    System.setProperty("java.security.policy", "../resources/mysecurity.policy");
	    /* System.setSecurityManager(new RMISecurityManager()); */

	    Registry registry = LocateRegistry.getRegistry(host, registryPort);
	    Identity stub = (Identity) registry.lookup("IdentityServer");

	    int result = 0;
	    for (int i = 0; i < count; i++) {
		result = stub.identity(value);
		if (i % 1000 == 0) System.out.printf("Call# %d result = %d\r ", i, result);
	    }
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
