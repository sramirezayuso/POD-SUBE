package ar.edu.itba.pod.mmxivii.sube.common;

import javax.annotation.Nonnull;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.List;
import java.util.Map;

public interface CardCacheNode extends Remote
{
    public double getCardBalance(@Nonnull UID id) throws RemoteException;

    public double travel(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException;

    public double recharge(@Nonnull UID id, @Nonnull String description, double amount) throws RemoteException;

    public void addBackupNode(CardCacheNode cacheNode) throws RemoteException;

    public void backup(Map<UID, List<Operation>> backupCardOperations, Map<UID, Double> backupCachedCardOperationAggregates, Map<UID, Double> backupCachedCardValues) throws RemoteException;

    public void flush() throws RemoteException;

    public void flushSecondary() throws RemoteException;
}
