package TCP;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class TCPServerSocket {
	static Vector<TCPThread> vector = new Vector<>();
	static Vector<TCPThread> sl = new Vector<>();
	static Map<String, String> map = new HashMap<String, String>();

	public static void main(String argv[]) throws Exception {
		String request;
		// Création de la socket d'accueil au port 8080
		ServerSocket welcomeSocket = new ServerSocket(8080);
		while (true) {
			try {
				// Attente d'une demande de connexion sur la socket d'accueil
				Socket connectionSocket = welcomeSocket.accept();

				//Démarrer la connexion avec le client , dans la classe TCPThread qui permet de
				//faire plusieurs connexions à la fois

				System.out.println("Nouvelle requete de connexion reçue : " + connectionSocket);

				// Création du flux en sortie
				PrintWriter outToServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream())),true);

				//Création du flux en entrée
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

				System.out.println("Connexion du client...");
				String utilisateur = inFromServer.readLine();
				// Creation nouveau objet de type TCPThread pour ce client.
				TCPThread mtch = new TCPThread(connectionSocket, utilisateur, inFromServer, outToServer);
				// Creation de t, un Thread pour le démarrage. 
				Thread t = new Thread(mtch); 

				System.out.println("Ajout du client à la liste"); 

				// add this client to active clients list 
				vector.add(mtch); 

				// start the thread. 
				t.start(); 
			}catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Une erreur est survenu avec un client !"); 
				e.printStackTrace();
			}
		}
	} // boucle et attend la connexion d'un nouveau client
}



