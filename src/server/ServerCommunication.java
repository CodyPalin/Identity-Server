package server;

import java.net.InetAddress;
import java.util.ArrayList;

public interface ServerCommunication extends java.rmi.Remote{
	/**
	 * sets up communications with all servers
	 * @param inetAddresses list of ips for all instances of IdServer
	 * @throws java.rmi.RemoteException
	 */
	void SetupCommunication(ArrayList<InetAddress> inetAddresses) throws java.rmi.RemoteException;
}
