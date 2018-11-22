package TCP;
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* Pour ce programme thread , au vu de l'architecture (je me suis rendu compte que il aurait mieux valu
 *  travailler avec un objet client) , 
 * j'ai été obligé de transmettre des commandes 
 * selon certaines conditions aux clients pour 
permettre de modifier le comportement des autres threads*/

public class TCPThread extends Thread {
	private Socket connectionSocket ;
	private String utilisateur; 
	final BufferedReader in; 
	final PrintWriter out;  
	boolean isloggedin = true; 
	ArrayList<String> historique = new ArrayList<String> ();


	public TCPThread(Socket connectionSocket, String name, BufferedReader in, PrintWriter out) {
		super();
		this.connectionSocket = connectionSocket;
		this.utilisateur = name;
		this.in = in;
		this.out = out;
		this.isloggedin = true;
	}


	public String getUtilisateur() {
		return utilisateur;
	}
	


	public synchronized void run()
	{
		while (isloggedin)
		{
			
		
		String request;
		try {
			// Création du flux en entrée attache a la socket
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			// Création du flux en sortie attache a la socket
			PrintWriter outToClient;

			outToClient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream())), true);
			
				request = "";
				try {
					request = in.readLine();
					Date madate = new Date();
					DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
							DateFormat.SHORT,
							DateFormat.SHORT, new Locale("FR","fr"));
					String dateEnvoie = shortDateFormat.format(madate);
					historique.add(dateEnvoie +" : "+request);

					if (request.startsWith("/cmd"))
					{
						out.println(" /liste : affiche les utilisateurs connectés");
						out.println(" /historique : affiche tous vos messages envoyés");
						out.println(" /join utilisateur : vous connecte à un utilisateur");
						out.println(" /quit : quitte l'application ");
						out.println(" /logout : quitte le chanel privé");
					}
					else if (request.startsWith("/liste"))
					{
						//Foreach de parcours du vecteur stockant mes clients (autres threads)
						for (TCPThread tcpth : TCPServerSocket.vector)
						{
							out.println(tcpth.getUtilisateur());
						}
					}
					else if (request.startsWith("/historique"))
					{
						out.println("Historique des messages envoyés :");
						for (String msg : historique)
						{
							out.println("----- "+msg+" -----");
						}
					}
					else if (request.startsWith("/join"))
					{
						System.out.println("requete envoyée " +request);
						//String date = LocalDateTime.now().toString();
						Calendar calendar = Calendar.getInstance(); 
						String utilisateurCible = request.substring(6);
						System.out.println("Connexion de "+ this.utilisateur +" vers "+utilisateurCible);
						boolean utilisateurTrouve = false;
						
						for (TCPThread tcpth : TCPServerSocket.vector)
						{
						
						 if (tcpth.utilisateur.equals(utilisateurCible))
							{
								utilisateurTrouve = true;
								
								//Si deja en conv avec qqun d'autre
								if(TCPServerSocket.map.containsValue(tcpth.utilisateur))
								{
									 if(!TCPServerSocket.map.get(tcpth.utilisateur).equals(this.utilisateur)) {
										//occupé
										out.println("Votre correspondant n'est pas disponible pour le moment.");
										tcpth.out.println(this.utilisateur+" à essayé de se connecter avec vous !");
									 }else {
										out.println("Vous êtes déjà connecté avec "+tcpth.utilisateur);
									 }
								}
								else if(!TCPServerSocket.map.containsValue(tcpth.utilisateur))
								{
									//si pas en conv avec moi meme
									//Ajout dans mon dictionnaire (pour salon privé) de moi et mon thread partenaire
									TCPServerSocket.map.put(tcpth.utilisateur, this.utilisateur);
									TCPServerSocket.map.put(this.utilisateur, tcpth.utilisateur);
									//Pour "forcer" le partenaire à se connecter
									tcpth.out.println("/connectTo "+this.utilisateur);
									this.out.println("Connecté avec "+tcpth.utilisateur);
								}
								else 
								{
									this.out.println("Un problème est survenu !");
								}
							}
						}
						if(!utilisateurTrouve) {
							this.out.println("Aucun utilisateur de ce nom ! /liste pour obtenir les utilisateurs connectés.");
						}
					}
					else if(request.startsWith("/logout"))
					{
						//Fonction pour quitter la conversation privée 
						if(TCPServerSocket.map.containsValue(this.utilisateur)) {
							this.out.println("Déconnecté de "+TCPServerSocket.map.get(this.utilisateur));
							for (TCPThread tcpth : TCPServerSocket.vector)
							{
								if(tcpth.utilisateur.equals(TCPServerSocket.map.get(this.utilisateur)))
								{
									tcpth.out.println("/logoutTo "+this.utilisateur);
									break;
								}
							}
							TCPServerSocket.map.remove(TCPServerSocket.map.get(this.utilisateur));
							TCPServerSocket.map.remove(this.utilisateur);
						}else {
							this.out.println("Vous n'avez aucune connexion privée pour le moment");
						}
					}
					else if(request.startsWith("/quit"))
					{
						for (TCPThread tcpth : TCPServerSocket.vector)
						{
							tcpth.out.println(this.utilisateur+" viens de s'en aller du serveur !");
						}
						TCPServerSocket.vector.remove(this);
						isloggedin = false;

					}
					else 
					{
						//discussion
						//Si tu parles avec une personne 
						if(TCPServerSocket.map.containsValue(this.utilisateur)) {
							for (TCPThread tcpth : TCPServerSocket.vector)
							{
								if(tcpth.utilisateur.equals(TCPServerSocket.map.get(this.utilisateur)))
								{
									tcpth.out.println(dateEnvoie +" [PRIVE] "+this.utilisateur+" : " +request);
								}
							}
						}else {
							//Avec tous le monde
							for (TCPThread tcpth : TCPServerSocket.vector)
							{
								if(!tcpth.utilisateur.equals(this.utilisateur))
								{
									tcpth.out.println(dateEnvoie +" [PUBLIC] "+this.utilisateur+" : " +request);
								}
							}
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		}
		
		
	}






}

