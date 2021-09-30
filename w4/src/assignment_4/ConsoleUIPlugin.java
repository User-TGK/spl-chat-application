package assignment_4;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.List;

public class ConsoleUIPlugin implements IUIPlugin {
	private PropertyChangeSupport support;
	private PluginRegistry registry;

	public ConsoleUIPlugin(PluginRegistry registry) {
		this.support = new PropertyChangeSupport(this);
		this.registry = registry;
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
			System.out.println(this.formatMessage((Message) evt.getNewValue()));
			break;
		case "ClientConnectionAuthorized":
			System.out.println("Authenticated you can now start messaging.");
			break;
		case "ClientConnectionUnauthorized":
			throw new RuntimeException("Wrong username and/or password");
		default:
			System.err.println("Unrecognized property changed: " + evt.getPropertyName());
			break;
		}
	}

	public void run(boolean authenticationEnabled) {
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter username");
		String username = null;
		try {
			username = stdIn.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			username = null;
		}

		if (authenticationEnabled) {

			System.out.println("Please enter password");
			String password = null;

			try {
				password = stdIn.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				password = null;
			}

			MessageColor c = MessageColor.BLACK;
			if (registry.colorer != null) {
				c = registry.colorer.getDefaultColor();
			}
			this.support.firePropertyChange("UI", "message", new Message(username, MessageType.AUTH, c, password));
		}

		String input = null;
		MessageColor color = MessageColor.BLACK;
		if (registry.colorer != null) {
			color = registry.colorer.getDefaultColor();
		}
		while (true) {
			try {
				input = stdIn.readLine();
				if (input == null) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (input.startsWith("/color")) {
				String[] cArgs = input.trim().split("\\s+");
				if (cArgs.length != 2) {
					System.err.println("Color command format: /color <COLOR>");
					continue;
				}
				
				MessageColor[] colors = MessageColor.values();
				if (registry.colorer != null) {
					colors = registry.colorer.getColors().toArray(new MessageColor[0]);
				}

				boolean colorExists = false;
				for (MessageColor c : colors) {
					if (cArgs[1].equalsIgnoreCase(c.toString())) {
						color = c;
						colorExists = true;
						break;
					}
				}
				
				if (!colorExists) {
					System.err.println("Unknown color, use one of the following:");
					for (MessageColor c : colors) {
						System.out.println(c.name());
					}
				}

				continue;
			}

			Message msg = new Message(username, MessageType.MESSAGE, color, input);
			this.support.firePropertyChange("UI", "message", msg);
		}
	}

	private String formatMessage(Message msg) {
		if (msg.type != MessageType.MESSAGE) {
			throw new IllegalArgumentException("Only messages can be printed.");
		}

		String startColor = "";

		switch (msg.color) {
		case BLACK:
			startColor = "\u001b[30m";
			break;
		case RED:
			startColor = "\u001b[31m";
			break;
		case GREEN:
			startColor = "\u001b[32m";
			break;
		case YELLOW:
			startColor = "\u001b[33m";
			break;
		case BLUE:
			startColor = "\u001b[34m";
			break;
		case MAGENTA:
			startColor = "\u001b[35m";
			break;
		case CYAN:
			startColor = "\u001b[36m";
			break;
		case WHITE:
			startColor = "\u001b[36m";
			break;
		}

		return startColor + msg.content + "\u001b[0m";
	}
}
