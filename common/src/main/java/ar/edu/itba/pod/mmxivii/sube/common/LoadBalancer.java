package ar.edu.itba.pod.mmxivii.sube.common;

import java.rmi.RemoteException;

public interface LoadBalancer extends CardService
{
    public void registerCacheNode(CardCacheNode cacheNode) throws RemoteException;

    public void unregisterCacheNode(CardCacheNode cacheNode) throws RemoteException;
}
