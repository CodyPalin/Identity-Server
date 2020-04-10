package server;

public interface Identity extends java.rmi.Remote
{
    long Create(int arg) throws java.rmi.RemoteException;
    
}