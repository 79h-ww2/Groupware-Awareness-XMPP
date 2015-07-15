package AwarenessClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import AwarenessListFenster.AwarenessListZeile;
import AwarenessListFenster.ClientFenster;



public class ClientMainXMPP extends ClientFenster{
	
	private XMPPConnection connection;
	private Presence status;
	private Roster kontaktliste;
		
	/**
	 * Konstruktor des Clients
	 */
	public ClientMainXMPP(/*String adresse*/){
		super("Awareness-Liste mit XMPP");
		
		//Listener, die einen Button-Klick beim Loginfenster entgegennehmen
		login.addActionListenerButtons(new buttonKlickListener());
		
		//Listener, der darauf lauscht, ob beim Menü ausgewählt wurde, dass eine neuer Kontakt zur Kontaktliste hinzugefügt werden soll
		kontaktHinzufuegen.addActionListener(new buttonKlickListener());

		
		//Verbindung zu OpenFire wird aufgebaut
		try {
			String adresse = JOptionPane.showInputDialog(this, "Bitte geben Sie IP-Adresse des Servers an.");
			
			//Verbindung zum Openfire-Server aufbauen
			connection = new XMPPConnection(adresse);
			connection.connect();			
						
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
			System.exit(1);
		}
		

	}

	/**
	 * Programmeinstiegspunkt
	 * @param args
	 */
	public static void main(String[] args) {
			ClientMainXMPP clientFenster = new ClientMainXMPP();
	}
	
	
	/**
	 * Listener, der darauf wartet, dass ein Button geklickt wird	
	 */
	private class buttonKlickListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			/*
			 * Benutzer hat den Login-Button geklickt, um sich anzumelden
			 */
			if (e.getActionCommand().equals("Login")){
				
				try {
					if (login.getBenutzername().equals("") || login.getPasswort().length == 0 ) 
						throw new Exception("Bitte geben Sie einen Benutzername und ein Passwort ein.");
					
					connection.login(login.getBenutzername(), String.valueOf(login.getPasswort()));
					
					//Kennzeichnet, dass das Login-Dialog mit einen Button geschlossen wurde
					login.setGeschlossendurchButton(true);
					login.dispose(); //Schließt das Anmeldedialog
					setVisible(true); //zeigt das Awareness-Dialog an
					
					//neue Presence-Instanze anlegen
					status = new Presence(Presence.Type.available);
					connection.sendPacket(status);
					
					//Kontaktliste laden
					kontaktliste = connection.getRoster();
					kontaktliste.addRosterListener(new KontaktlistenListener());
					kontaktlisteAnzeigen();
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
			/*
			 * Benutzer hat den Registieren-Button geklickt
			 */
			else if(e.getActionCommand().equals("Registrieren")){
				try {
					if (login.getBenutzername().equals("") || login.getPasswort().length == 0 ) 
						throw new Exception("Bitte geben Sie einen Benutzername und ein Passwort ein.");
					
					connection.getAccountManager().createAccount(login.getBenutzername(), String.valueOf(login.getPasswort()));
					
					//Kennzeichnet, dass das Login-Dialog mit einen Button geschlossen wurde
					login.setGeschlossendurchButton(true);
					login.dispose(); //Schließt das Anmeldedialog
					setVisible(true); //zeigt das Awareness-Dialog an
					
					//neue Presence-Instanze anlegen
					status = new Presence(Presence.Type.available);
					connection.sendPacket(status);
					
					//Kontaktliste laden
					kontaktliste = connection.getRoster();
					kontaktliste.addRosterListener(new KontaktlistenListener());
					kontaktlisteAnzeigen();
					
				} catch (Exception e1) {
					String meldung = e1.getMessage().equals("conflict(409)") ? "Der Benutzername exisiert schon." : e1.getMessage();
					JOptionPane.showMessageDialog(null, meldung);
				}
			}
			/*
			 * Benutzer möchte einen neuen Kontakt zur Kontaktliste hinzufügen
			 */
			else if (e.getActionCommand().equals("Kontakt hinzufuegen")){
				kontaktZurKontaktlisteHinzufuegen();
			}
		}
	}
	
	/**
	 * fügt einen Kontakt zur Kontaktliste hinzu
	 */
	public void kontaktZurKontaktlisteHinzufuegen(){
		String kontakt = JOptionPane.showInputDialog(this, "Bitte geben Sie den Kontaktnamen ein, der zur Kontaktliste hinzugefügt werden soll.");
		
		try {
			if ( kontakt.equals("")) throw new Exception("Bitte geben Sie einen Kontaktnamen an.");
			kontaktliste.createEntry(kontakt, kontakt, null);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	
	/**
	 * Ändert die statusnachricht
	 * @param nachricht Nachrichttext
	 * @param typ Statussymbol
	 */
	public void statusSetzen(String nachricht, org.jivesoftware.smack.packet.Presence.Mode typ){
		status.setMode(typ);
		status.setStatus(nachricht);
		connection.sendPacket(status);
	}
	
	/**
	 * Listener, der bei Änderung der Kontaktliste aufgerufen wird
	 * @author benedikt
	 *
	 */
	private class KontaktlistenListener implements RosterListener{

		@Override
		public void entriesAdded(Collection<String> arg0) {
			kontaktlisteAnzeigen();
			
		}

		@Override
		public void entriesDeleted(Collection<String> arg0) {
			kontaktlisteAnzeigen();
			
		}

		@Override
		public void entriesUpdated(Collection<String> arg0) {
			kontaktlisteAnzeigen();
			
		}

		@Override
		public void presenceChanged(Presence arg0) {
			kontaktlisteAnzeigen();
		}
		
	}
	
	/**
	 * Kontaktliste anzeigen
	 */
	public void kontaktlisteAnzeigen(){
		
		//Der als Input für auf Kontaktliste im Fenster gilt
		Vector<AwarenessListZeile> kontaktliste_fenster_vector = new Vector<AwarenessListZeile>();
		
		HashMap<org.jivesoftware.smack.packet.Presence.Mode, String> icons = new HashMap<>();
		icons.put(Presence.Mode.available, "online.png");
		icons.put(Presence.Mode.away, "abwsend.png");
		icons.put(Presence.Mode.xa, "laenger_abwesend.png");
		icons.put(Presence.Mode.dnd, "beschaeftigt.png");
		//icons.put("off", "offline.png");
		String pfadZumIcon = System.getProperty("user.dir") + "/bilder/";
		
		System.out.println(kontaktliste.getEntryCount());
		
		Collection<RosterEntry> kontakte = kontaktliste.getEntries();
		boolean farbwechsel = false;
		for (RosterEntry kontakt : kontakte){
			
			String bildPfad = "";
			if ( kontakt.getType().equals(Presence.Type.unavailable)){
				bildPfad = pfadZumIcon + "offline.png";
			}else{
				bildPfad = pfadZumIcon + icons.get(kontaktliste.getPresence(kontakt.getUser()).getMode());
			}
			AwarenessListZeile zeile = new AwarenessListZeile(kontakt.getName(), kontakt.getStatus().toString(), new ImageIcon(bildPfad), farbwechsel);
			
			farbwechsel = !farbwechsel;
			kontaktliste_fenster_vector.add(zeile);
		}
		
		
		//Die neue Liste im Fenster anzeigen
		awarenessListe.setListData(kontaktliste_fenster_vector);
		awarenessListe.repaint();
	}
	
}
