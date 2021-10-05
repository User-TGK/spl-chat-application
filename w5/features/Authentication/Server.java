import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Server {
	private UserThread[] authenticated;
	private final String[] passwords = { "foo", "bar" };

	public Server(int port) {
		this.port = port;
		this.unauthenticated = new UserThread[MAX_CONNECTIONS];
		this.authenticated = new UserThread[MAX_CONNECTIONS];
	}

	public void execute() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();

				System.out.println("New client connected");

				UserThread newConnection = new UserThread(socket, this);

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
		}
	}

	void broadcast(Message msg) {
		System.out.println(msg);

		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.authenticated[i] != null) {
				this.authenticated[i].sendMessage(msg);
			}
		}
	}

	void removeConnection(UserThread con) {
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.unauthenticated[i] != null && this.unauthenticated[i] == con) {
				this.unauthenticated[i] = null;
				break;
			}
		}

		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.authenticated[i] != null && this.authenticated[i] == con) {
				this.authenticated[i] = null;
				break;
			}
		}
	}

	boolean authenticate(String password, UserThread authenticator) {
		for (int i = 0; i < this.passwords.length; i++) {
			if (this.passwords[i].equals(password)) {

				// Remove userthread from unauthenticated
				for (int j = 0; j < MAX_CONNECTIONS; j++) {
					if (unauthenticated[j] == authenticator) {
						unauthenticated[j] = null;
						break;
					}
				}

				// Add userthread to authenticated
				for (int j = 0; i < MAX_CONNECTIONS; j++) {
					if (authenticated[j] == null) {
						authenticated[j] = authenticator;
						break;
					}
				}

				return true;
			}
		}

		return false;
	}
}

class UserThread extends Thread {
	private boolean authenticated;

	public UserThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;

		this.authenticated = false;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			// #if Color
			MessageColor msgColor = MessageColor.BLACK;
			// #endif
			String sName = "Server";
			String clientMessage;

			while ((clientMessage = reader.readLine()) != null) {
				Message msg = Message.deserialize(clientMessage);

				switch (msg.type) {
				case AUTH:
					Message authResponse = new Message(sName, MessageType.AUTH_RESPONSE,
							// #if Color
							msgColor,
							// #endif
							"false");

					if (server.authenticate(msg.content, this)) {
						this.authenticated = true;
						authResponse.content = "true";
					}

					this.sendMessage(authResponse);
					break;
				case AUTH_RESPONSE:
					System.out.println("Server received unexpected AUTH_RESPONSE");

					break;
				case MESSAGE:
					if (this.authenticated) {
						server.broadcast(msg);
					}
					break;
				}
			}
		}

		catch (IOException ex) {
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			server.removeConnection(this);
		}

		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
