package Core;


import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class ServerUI extends JFrame{
	
	JFrame mainWindow;
	JTextPane textPanel;
	JScrollPane scrollBar;
	Document doc;
	
	public ServerUI() {
		
		//Composants
		mainWindow = new JFrame("ServerUI");
		textPanel = new JTextPane();
	    scrollBar = new JScrollPane(textPanel);
	    doc = textPanel.getDocument();
	    
		//Parametres
	    mainWindow.setSize(600, 300);
	    mainWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    textPanel.setBackground(Color.BLACK);
	    textPanel.setEnabled(false);
	    
	    //Ajouts
	    mainWindow.getContentPane().add( scrollBar );
	    
	    //End
	    mainWindow.setVisible( true );
	
	}
	
	public void addText(String message) throws BadLocationException {
		doc.insertString(doc.getLength(), new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "| " + message + "\n", null);
	}
	
}
