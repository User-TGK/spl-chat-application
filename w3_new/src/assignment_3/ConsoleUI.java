package assignment_3;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class ConsoleUI implements IUI {
	private PropertyChangeSupport support;

	public ConsoleUI() {
		this.support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(this.formatMessage((Message) evt.getNewValue()));
	}

	public void run() {
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter username");
		String username = null;
		try {
			username = stdIn.readLine();
		}
		catch (IOException e) {
			//#if Logging
			e.printStackTrace();
			//#endif
			username = null;
		} 

		//#if Authentication
//@		System.out.println("Please enter password");
//@		String password = null;
//@
//@		try {
//@			password = stdIn.readLine();
//@		}
//@		catch (IOException e) {
			//#if Logging
//@			e.printStackTrace();
			//#endif
//@			password = null;
//@		} 
//@
		//#if Color
//@		this.conn.sendData(new Message(username, MessageType.AUTH, MessageColor.BLACK, password));
		//#else
//@		this.conn.sendData(new Message(username, MessageType.AUTH, password));
		//#endif
//@
		//#endif

		String input;

		//#if Color
//@		MessageColor color = MessageColor.BLACK;
		//#endif
		try {
			input = stdIn.readLine();
		}
		catch (IOException e) {
			//#if Logging
			e.printStackTrace();
			//#endif
			input = null;
		} 
		while (input != null) {
			//#if Color
//@			if (input.startsWith("/color")) {
//@				String[] cArgs = input.trim().split("\\s+");
//@				if (cArgs.length != 2) {
//@					System.err.println("Color command format: /color <COLOR>");
//@					continue;
//@				}
//@
//@				try {
//@					color = MessageColor.valueOf(cArgs[1].toUpperCase());
//@				} catch (Exception e) {
//@					System.err.println("Unknown color, use one of the following:");
//@					for (MessageColor c : MessageColor.values()) {
//@						System.out.println(c.name());
//@					}
//@				}
//@				
//@				continue;
//@			}
			//#endif

			//#if Color
			//@		Message msg = new Message(username, MessageType.MESSAGE, color, input)
			//#else
					Message msg = new Message(username, MessageType.MESSAGE, input);
			//#endif

			this.support.firePropertyChange("UI", "message", msg);

			try {
				input = stdIn.readLine();
			}
			catch (IOException e) {
				//#if Logging
					e.printStackTrace();
				//#endif
			} 
		}
	}

	private String formatMessage(Message msg) {
		if (msg.type != MessageType.MESSAGE) {
			throw new IllegalArgumentException("Only messages can be printed.");
		}

		//#if Color
//@		String startColor = "";
//@
//@		switch (msg.color) {
//@		case BLACK:
//@			startColor = "\u001b[30m";
//@			break;
//@		case RED:
//@			startColor = "\u001b[31m";
//@			break;
//@		case GREEN:
//@			startColor = "\u001b[32m";
//@			break;
//@		case YELLOW:
//@			startColor = "\u001b[33m";
//@			break;
//@		case BLUE:
//@			startColor = "\u001b[34m";
//@			break;
//@		case MAGENTA:
//@			startColor = "\u001b[35m";
//@			break;
//@		case CYAN:
//@			startColor = "\u001b[36m";
//@			break;
//@		case WHITE:
//@			startColor = "\u001b[36m";
//@			break;
//@		}
//@
//@		return startColor + msg.content + "\u001b[0m";
		//#else
		return msg.content;
		//#endif
	}
}
