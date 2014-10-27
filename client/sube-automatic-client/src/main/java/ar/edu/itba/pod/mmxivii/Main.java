package ar.edu.itba.pod.mmxivii;

import ar.edu.itba.pod.mmxivii.sube.common.*;

import javax.annotation.Nonnull;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.CARD_REGISTRY_BIND;
import static ar.edu.itba.pod.mmxivii.sube.common.Utils.CARD_SERVICE_REGISTRY_BIND;
import static ar.edu.itba.pod.mmxivii.sube.common.Utils.delay;

public class Main extends BaseMain
{
	private CardClient cardClient = null;

	private Main(@Nonnull String[] args) throws NotBoundException
	{
		super(args, DEFAULT_CLIENT_OPTIONS);
		getRegistry();
        CardRegistry cardRegistry = Utils.lookupObject(CARD_REGISTRY_BIND);
        CardServiceRegistry cardServiceRegistry = Utils.lookupObject(CARD_SERVICE_REGISTRY_BIND);
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
        final List<Card> registeredCards = new ArrayList<>();

        Runnable loadTester = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Random rand = new Random();
                    boolean create  = rand.nextBoolean();

                    if(create || registeredCards.size() == 0)
                    {
                        final Card card = cardClient.newCard(String.valueOf(rand.nextBoolean()), "tarjeta");
                        registeredCards.add(card);
                        System.out.println(cardClient.getCardBalance(card.getId()));
                        final double recharge = cardClient.recharge(card.getId(), "recharged", rand.nextInt(100));
                        System.out.println("recharged = " + recharge);
                        final double spent = cardClient.travel(card.getId(), "smh", rand.nextInt(100));
                        System.out.println("spent = " + spent);
                    }
                    else
                    {
                        int randomCard = rand.nextInt(registeredCards.size());
                        final Card card = registeredCards.get(randomCard);
                        System.out.println(cardClient.getCardBalance(card.getId()));
                        final double recharge = cardClient.recharge(card.getId(), "recharged", rand.nextInt(100));
                        System.out.println("recharged = " + recharge);
                        final double spent = cardClient.travel(card.getId(), "smh", rand.nextInt(100));
                        System.out.println("spent = " + spent);
                    }
                }
                catch (RemoteException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    System.out.println("Attempted to travel without enough money.");
                }
            }
        };

        final Scanner scan = new Scanner(System.in);
        String line;

        System.out.println("Automatic client started.");
        do {
            try
            {
                delay();
                Thread task = new Thread(loadTester, "Load Tester");
                task.setDaemon(true);
                task.start();
                try {
                    task.join(1000);
                } catch (InterruptedException e) {
            /* if somebody interrupts us he knows what he is doing */
                }
                if (task.isAlive()) {
                    task.interrupt();
                    throw new TimeoutException();
                }
            }
            catch( TimeoutException e)
            {

            }
        } while(true);
    }
}
