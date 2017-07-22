package Core;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class ServerUI extends JFrame{

	private final Document doc;
	private final BoundedRangeModel brm;
	
	public ServerUI() {
		
		//Composants
		JFrame mainWindow = new JFrame("ServerUI");
		JTextPane textPanel = new JTextPane();
		JScrollPane scrollBar = new JScrollPane(textPanel);
		brm = scrollBar.getVerticalScrollBar().getModel();
		doc = textPanel.getDocument();

		//Parametres
	    mainWindow.setSize(600, 300);
	    mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    textPanel.setBackground(Color.BLACK);
	    textPanel.setEnabled(false);
	    
	    //Ajouts
	    mainWindow.getContentPane().add(scrollBar);
	    
	    //End
	    mainWindow.setVisible( true );
	
	}

	public void close() {
		System.exit(1);
	}

	public void addText(String message) throws BadLocationException {
		doc.insertString(doc.getLength(), new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "| " + message + "\n", null);
		//Autoscroll
		brm.setValue(brm.getMaximum());
	}
	
}
