package Core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import javax.swing.text.BadLocationException;

public class ClientWorker implements Runnable {
	
	Socket sc1;
	Socket sc2;
	ServerUI ui;
	int p1;
	int p2;
	OutputStream wr1;
	OutputStream wr2;
	InputStream r1;
	InputStream r2;
	PrintWriter bf1;
	PrintWriter bf2;
	Scanner br1;
	Scanner br2;
	
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
			//On ecoute pour savoir si les clients sont prêtes
			startListening("ready");
			
			//Debut de partie
			play();
			
			//On ecoute la reponse du joueur
			startListening("done");
			
			System.out.println("fin d'execution");
			//stop();
			
			
		} catch (BadLocationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void open() throws IOException {
		// Writer
		bf1 = new PrintWriter(sc1.getOutputStream(), true);
		bf2 = new PrintWriter(sc2.getOutputStream(), true);
		
		// Reader
		br1 = new Scanner(sc1.getInputStream());
		br2 = new Scanner(sc2.getInputStream());
	}

	private void randomizePlayers() throws BadLocationException, IOException {
		// TODO Auto-generated method stub
		ui.addText("Worker: Randomisation des joueurs");
		Random random = new Random();
		p1 = random.nextInt(2);
		if (p1 == 0) {
			p2 = 1;
			ui.addText("Worker: Joueur 1: " + p1 + "  Joueur 2: " + p2);
			sendToSC1("P1");
			sendToSC2("P2");
		}
		else {
			p2 = 0;
			ui.addText("Worker: Joueur 1: " + p1 + "  Joueur 2: " + p2);
			sendToSC1("P2");
			sendToSC2("P1");
		}
		
	}
	
	public void sendToBoth(String message) throws IOException, BadLocationException {
		ui.addText("Worker: Send to Socket 1: " + message);
		ui.addText("Worker: Send to Socket 2: " + message);
		bf1.println("COMMAND#" + message);
		bf2.println("COMMAND#" + message);
		
	}
	
	public void sendToSC1(String message) throws IOException, BadLocationException {
		ui.addText("Worker: Send to Socket 1: " + message);
		bf1.println("COMMAND#" + message);
		
	}
	
	public void sendToSC2(String message) throws IOException, BadLocationException {
		ui.addText("Worker: Send to Socket 2: " + message);
		bf2.println("COMMAND#" + message);
		
	}
	
	public void startListening(String index) throws IOException, BadLocationException {
		if (index.equals("ready")) {
			//Ecoute pour savoir si pret
			String reponse1 = null;
			String reponse2 = null;
			reponse1 = br1.nextLine();
			ui.addText("Worker: Received from Socket 1: " + reponse1);
			reponse2 = br2.nextLine();
			ui.addText("Worker: Received from Socket 2: " + reponse2);
			if (reponse1.equals("COMMAND#READY") && reponse2.equals("COMMAND#READY")) {
				sendToBoth("START");
			}
		}
		else if (index.equals("done")) {
			//Ecoute pour savoir si pret
			String reponse1 = null;
			String reponse2 = null;
			reponse1 = br1.nextLine();
			ui.addText("Worker: Received from Socket 1: " + reponse1);
			reponse2 = br2.nextLine();
			ui.addText("Worker: Received from Socket 2: " + reponse2);
		}
	}
	
	public void play() throws IOException, BadLocationException {
		if (p1 == 0) {
			sendToSC1("PLAY");
			sendToSC2("WAIT");
		}
		else {
			sendToSC2("PLAY");
			sendToSC1("WAIT");
		}
	}
	
	public void stop() throws IOException {
		bf1.close();
		bf2.close();
		br1.close();
		br2.close();
	}
}
