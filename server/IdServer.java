package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
public class IdServer extends UnicastRemoteObject implements Identity{
	private static final long serialVersionUID = 8510789827054962873L;
    private static int registryPort = 1099; //by default rmiregistry service runs on port 1099
    private static boolean verbose = false;
    private String name;
    static Registry registry;
    public IdServer(String s) throws RemoteException {
        super();
        name = s;
        }
    
    
        public void bind(String name, int registryPort) {
	        try {
	            RMIClientSocketFactory rmiClientSocketFactory = new SslRMIClientSocketFactory();
	            RMIServerSocketFactory rmiServerSocketFactory = new SslRMIServerSocketFactory();
	            Identity server = (Identity) UnicastRemoteObject.exportObject(this, 0, rmiClientSocketFactory,
	                    rmiServerSocketFactory);
	            Registry registry = LocateRegistry.createRegistry(registryPort);
	            registry.rebind(name, server);
	            System.out.println(name + " bound in registry");
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Exception occurred: " + e);
	        }
        }

    
        public static void main(String args[]) {

	        /*
	        if (args.length > 0) {
	            registryPort = Integer.parseInt(args[0]);
	        }
	        System.out.println("Setting System Properties....");
	        System.setProperty("javax.net.ssl.keyStore", "../resources/Server_Keystore");
	        // Warning: change to match your password! Also the password should be
	        // stored encrypted in a file outside the program.
	        System.setProperty("javax.net.ssl.keyStorePassword", "test123");
	        System.setProperty("java.security.policy", "../resources/mysecurity.policy");
	        */
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
		public long Create(int arg) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}


		@Override
		public long Create(int arg, String realname) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}


		@Override
		public String Lookup(String loginname) throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public String reverseLookup(int UUID) throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public boolean Modify(String oldLoginName, String newLoginName) throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public boolean Delete(String loginname) throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public String get(Level level) throws RemoteException {
			// TODO Auto-generated method stub
			return "get all info for " +level.toString();
		}


		@Override
		public boolean CheckPassword(String realname, String inputPassword) {
			// TODO Auto-generated method stub
			return false;
		}

        
    }
    