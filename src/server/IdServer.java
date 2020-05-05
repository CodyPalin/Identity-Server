package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.naming.NamingException;

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
public class IdServer extends UnicastRemoteObject implements Identity,ServerCommunication{
	
	private static final long serialVersionUID = 8510789827054962873L;
    private static int registryPort = 1099; //by default rmiregistry service runs on port 1099
    private static boolean verbose = false;
    private String name;
    ArrayList<InetAddress> allIPs = new ArrayList<InetAddress>();
    private static InetAddress myIP;
    private int myID;
    private int coordinatorID = -1;
    private Registry coordinatorRegistry;
    private HashMap<Long, String> loginsReverse = new HashMap<Long, String>();
    private HashMap<String, Long> logins = new HashMap<String, Long>();
    private HashMap<Long, Data> logindata = new HashMap<Long, Data>();
	ArrayList<Data> realusers = new ArrayList<Data>();
    static Registry registry;
	volatile static Timer timer;
	volatile static TimerTask task;
	volatile static Timer alivetimer;
	volatile static TimerTask alivetask;
	
	
    class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			State state = new State(loginsReverse, logins, logindata, realusers);
    		FileOutputStream fout;
			try {
				fout = new FileOutputStream("./state.ser");
	    		ObjectOutputStream oos;
				oos = new ObjectOutputStream(fout);
	    		oos.writeObject(state);
	    		oos.close();
	    		fout.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
    	
    }
    class AliveTimerTask extends TimerTask{

		@Override
		public void run() {
			if(coordinatorID == -1)
			{
				//System.err.println("There is currently no coordinator");
				return;
			}
			boolean alive = false;
			try {
				if(coordinatorRegistry == null)
					coordinatorRegistry = LocateRegistry.getRegistry(allIPs.get(coordinatorID).getHostAddress(), registryPort);
				
		    	ServerCommunication stub = (ServerCommunication) registry.lookup("IdServer");
	    	
				alive = stub.AreYouAlive();
			} catch (RemoteException | NotBoundException e) {
				alive = false;
			}
			
			if(!alive) {
				try {
					StartElection();
				} catch (RemoteException e) {
					System.err.println("failed to start election");
					e.printStackTrace();
				}
			}
		}
    	
    }
    public static class Data implements Serializable{
	    /**
		 * 
		 */
		private static final long serialVersionUID = -209569222800945680L;
		public String username;
	    public int passHash;
	}
    public class State implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 757474264047726727L;
    	public HashMap<Long, String> loginsReverse;
    	public HashMap<String, Long> logins;
    	public HashMap<Long, Data> logindata;
    	public ArrayList<Data> realusers;
		public State(HashMap<Long, String> loginsReverse, HashMap<String, Long> logins, HashMap<Long, Data> logindata, ArrayList<Data> realusers) {
			this.loginsReverse = loginsReverse;
			this.logins = logins;
			this.logindata = logindata;
			this.realusers = realusers;
		}
    	
    }
    
    /**
     * Creates IdServer
     * @param s
     * @throws RemoteException
     */
    public IdServer(String s) throws RemoteException {
        super();
        name = s;
        Data user = new Data();
        user.username = "Codyr";
        user.passHash = "1234".hashCode(); 
        realusers.add(user);
        File stateFile = new File("./state.ser");
        if(stateFile.exists()) 
        {
        	FileInputStream fin;
			try {
				fin = new FileInputStream("./state.ser");
				ObjectInputStream oin = new ObjectInputStream(fin); 
				State loadedState = (State)(oin.readObject());
				realusers = loadedState.realusers;
				logins = loadedState.logins;
				loginsReverse = loadedState.loginsReverse;
				logindata = loadedState.logindata;
				oin.close();
				fin.close();
				
			} catch ( IOException | ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} 
        }
        else {
        	try {
				stateFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
        }
        task = new MyTimerTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 1000);

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
        InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		myIP = inetAddress;
        System.out.println("IP Address:- " + inetAddress.getHostAddress());
        //System.out.println("Host Name:- " + inetAddress.getHostName());
        
    }
        
	@Override
	 /**
     * Creates a new Account or UUID given login name, realname and password are required.
     * @param loginname the login name the account will use
     * @param realname The user behind the login name
     * @param password The password the account will use
     * @return The end result is a new UUID is created and stored with the server.
     * @throws java.rmi.RemoteException
     */
	public long Create(String loginname, String realname, String password) throws RemoteException, NamingException {
		if(myID != coordinatorID) {
			if(verbose) {
				System.out.println("recieved rmi from client, bouncing to coordinator");
			}
			try {
			Registry registry = LocateRegistry.getRegistry(allIPs.get(coordinatorID).getHostAddress(), registryPort);
		    Identity stub = (Identity) registry.lookup("IdServer");
		    return stub.Create(loginname, realname, password);
			} catch (RemoteException | NotBoundException e) {
			    System.err.println("Coordinator did not respond, starting election and then trying again in 5 seconds");
			    StartElection();
			    try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    return Create(loginname, realname, password);
			}
		}
		if(verbose)
			System.out.println("creating new login: "+loginname);
		Data user = new Data();
		user.username = realname;
		user.passHash = password.hashCode();
		realusers.add(user);
			
		long value = UUID.randomUUID().getMostSignificantBits();
		if(logins.containsKey(loginname)) {
			throw new NamingException("the login name" + loginname + "already exists in the database");
		}
		loginsReverse.put(value, loginname);
		logins.put(loginname, value);
		logindata.put(value, user);
		StartStateMessage();
		return value;
	}

	@Override
	 /**
     * Searches for UUID based on loginname.
     * @param loginname The login name that the method will search for the matching one.
     * @return UUID the account or will return null if the login name does not match with any UUIDs
     * @throws java.rmi.RemoteException
     */
	public String Lookup(String loginname) throws RemoteException {
		if(myID != coordinatorID) {
			if(verbose) {
				System.out.println("recieved rmi from client, bouncing to coordinator");
			}
			try {
			Registry registry = LocateRegistry.getRegistry(allIPs.get(coordinatorID).getHostAddress(), registryPort);
		    Identity stub = (Identity) registry.lookup("IdServer");
		    return stub.Lookup(loginname);
			} catch (RemoteException | NotBoundException e) {
			    System.err.println("Coordinator did not respond, starting election and then trying again in 5 seconds");
			    StartElection();
			    try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    return Lookup(loginname);
			}
		}
		if(verbose)
			System.out.println("looking up login: "+loginname);
		long uuid = logins.get(loginname);
		String result = "Login: "+loginname+" UUID: "+uuid+" Created by: "+logindata.get(uuid).username;
		return result;
	}

	@Override
	/**
     * Searches for login name based on UUID #.
     * @param UUID the UUID the method will try to search for the matching one.
     * @return loginname  The loginname the UUID matches to, will return null if the UUID does not match with any UUIDs
     * @throws java.rmi.RemoteException
     */
	public String reverseLookup(long UUID) throws RemoteException {
		if(myID != coordinatorID) {
			if(verbose) {
				System.out.println("recieved rmi from client, bouncing to coordinator");
			}
			try {
			Registry registry = LocateRegistry.getRegistry(allIPs.get(coordinatorID).getHostAddress(), registryPort);
		    Identity stub = (Identity) registry.lookup("IdServer");
		    return stub.reverseLookup(UUID);
			} catch (RemoteException | NotBoundException e) {
			    System.err.println("Coordinator did not respond, starting election and then trying again in 5 seconds");
			    StartElection();
			    try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    return reverseLookup(UUID);
			}
		}
		if(verbose)
			System.out.println("performing reverse lookup: "+UUID);
		if(!loginsReverse.containsKey(UUID))
			return null;
		String result = "Login: "+loginsReverse.get(UUID)+" UUID: "+UUID+" Created by: "+logindata.get(UUID).username;
		return result;
	}


	@Override
	 /**
     * Changes the old login name to a new login name. Requires a password to validate the change.
     * @param oldLoginName The oldloginname the UUID had associated with.
     * @param newLoginName The newloginname the UUID will be associated with.
     * @param password The password required and must match the UUID's password to validate the change of login names.
     * @return returns true if modified successfully, false otherwise
     * @throws java.rmi.RemoteException
     */
	public boolean Modify(String oldLoginName, String newLoginName, String password) throws RemoteException {
		if(myID != coordinatorID) {
			if(verbose) {
				System.out.println("recieved rmi from client, bouncing to coordinator");
			}
			try {
			Registry registry = LocateRegistry.getRegistry(allIPs.get(coordinatorID).getHostAddress(), registryPort);
		    Identity stub = (Identity) registry.lookup("IdServer");
		    return stub.Modify(oldLoginName, newLoginName, password);
			} catch (RemoteException | NotBoundException e) {
			    System.err.println("Coordinator did not respond, starting election and then trying again in 5 seconds");
			    StartElection();
			    try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    return Modify(oldLoginName, newLoginName, password);
			}
		}
		if(verbose)
			System.out.println("modifying login: ");
		long uuid = logins.get(oldLoginName);
		if(logindata.get(uuid).passHash != password.hashCode())
			return false;
		if(!loginsReverse.replace(uuid,oldLoginName, newLoginName))
			return false;
		logins.remove(oldLoginName);
		logins.put(newLoginName, uuid);
		StartStateMessage();
		return true;
	}


	@Override
	/**
     * Searches for the matching loginname, then verifies if the password matches and then will delete the UUID
     * @param loginname the loginname that is connected to a uuid that will be removed.
     * @param password The password that must match to the uuid
     * @return returns true if the UUID is removed, false otherwise
     * @throws java.rmi.RemoteException
     */
	public boolean Delete(String loginname, String password) throws RemoteException {
		if(myID != coordinatorID) {
			if(verbose) {
				System.out.println("recieved rmi from client, bouncing to coordinator");
			}
			try {
			Registry registry = LocateRegistry.getRegistry(allIPs.get(coordinatorID).getHostAddress(), registryPort);
		    Identity stub = (Identity) registry.lookup("IdServer");
		    return stub.Delete(loginname, password);
			} catch (RemoteException | NotBoundException e) {
			    System.err.println("Coordinator did not respond, starting election and then trying again in 5 seconds");
			    StartElection();
			    try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    return Delete(loginname, password);
			}
		}
		if(verbose)
			System.out.println("deleting login: ");
		long uuid = logins.get(loginname);
		if(logindata.get(uuid).passHash != password.hashCode())
			return false;
		if(!logins.containsKey(loginname))
			return false;
		logindata.remove(uuid);
		loginsReverse.remove(uuid);
		logins.remove(loginname);
		StartStateMessage();
		return true;
	}


	@Override
	/**
	 * Gets all info for level
	 */
	public String get(Level level) throws RemoteException {
		if(myID != coordinatorID) {
			if(verbose) {
				System.out.println("recieved rmi from client, bouncing to coordinator");
			}
			try {
			Registry registry = LocateRegistry.getRegistry(allIPs.get(coordinatorID).getHostAddress(), registryPort);
		    Identity stub = (Identity) registry.lookup("IdServer");
		    return stub.get(level);
			} catch (RemoteException | NotBoundException e) {
			    System.err.println("Coordinator did not respond, starting election and then trying again in 5 seconds");
			    StartElection();
			    try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    return get(level);
			}
		}
		if(verbose)
			System.out.println("getting all info at level: "+level);
		switch(level){
			case users:
				return logins.keySet().toString();
			case uuids:
				return loginsReverse.keySet().toString();
			case all:
				String result="";
				for(Long l : loginsReverse.keySet())
				{
					result+=loginsReverse.get(l)+" : "+l+" : user-"+logindata.get(l).username+"\r\n";
				}
				return result;
		}
		return "get all info for " +level.toString();
	}

	@Override
	public void SetupCommunication(ArrayList<InetAddress> inetAddresses) throws RemoteException {
		if(verbose)
			System.out.println("Got IP list:");
		allIPs = inetAddresses;
		myID = inetAddresses.indexOf(myIP);
		if(verbose)
			System.out.println("My ID is: "+myID);
        task = new MyTimerTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 1000);
        alivetask = new AliveTimerTask();
		alivetimer = new Timer();
		alivetimer.scheduleAtFixedRate(alivetask, 0, 5000);

	}

	@Override
	public void StartElection() throws RemoteException{
		if(verbose) {
			System.out.println("Starting an election");
		}
		ArrayList<Integer> ids = new ArrayList<Integer>();
		try {
			SendElectionMessage(ids);
		} catch (RemoteException e) {
			System.err.println("failed to start election");
			e.printStackTrace();
		}
		
	}

	private int incrementID(int nextid) {

		if(nextid+1 < allIPs.size()) {
			nextid = nextid+1;
		}
		else {
			nextid = 0;
		}
		return nextid;
	}

	private void StartStateMessage() {
		if(verbose) {
			System.out.println("Sending state.");
		}
		int nextid = incrementID(myID);
		while(nextid != myID) {
			try {	
				Registry registry = LocateRegistry.getRegistry(allIPs.get(nextid).getHostAddress(), registryPort);
		
			    ServerCommunication stub = (ServerCommunication) registry.lookup("IdServer");
			    stub.SendState(loginsReverse, logins, logindata, realusers, allIPs, coordinatorID);
			    return;
			} catch (RemoteException | NotBoundException e) {
				System.err.println("Server with ID: "+nextid+" not responding");
			} catch (ClassCastException e) {
				System.err.println(e.getMessage());
			}
			nextid = incrementID(nextid);
		}
		
	}
	@Override
	public void SendElectionMessage(ArrayList<Integer> ids) throws RemoteException {
		if(ids.contains(myID))
		{
			int coordinatorID = myID;
			for(int id : ids) {
				if(id > coordinatorID) {
					coordinatorID = id;
				}
			}
			SendCoordinatorMessage(myID, coordinatorID);
			
		}
		else 
		{
			this.coordinatorID = -1;
			coordinatorRegistry = null;
			ids.add(myID);
			int nextid = incrementID(myID);
			while(nextid != myID) {
				try {	
					Registry registry = LocateRegistry.getRegistry(allIPs.get(nextid).getHostAddress(), registryPort);
			
				    ServerCommunication stub = (ServerCommunication) registry.lookup("IdServer");
				    stub.SendElectionMessage(ids);
				    return;
				} catch (RemoteException | NotBoundException e) {
				    System.err.println("Server with ID: "+nextid+" not responding");
				}
				nextid = incrementID(nextid);
			}
			//no other servers responding
			SendElectionMessage(ids); //send to self
			
		}
		
	}

	@Override
	public void SendCoordinatorMessage(int originatorID, int coordinatorID) throws RemoteException {
		if(verbose) {
			System.out.println(coordinatorID + " is coordinator.");
		}
		if(originatorID == myID && this.coordinatorID != -1) {
			//no more messages needed
			if(verbose)
				System.out.println("Coordinator message complete");
		}
		else {
			this.coordinatorID = coordinatorID;
			if(verbose && myID == coordinatorID) {
				System.out.println("Election won, I am coordinator");
			}
			int nextid = incrementID(myID);
			while(nextid != myID) {
				try {	
					Registry registry = LocateRegistry.getRegistry(allIPs.get(nextid).getHostAddress(), registryPort);
			
				    ServerCommunication stub = (ServerCommunication) registry.lookup("IdServer");
				    stub.SendCoordinatorMessage(originatorID, coordinatorID);
				    return;
				} catch (RemoteException | NotBoundException e) {
				    System.err.println("Server with ID: "+nextid+" not responding");
				}
				nextid = incrementID(nextid);
			}
		}
	}

	@Override
	public boolean AreYouAlive() throws RemoteException {
		return true;
	}

	@Override
	public void SendState(HashMap<Long, String> loginsReverse, HashMap<String, Long> logins, HashMap<Long, Data> logindata, ArrayList<Data> realusers, ArrayList<InetAddress> inetAddresses, int coordinatorID) throws RemoteException {
		if(verbose)
			System.out.println("received a state, I think coordinator is:"+coordinatorID);
		if(myID == coordinatorID)
		{
			//states synchronized
			return;
		}
		if(coordinatorID == -1) {
			//this means I have crashed and other servers are still running
			this.allIPs = inetAddresses;
			this.coordinatorID = coordinatorID;
		}
		this.loginsReverse = loginsReverse;
		this.logins = logins;
		this.logindata = logindata;
		this.realusers = realusers;
		
		int nextid = incrementID(myID);
		while(nextid != myID) {
			try {	
				Registry registry = LocateRegistry.getRegistry(allIPs.get(nextid).getHostAddress(), registryPort);
		
			    ServerCommunication stub = (ServerCommunication) registry.lookup("IdServer");
			    stub.SendState(loginsReverse, logins, logindata, realusers, inetAddresses, coordinatorID);
			    return;
			} catch (RemoteException | NotBoundException e) {
				System.err.println("Server with ID: "+nextid+" not responding");
			}
			nextid = incrementID(nextid);
		}
	}





		

        
}
    