/*
* (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
* law. All use of this software is subject to MuleSoft's Master Subscription Agreement
* (or other master license agreement) separately entered into in writing between you and
* MuleSoft. If such an agreement is not in place, you may not use the software.
*/

package ar.edu.itba.pod.mmxivii.sube.balancer;

import ar.edu.itba.pod.mmxivii.sube.common.CardCacheNode;
import ar.edu.itba.pod.mmxivii.sube.common.CardService;
import ar.edu.itba.pod.mmxivii.sube.common.LoadBalancer;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;

import javax.annotation.Nonnull;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancingCardService extends UnicastRemoteObject implements LoadBalancer
{
    private static final long serialVersionUID = 2919260533266908792L;
    private final List<CardCacheNode> cacheNodes;
    private int cacheNodeNumber;

    public LoadBalancingCardService() throws RemoteException
    {
        super(0);
        cacheNodes = new ArrayList<>();
        cacheNodeNumber = 0;
    }

    @Override
    public double getCardBalance(@Nonnull UID id) throws RemoteException
    {
        return cacheNodes.get(id.hashCode() % cacheNodeNumber).getCardBalance(id);
    }

    @Override
    public double travel(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        return cacheNodes.get(id.hashCode() % cacheNodeNumber).travel(id, description, amount);
    }

    @Override
    public double recharge(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        return cacheNodes.get(id.hashCode() % cacheNodeNumber).recharge(id, description, amount);
    }

    public void registerCacheNode(CardCacheNode cacheNode) throws RemoteException
    {
        cacheNodes.add(cacheNode);
        cacheNodeNumber++;
    }

    public void unregisterCacheNode(CardCacheNode cacheNode) throws RemoteException
    {
        cacheNodes.remove(cacheNode);
        cacheNodeNumber--;
    }


}
