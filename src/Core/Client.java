package Core;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

class Client extends JFrame implements Runnable {

    private final JFrame mainWindow;
    private final JPanel mainPanel;
	private final JLabel statusCO;
    String again;
    private Socket client;
    private Scanner reader;
    private PrintWriter writer;
	private Grid grid;
	private String joueur;
	private String etat;
    private Thread t;

	private Client() {
		
		// Composants
        mainWindow = new JFrame("Client");
        mainPanel = new JPanel();
		statusCO = new JLabel("Status", SwingConstants.CENTER);
		
		// Parametres
		mainWindow.setResizable(false);
		mainWindow.setSize(735, 760);
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainPanel.setBackground(Color.black);

        //Label
        statusCO.setForeground(Color.white);
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

            System.out.println("FIN DE CODE");

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

    public static void main(String[] args) {
        Client client = new Client();
    }

    private void open() throws IOException {
        // Writer
		writer = new PrintWriter(client.getOutputStream(), true);
		// Reader
		reader = new Scanner(client.getInputStream());
	}

    private void close() throws IOException {
        client.close();
        writer.close();
		reader.close();
	}

    private void startListening(String index) throws IOException {
        String reponse1;
		switch (index) {
			case "player":
				//On attend de savoir qui on est
				while (true) {
					if (reader.hasNextLine()) {
						reponse1 = reader.nextLine();
						System.out.println(reponse1);
						if (reponse1.equals("COMMAND#P1")) {
							statusCO.setForeground(Color.RED);
							joueur = "Joueur 1";
							statusCO.setText(joueur);
							break;
						} else if (reponse1.equals("COMMAND#P2")) {
							statusCO.setForeground(Color.ORANGE);
							joueur = "Joueur 2";
							statusCO.setText(joueur);
							break;
						}
					}
				}
				break;
			case "start":
				while (true) {
					if (reader.hasNextLine()) {
						reponse1 = reader.nextLine();
						System.out.println(reponse1);
						if (reponse1.equals("COMMAND#START")) {
							System.out.println("Start partie");
                            grid = null;
                            grid = new Grid();
                            statusCO.setText(joueur);
                            grid.joueur = joueur;
							mainPanel.add(grid, BorderLayout.CENTER);
							mainPanel.revalidate();
                            startListening("game");
                            break;
						}
					}
				}
				break;
			case "game":

				if (joueur.equals("Joueur 1")) {
					etat = "PLAYING";
					grid.etat = etat;
					statusCO.setText(joueur + " " + etat);
				} else if (joueur.equals("Joueur 2")) {
					etat = "WAITING";
					grid.etat = etat;
					statusCO.setText(joueur + " " + etat);
				}

                if (t == null) {
                    t = new Thread(this);
                    t.start();
                }

				break;
			case "check":
				while (true) {
					if (reader.hasNextLine()) {
						String reponse = reader.nextLine();
						if (reponse.contains("COMMAND#PLAYED")) {
							String split2[] = reponse.split(";");
							int lastRow = Integer.parseInt(split2[2]);
							int lastCol = Integer.parseInt(split2[1]);
							System.out.println("Le joueur a joue: " + lastRow + " " + lastCol);
							grid.fillThatCaseForMe(lastRow, lastCol);
							etat = "PLAYING";
							statusCO.setText(joueur + " " + etat);
							Thread s = new Thread(this);
							s.start();
						} else if (reponse.equals("COMMAND#ANYWIN")) {
							if (grid.isThereAnyWinner()) {
								if (joueur.equals(grid.winner)) {
									statusCO.setText(joueur + " WINNER");
									etat = "DONE";
									grid.clean();
                                    askRestart();
                                    break;
								} else {
									statusCO.setText(joueur + " LOOSER");
									etat = "DONE";
									grid.clean();
                                    askRestart();
                                    break;
								}
							} else {
								System.out.println("No winner");
							}
                        } else if (reponse.equals("COMMAND#DRAW")) {
                            statusCO.setText(joueur + " " + "DRAW");
                            etat = "DONE";
                            grid.clean();
                            break;
                        } else if (reponse.equals("COMMAND#STOP")) {
                            JOptionPane.showMessageDialog(mainWindow, "Votre adversaire ne veut plus jouer.");
                            close();
                            System.exit(1);
                        }
					}
				}
				break;
		}
	}

    private void askRestart() throws IOException {
        //Custom button text
        Object[] options = {"Oui, svp", "Non merci"};
        int n = JOptionPane.showOptionDialog(mainWindow,
                "Partie terminée, voulez vous recommancer ? ",
                "Done ?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == 0) {
            sendToW("AGAIN");
            statusCO.setText(joueur);
            mainPanel.remove(grid);
            mainPanel.revalidate();
            mainPanel.repaint();
            grid = new Grid();
            grid.joueur = joueur;
            mainPanel.add(grid, BorderLayout.CENTER);
            mainPanel.revalidate();
            if (joueur.equals("Joueur 1")) {
                etat = "PLAYING";
                grid.etat = etat;
                statusCO.setText(joueur + " " + etat);
            } else if (joueur.equals("Joueur 2")) {
                etat = "WAITING";
                grid.etat = etat;
                statusCO.setText(joueur + " " + etat);
            }
            Thread s = new Thread(this);
            s.start();
            startListening("check");
        } else {
            sendToW("IGIVEUP");
            close();
            System.exit(1);
        }
    }

	private void sendToW(String message) {
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
				}
			}
		}
		if (etat.equals("WAITING")) {
			System.out.println("Playing vers Waiting");
			statusCO.setText(joueur + " WAITING");
		}
		System.out.println("J'ai jou�");
	}
}
