import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLI implements PropertyChangeListener {
	private PropertyChangeSupport support;

	public CLI() {
		this.support = new PropertyChangeSupport(this);
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
			// #if Logging
			System.out.println("Authenticated you can now start messaging.");
			// #endif
			break;
		case "ClientConnectionUnauthorized":
			throw new RuntimeException("Wrong username and/or password");
		default:
			// #if Logging
			System.err.println("Unrecognized property changed: " + evt.getPropertyName());
			// #endif
			break;
		}
	}

	public void run() {
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter username");
		String username = null;
		try {
			username = stdIn.readLine();
		} catch (IOException e) {
			// #if Logging
			e.printStackTrace();
			// #endif
			username = null;
		}

		// #if Authentication
		System.out.println("Please enter password");
		String password = null;

		try {
			password = stdIn.readLine();
		} catch (IOException e) {
			// #if Logging
			e.printStackTrace();
			// #endif
			password = null;
		}

		this.support.firePropertyChange("UI", "message", new Message(username, MessageType.AUTH, MessageColor.BLACK, password));

		String input = null;
		while (true) {
			try {
				input = stdIn.readLine();
				if (input == null) {
					break;
				}
			} catch (IOException e) {
				// #if Logging
				e.printStackTrace();
				// #endif
			}

    		this.handleInput(username, input);
		}
	}
	
	private void handleInput(String username, String line) {
		Message msg = this.constructMessage(username, MessageType.MESSAGE, line);
		this.support.firePropertyChange("UI", "message", msg);
	}
	
	private Message constructMessage(String username, MessageType type, String content) {
		return new Message(username, MessageType.MESSAGE, content);
	}

	private String formatMessage(Message msg) {
		if (msg.type != MessageType.MESSAGE) {
			throw new IllegalArgumentException("Only messages can be printed.");
		}

		return msg.content;
	}
}