import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Server {
	private int port;
	private final int MAX_CONNECTIONS = 100;

	private ServerConnection[] unauthenticated;

	public Server(int port) {
		this.port = port;
		this.unauthenticated = new ServerConnection[MAX_CONNECTIONS];
	}

	public void execute() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();

				System.out.println("New client connected");

				ServerConnection newConnection = new ServerConnection(socket, this);

				for (int i = 0; i < MAX_CONNECTIONS; i++) {
					if (this.unauthenticated[i] == null) {
						this.unauthenticated[i] = newConnection;
						break;
					}
				}

				newConnection.start();
			}

		} catch (IOException ex) {
			System.out.println("Error in the server: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException ioe) {
				System.err.println("Could not close server socket: " + ioe.getMessage());
			}
			
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java server PORT");
			System.exit(-1);
		}

		int port = Integer.parseInt(args[0]);

		Server server = new Server(port);
		server.execute();
	}

	void broadcast(Message msg) {
		System.out.println(msg);

		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.unauthenticated[i] != null) {
				this.unauthenticated[i].sendMessage(msg);
			}
		}
	}

	void removeConnection(ServerConnection con) {
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.unauthenticated[i] != null && this.unauthenticated[i].equals(con)) {
				this.unauthenticated[i] = null;
				break;
			}
		}
	}
}
