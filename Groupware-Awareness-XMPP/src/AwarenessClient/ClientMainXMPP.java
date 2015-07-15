package AwarenessClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import AwarenessListFenster.AwarenessListZeile;
import AwarenessListFenster.ClientFenster;



public class ClientMainXMPP extends ClientFenster{
	
	private XMPPConnection connection;
	private Presence status;
	private Roster kontaktliste;
	private Vector<AwarenessListZeile> kontaktliste_fenster_vector = new Vector<AwarenessListZeile>();
	private String serverAdresse;
		
	/**
	 * Konstruktor des Clients
	 */
	public ClientMainXMPP(/*String adresse*/){
		super("Awareness-Liste mit XMPP");
		
		//Listener, die einen Button-Klick beim Loginfenster entgegennehmen
		login.addActionListenerButtons(new buttonKlickListener());
		
		//Listener, der darauf lauscht, ob beim Menü ausgewählt wurde, dass eine neuer Kontakt zur Kontaktliste hinzugefügt werden soll
		kontaktHinzufuegen.addActionListener(new buttonKlickListener());
		
		//Listener, der auf eine Änderung im Statustextfeld lauscht
		txtStatusnachricht.getDocument().addDocumentListener(new StatusTextAenderungListener());
		
		//Listener zur Combo-Box hinzufügen, der reagiert, wenn eine anderer Status ausgewählt wurde
		comStatusSymbol.addItemListener(new statusSymbolListener());

		
		//Verbindung zu OpenFire wird aufgebaut
		try {
			String serverAdresse = JOptionPane.showInputDialog(this, "Bitte geben Sie IP-Adresse des Servers an.");
			
			if ( serverAdresse != null){
				//Verbindung zum Openfire-Server aufbauen
				connection = new XMPPConnection(serverAdresse);
				connection.connect();
				connection.addPacketListener(new KontaktanfragenListener(), new PacketFilter() {
					public boolean accept(Packet arg0) {
						boolean result = false;
						if (arg0 instanceof Presence){
							Presence p = (Presence)arg0;
							result = p.getType().equals(Presence.Type.subscribe);
						}
						return result;
					}
				});
			}else{
				System.exit(0);
			}
						
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
					if (login.getBenutzername()== null || login.getPasswort().length == 0 ) 
						throw new Exception("Bitte geben Sie einen Benutzername und ein Passwort ein.");
					
					connection.login(login.getBenutzername(), String.valueOf(login.getPasswort()));
					
					//Kennzeichnet, dass das Login-Dialog mit einen Button geschlossen wurde
					login.setGeschlossendurchButton(true);
					login.dispose(); //Schließt das Anmeldedialog
					setVisible(true); //zeigt das Awareness-Dialog an
					
					//neue Presence-Instanze anlegen
					status = new Presence(Presence.Type.available);
					status.setMode(Presence.Mode.chat);
					connection.sendPacket(status);
					
					//Kontaktliste laden
					kontaktliste = connection.getRoster();
					kontaktliste.setSubscriptionMode(Roster.SubscriptionMode.manual);
					kontaktliste.addRosterListener(new KontaktlistenListener());
					kontaktlisteAnzeigen();
					
					//Status laden
					txtStatusnachricht.setText(status.getStatus());
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
			/*
			 * Benutzer hat den Registieren-Button geklickt
			 */
			else if(e.getActionCommand().equals("Registrieren")){
				try {
					if (login.getBenutzername() == null || login.getPasswort().length == 0 ) 
						throw new Exception("Bitte geben Sie einen Benutzername und ein Passwort ein.");
					
					connection.getAccountManager().createAccount(login.getBenutzername(), String.valueOf(login.getPasswort()));
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
					kontaktliste.setSubscriptionMode(Roster.SubscriptionMode.manual);
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
			if ( kontakt == null) throw new Exception("Bitte geben Sie einen Kontaktnamen an.");
			kontaktliste.createEntry(kontakt, kontakt, null);
			
			Presence neuerBenutzerPaket = new Presence(Presence.Type.subscribe);
			neuerBenutzerPaket.setTo(kontakt);
			connection.sendPacket(neuerBenutzerPaket);
			
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
				
		
		HashMap<org.jivesoftware.smack.packet.Presence.Mode, String> icons = new HashMap<>();
		icons.put(Presence.Mode.chat, "online.png");
		icons.put(Presence.Mode.available, "online.png");
		icons.put(Presence.Mode.away, "abwsend.png");
		icons.put(Presence.Mode.xa, "laenger_abwesend.png");
		icons.put(Presence.Mode.dnd, "beschaeftigt.png");
		//icons.put("off", "offline.png");
		String pfadZumIcon = System.getProperty("user.dir") + "/bilder/";
		
		
		
		Collection<RosterEntry> kontakte = kontaktliste.getEntries();
		boolean farbwechsel = false;
		kontaktliste_fenster_vector.clear();

		for (RosterEntry kontakt : kontakte){
			String bildPfad = "";
			if ( kontakt.getType().equals(Presence.Type.unavailable)){
				bildPfad = pfadZumIcon + "offline.png";
			}else if (kontaktliste.getPresence(kontakt.getUser()).getMode() == Presence.Mode.chat ||
					kontaktliste.getPresence(kontakt.getUser()).getMode() == Presence.Mode.away ||
					kontaktliste.getPresence(kontakt.getUser()).getMode() == Presence.Mode.dnd ||
					kontaktliste.getPresence(kontakt.getUser()).getMode() == Presence.Mode.xa
					) {
				bildPfad = pfadZumIcon + icons.get(kontaktliste.getPresence(kontakt.getUser()).getMode());		
			}
			else{
				bildPfad = pfadZumIcon + "offline.png";
			}
			String name = kontakt.getUser() == null ? "" : kontakt.getUser();
			String status_ = kontaktliste.getPresence(kontakt.getUser()).getStatus();
			
			AwarenessListZeile zeile = new AwarenessListZeile(name, status_, new ImageIcon(bildPfad), farbwechsel);
			
			farbwechsel = !farbwechsel;
			kontaktliste_fenster_vector.add(zeile);
		}
		
		//Die neue Liste im Fenster anzeigen
		awarenessListe.setListData(kontaktliste_fenster_vector);
		awarenessListe.validate();
		awarenessListe.repaint();
	}
	
	/**
	 * Listener, der darauf wartet, dass ein Kontaktb eine Freundschafsanfrage sendet
	 * @author benedikt
	 *
	 */
	private class KontaktanfragenListener implements PacketListener{
		public void processPacket(Packet arg0) {
			
			kontaktliste.setSubscriptionMode(Roster.SubscriptionMode.manual);
			Presence p1 = (Presence) arg0;
			
			//bestätigt das erhaltene Paket
			Presence bestaetigung = new Presence(Presence.Type.subscribed);
			bestaetigung.setTo(p1.getFrom());
			connection.sendPacket(bestaetigung);
			System.out.println("bestaetigung");
			
			if (p1.getType() ==Presence.Type.subscribe){				
				//sendet eine Gegenanfrage
				Presence gegenanfrage = new Presence(Presence.Type.subscribe);
				gegenanfrage.setTo(p1.getFrom());
				connection.sendPacket(gegenanfrage);
				System.out.println("anfrage2");
			}
		}
		
	}
	
	/**
	 * Listener, der die Änderung der Statusnachricht an den Server überträgt
	 * @author Benedikt Brüntrup
	 *
	 */
	private class StatusTextAenderungListener implements DocumentListener{

		public void insertUpdate(DocumentEvent e) {
			textAenderung(e);;	
		}

		public void removeUpdate(DocumentEvent e) {
			textAenderung(e);
		}

		public void changedUpdate(DocumentEvent e) {
			textAenderung(e);	
		}
		
		public void textAenderung(DocumentEvent e){
			status.setStatus(txtStatusnachricht.getText());
			connection.sendPacket(status);

		}
		
	}
	
	/**
	 * Dieser Listener warte, bis eine anderen Statussymbol ausgewählt wird
	 * @author Benedikt Brüntrup
	 */
	private class statusSymbolListener implements ItemListener{

		/**
		 * Sendet zum Server das neue Statussymbol
		 */
		public void itemStateChanged(ItemEvent e) {
			
			HashMap<String, Presence.Mode> statusliste = new HashMap<>();
			statusliste.put("Online", Presence.Mode.chat);
			statusliste.put("Abwesend", Presence.Mode.away);
			statusliste.put("Laenger Abwesend", Presence.Mode.xa);
			statusliste.put("Beschaeftigt", Presence.Mode.dnd);
			JLabel auswahl = (JLabel)e.getItem();
			
			status.setMode(statusliste.get(auswahl.getText()));
			connection.sendPacket(status);
		}
	}
	
	
}
