package TCP;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Thread;
import java.net.Socket;
import java.util.Scanner;

public class Client  {

	
	
	public static void main(String argv[]) throws Exception 
	{
		String request = "";
		String answer = "test";
		String as;
	

		
		try  {
			
			Scanner sc = new Scanner(System.in);
			
			//System.out.println("Rentrer l'adresse du serveur avec lequel vous souhaitez communiquer : ");
			as = "localhost" ;//sc.nextLine();
			
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			 //Création de la socket client, demande de connexion
			
			Socket clientSocket = new Socket(as, 8080);
			
			
			// Création du flux en sortie
			PrintWriter outToServer = new PrintWriter(
			new BufferedWriter(
			new OutputStreamWriter(
			clientSocket.getOutputStream())),
			true);
			
			
			//Création du flux en entrée
			
			BufferedReader inFromServer = new BufferedReader(
			new InputStreamReader(
			clientSocket.getInputStream()));
			
			System.out.println("Rentrez votre nom :");
			String nom = sc.nextLine();
			outToServer.println(nom);
			
//			while (!request.contains("STOP"))
//			{
//				System.out.println("Veuillez rentrer /liste pour afficher la liste des utilisateurs ");
//				request = inFromUser.readLine();
//
//				
//			// Emission des données au serveur
//			outToServer.println(request);
//			
//			// Lecture des données arrivant du serveur
//			answer = inFromServer.readLine();
//			System.out.println("FROM SERVER: " + answer);
//			
//			}
//			

			// sendMessage thread 
			Thread sendMessage = new Thread(new Runnable() 
			{ 
				@Override
				public synchronized void run() { 
					while (true) { 

						// read the message to deliver. 
						//System.out.println("Veuillez rentrer /liste pour afficher la liste des utilisateurs, ou taper le nom de l'utilisateur avec lequel vous souhaitez communiquer : ");
						outToServer.println(""); 

						try {
							String request;
							request = inFromUser.readLine();
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
							
							if(answer!=" " || answer!= null || !answer.isEmpty())
							{
								System.out.println(answer);

							}
							
						} catch (IOException e) { 

							e.printStackTrace(); 
						} 
					} 
				} 
			}); 

			sendMessage.start(); 
			readMessage.start(); 
				
		}
		
	     catch(IOException e){
	    	 System.err.println("Echec");
	    	 
	     }
	
	
	}
}
