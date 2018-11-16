package TCP;
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/* Pour ce programme thread , au vu de l'architecture (je me suis rendu compte que il aurait mieux valu
 *  travailler avec un objet client) , 
 * j'ai �t� oblig� de transmettre des commandes 
 * selon certaines conditions aux clients pour 
permettre de modifier le comportement des autres threads*/

public class TCPThread extends Thread {
	private Socket connectionSocket ;
	private String utilisateur; 
	final BufferedReader in; 
	final PrintWriter out;  
	boolean isloggedin; 
	TCPThread tcpth = null;
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
		String request;
		try {
			// Cr�ation du flux en entr�e attache a la socket
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			// Cr�ation du flux en sortie attache a la socket
			PrintWriter outToClient;

			outToClient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream())), true);
			// request = in.readLine();
			do 
			{
				request = "";
				try {
					request = in.readLine();
					//out.println("");
					Date maDate = new Date();
					String dateEnvoie = maDate.toString(); //LocalDateTime.now().toString();
					historique.add(dateEnvoie +" : "+request);
					out.println(dateEnvoie +" MOI : "+request);

					if (request.startsWith("/cmd"))
					{
						out.println(" /liste : affiche les utilisateurs connect�");
						out.println(" /historique : affiche tous vos messages envoy�s");
						out.println(" /join utilisateur : vous connecte � un utilisateur");
						out.println(" /quit : quitte l'application ");
						out.println(" /logout : quitte le channel priv�");
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
						out.println("Historique des messages :");
						for (String msg : historique)
						{
							out.println(msg);
						}
					}
					else if (tcpth == null && request.startsWith("/join"))
					{
						System.out.println("requete envoy�e " +request);
						String date = LocalDateTime.now().toString();
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
										//occup�
										out.println("Votre correspondant n'est pas disponible pour le moment.");
									 }else {
										out.println("Vous �tes d�j� connect� avec "+tcpth.utilisateur);
									 }
								}
								else if(!TCPServerSocket.map.containsValue(tcpth.utilisateur))
								{
									//si pas en conv avec moi meme
									//Ajout dans mon dictionnaire (pour salon priv�) de moi et mon thread partenaire
									TCPServerSocket.map.put(tcpth.utilisateur, this.utilisateur);
									TCPServerSocket.map.put(this.utilisateur, tcpth.utilisateur);
									//Pour "forcer" le partenaire � se connecter
									tcpth.out.println("/connectTo "+this.utilisateur);
									this.out.println("Connect� avec "+tcpth.utilisateur);
								}
								else 
								{
									this.out.println("Un probl�me est survenu !");
								}
							}
						}
						if(!utilisateurTrouve) {
							this.out.println("Aucun utilisateur de ce nom ! /liste pour obtenir les utilisateurs connect�s.");
						}
					}
					else if(request.startsWith("/logout"))
					{
						//Fonction pour quitter la conversation priv�e 
						if(TCPServerSocket.map.containsValue(this.utilisateur)) {
							this.out.println("D�connect� de "+TCPServerSocket.map.get(this.utilisateur));
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
							this.out.println("Vous n'avez aucune connexion priv�e pour le moment");
						}
					}
					else if(request.startsWith("/quit"))
					{
						if(TCPServerSocket.map.containsValue(this.utilisateur)) {
							this.out.println("D�connect� de "+TCPServerSocket.map.get(this.utilisateur));
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
						}
						for (TCPThread tcpth : TCPServerSocket.vector)
						{
							tcpth.out.println("/hasLeave "+this.utilisateur);
						}
						// Couper les liaisons Thread et TCP
						TCPThread toDelete = null;
						for (TCPThread tcpth : TCPServerSocket.vector)
						{
							if(tcpth.utilisateur.equals(this.utilisateur))
								toDelete = tcpth;
						}
						TCPServerSocket.vector.remove(toDelete);
						break;
					}
					else 
					{
						//discution
						//Si tu parle avec une personne 
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
			}while (true);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			connectionSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





	public void setTcpth(TCPThread tcpth) {
		this.tcpth = tcpth;
	} 
}

