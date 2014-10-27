package ar.edu.itba.pod.mmxivii.sube.balancer;

import ar.edu.itba.pod.mmxivii.sube.common.CardService;
import ar.edu.itba.pod.mmxivii.sube.common.CardServiceRegistry;

import javax.annotation.Nonnull;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CardServiceRegistryImpl extends UnicastRemoteObject implements CardServiceRegistry
{
	private static final long serialVersionUID = 2473638728674152366L;
	private final List<CardService> serviceList;

	protected CardServiceRegistryImpl() throws RemoteException
    {
        serviceList = Collections.singletonList((CardService) new LoadBalancingCardService());
    }

	@Override
	public void registerService(@Nonnull CardService service) throws RemoteException
	{
		// Do nothing
	}

	@Override
	public void unRegisterService(@Nonnull CardService service) throws RemoteException
	{
		// Do nothing
	}

	@Override
	public Collection<CardService> getServices() throws RemoteException
	{
		return serviceList;
	}
}
