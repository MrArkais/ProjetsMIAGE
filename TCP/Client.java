package TCP;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Thread;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Client  {

	
	
	public static void main(String argv[]) throws Exception 
	{
		String as;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		ArrayList<String> historique = new ArrayList<String> ();

		
		try  {
			
			Scanner sc = new Scanner(System.in);
			
			System.out.println("Rentrer l'adresse du serveur avec lequel vous souhaitez communiquer : ");
			as = /*"localhost"*/ sc.nextLine();
			
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			 //Création de la socket client, demande de connexion
			
			Socket clientSocket = new Socket(as, 8080);
			
			
			// Création du flux en sortie
			PrintWriter outToServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())),true);
			
			
			//Création du flux en entrée
			
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			System.out.println("Rentrez votre nom :");
			String nom = sc.nextLine();
			outToServer.println(nom);
	
			System.out.println("Veuillez rentrer /cmd pour obtenir la liste des commandes ");

			// sendMessage thread 
			Thread sendMessage = new Thread(new Runnable() 
			{ 
				@Override
				public synchronized void run() { 
					while (true) { 
						try {
							String request;
							request = inFromUser.readLine();
							if(request.startsWith("/historique"))
							{
								System.out.println("Historique des messages reçus:");
								for (String message : historique)
								{
									System.out.println("----- "+message+" -----");
								}
							}
							// Emission des données au serveur
							outToServer.println(request); 
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
				} 
			}); 
			
			// readMessage thread 
			Thread readMessage = new Thread(new Runnable() 
			{ 
				@Override
				public synchronized void run() { 

					while (true) { 
						try { 
							// read the message sent to this client 
							// Lecture des données arrivant du serveur
							String answer = inFromServer.readLine();
							if((!answer.equals("") || !answer.equals(null)) && answer.startsWith("/quit"))
							{
								break;
							}
							if((!answer.equals("") || !answer.equals(null)) && answer.startsWith("/join") )
							{
								outToServer.println("/join "+ answer.substring(6));
							
							}
							else if((!answer.equals("") || !answer.equals(null)) && answer.startsWith("/connectTo") )
							{
								System.out.println("Connecté avec "+answer.substring(11));
							}
							else if((!answer.equals("") || !answer.equals(null)) && answer.startsWith("/logoutTo") )
							{
								System.out.println("Deconnecté de "+answer.substring(10));
							}
							else if((!answer.equals("") || !answer.equals(null)) && answer.startsWith("/hasLeave") )
							{
								System.out.println(answer.substring(10) + " a quitté le serveur !");
								return ;
							}
							else if(!answer.equals("") || !answer.equals(null) )
							{
								String dateReception = LocalDateTime.now().toString();
								System.out.println(answer);
								historique.add(answer);

							}
						} catch (IOException e) { 
							System.out.println("Erreur client :");
							e.printStackTrace(); 
						} 
					} 
				}
				//Quitter le programme et fermer tous les threads de l'utilisateurs
			}); 
			sendMessage.start(); 
			readMessage.start(); 
		}
		
	     catch(IOException e){
	    	 System.err.println("Echec");
	    	 
	     }
	
	
	}
}