package server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import server.IdServer.Data;

public interface ServerCommunication extends java.rmi.Remote{
	/**
	 * sets up communications with all servers
	 * @param inetAddresses list of ips for all instances of IdServer
	 * @throws java.rmi.RemoteException
	 */
	void SetupCommunication(ArrayList<InetAddress> inetAddresses) throws java.rmi.RemoteException;
	/**
	 * starts an election from this server.
	 * @throws java.rmi.RemoteException
	 */
	void StartElection() throws java.rmi.RemoteException;
	void SendElectionMessage(ArrayList<Integer> ids) throws java.rmi.RemoteException;
	void SendCoordinatorMessage(int originatorID, int coordinatorID) throws java.rmi.RemoteException;
	boolean AreYouAlive() throws java.rmi.RemoteException;
	void SendState(HashMap<Long, String> loginsReverse, HashMap<String, Long> logins, HashMap<Long, Data> logindata, ArrayList<Data> realusers, ArrayList<InetAddress> inetAddresses, int coordinatorID) throws java.rmi.RemoteException;
}
