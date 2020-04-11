package server;

/**
 * The Identity Interface, Has necessary methods that the IdServer class must implement
 * @author Cody Palin, Omar Gonzalez
 *
 */
public interface Identity extends java.rmi.Remote 
{
	//username and password optional? how does this work?
    /**
     * Necessary Method and Requirement for the server to work smoothly, creates a new Account or UUID
     * @param loginname the login name the account will use
     * @param realname The user behind the login name
     * @param password The password the account will use
     * @return The end result is a new UUID is created and stored with the server.
     * @throws java.rmi.RemoteException
     */
    long Create(String loginname, String realname, String password) throws java.rmi.RemoteException;
    
    /**
     * Skeleton method for Lookup, searches for UUID based on loginname.
     * @param loginname The login name that the method will search for the matching one.
     * @return UUID the account or will return null if the login name does not match with any UUIDs
     * @throws java.rmi.RemoteException
     */
    String Lookup(String loginname) throws java.rmi.RemoteException; 
    
    /**
     * Skeleton method for reverseLookup, searches for login name based on UUID #.
     * @param UUID the UUID the method will try to search for the matching one.
     * @return loginname  The loginname the UUID matches to, will return null if the UUID does not match with any UUIDs
     * @throws java.rmi.RemoteException
     */
    String reverseLookup(long UUID) throws java.rmi.RemoteException;
    //password
    
    /**
     * Skeleton method for Modify, changes the old login name to a new login name. Requires a password to validate the change.
     * @param oldLoginName The oldloginname the UUID had associated with.
     * @param newLoginName The newloginname the UUID will be associated with.
     * @param password The password required and must match the UUID's password to validate the change of login names.
     * @return Should return nothing
     * @throws java.rmi.RemoteException
     */
    boolean Modify(String oldLoginName, String newLoginName, String password) throws java.rmi.RemoteException;
    
    /**
     * Skeleton method for Delete, searches for the matching loginname, then verifies if the password matches and then will delete the UUID
     * @param loginname the loginname that is connected to a uuid that will be removed.
     * @param password The password that must match to the uuid
     * @return Nothing returns, the UUID is removed
     * @throws java.rmi.RemoteException
     */
    boolean Delete(String loginname, String password) throws java.rmi.RemoteException;
    enum Level {
    	  users,
    	  uuids,
    	  all
    	}
    String get(Level level) throws java.rmi.RemoteException;
    
    
}