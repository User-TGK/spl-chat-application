package assignment_3;

import java.io.*;
import java.net.*;
import java.beans.*;

import org.json.simple.parser.ParseException;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length != 2) {
			System.err.println("usage: HOST PORT");
			System.exit(-1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		Socket socket = new Socket(host, port);
		ClientConnection connection = new ClientConnection(socket);
		IUI ui = new ConsoleUI();

		connection.addPropertyChangeListener(ui);
		ui.addPropertyChangeListener(connection);

		connection.start();
		ui.run();
	}

}

class ClientConnection extends Thread implements PropertyChangeListener {
	private PropertyChangeSupport support;
	private Socket socket = null;
	//#if Authentication
//@	private Boolean auth = null;
	//#endif
	private PrintWriter writer;

	public ClientConnection(Socket socket) throws IOException {
		this.support = new PropertyChangeSupport(this);
		this.socket = socket;

		OutputStream output = socket.getOutputStream();
		this.writer = new PrintWriter(output, true);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.support.removePropertyChangeListener(pcl);
	}

	public void propertyChange(PropertyChangeEvent evt) throws IOException {
		this.sendData((Message) evt.getNewValue())
	}

	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			while (true) {
				Message message = Message.deserialize(in.readLine());
				if (message.type == MessageType.MESSAGE) {
					this.support.firePropertyChange("ClientConnection", "message", message);
				}
				//#if Authentication
//@				else if (message.type == MessageType.AUTH_RESPONSE) {
//@					this.auth = Boolean.valueOf(message.content);
//@					if (this.auth) {
						//#if Logging
//@						System.out.println("Authenticated you can now start messaging.");
						//#endif
//@					} else {
//@						throw new RuntimeException("Wrong username and/or password");
//@					}
//@				} 
				//#endif
				else {
					//#if Logging
//@					System.err.println("Received unknown message type");
					//#endif
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void sendData(Message msg) throws IOException {
		this.writer.println(msg.serialize());
	}
}
