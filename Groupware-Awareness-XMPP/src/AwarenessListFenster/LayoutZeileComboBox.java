package AwarenessListFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

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
public class LayoutZeileComboBox<E> extends JPanel implements ListCellRenderer<E>{
	
	private JLabel lblIcon, lblText;
	
	public LayoutZeileComboBox() {
		setLayout(new BorderLayout(10,10));
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		lblIcon = new JLabel();
		lblText = new JLabel();
	}

	/**
	 * Dieses Methode legt das Layout einer Zeile fest
	 */
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {
		
		JLabel zeile= (JLabel) value;
		
		lblText.setText(zeile.getText());
		lblText.setEnabled(zeile.isEnabled());
		lblIcon.setEnabled(zeile.isEnabled());
		lblIcon.setIcon(zeile.getIcon());
		
		 
		if (isSelected) {
			setBackground(new Color(137, 179, 200));
		}else
		{
			setBackground(UIManager.getColor("ComboBox.background"));
		}
		
		
		add(lblIcon,BorderLayout.WEST);
		lblIcon.setVisible(true);
		add(lblText, BorderLayout.CENTER);
		lblText.setVisible(true);
		
		return this;
	}

}
