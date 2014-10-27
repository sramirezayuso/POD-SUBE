package ar.edu.itba.pod.mmxivii;

import ar.edu.itba.pod.mmxivii.sube.common.*;

import javax.annotation.Nonnull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.*;

public class Main extends BaseMain
{
	private CardClient cardClient = null;

	private Main(@Nonnull String[] args) throws NotBoundException
	{
		super(args, DEFAULT_CLIENT_OPTIONS);
		getRegistry();
        CardRegistry cardRegistry = lookupObject(CARD_REGISTRY_BIND);
        CardServiceRegistry cardServiceRegistry = lookupObject(CARD_SERVICE_REGISTRY_BIND);
        try
        {
            cardClient = new InteractiveCardClient(cardRegistry, cardServiceRegistry);
        }
        catch (RemoteException e)
        {

        }
	}

	public static void main(@Nonnull String[] args ) throws Exception
	{
		final Main main = new Main(args);
		main.run();
	}

	private void run() throws RemoteException
	{
		System.out.println("Main.run");
		final Card card = cardClient.newCard("alumno", "tarjeta");
        System.out.println(cardClient.getCardBalance(card.getId()));
        final double primero = cardClient.recharge(card.getId(), "primero", 100);
		System.out.println("primero = " + primero);
		final double bondi = cardClient.travel(card.getId(), "bondi", 3);
		System.out.println("bondi = " + bondi);
//		cardClient.newCard()
	}
}
