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
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadBalancingCardService extends UnicastRemoteObject implements LoadBalancer
{
    private static final long serialVersionUID = 2919260533266908792L;
    private final List<CardCacheNode> cacheNodes;
    private int cacheNodeNumber;
    private ScheduledExecutorService flushingExecutor = Executors.newScheduledThreadPool(1);

    public LoadBalancingCardService() throws RemoteException
    {
        super(0);
        cacheNodes = new ArrayList<>();
        cacheNodeNumber = 0;
        flushingExecutor.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run()
            {
                for (CardCacheNode cacheNode : cacheNodes)
                {
                    try
                    {
                        cacheNode.flush();
                    }
                    catch (RemoteException e)
                    {

                    }
                }
            }
        }, 1, 1, TimeUnit.MINUTES);

    }

    @Override
    public double getCardBalance(@Nonnull UID id) throws RemoteException
    {
        try
        {
            return cacheNodes.get(id.toString().hashCode() % cacheNodeNumber).getCardBalance(id);
        }
        catch (RemoteException e)
        {
            handleDeadNode(id);
            return getCardBalance(id);
        }
    }

    @Override
    public double travel(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        try
        {
            return cacheNodes.get(id.toString().hashCode() % cacheNodeNumber).travel(id, description, amount);
        }
        catch (RemoteException e)
        {
            handleDeadNode(id);
            return travel(id, description, amount);
        }
    }

    @Override
    public double recharge(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        try
        {
            return cacheNodes.get(id.toString().hashCode() % cacheNodeNumber).recharge(id, description, amount);
        }
        catch (RemoteException e)
        {
            handleDeadNode(id);
            return recharge(id, description, amount);
        }
    }

    public void registerCacheNode(CardCacheNode cacheNode) throws RemoteException
    {
        System.out.println("Adding node...");
        for (CardCacheNode originalCacheNode : cacheNodes)
        {
            try
            {
                originalCacheNode.flush();
            }
            catch (RemoteException e)
            {

            }
        }
        if (cacheNodeNumber == 0)
        {
            cacheNode.addBackupNode(cacheNode);
        }
        else
        {
            cacheNodes.get(cacheNodeNumber - 1).addBackupNode(cacheNode);
        }
        cacheNodes.add(cacheNode);
        cacheNode.addBackupNode(cacheNodes.get(0));
        cacheNodeNumber++;
        System.out.println("Node added");
    }

    public void unregisterCacheNode(CardCacheNode cacheNode) throws RemoteException
    {
        System.out.println("Removing node...");
        for (CardCacheNode originalCacheNode : cacheNodes)
        {
            try
            {
                originalCacheNode.flush();
            }
            catch (RemoteException e)
            {

            }
        }
        if (cacheNodes.indexOf(cacheNode) == cacheNodeNumber - 1)
        {
            cacheNodes.get(cacheNodes.indexOf(cacheNode) - 1).addBackupNode(cacheNodes.get(0));
        }
        else
        {
            cacheNodes.get(cacheNodes.indexOf(cacheNode) - 1).addBackupNode(cacheNodes.get(cacheNodes.indexOf(cacheNode) + 1));
        }
        cacheNodes.remove(cacheNode);
        cacheNodeNumber--;
        System.out.println("Node removed");
    }

    private void handleDeadNode(UID id) throws RemoteException
    {
        System.out.println("Removing dead node...");
        CardCacheNode backupNode;
        if (id.toString().hashCode() % cacheNodeNumber == cacheNodeNumber-1)
        {
            backupNode = cacheNodes.get((id.toString().hashCode() % cacheNodeNumber) + 1);
        }
        else
        {
            backupNode = cacheNodes.get(0);
        }
        backupNode.flushSecondary();

        CardCacheNode cacheNode = cacheNodes.get(id.toString().hashCode() % cacheNodeNumber);
        for (CardCacheNode originalCacheNode : cacheNodes)
        {
            try
            {
                if(!originalCacheNode.equals(cacheNode))
                {
                    originalCacheNode.flush();
                }
            }
            catch (RemoteException e)
            {

            }
        }
        if (cacheNodes.indexOf(cacheNode) == cacheNodeNumber - 1)
        {
            cacheNodes.get(cacheNodes.indexOf(cacheNode) - 1).addBackupNode(cacheNodes.get(0));
        }
        else
        {
            cacheNodes.get(cacheNodes.indexOf(cacheNode) - 1).addBackupNode(cacheNodes.get(cacheNodes.indexOf(cacheNode) + 1));
        }
        cacheNodes.remove(cacheNode);
        cacheNodeNumber--;
        System.out.println("Node removed");
    }


}
