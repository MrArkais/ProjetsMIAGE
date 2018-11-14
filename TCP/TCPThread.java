package TCP;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
		String request ;
		
		
		try {
	// Création du flux en entrée attache a la socket
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	// Création du flux en sortie attache a la socket
					PrintWriter outToClient;
					
						outToClient = new PrintWriter(
							new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream())), true);
			
		// Lecture des données arrivant du client
//					do {
//						
//						
//						request = in.readLine();
//
//					
//
//							// Récuperer IP Client
//							SocketAddress IP = connectionSocket.getLocalSocketAddress();
//							// Emission des données au client
//							System.out.println(IP + " - " + request.toUpperCase());
//							out.println(request.toUpperCase());
//					
//							if (request.contains("/liste"))
//							{
//								for (TCPThread cp : TCPServerSocket.vector)
//								{
//									out.print(cp.getUtilisateur());
//								}
//							}
//							
//					
//			
//		
//					} while (!request.contains("STOP"));
						
						
			
						// request = in.readLine();
			do 
			{
			
				//out.println("");
				request = in.readLine();
				historique.add(request);
				
				if (request.contains("/liste"))
				{
					for (TCPThread tcpth : TCPServerSocket.vector)
					{
						out.println(tcpth.getUtilisateur());
					}
				}
				
				else if (request.contains("/historique"))
				{
					out.println("Historique des messages :");
					for (String msg : historique)
					{
						out.println(msg);
					}
				}
				else if (tcpth == null)
				{
					String utilisateur = request;
					for (TCPThread tcpth : TCPServerSocket.vector)
					{
						System.out.println("requete envoyée " +request);
						// break the string into message and recipient part 
						StringTokenizer st = new StringTokenizer(request, ":"); 
						String date = st.nextToken(); 
						String recipient = st.nextToken(); 
						
						System.out.println("recipient " +recipient);

						if (tcpth.utilisateur.equals(recipient))
						{
							//TCPServerSocket.miseenr(this, tcpth);
							while (true)
							{
								request = in.readLine();
								tcpth.out.println(this.utilisateur+" : " +request);
							}
						}
						
						else if (tcpth.utilisateur.equals(this.utilisateur))
						{
							this.out.println("Connexion Impossible");
						}

					}
				}
				
				else 
				{
					
				}
			
				
				
			} while (!request.contains("/logout"));
			
						
			
				

			
				
		} catch (IOException e) {
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
	
	