/*
* (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
* law. All use of this software is subject to MuleSoft's Master Subscription Agreement
* (or other master license agreement) separately entered into in writing between you and
* MuleSoft. If such an agreement is not in place, you may not use the software.
*/

package ar.edu.itba.pod.mmxivii.sube.client;

import ar.edu.itba.pod.mmxivii.sube.common.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.CARD_REGISTRY_BIND;
import static ar.edu.itba.pod.mmxivii.sube.common.Utils.checkNotNull;

public class InteractiveCardClient extends UnicastRemoteObject implements CardClient
{
    private static final long serialVersionUID = 3498345765116694167L;
    private CardRegistry cardRegistry;
    private final CardService loadBalancer;

    public InteractiveCardClient(@Nonnull CardRegistry cardRegistry, @Nonnull CardServiceRegistry cardServiceRegistry) throws RemoteException
    {
        super();
        this.cardRegistry = cardRegistry;
        this.loadBalancer = new ArrayList<CardService>(cardServiceRegistry.getServices()).get(0);
    }

    @Nonnull
    @Override
    public Card newCard(@Nonnull String cardHolder, @Nonnull String label) throws RemoteException
    {
        try {
            return cardRegistry.newCard(cardHolder, label);
        } catch (ConnectException e) {
            try {
                cardRegistry = Utils.lookupObject(CARD_REGISTRY_BIND);
                return cardRegistry.newCard(cardHolder, label);
            } catch (NotBoundException e1) {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public Card getCard(@Nonnull UID id) throws RemoteException
    {
        try {
            return cardRegistry.getCard(checkNotNull(id));
        } catch (ConnectException e) {
            try {
                cardRegistry = Utils.lookupObject(CARD_REGISTRY_BIND);
                return cardRegistry.getCard(checkNotNull(id));
            } catch (NotBoundException e1) {
                return null;
            }
        }
    }

    @Override
    public double getCardBalance(@Nonnull UID id) throws RemoteException
    {
        return loadBalancer.getCardBalance(id);
    }

    @Override
    public double travel(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        return loadBalancer.travel(id, description, amount);
    }

    @Override
    public double recharge(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException
    {
        return loadBalancer.recharge(id, description, amount);
    }
}
