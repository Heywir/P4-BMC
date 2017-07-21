package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.*;

class Grid extends JPanel implements ActionListener {

	private final ArrayList<Case> listeCases;
	String joueur;
	boolean done = false;
	int lastCol;
	int lastRow;
	String etat;
	String winner;

	Grid() {
		//Definition
		int rows = 7;
		int cols = 7;
		listeCases = new ArrayList<>();

		//Layout
		GridLayout layout = new GridLayout(rows, cols);
		this.setLayout(layout);
		//Options
		this.setPreferredSize(new Dimension(725, 625));

		//Creation de la grid

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				JButton button = new JButton();

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
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		for (Case cross : listeCases) {
			if (arg0.getSource() == cross.button) {
				play(cross.col);
			}
		}

	}

	public boolean isThereAnyWinner() {
		// TODO Auto-generated method stub
		// Vertical
		for (int i = listeCases.size()-1; i>6; i--) {
			if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("caserouge.png")) { //1
				if (Objects.equals(listeCases.get(i).col, listeCases.get(i - 7).col) && listeCases.get(i-7).button.isSelected() && listeCases.get(i-7).button.getIcon().toString().contains("caserouge.png")) { //2
					if (Objects.equals(listeCases.get(i).col, listeCases.get(i - 14).col) && listeCases.get(i-14).button.isSelected() && listeCases.get(i-14).button.getIcon().toString().contains("caserouge.png"))	{ //3
						if (Objects.equals(listeCases.get(i).col, listeCases.get(i - 21).col) && listeCases.get(i-21).button.isSelected() && listeCases.get(i-21).button.getIcon().toString().contains("caserouge.png")) { //4
							winner = "Joueur 1";
							return true;
						}
					}
				}
			}
			else if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("casejaune.png")) { //1
				if (Objects.equals(listeCases.get(i).col, listeCases.get(i - 7).col) && listeCases.get(i-7).button.isSelected() && listeCases.get(i-7).button.getIcon().toString().contains("casejaune.png")) { //2
					if (Objects.equals(listeCases.get(i).col, listeCases.get(i - 14).col) && listeCases.get(i-14).button.isSelected() && listeCases.get(i-14).button.getIcon().toString().contains("casejaune.png"))	{ //3
						if (Objects.equals(listeCases.get(i).col, listeCases.get(i - 21).col) && listeCases.get(i-21).button.isSelected() && listeCases.get(i-21).button.getIcon().toString().contains("casejaune.png")) { //4
							winner = "Joueur 2";
							return true;
						}
					}
				}
			}
		}
		// Fin check vertical
		// Check horizontal
		for (int i = listeCases.size()-1; i>6; i--) {
			if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("caserouge.png")) { //1
				if (Objects.equals(listeCases.get(i).row, listeCases.get(i - 1).row) && listeCases.get(i-1).button.isSelected() && listeCases.get(i-1).button.getIcon().toString().contains("caserouge.png")) { //2
					if (Objects.equals(listeCases.get(i).row, listeCases.get(i - 2).row) && listeCases.get(i-2).button.isSelected() && listeCases.get(i-2).button.getIcon().toString().contains("caserouge.png"))	{ //3
						if (Objects.equals(listeCases.get(i).row, listeCases.get(i - 3).row) && listeCases.get(i-3).button.isSelected() && listeCases.get(i-3).button.getIcon().toString().contains("caserouge.png")) { //4
							winner = "Joueur 1";
							return true;
						}
					}
				}
			}
			else if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("casejaune.png")) { //1
				if (Objects.equals(listeCases.get(i).row, listeCases.get(i - 1).row) && listeCases.get(i-1).button.isSelected() && listeCases.get(i-1).button.getIcon().toString().contains("casejaune.png")) { //2
					if (Objects.equals(listeCases.get(i).row, listeCases.get(i - 2).row) && listeCases.get(i-2).button.isSelected() && listeCases.get(i-2).button.getIcon().toString().contains("casejaune.png"))	{ //3
						if (Objects.equals(listeCases.get(i).row, listeCases.get(i - 3).row) && listeCases.get(i-3).button.isSelected() && listeCases.get(i-3).button.getIcon().toString().contains("casejaune.png")) { //4
							winner = "Joueur 2";
							return true;
						}
					}
				}
			}
		}
		//Fin check honrizontal
		//Check diagonal/ Pour chaque direction et chaque coordone check si les 3 elements qui suivent sont de la meme couleur
		//Vers la gauche
		for (int i = listeCases.size()-1; i>6; i--) {
			if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("caserouge.png")) { //1
				if (listeCases.get(i-8).button.isSelected() && listeCases.get(i-8).button.getIcon().toString().contains("caserouge.png")) { //2
					if (listeCases.get(i-16).button.isSelected() && listeCases.get(i-16).button.getIcon().toString().contains("caserouge.png")) { //3
						if (listeCases.get(i-24).button.isSelected() && listeCases.get(i-24).button.getIcon().toString().contains("caserouge.png")) { //44
							winner = "Joueur 1";
							return true;
						}
					}
				}
			}
			else if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("casejaune.png")) { //1
				if (listeCases.get(i-8).button.isSelected() && listeCases.get(i-8).button.getIcon().toString().contains("casejaune.png")) { //2
					if (listeCases.get(i-16).button.isSelected() && listeCases.get(i-16).button.getIcon().toString().contains("casejaune.png")) { //3
						if (listeCases.get(i-24).button.isSelected() && listeCases.get(i-24).button.getIcon().toString().contains("casejaune.png")) { //44
							winner = "Joueur 2";
							return true;
						}
					}
				}
			}
		}
		//Vers la droite
		for (int i = listeCases.size()-1; i>6; i--) {
			if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("caserouge.png")) { //1
				if (listeCases.get(i-6).button.isSelected() && listeCases.get(i-6).button.getIcon().toString().contains("caserouge.png")) { //2
					if (listeCases.get(i-12).button.isSelected() && listeCases.get(i-12).button.getIcon().toString().contains("caserouge.png")) { //3
						if (listeCases.get(i-18).button.isSelected() && listeCases.get(i-18).button.getIcon().toString().contains("caserouge.png")) { //44
							winner = "Joueur 1";
							return true;
						}
					}
				}
			}
			else if (listeCases.get(i).button.isSelected() && listeCases.get(i).button.getIcon().toString().contains("casejaune.png")) { //1
				if (listeCases.get(i-6).button.isSelected() && listeCases.get(i-6).button.getIcon().toString().contains("casejaune.png")) { //2
					if (listeCases.get(i-12).button.isSelected() && listeCases.get(i-12).button.getIcon().toString().contains("casejaune.png")) { //3
						if (listeCases.get(i-18).button.isSelected() && listeCases.get(i-18).button.getIcon().toString().contains("casejaune.png")) { //44
							winner = "Joueur 2";
							return true;
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
	
	private void playerFillIt(int r, int c) {
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
