package TCP;

import java.io.*;
import java.net.*;
import java.util.Vector;

class TCPServerSocket {
	static Vector<TCPThread> vector = new Vector<>();
	
	

	public static void main(String argv[]) throws Exception {
		String request;
// Création de la socket d'accueil au port 8080
		ServerSocket welcomeSocket = new ServerSocket(8080);
		while (true) {
// Attente d'une demande de connexion sur la socket d'accueil
			Socket connectionSocket = welcomeSocket.accept();

//			 Démarrer la connexion avec le client , dans la classe TCPThread qui permet de
//		  faire plusieurs connexions à la fois
//			new TCPThread(connectionSocket).start();

			System.out.println("New client request received : " + connectionSocket);

			// Création du flux en sortie
						PrintWriter outToServer = new PrintWriter(
						new BufferedWriter(
						new OutputStreamWriter(
						connectionSocket.getOutputStream())),
						true);
						
						
						//Création du flux en entrée
						
						BufferedReader inFromServer = new BufferedReader(
						new InputStreamReader(
						connectionSocket.getInputStream()));

			System.out.println("Connexion du client...");
			String utilisateur = inFromServer.readLine();
			// Create a new handler object for handling this request.
			TCPThread mtch = new TCPThread(connectionSocket, utilisateur, inFromServer, outToServer);
			// Create a new Thread with this object. 
						Thread t = new Thread(mtch); 
						
						System.out.println("Ajout du client à la liste"); 

						// add this client to active clients list 
						vector.add(mtch); 

						// start the thread. 
						t.start(); 
						
						
	
			
		
		} // boucle et attend la connexion d'un nouveau client
		
		
	}
	
	public static void liste (TCPThread thr)
	{
		for (TCPThread cp : TCPServerSocket.vector)
		{
			thr.out.print("["+cp.getUtilisateur()+"] ");
		}
	
	}
	
	public static void miseenr (TCPThread thr1 , TCPThread thr2) throws IOException
	{
		String request;
		while (true)
		{
			
			request = thr1.in.readLine();
			thr2.out.println(thr1.getUtilisateur()+" : "+request);
			
			
		}
	}
	




}

