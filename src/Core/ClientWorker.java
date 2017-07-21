package Core;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

class ClientWorker implements Runnable {
	
	private final Socket sc1;
	private final Socket sc2;
	private final ServerUI ui;
	private int p1;
	private PrintWriter bf1;
	private PrintWriter bf2;
	private Scanner br1;
	private Scanner br2;
	private String joueurSC1;
	private String joueurSC2;

	public ClientWorker(Socket s1, Socket s2, ServerUI u) {
		sc1 = s1;
		sc2 = s2;
		ui = u;
	}

	@Override
	public void run() {
		
		try {
			open();
			
			ui.addText("Worker: Creation Reussite");
			//Ordre de jeu
			randomizePlayers();
			//On ecoute pour savoir si les clients sont pr�tes
			startListening("ready");
			
			//Debut de partie
			play();
			
			//On ecoute la reponse du joueur
			startListening("done");
			
			System.out.println("fin d'execution");
            stop();


        } catch (BadLocationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void open() throws IOException {
		// Writer
		bf1 = new PrintWriter(sc1.getOutputStream(), true);
		bf2 = new PrintWriter(sc2.getOutputStream(), true);
		
		// Reader
		br1 = new Scanner(sc1.getInputStream());
		br2 = new Scanner(sc2.getInputStream());
	}

	private void randomizePlayers() throws BadLocationException {
		// TODO Auto-generated method stub
		ui.addText("Worker: Randomisation des joueurs");
		Random random = new Random();
		p1 = random.nextInt(2);
		p1 = 1;
		int p2;
		p2 = 0;
		joueurSC1 = "Joueur 2";
		joueurSC2 = "Joueur 1";
		ui.addText("Worker: Joueur 1: " + p1 + "  Joueur 2: " + p2);
		sendToSC1("P2");
		sendToSC2("P1");

	}
	
	private void sendToBoth(String message) throws BadLocationException {
		ui.addText("Worker: Send to Socket 1: " + message);
		ui.addText("Worker: Send to Socket 2: " + message);
		bf1.println("COMMAND#" + message);
		bf2.println("COMMAND#" + message);
		
	}
	
	private void sendToSC1(String message) throws BadLocationException {
		ui.addText("Worker: Send to Socket 1: " + message);
		bf1.println("COMMAND#" + message);
		
	}
	
	private void sendToSC2(String message) throws BadLocationException {
		ui.addText("Worker: Send to Socket 2: " + message);
		bf2.println("COMMAND#" + message);
		
	}
	
	private void startListening(String index) throws BadLocationException {
		if (index.equals("ready")) {
			//Ecoute pour savoir si pret
			String reponse1;
			String reponse2;
			reponse1 = br1.nextLine();
			ui.addText("Worker: Received from Socket 1: " + reponse1);
			reponse2 = br2.nextLine();
			ui.addText("Worker: Received from Socket 2: " + reponse2);
			if (reponse1.equals("COMMAND#READY") && reponse2.equals("COMMAND#READY")) {
				sendToBoth("START");
			}
		}
		else if (index.equals("done")) {
			int tour = 1;
			while (tour <= 42) {
				int lastCol;
				int lastRow;
				if (joueurSC1.equals("Joueur 1")) {
					String reponse1;
					if (br1.hasNextLine()) {
						reponse1 = br1.nextLine();
						ui.addText("Worker: Received from Socket 1: " + reponse1);
						System.out.println(reponse1);
						if (reponse1.contains("COMMAND#DONE")) {
							String split1[] = reponse1.split(";");
							lastRow = Integer.parseInt(split1[2]);
							lastCol = Integer.parseInt(split1[1]);
							System.out.println("Le joueur a joue: " + lastRow + " " + lastCol);
							sendToSC2("PLAYED;" + lastRow + ";" + lastCol);
							sendToBoth("ANYWIN");
							joueurSC1 = "Joueur 2";
							joueurSC2 = "Joueur 1";
							tour++;

						}
					}
				}
				else if (joueurSC1.equals("Joueur 2")) {
					String reponse2;
					if (br2.hasNextLine()) {
						reponse2 = br2.nextLine();
						ui.addText("Worker: Received from Socket 2: " + reponse2);
						System.out.println(reponse2);
						if (reponse2.contains("COMMAND#DONE")) {
							String split2[] = reponse2.split(";");
							lastRow = Integer.parseInt(split2[2]);
							lastCol = Integer.parseInt(split2[1]);
							System.out.println("Le joueur a joue: " + lastRow + " " + lastCol);
							sendToSC1("PLAYED;" + lastRow + ";" + lastCol);
							sendToBoth("ANYWIN");
							joueurSC1 = "Joueur 1";
							joueurSC2 = "Joueur 2";
							tour++;
						}
					}
				}
			}
			sendToBoth("DRAW");
		}
	}
	
	private void play() throws BadLocationException {
		if (p1 == 0) {
			sendToSC1("PLAY");
			sendToSC2("WAIT");
		}
		else {
			sendToSC2("PLAY");
			sendToSC1("WAIT");
		}
	}
	
	public void stop() {
		bf1.close();
		bf2.close();
		br1.close();
		br2.close();
	}
}
