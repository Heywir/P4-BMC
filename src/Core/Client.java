package Core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Client extends JFrame implements Runnable {
	
	Socket client;
	JFrame mainWindow;
	JPanel mainPanel;
	JPanel gridPanel;
	JLabel statusCO;
	Scanner reader;
	PrintWriter writer;
	String reponse;
	Grid grid;
	Boolean done = false;
	String joueur;
	String etat;
	
	Client() {
		
		// Composants
		mainWindow = new JFrame("Client");
		mainPanel = new JPanel();
		statusCO = new JLabel("Status", SwingConstants.CENTER);
		
		// Parametres
		mainWindow.setResizable(false);
		mainWindow.setSize(735, 760);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		statusCO.setText("En attente de Connexion");
		
		// Layout
		BorderLayout layout = new BorderLayout();
		mainPanel.setLayout(layout);
		
		try {
			
			// Tentative de creation de socket
			client = new Socket(InetAddress.getByName("127.0.0.1"), 9090);	
			
			// Si succes alors affichage de fenetre;
			mainPanel.add(statusCO, BorderLayout.NORTH);
			mainWindow.add(mainPanel);
			mainWindow.setVisible(true);
			
			open();
			
			//Premiere ecoute pour savoir numero joueur
			startListening("player");
			
			//Envoyer que pret au worker
			sendToW("READY");
			
			//Attente de la reponse du worker pour demarrer
			startListening("start");
			
			//Debut de partie, ecoute si play ou attente
			startListening("game");
			
			//Update grid si c'était pas notre tour
			//startListening("check");
			
			System.out.println("Fin de code");
			
			//stop();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void open() throws IOException {
		// Writer
		writer = new PrintWriter(client.getOutputStream(), true);
		// Reader
		reader = new Scanner(client.getInputStream());
	}
	
	
	public void stop() {
		writer.close();
		reader.close();
	}
	
	public void startListening(String index) throws IOException {
		reponse = null;
		if (index.equals("player")) {
			//On attend de savoir qui on est
			while (true) {
				if (reader.hasNextLine()) {
					reponse = reader.nextLine();
					System.out.println(reponse);
					if (reponse.equals("COMMAND#P1")) {
						statusCO.setForeground(Color.RED);
						joueur = "Joueur 1";
						statusCO.setText(joueur);
						break;
					}
					else if (reponse.equals("COMMAND#P2")){
						statusCO.setForeground(Color.ORANGE);
						joueur = "Joueur 2";
						statusCO.setText(joueur);
						break;
					}
				}
			}
		}
		else if (index.equals("start")) {
			while (true) {
				if (reader.hasNextLine()) {
					reponse = reader.nextLine();
					System.out.println(reponse);
					if (reponse.equals("COMMAND#START")) {
						System.out.println("Start partie");
						grid = new Grid(7,7);
						grid.joueur = joueur;
						mainPanel.add(grid, BorderLayout.CENTER);
						mainPanel.revalidate();
						break;
					}
				}
			}
		}
		else if (index.equals("game")) {
			
			if (joueur.equals("Joueur 1")) {
				etat = "PLAYING";
				grid.etat = etat;
				statusCO.setText(joueur + " " + etat);
			}
			else if (joueur.equals("Joueur 2")) {
				etat = "WAITING";
				grid.etat = etat;
				statusCO.setText(joueur + " " + etat);
			}
			
			if (etat.equals("PLAYING")) {
				Thread t = new Thread(this);
				t.start();
			}
			
		}
	}
	
	public void sendToW(String message) throws IOException {
		String full = "COMMAND#" + message;
		writer.println(full);
		System.out.println(full + " Envoye");

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (etat.equals("PLAYING")) {
			try {
				Thread.sleep(250);
				etat = grid.etat;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			System.out.println("J'ai joué");
			
			//Spamm pour debug
			for (int i = 0; i <6; i++) {
				Thread.sleep(550);
				sendToW("DONE" + ";" + grid.lastCol + ";" + grid.lastRow);
			}
			statusCO.setText(joueur + " " + etat);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		Client client = new Client();
	}
}
