package AwarenessListFenster;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.Dialog;


public abstract class ClientFenster  extends JFrame{

	protected JTextField txtStatusnachricht;
	protected JComboBox<JLabel> comStatusSymbol;
	protected JScrollPane scrollLeistenAwarenessListen;
	protected JList<AwarenessListZeile> awarenessListe;
	protected Vector<AwarenessListZeile> werteAwarenessListe;
	protected JMenu kontakteMenu;
	protected JMenuItem kontaktHinzufuegen;
	protected DialogLogin login;
	
	/**
	 * Konstruktor
	 */
	public ClientFenster(String Fenstertitel){
		super(Fenstertitel);
		
		//Bei Windows wird das Design in den Betriebsystem eigenen Design angezeigt
		try {
			if( System.getProperty("os.name").startsWith("Windows")){
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
		} catch (Exception e) {
			
		} 
		
		//designen des Fensters
		
		//Fensterabmaße angeben
		setSize(290, 450);
				
		//Festlegen, dass beim Schließen des Fensters das Programm beendet werden soll
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Menü-Leiste hinzufügen
		setJMenuBar(new JMenuBar());
		kontakteMenu = new JMenu("Kontakte");
		getJMenuBar().add(kontakteMenu);
		kontakteMenu.setVisible(true);
		getJMenuBar().setVisible(true);
		kontaktHinzufuegen = new JMenuItem("Kontakt hinzufuegen");
		kontakteMenu.add(kontaktHinzufuegen);
		kontaktHinzufuegen.setVisible(true);
		
		
		JPanel cont = new JPanel();
		add(cont);
		cont.setLayout(new BorderLayout(5,5));
		cont.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		//Bereich, wo der Benutzer seine Awareness auswählen kann
		JPanel panelBereichAwarenessSetzung = new JPanel();
		
		cont.add(panelBereichAwarenessSetzung, BorderLayout.SOUTH);
		panelBereichAwarenessSetzung.setLayout(new BorderLayout(10,5));
		panelBereichAwarenessSetzung.setVisible(true);
		
		//Bereich: Textfeld für die Statusnachricht
		JPanel panelStatusMeldung = new JPanel();
		panelBereichAwarenessSetzung.add(panelStatusMeldung, BorderLayout.NORTH);
		panelStatusMeldung.setLayout(new BorderLayout(10,10));
		panelStatusMeldung.add(new JLabel("Statusmeldung:"), BorderLayout.WEST);
		txtStatusnachricht = new JTextField();
		panelStatusMeldung.add(txtStatusnachricht, BorderLayout.CENTER);
		txtStatusnachricht.setVisible(true);
		panelStatusMeldung.setVisible(true);
		
		//Vektor, der die Zeilen der Combo-Box enthält, wo der Status ausgewählt werden kann
		Vector<JLabel> comboEintraegeStatus =  new Vector<JLabel>();
		String[] zeileIcons = {"online.png", "abwsend.png", "laenger_abwesend.png", "beschaeftigt.png"};
		String[] zeileBeschriftung = {"Online", "Abwesend", "Laenger Abwesend", "Beschaeftigt"};
		
		for( int i = 0; i < zeileIcons.length; i++){
			//bestimmt die Adresse des Icons
			String adresseIcon = System.getProperty("user.dir") + "/bilder/" + zeileIcons[i];
			
			JLabel tempLabel = new JLabel();
			
			//lädt das Icon in die Combobox-Zeile
			try{
				File f = new File(adresseIcon);
				ImageIcon icon = new ImageIcon(ImageIO.read(f));
				tempLabel.setIcon(icon);
			}catch (IOException e) {
				System.out.println(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
			//fügt zur Combobox-Zeile einen Text hinzu
			tempLabel.setText(zeileBeschriftung[i]);
			
			//fügt die Zeile zum Vektor hinzu
			comboEintraegeStatus.add(tempLabel);
		}
		
		//ComboBox, wo das Statussymbol ausgewählt wird
		comStatusSymbol = new JComboBox<>(comboEintraegeStatus);
		panelBereichAwarenessSetzung.add(comStatusSymbol,BorderLayout.SOUTH);
		comStatusSymbol.setRenderer(new LayoutZeileComboBox<JLabel>());
		comStatusSymbol.setVisible(true);
		
		//ListBox erstellen, wo die Awareness-Informationen angezeigt werden
		awarenessListe = new JList<>();
		awarenessListe.setCellRenderer(new LayoutZeileAwarenessList<AwarenessListZeile>());
		scrollLeistenAwarenessListen = new JScrollPane(awarenessListe);
		scrollLeistenAwarenessListen.setVisible(true);
		cont.add(scrollLeistenAwarenessListen, BorderLayout.CENTER);
		
		
		
		//Fenster anzeigen
		cont.setVisible(true);
		setFensterAktiviert(false);
		
		//zeigt zunächst das Login-Fesnter an
		login = new DialogLogin(this,Dialog.ModalityType.MODELESS);
		setFensterAktiviert(true);
		
	}
	
	/**
	 * legt fest, ob das Fenster aktiviert sein soll
	 * @param aktiviert
	 */
	public void setFensterAktiviert(boolean aktiviert){
		comStatusSymbol.setEnabled(aktiviert);
		txtStatusnachricht.setEnabled(aktiviert);
		getJMenuBar().setEnabled(aktiviert);
		awarenessListe.setEnabled(aktiviert);
	}
	
}
