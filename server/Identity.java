package server;

public interface Identity extends java.rmi.Remote 
{
	//username and password optional? how does this work?
    /**
     * @param loginname
     * @param realname
     * @return
     * @throws java.rmi.RemoteException
     */
    long Create(String loginname, String realname, String password) throws java.rmi.RemoteException;
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
    String reverseLookup(long UUID) throws java.rmi.RemoteException;
    //password
    /**
     * @param oldLoginName
     * @param newLoginName
     * @return
     * @throws java.rmi.RemoteException
     */
    boolean Modify(String oldLoginName, String newLoginName, String password) throws java.rmi.RemoteException; 
    boolean Delete(String loginname, String password) throws java.rmi.RemoteException;
    enum Level {
    	  users,
    	  uuids,
    	  all
    	}
    String get(Level level) throws java.rmi.RemoteException;
    
    
}