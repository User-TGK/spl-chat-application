import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class GUI implements PropertyChangeListener {
	private JComboBox<MessageColor> colorCombo = null;

	private Color mClrToJClr(MessageColor c) {
		switch (c) {
		case BLACK:
			return Color.BLACK;
		case RED:
			return Color.RED;
		case GREEN:
			return Color.GREEN;
		case YELLOW:
			return Color.YELLOW;
		case BLUE:
			return Color.BLUE;
		case MAGENTA:
			return Color.MAGENTA;
		case CYAN:
			return Color.CYAN;
		case WHITE:
			return Color.WHITE;
		default:
			return Color.BLACK;
		}
	}

	private void appendToPane(JTextPane tp, Message msg) {
		Color c = this.mClrToJClr(msg.color);
		String m = msg.user + ": " + msg.content + "\n";
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		int len = tp.getDocument().getLength();
		try {
			tp.getDocument().insertString(len, m, aset);
		} catch (Exception e) {

		}
	}

	private Message constructMessage(String username, MessageType type, String content) {
		Color c = this.colorCombo.getSelectedItem();
		return new Message(username, type, c, content);
	}
	
	private JPanel initOptionsPane() {

		original();
		this.colorCombo = new JComboBox<MessageColor>(MessageColor.values());
		chatPane.add(this.colorCombo, BorderLayout.NORTH);
	}
}