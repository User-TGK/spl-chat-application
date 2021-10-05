import java.beans.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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

		UI ui = new UI();

		connection.addPropertyChangeListener(ui);
		ui.addPropertyChangeListener(connection);

		connection.start();
		ui.run();
	}
}
