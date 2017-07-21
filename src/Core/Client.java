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
	int lastRow;
	int lastCol;
	
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
			
			//Update grid si c'�tait pas notre tour
			startListening("check");
			
			System.out.println("Fin de code");

			
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
	
	
	public void close() {
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

			Thread t = new Thread(this);
			t.start();

			
		}
		else if (index.equals("check")) {
			while (true) {
				if (reader.hasNextLine()) {
					String reponse = reader.nextLine();
					if (reponse.contains("COMMAND#PLAYED")) {
						String split2[] = reponse.split(";");
						lastRow = Integer.parseInt(split2[2]);
						lastCol = Integer.parseInt(split2[1]);
						System.out.println("Le joueur a joue: " + lastRow + " " + lastCol);
						grid.fillThatCaseForMe(lastRow, lastCol);
						etat = "PLAYING";
						statusCO.setText(joueur + " " + etat);
						Thread t = new Thread(this);
						t.start();
					}
					else if (reponse.equals("COMMAND#ANYWIN")) {
						if (grid.isThereAnyWinner()) {
							if (joueur.equals(grid.winner)) {
								statusCO.setText(joueur + " WINNER" );
								etat = "DONE";
								grid.clean();
								break;
							}
							else {
								statusCO.setText(joueur + " LOOSER");
								etat = "DONE";
								grid.clean();
								break;
							}
						}
						else {
							System.out.println("No winner");
						}
					}
					else if (reponse.equals("COMMAND#DRAW")) {
						statusCO.setText(joueur + " " + "DRAW");
						etat = "DONE";
						grid.clean();
						break;
					}
				}
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
		while (true) {
			if (etat.equals("PLAYING")) {
				try {
					Thread.sleep(250);
					etat = grid.etat;
					if (etat.equals("WAITING")) {
						sendToW("DONE" + ";" + grid.lastRow + ";" + grid.lastCol);
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (etat.equals("WAITING")) {
			System.out.println("Playing vers Waiting");
			statusCO.setText(joueur + " WAITING");
		}
		System.out.println("J'ai jou�");
	}
	
	public static void main(String [] args) {
		Client client = new Client();
	}
}
