package assignment_3;

import javax.swing.*;
import java.awt.*;

public class GraphicalUI implements IUI {
    private PropertyChangeSupport support;

	public ClientConnection() throws IOException {
        this.support = new PropertyChangeSupport(this);
	}

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		Message msg = (Message) evt.getNewValue();
		// TODO: append message to chat window.
	}


	private void run() {
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
		JPopupMenu menu = new JPopupMenu();
		for (MessageColor c : MessageColor.values()) { 
			menu.add(c.toString());
		}

		final JButton button = new JButton();
		button.setText("Color");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				menu.show(button, button.getBounds().x, button.getBounds().y
				+ button.getBounds().height);
			}
		});

		panel.add(label); // Components Added using Flow Layout
		panel.add(tf);
		panel.add(send);

		// Text Area at the Center
		JTextArea ta = new JTextArea();

		//Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.CENTER, ta);
		frame.setVisible(true);
	}

}