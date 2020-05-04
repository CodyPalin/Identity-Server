package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SetupCommunication {
    static int registryPort = 1099;

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
	    Options options = new Options();
	    options.addRequiredOption("f", "file", true, "<filename> list of IPs");
	    CommandLine cmd = null;
	    try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + e1.getMessage() );
	        System.exit(1);
		}
	    File f = new File(cmd.getOptionValue("file"));
	    Scanner fileScanner = null;
	    try {
			fileScanner = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println("File not Found");
			System.exit(1);
		}
	    
	    ArrayList<InetAddress> inetAddresses = new ArrayList<InetAddress>();
	    while(fileScanner.hasNextLine()) {
	    	String line = fileScanner.nextLine();
	    	InetAddress address = InetAddress.getLoopbackAddress();
	    	try {
				address = InetAddress.getByName(line);
			} catch (UnknownHostException e) {
				System.err.println("Host '"+line+"' could not be resolved");
				System.exit(1);
			}
	    	inetAddresses.add(address);
	    	
	    }
	    
	    for(InetAddress address: inetAddresses) {
	    	try {
				Registry registry = LocateRegistry.getRegistry(address.getHostAddress(), registryPort);

			    ServerCommunication stub = (ServerCommunication) registry.lookup("IdServer");
			    stub.SetupCommunication(inetAddresses);
			} catch (RemoteException | NotBoundException e) {
			    System.err.println("Could not access host at: "+ address.getHostAddress());
			}
	    }
	}

}
