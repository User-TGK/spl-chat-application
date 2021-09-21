package assignment_3;

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

public class GraphicalUI implements IUI {
    private PropertyChangeSupport support;
    //#if Color
	private MessageColor color;
	//#endif
	private String username;
	private JTextPane textPane;

	public GraphicalUI() throws IOException {
        this.support = new PropertyChangeSupport(this);
        //#if Color
		this.color = MessageColor.BLACK;
		//#endif
		this.username = null;
		this.textPane = null;
	}

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	//#if Color
	private Color mClrToJClr(MessageColor c) {
//@		switch (c) {
//@		case BLACK:
//@			return Color.BLACK;
//@		case RED:
//@			return Color.RED;
//@		case GREEN:
//@			return Color.GREEN;
//@		case YELLOW:
//@			return Color.YELLOW;
//@		case BLUE:
//@			return Color.BLUE;
//@		case MAGENTA:
//@			return Color.MAGENTA;
//@		case CYAN:
//@			return Color.CYAN;
//@		case WHITE:
//@			return Color.WHITE;
//@		}
	}
	//#endif

	public void propertyChange(PropertyChangeEvent evt) {
		Message message = (Message) evt.getNewValue();
		String msg = message.user + ": " message.content;

		//#if Color
//@		Color c = this.mClrToJClr(message.color);
		//#else
		Color c = Color.BLACK;
		//#endif
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = this.textPane.getDocument().getLength();
		this.textPane.setCaretPosition(len);
		this.textPane.setCharacterAttributes(aset, false);
		this.textPane.replaceSelection(msg);
	}


	public void run() {
		GraphicalUI that = this;
		JFrame frame = new JFrame("Chat Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);

		//Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("FILE");
		JMenu m2 = new JMenu("Help");
		mb.add(m1);
		mb.add(m2);
		JMenuItem m11 = new JMenuItem("Open");
		JMenuItem m22 = new JMenuItem("Save as");
		m1.add(m11);
		m1.add(m22);

		//Creating the panel at bottom and adding components
		JPanel panel = new JPanel(); // the panel is not visible in output
		JLabel label = new JLabel("");

		JTextField tf = new JTextField(1000); // accepts upto 10 characters
		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String input = tf.getText();
				//#if Color
				//@		Message msg = new Message(that.username, MessageType.MESSAGE, color, input)
				//#else
						Message msg = new Message(that.username, MessageType.MESSAGE, input);
				//#endif
				that.support.firePropertyChange("UI", "message", msg);
			}
		});

		//#if Color
		JPopupMenu clrMenu = new JPopupMenu();
		for (MessageColor c : MessageColor.values()) {
			JMenuItem cmi = new JMenuItem(c.toString());
			cmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					that.color = c;
				}
			});

			clrMenu.add(cmi);
		}

		final JButton clrButton = new JButton();
		clrButton.setText("Color");
		clrButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				clrMenu.show(clrButton, clrButton.getBounds().x, clrButton.getBounds().y
				+ clrButton.getBounds().height);
			}
		});
		//#endif

		panel.add(label); // Components Added using Flow Layout
		panel.add(tf);
		panel.add(send);
		
		//#if Color
		panel.add(clrButton);
		//#endif
		
		// Text Area at the Center
		this.textPane = new JTextPane();

		//Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.CENTER, this.textPane);
		frame.setVisible(true);
	}

}