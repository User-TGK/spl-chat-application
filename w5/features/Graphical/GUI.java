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
	private PropertyChangeSupport support;

	private JFrame mainFrame = null;
	private JTextPane chatText = null;
	private JTextField chatLine = null;
	private JTextField userField = null;
	private JButton connectButton = null;
	// #ifdef Authentication
	private JTextField passwordField = null;
	// #endif

	private String username;

	public GUI() {
		this.support = new PropertyChangeSupport(this);
		this.username = null;
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "ClientConnectionMessage":
			Message message = (Message) evt.getNewValue();
			appendToPane(chatText, message);
			break;
		// #ifdef Authentication
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
		// #endif
		default:
			// #if Logging
			System.err.println("Unrecognized property changed: " + evt.getPropertyName());
			// #endif
			break;
		}
	}

	private void appendToPane(JTextPane tp, Message msg) {
		String m = msg.user + ": " + msg.content + "\n";
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.black);
		int len = tp.getDocument().getLength();
		try {
			tp.getDocument().insertString(len, m, aset);
		} catch (Exception e) {

		}
	}

	private JPanel initOptionsPane() {
		JPanel pane = null;

		// Create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(5, 1));

		// Username
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Username:"));
		userField = new JTextField(30);
		pane.add(userField);
		optionsPane.add(pane);

		// #ifdef Authentication
		// Password
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Password"));
		passwordField = new JTextField(30);
		pane.add(passwordField);
		optionsPane.add(pane);
		// #endif

		// Connect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = userField.getText();
				username = user;
				// #if Authentication
				String password = passwordField.getText();
				
				support.firePropertyChange("UI", "message", constructMessage(user, MessageType.AUTH, password));
				// #else
//@	            	connectButton.setEnabled(false);
//@	            	chatLine.setEnabled(true);
//@	            	chatLine.requestFocus();
				// #endif
			}
		});
		buttonPane.add(connectButton);
		optionsPane.add(buttonPane);

		optionsPane.setPreferredSize(new Dimension(500, 200));

		return optionsPane;
	}

	private void initGUI() {
		// Set up the options pane
		JPanel optionsPane = initOptionsPane();

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
				support.firePropertyChange("UI", "message", constructMessage(username, MessageType.MESSAGE, s));
			}
		});
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(jsp, BorderLayout.CENTER);

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

	private Message constructMessage(String username, MessageType type, String content) {
		return new Message(username, type, content);
	}
	
	public void run() {
		initGUI();
	}

}