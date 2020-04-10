package server;

public interface Identity extends java.rmi.Remote
{
	//username and password optional? how does this work?
    /**
     * @param arg
     * @param realname
     * @return
     * @throws java.rmi.RemoteException
     */
    long Create(int arg, String realname) throws java.rmi.RemoteException;
    /**
     * @param arg
     * @return
     * @throws java.rmi.RemoteException
     */
    long Create(int arg) throws java.rmi.RemoteException;
    /**
     * @param loginname
     * @return
     * @throws java.rmi.RemoteException
     */
    String Lookup(String loginname) throws java.rmi.RemoteException; 
    /**
     * @param UUID
     * @return
     * @throws java.rmi.RemoteException
     */
    String reverseLookup(int UUID) throws java.rmi.RemoteException;
    //password
    /**
     * @param oldLoginName
     * @param newLoginName
     * @return
     * @throws java.rmi.RemoteException
     */
    boolean Modify(String oldLoginName, String newLoginName) throws java.rmi.RemoteException; 
    boolean Delete(String loginname) throws java.rmi.RemoteException;
    enum Level {
    	  users,
    	  uuids,
    	  all
    	}
    String get(Level level) throws java.rmi.RemoteException;
    
    /**
     * @param realname
     * @param inputPassword
     * @return
     */
    boolean CheckPassword(String realname, String inputPassword);
    
    
}