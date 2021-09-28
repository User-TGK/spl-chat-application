package assignment_4;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.beans.*;

import org.json.simple.parser.ParseException;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length < 2) {
			System.err.println("usage: HOST PORT [plugin_1 plugin_2 ... plugin_n]");
			System.exit(-1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		PluginLoader loader = new PluginLoader();
		List<String> plugins = new ArrayList<String>();

		if (args.length > 2) {
			plugins = Arrays.asList(args).subList(2, args.length);
		}

		loader.load(plugins);

		Socket socket = new Socket(host, port);
		ClientConnection connection = new ClientConnection(socket, loader.registry);

		for (IUIPlugin ui : loader.registry.uis) {
			connection.addPropertyChangeListener(ui);
			ui.addPropertyChangeListener(connection);
		}

		connection.start();
		
		boolean authenticationEnabled = loader.registry.authenticator != null;

		for (IUIPlugin ui : loader.registry.uis) {
			ui.run(authenticationEnabled);
		}
	}
}

class ClientConnection extends Thread implements PropertyChangeListener {
	private PropertyChangeSupport support;
	private Socket socket = null;
	private Boolean auth = null;
	private PrintWriter writer;

	private PluginRegistry pluginRegistry;

	public ClientConnection(Socket socket, PluginRegistry registry) throws IOException {
		this.support = new PropertyChangeSupport(this);
		this.socket = socket;

		OutputStream output = socket.getOutputStream();
		this.writer = new PrintWriter(output, true);

		this.pluginRegistry = registry;
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		try {
			this.sendData((Message) evt.getNewValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			while (true) {
				String msg = in.readLine();
				String decryptedMessage = msg;
				for (int i = pluginRegistry.encryptors.size(); i-- > 0;) {
					IEncryptionPlugin plugin = pluginRegistry.encryptors.get(i);
					decryptedMessage = plugin.decrypt(decryptedMessage);
				}

				Message message = Message.deserialize(decryptedMessage);
				if (message.type == MessageType.MESSAGE) {
					this.support.firePropertyChange("ClientConnectionMessage", null, message);
				} else if (message.type == MessageType.AUTH_RESPONSE) {
					this.auth = Boolean.valueOf(message.content);
					if (this.auth) {
						this.support.firePropertyChange("ClientConnectionAuthorized", null, null);
					} else {
						this.support.firePropertyChange("ClientConnectionUnauthorized", null, null);
					}
				} else {
					System.err.println("Received unknown message type");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void sendData(Message msg) throws IOException {
		String message = msg.serialize();
		String encryptedMessage = message;

		for (IEncryptionPlugin plugin : pluginRegistry.encryptors) {
			encryptedMessage = plugin.encrypt(encryptedMessage);
		}

		this.writer.println(encryptedMessage);
	}
}
