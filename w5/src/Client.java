import java.io.*; 
import java.net.*; import java.beans.*; 

import org.json.simple.parser.ParseException; 
import java.io.IOException; 
import java.net.Socket; 
import java.net.UnknownHostException; 

public   class  Client {
	
	public static void main  (String[] args) throws UnknownHostException, IOException {
		if (args.length != 2) {
			System.err.println("usage: HOST PORT");
			System.exit(-1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		Socket socket = new Socket(host, port);
		ClientConnection connection = new ClientConnection(socket);

		UI ui = new UI();

		connection.addPropertyChangeListener(ui);
		ui.addPropertyChangeListener(connection);

		connection.start();
		ui.run();
	}


} 

 

class  ClientConnection  extends Thread  implements PropertyChangeListener {
	
	private PropertyChangeSupport support;

	
	private Socket socket = null;

	
	// #if Authentication
	private Boolean auth = null;

	
	// #endif
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

	

	public void propertyChange(PropertyChangeEvent evt) {
		try {
			this.sendData((Message) evt.getNewValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				Message message = Message.deserialize(in.readLine());
				if (message.type == MessageType.MESSAGE) {
					this.support.firePropertyChange("ClientConnectionMessage", null, message);
				}
				// #if Authentication
				else if (message.type == MessageType.AUTH_RESPONSE) {
					this.auth = Boolean.valueOf(message.content);
					if (this.auth) {
						this.support.firePropertyChange("ClientConnectionAuthorized", null, null);
					} else {
						this.support.firePropertyChange("ClientConnectionUnauthorized", null, null);
					}
				}
				// #endif
				else {
					// #if Logging
					System.err.println("Received unknown message type");
					// #endif
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
