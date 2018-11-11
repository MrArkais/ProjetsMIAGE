package TCP;

import java.io.*;
import java.net.*;
import java.util.Vector;

class TCPServerSocket {
	static Vector<TCPThread> vector = new Vector<>();
	
	

	public static void main(String argv[]) throws Exception {
		String request;
// Cr�ation de la socket d'accueil au port 8080
		ServerSocket welcomeSocket = new ServerSocket(8080);
		while (true) {
// Attente d'une demande de connexion sur la socket d'accueil
			Socket connectionSocket = welcomeSocket.accept();

//			 D�marrer la connexion avec le client , dans la classe TCPThread qui permet de
//		  faire plusieurs connexions � la fois
//			new TCPThread(connectionSocket).start();

			System.out.println("New client request received : " + connectionSocket);

			// Cr�ation du flux en sortie
						PrintWriter outToServer = new PrintWriter(
						new BufferedWriter(
						new OutputStreamWriter(
						connectionSocket.getOutputStream())),
						true);
						
						
						//Cr�ation du flux en entr�e
						
						BufferedReader inFromServer = new BufferedReader(
						new InputStreamReader(
						connectionSocket.getInputStream()));

			System.out.println("Connexion du client...");
			String utilisateur = inFromServer.readLine();
			// Create a new handler object for handling this request.
			TCPThread mtch = new TCPThread(connectionSocket, utilisateur, inFromServer, outToServer);
			// Create a new Thread with this object. 
						Thread t = new Thread(mtch); 
						
						System.out.println("Ajout du client � la liste"); 

						// add this client to active clients list 
						vector.add(mtch); 

						// start the thread. 
						t.start(); 
						
	
			
		
		} // boucle et attend la connexion d'un nouveau client

	}

}