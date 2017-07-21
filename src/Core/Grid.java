package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.*;

public class Grid extends JPanel implements ActionListener {

	int rows;
	int cols;
	ArrayList<Case> listeCases;
	String joueur;
	boolean done = false;
	int lastCol;
	int lastRow;
	String etat;
	String winner;
	JButton[][] matrice;

	Grid(int r, int c) {
		//Definition
		rows = r;
		cols = c;
		listeCases = new ArrayList<Case>();

		//Layout
		GridLayout layout = new GridLayout(rows, cols);
		this.setLayout(layout);
		//Options
		this.setPreferredSize(new Dimension(725, 625));

		//Creation de la grid

		for (int i = 0; i < rows ; i++) {
			for (int j = 0; j < cols ; j++) {
				JButton button = new JButton();
				matrice = new JButton[rows][cols];

				//Premiere ligne qui sert juste a faire tomber les pieces
				if (i < 1 && j < 7) {
					button.addActionListener(this);
					ImageIcon nonClicked = new ImageIcon(getClass().getResource("/firstrow.png"));
					ImageIcon clicked = new ImageIcon(getClass().getResource("/firstrowpressed.png"));
					button.setIcon(nonClicked);
					button.setPressedIcon(clicked);
				}
				else {
					button.setSelected(false);
					ImageIcon nonClicked = new ImageIcon(getClass().getResource("/casevide.png"));
					button.setIcon(nonClicked);
				}
				button.setBorder(null);
				button.setContentAreaFilled(false);
				this.add(button);

				// On cree une case pour referencer
				Case ref = new Case(i, j, button);
				// On lajoute dans une liste
				listeCases.add(ref);
				matrice[i][j] = button;
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Iterator<Case> iterator = listeCases.iterator();
		while (iterator.hasNext()) {
			Case cross = iterator.next();
			if (arg0.getSource() == cross.button) {
				play(cross.col);
			}
		}

	}

	public boolean isThereAnyWinner() {
		// TODO Auto-generated method stub
		// Vertical bas droite vers gauche
		for (int i = listeCases.size()-1; i>=0 ; i--) {
			Case cross = listeCases.get(i);
			if (cross.row > 0) {
				//System.out.println("Boutton " + cross.row + " " + cross.col + " " + cross.button.isSelected());
				if (cross.button.isSelected()) { //Check 1
					String name = cross.button.getIcon().toString();
					int s = i - 1;
					if (s > 0) {
						Case cross2 = listeCases.get(s);
						String name2 = cross2.button.getIcon().toString();
						if (cross2.button.isSelected() && name.contains("caserouge.png") && name2.contains("caserouge.png")) { //Check 2
							//System.out.println("2 in a row RED");
							s = i - 2;
							if (s > 0) {
								Case cross3 = listeCases.get(s);
								String name3 = cross3.button.getIcon().toString();
								if (cross3.button.isSelected() && name.contains("caserouge.png") && name2.contains("caserouge.png") && name3.contains("caserouge.png")) { //Check 3
									//System.out.println("3 in a row RED");
									s = i - 3;
									if (s > 0) {
										Case cross4 = listeCases.get(s);
										String name4 = cross4.button.getIcon().toString();
										if (cross4.button.isSelected() && name.contains("caserouge.png") && name2.contains("caserouge.png") && name3.contains("caserouge.png") && name4.contains("caserouge.png")) { //Check 4
											//System.out.println("4 in a row RED");
											winner = "Joueur 1";
											return true;
										}
									}
								}
							}
						}
						else if (cross2.button.isSelected() && name.contains("casejaune.png") && name2.contains("casejaune.png")) { //Check 2
							//System.out.println("2 in a row YELLOW");
							s = i - 2;
							if (s > 0) {
								Case cross3 = listeCases.get(s);
								String name3 = cross3.button.getIcon().toString();
								if (cross3.button.isSelected() && name.contains("casejaune.png") && name2.contains("casejaune.png") && name3.contains("casejaune.png")) { //Check 3
									//System.out.println("3 in a row YELLOW");
									s = i - 3;
									if (s > 0) {
										Case cross4 = listeCases.get(s);
										String name4 = cross4.button.getIcon().toString();
										if (cross4.button.isSelected() && name.contains("casejaune.png") && name2.contains("casejaune.png") && name3.contains("casejaune.png") && name4.contains("casejaune.png")) { //Check 4
											//System.out.println("4 in a row RED");
											winner = "Joueur 2";
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	private void play(int c) {
		System.out.println("Je suis le " + joueur);
		// TODO Auto-generated method stub
		for (int i = listeCases.size()-1; i>=0 ; i--) {
			Case cross = listeCases.get(i);
			if (cross.col == c && cross.row > 0 && !cross.button.isSelected()){
				lastCol = cross.col;
				lastRow = cross.row;
				System.out.println("Colonne: " + cross.col + " Ligne: " + cross.row);
				playerFillIt(lastRow, lastCol);
				i = 0;
			}
		}		
		
	}
	
	public void playerFillIt(int r, int c) {
		for (int i = listeCases.size()-1; i>=0 ; i--) {
			Case cross = listeCases.get(i);
			if (cross.col == c && cross.row == r){
				//Si c'est le joueur qui joue
				if (joueur.equals("Joueur 1") && etat.equals("PLAYING")) {
					ImageIcon red = new ImageIcon(getClass().getResource("/caserouge.png"));
					cross.button.setIcon(red);
					cross.button.setSelected(true);
					etat = "WAITING";
				}
				else if (joueur.equals("Joueur 2") && etat.equals("PLAYING")) {
					ImageIcon yellow = new ImageIcon(getClass().getResource("/casejaune.png"));
					cross.button.setIcon(yellow);
					cross.button.setSelected(true);
					etat = "WAITING";
				}
			}
		}
	}
	
	public void fillThatCaseForMe(int r, int c) {
		for (int i = listeCases.size()-1; i>=0 ; i--) {
			Case cross = listeCases.get(i);
			if (cross.col == c && cross.row == r){
				//Si c'est ladversaire qui joue
				if (joueur.equals("Joueur 1") && etat.equals("WAITING")) {
					ImageIcon yellow = new ImageIcon(getClass().getResource("/casejaune.png"));
					cross.button.setIcon(yellow);
					cross.button.setSelected(true);
					etat = "PLAYING";
				}
				else if (joueur.equals("Joueur 2") && etat.equals("WAITING")) {
					ImageIcon red = new ImageIcon(getClass().getResource("/caserouge.png"));
					cross.button.setIcon(red);
					cross.button.setSelected(true);
					etat = "PLAYING";
				}
			}
		}
		
	}

	public void clean() {
		listeCases.clear();
	}

}
