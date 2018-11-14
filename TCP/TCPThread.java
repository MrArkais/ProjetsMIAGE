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

public class TCPThread extends Thread {
	private Socket connectionSocket ;
	private String utilisateur; 
	final BufferedReader in; 
	final PrintWriter out;  
	boolean isloggedin; 
	TCPThread tcpth = null;
	
	
	
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
						
						
			
						request = in.readLine();
			while (!request.contains("/logout"))
			{
			
				//out.println("");
				request = in.readLine();
				
				if (request.contains("/liste"))
				{
					TCPServerSocket.liste(this);

				}
				
				else if (tcpth == null)
				{
					String utilisateur = request;
					for (TCPThread tcpth : TCPServerSocket.vector)
					{
						if (tcpth.utilisateur.equals(utilisateur) && tcpth.utilisateur!=this.utilisateur)
						{
							TCPServerSocket.miseenr(this, tcpth);
							tcpth.stop();
							tcpth.setTcpth(this);
							tcpth.start();
						}
						
						else if (tcpth.utilisateur.equals(this.utilisateur))
						{
							this.out.println(" ");
						}

					}
				}
				
				else 
				{
					System.out.println("squalalal");
					TCPServerSocket.miseenr(this, tcpth);
				}
			
				
				
			}
			
						
			
				

			
				
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
	
	



