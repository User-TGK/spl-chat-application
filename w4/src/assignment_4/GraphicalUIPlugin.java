package assignment_4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

import javax.swing.border.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class GraphicalUIPlugin implements IUIPlugin {
	private PropertyChangeSupport support;

	private JFrame mainFrame = null;
	private JTextPane chatText = null;
	private JTextField chatLine = null;
	private JTextField userField = null;
	private JButton connectButton = null;
	private JTextField passwordField = null;
	// #ifdef Color
	private JComboBox<MessageColor> colorCombo = null;
	// #endif

	private String username;

	public GraphicalUIPlugin() {
		this.support = new PropertyChangeSupport(this);
		this.username = null;
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	// #if Color
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
	// #endif

	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "ClientConnectionMessage":
			Message message = (Message) evt.getNewValue();
			String msg = message.user + ": " + message.content + "\n";

			// #if Color
			Color c = this.mClrToJClr(message.color);
			// #else
//@			Color c = Color.BLACK;
			// #endif
			appendToPane(chatText, msg, c);
			break;
		case "ClientConnectionAuthorized":
			JOptionPane.showMessageDialog(mainFrame, "Authentication succeeded.");
			connectButton.setEnabled(false);
			chatLine.setEnabled(true);
			chatLine.requestFocus();
			break;
		case "ClientConnectionUnauthorized":
			JOptionPane.showMessageDialog(mainFrame, "Incorrect username or password", "Warning",
					JOptionPane.WARNING_MESSAGE);
			break;
		default:
			System.err.println("Unrecognized property changed: " + evt.getPropertyName());
			break;
		}
	}

	private void appendToPane(JTextPane tp, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		int len = tp.getDocument().getLength();
		try {
			tp.getDocument().insertString(len, msg, aset);
		} catch (Exception e) {

		}
	}

	private JPanel initOptionsPane(final boolean authenticationEnabled) {
		JPanel pane = null;

		// Create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(5, 1));

		// Username
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Username:"));
		userField = new JTextField(30);
		pane.add(userField);
		optionsPane.add(pane);

		// Password
		if (authenticationEnabled) {
			pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			pane.add(new JLabel("Password"));
			passwordField = new JTextField(30);
			pane.add(passwordField);
			optionsPane.add(pane);
		}

		// Connect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = userField.getText();
				username = user;
				// #if Color
				if (authenticationEnabled) {
					String password = passwordField.getText();
					support.firePropertyChange("UI", "message",
							new Message(user, MessageType.AUTH, MessageColor.BLACK, password));
					// #else
					// @ support.firePropertyChange("UI", "message", new Message(user,
					// MessageType.AUTH, password));
					// #endif
				} else {
					connectButton.setEnabled(false);
					chatLine.setEnabled(true);
					chatLine.requestFocus();
				}
			}
		});
		buttonPane.add(connectButton);
		optionsPane.add(buttonPane);

		optionsPane.setPreferredSize(new Dimension(500, 200));

		return optionsPane;
	}

	private void initGUI(boolean authenticationEnabled) {
		// Set up the options pane
		JPanel optionsPane = initOptionsPane(authenticationEnabled);

		// Set up the chat pane
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextPane();
		chatText.setEditable(false);
		chatText.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		chatText.setMargin(new Insets(5, 5, 5, 5));
		JScrollPane jsp = new JScrollPane(chatText);

		chatLine = new JTextField();
		chatLine.setEnabled(false);
		chatLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = chatLine.getText();
				chatLine.setText("");
				// #if Color
				MessageColor c = (MessageColor) colorCombo.getSelectedItem();
				support.firePropertyChange("UI", "message", new Message(username, MessageType.MESSAGE, c, s));
				// #else
//@		   			support.firePropertyChange("UI", "message", new Message(username, MessageType.MESSAGE, s));
				// #endif
			}
		});
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(jsp, BorderLayout.CENTER);

		// #ifdef Color
		// Dropdown
		colorCombo = new JComboBox<MessageColor>(MessageColor.values());
		chatPane.add(colorCombo, BorderLayout.NORTH);
		// #endif

		chatPane.setPreferredSize(new Dimension(500, 200));

		// Set up the main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);

		// Set up the main frame
		mainFrame = new JFrame("SPL Chat");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public void run(boolean authenticationEnabled) {
		initGUI(authenticationEnabled);
	}

}
