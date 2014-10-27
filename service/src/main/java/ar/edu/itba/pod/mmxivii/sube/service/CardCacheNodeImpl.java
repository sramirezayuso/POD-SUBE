/*
* (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
* law. All use of this software is subject to MuleSoft's Master Subscription Agreement
* (or other master license agreement) separately entered into in writing between you and
* MuleSoft. If such an agreement is not in place, you may not use the software.
*/

package ar.edu.itba.pod.mmxivii.sube.service;

import ar.edu.itba.pod.mmxivii.sube.common.CardCacheNode;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;

import javax.annotation.Nonnull;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CardCacheNodeImpl extends UnicastRemoteObject implements CardCacheNode
{
    private static final long serialVersionUID = 2473632348674152366L;
    private CardRegistry cardRegistry;

    private Map<UID, List<Operation>> cardOperations;
    private Map<UID, Double> cachedCardOperationAggregates;
    private Map<UID, Double> cachedCardValues;

    public CardCacheNodeImpl(CardRegistry cardRegistry) throws RemoteException
    {
        this.cardRegistry = cardRegistry;
        this.cardOperations = new HashMap<>();
        this.cachedCardOperationAggregates = new HashMap<>();
        this.cachedCardValues = new HashMap<>();
    }

    @Override
    public double getCardBalance(@Nonnull UID id) throws RemoteException
    {
        try
        {
            if (cachedCardValues.containsKey(id))
            {
                return cachedCardValues.get(id) + cachedCardOperationAggregates.get(id);
            }
            else
            {
                double balance = cardRegistry.getCardBalance(id);
                cachedCardValues.put(id, balance);
                cachedCardOperationAggregates.put(id, 0d);
                return balance;
            }
        }
        catch (RemoteException e)
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public double travel(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        String[] s = Double.toString(amount).split("\\.");
        if (amount > 100 || amount < 1 || s[s.length - 1].length() > 2)
        {
            throw new IllegalArgumentException();
        }
        try
        {
            if (cachedCardValues.containsKey(id))
            {
                if (amount > cachedCardValues.get(id))
                {
                    throw new IllegalArgumentException();
                }
                double newAmount = cachedCardOperationAggregates.get(id) - amount;
                cachedCardOperationAggregates.put(id, newAmount);
                return cachedCardValues.get(id) + newAmount;
            }
            else
            {
                double balance = cardRegistry.getCardBalance(id);
                if (amount > balance)
                {
                    throw new IllegalArgumentException();
                }
                cachedCardValues.put(id, balance);
                cachedCardOperationAggregates.put(id, 0d - amount);
                if (!cardOperations.containsKey(id))
                {
                    cardOperations.put(id, new LinkedList<Operation>());
                }
                cardOperations.get(id).add(new Operation(description, -amount));
                return balance - amount;
            }
        }
        catch (RemoteException e)
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public double recharge(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        String[] s = Double.toString(amount).split("\\.");
        if (amount > 100 || s[s.length - 1].length() > 2)
        {
            throw new IllegalArgumentException();
        }
        try
        {
            if (cachedCardValues.containsKey(id))
            {
                double newAmount = cachedCardOperationAggregates.get(id) + amount;
                cachedCardOperationAggregates.put(id, newAmount);
                return cachedCardValues.get(id) + newAmount;
            } else
            {
                double balance = cardRegistry.getCardBalance(id);
                cachedCardValues.put(id, balance);
                cachedCardOperationAggregates.put(id, 0d + amount);
                if (!cardOperations.containsKey(id))
                {
                    cardOperations.put(id, new LinkedList<Operation>());
                }
                cardOperations.get(id).add(new Operation(description, amount));
                return balance + amount;
            }
        } catch (RemoteException e)
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void flush() throws RemoteException
    {
        Set<UID> writeIds = cardOperations.keySet();
        for (UID id : writeIds)
        {
            try
            {
                for (Operation operation : cardOperations.get(id))
                {
                    cardRegistry.addCardOperation(id, operation.getDescription(), operation.getAmount());
                }
                cardOperations.remove(id);
                cachedCardOperationAggregates.remove(id);
                cachedCardValues.remove(id);
            }
            catch (RemoteException e)
            {

            }
        }
    }

    private class Operation
    {
        private String description;
        private double amount;

        public Operation(String description, double amount)
        {
            this.description = description;
            this.amount = amount;
        }

        public String getDescription()
        {
            return description;
        }

        public double getAmount()
        {
            return amount;
        }
    }
}
