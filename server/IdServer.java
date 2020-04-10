package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
public class IdServer implements Identity{
    private static int registryPort = 5121;

    public IdServer(String s) throws RemoteException {
        super();
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
        if (args.length > 0) {
            registryPort = Integer.parseInt(args[0]);
        }
    
        System.out.println("Setting System Properties....");
        System.setProperty("javax.net.ssl.keyStore", "../resources/Server_Keystore");
        // Warning: change to match your password! Also the password should be
        // stored encrypted in a file outside the program.
        System.setProperty("javax.net.ssl.keyStorePassword", "test123");
        System.setProperty("java.security.policy", "../resources/mysecurity.policy");
        try {
            IdServer server = new IdServer("IdServer");
            server.bind("SquareServer", registryPort);
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

        
    }
    