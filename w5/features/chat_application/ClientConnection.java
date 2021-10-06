import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.parser.ParseException;

public class ClientConnection extends Thread implements PropertyChangeListener {
	private PropertyChangeSupport support;
	private Socket socket = null;
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
	
	protected Boolean processMessage(Message msg)
	{
		switch (msg.type) {
		case MESSAGE:
			this.support.firePropertyChange("ClientConnectionMessage", null, msg);
			return true;
		default:
			return false;
		}
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				Message message = Message.deserialize(in.readLine());
				if (!processMessage(message)) {
					System.err.println("Message was not processed, unknown message type");
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