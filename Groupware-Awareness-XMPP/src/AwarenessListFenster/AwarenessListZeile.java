package AwarenessListFenster;

import javax.swing.ImageIcon;

public class AwarenessListZeile {
	
	protected String benutzername, status;
	protected ImageIcon statusSymbol;
	private boolean farbwechsel;
	
	/**
	 * Konstruktor
	 * @param benutzername Benutzername, der angezeigt werden soll
	 * @param status Statustext, der angezeigt werden soll
	 * @param statusSymbol Das Statussymbol, welches angezeigt werden soll
	 */
	public AwarenessListZeile(String benutzername, String status, ImageIcon statusSymbol, boolean farbwechsel) {
		this.benutzername = benutzername;
		this.status = status;
		this.statusSymbol = statusSymbol;
		this.farbwechsel=farbwechsel;
	}

	public String getBenutzername() {
		return benutzername;
	}

	public void setBenutzername(String benutzername) {
		this.benutzername = benutzername;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ImageIcon getStatusSymbol() {
		return statusSymbol;
	}

	public void setStatusSymbol(ImageIcon statusSymbol) {
		this.statusSymbol = statusSymbol;
	}

	public boolean isFarbwechsel() {
		return farbwechsel;
	}

	public void setFarbwechsel(boolean farbwechsel) {
		this.farbwechsel = farbwechsel;
	}

	

}
