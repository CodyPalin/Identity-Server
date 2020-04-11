package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 * The IdServer class, handles the methods for the accounts, stores, manages the accounts and returns what the IdClient requests
 * @author Cody Palin, Omar Gonzalez
 *
 */
public class IdServer extends UnicastRemoteObject implements Identity{
	private static final long serialVersionUID = 8510789827054962873L;
    private static int registryPort = 1099; //by default rmiregistry service runs on port 1099
    private static boolean verbose = false;
    private String name;
    static Registry registry;
    /**
     * Creates IdServer
     * @param s
     * @throws RemoteException
     */
    public IdServer(String s) throws RemoteException {
        super();
        name = s;
        }
    
    
        

    
        public static void main(String args[]) {

	        
            CommandLineParser parser = new DefaultParser();
            Options options = new Options();
            options.addOption("n", "numport", 			true, 	"<password> port number");
            options.addOption("v","verbose", false, "makes the server print detailed messages on the operations as it executes them.");
            
            try {
        		CommandLine cmd = parser.parse(options, args);
        		//do stuff with commands here.
        		if(cmd.hasOption("verbose"))
        			verbose = true;
        		if(cmd.hasOption("numport"))
        		{
        			String portstring = cmd.getOptionValue("numport");
        			try{
        				registryPort = Integer.parseInt(portstring);
        			}
        			catch(NumberFormatException e) {
        				System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
                        System.exit(1);
        			}
        		}
        	} catch (ParseException e1) {
                // oops, something went wrong
                System.err.println( "Parsing failed.  Reason: " + e1.getMessage() );
                System.exit(1);
        	}
            
	        try {
		        //System.setProperty("java.security.policy", "../resources/mysecurity.policy");
	        	//-Djava.security.policy=file:/${workspace_loc:identity-server/mysecurity.policy} //use this in java VM for eclipse if security manager is needed.
	        	//System.setSecurityManager(new SecurityManager()); 
	        	
	        	//using createRegistry instead of getRegistry so we don't have to mess with starting up the registry separately.
	    	    registry = LocateRegistry.createRegistry(registryPort);
	            IdServer obj = new IdServer("IdServer");
	            registry.bind("IdServer", obj);
	            System.out.println("IDServer bound in registry");
	        } catch (Throwable th) {
	            th.printStackTrace();
	            System.out.println("Exception occurred: " + th);
	        }
        }

		@Override
		 /**
	     * Creates a new Account or UUID given login name, realname and password are optional.
	     * @param loginname the login name the account will use
	     * @param realname The user behind the login name
	     * @param password The password the account will use
	     * @return The end result is a new UUID is created and stored with the server.
	     * @throws java.rmi.RemoteException
	     */
		public long Create(String loginname, String realname, String password) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		 /**
	     * Searches for UUID based on loginname.
	     * @param loginname The login name that the method will search for the matching one.
	     * @return UUID the account or will return null if the login name does not match with any UUIDs
	     * @throws java.rmi.RemoteException
	     */
		public String Lookup(String loginname) throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		/**
	     * Searches for login name based on UUID #.
	     * @param UUID the UUID the method will try to search for the matching one.
	     * @return loginname  The loginname the UUID matches to, will return null if the UUID does not match with any UUIDs
	     * @throws java.rmi.RemoteException
	     */
		public String reverseLookup(long UUID) throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		 /**
	     * Changes the old login name to a new login name. Requires a password to validate the change.
	     * @param oldLoginName The oldloginname the UUID had associated with.
	     * @param newLoginName The newloginname the UUID will be associated with.
	     * @param password The password required and must match the UUID's password to validate the change of login names.
	     * @return Should return nothing
	     * @throws java.rmi.RemoteException
	     */
		public boolean Modify(String oldLoginName, String newLoginName, String password) throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		/**
	     * Searches for the matching loginname, then verifies if the password matches and then will delete the UUID
	     * @param loginname the loginname that is connected to a uuid that will be removed.
	     * @param password The password that must match to the uuid
	     * @return Nothing returns, the UUID is removed
	     * @throws java.rmi.RemoteException
	     */
		public boolean Delete(String loginname, String password) throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		/**
		 * Gets all info for level
		 */
		public String get(Level level) throws RemoteException {
			// TODO Auto-generated method stub
			return "get all info for " +level.toString();
		}





		

        
    }
    