package AwarenessListFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.BrokenBarrierException;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * Dieses Klasse definiert, wie eine Zeile in der ComboBox aussehen soll
 * @author Benedikt Brüntrup
 *
 * @param <E>
 */
public class LayoutZeileAwarenessList<E> extends JPanel implements ListCellRenderer<E>{
	
	private JLabel lblIcon, lblBenutzername, lblStatusmeldung;
	private JPanel bereichText;
	
	public LayoutZeileAwarenessList() {
		setLayout(new BorderLayout(10,10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		lblIcon = new JLabel();
		lblBenutzername = new JLabel();
		lblStatusmeldung = new JLabel();
		bereichText = new JPanel();
	}

	/**
	 * Dieses Methode legt das Layout einer Zeile fest
	 */
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {
		
		AwarenessListZeile zeile= (AwarenessListZeile) value;
		
		
		lblBenutzername.setText("<html><font size=+1>" + zeile.getBenutzername() +"</font></html>" );
		lblBenutzername.setEnabled(true);
		lblIcon.setEnabled(true);
		lblIcon.setIcon(zeile.getStatusSymbol());
		lblStatusmeldung.setText("<html><font size=-1>" +zeile.getStatus() +"</font></html>" );
		lblStatusmeldung.setEnabled(true);
		
		
		//Bereich Text
		bereichText.setLayout(new BorderLayout(5,5));
		add(bereichText, BorderLayout.CENTER);
		bereichText.add(lblBenutzername, BorderLayout.CENTER);
		bereichText.add(lblStatusmeldung, BorderLayout.SOUTH);
		lblStatusmeldung.setForeground(new Color(119, 51, 6));
		bereichText.setVisible(true);
		lblBenutzername.setVisible(true);
		lblStatusmeldung.setVisible(true);
		
		//Bereich ICON
		lblIcon.setVisible(true);
		add(lblIcon, BorderLayout.WEST);
		
		if (zeile.isFarbwechsel()){
			setBackground(new Color(137, 179, 200));
			bereichText.setBackground(getBackground());
		}else{
			setBackground(UIManager.getColor("ComboBox.background"));
			bereichText.setBackground(getBackground());
		}
		
		
		return this;
	}

}
