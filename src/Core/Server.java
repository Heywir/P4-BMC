package Core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.text.BadLocationException;

public class Server {
	
	ServerSocket ss;
	Socket sc1;
	Socket sc2;
	ClientWorker cw;
	ServerUI ui;

	public Server() throws BadLocationException {
	
		try {
			
			ss = new ServerSocket(9090, 10, InetAddress.getByName("127.0.0.1"));
			ui = new ServerUI();
			while (true) {	
				ui.addText("Serveur: Attente de connexion");
				sc1 = ss.accept();
				ui.addText("Serveur: Connexion du premier client: " + sc1.toString());
				sc2 = ss.accept();
				ui.addText("Serveur: Connexion du deuxième client: " + sc2.toString());
				ui.addText("Serveur: Creation du Worker");
				cw = new ClientWorker(sc1, sc2, ui);
				Thread t = new Thread(cw);
				t.start();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String [] args) throws BadLocationException {
		Server s1 = new Server();
	}

}


