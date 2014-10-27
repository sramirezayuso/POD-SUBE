package ar.edu.itba.pod.mmxivii;

import ar.edu.itba.pod.mmxivii.sube.common.*;

import javax.annotation.Nonnull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        Map<String, UID> ids = new HashMap<>();
        System.out.println("Starting Server!");
        final Scanner scan = new Scanner(System.in);
        String line;
        do {
            line = scan.nextLine();
            if (line.startsWith("Create Card"))
            {
                Card newCard = cardClient.newCard(line.split(" ")[2], line.split(" ")[3]);
                ids.put(newCard.getId().toString(), newCard.getId());
                System.out.println("New Card " + newCard.getId().toString());
            }
            else if (line.startsWith("Get Balance"))
            {
                System.out.println(cardClient.getCardBalance(ids.get(line.split(" ")[2])));
            }
            else if (line.startsWith("Recharge"))
            {
                System.out.println(cardClient.recharge(ids.get(line.split(" ")[1]), line.split(" ")[2], Double.valueOf(line.split(" ")[3])));
            }
            else if (line.startsWith("Travel"))
            {
                System.out.println(cardClient.recharge(ids.get(line.split(" ")[1]), line.split(" ")[2], Double.valueOf(line.split(" ")[2])));
            }
        } while(!"x".equals(line));
        System.out.println("Server exit.");
        System.exit(0);
	}
}
