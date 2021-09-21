package assignment_2_task_5;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Server extends Observable {
	private final List<String> passwords = Arrays.asList("foo", "bar");

	private int port;
	private Set<UserThread> threads = new HashSet<>();


	public Server(int port) {
		this.port = port;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("New client connected");

				UserThread newConnection = new UserThread(socket, this);
				this.threads.add(newConnection);
				newConnection.start();
			}

		} catch (IOException ex) {
			System.out.println("Error in the server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java server PORT");
			System.exit(0);
		}

		int port = Integer.parseInt(args[0]);

		Server server = new Server(port);
		server.execute();
	}

	public void sendMessage(Message msg) {
		if (msg.type == MessageType.MESSAGE) {
			setChanged();
			notifyObservers(msg);
		}
	}

	boolean authenticate(String password, UserThread authenticator) {
		return this.passwords.contains(password);
	}
}

class UserThread extends Thread implements Observer {
	boolean authenticated;
	private Socket socket;
	private Server server;
	private PrintWriter writer;

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

			MessageColor msgColor = MessageColor.BLACK;
			String sName = "Server";
			String clientMessage;

			while (true) {

				clientMessage = reader.readLine();

				if (clientMessage == null) {
					continue;
				}

				Message msg = Message.deserialize(clientMessage);
				this.server.sendMessage(msg);

				switch (msg.type) {
				case AUTH:
					Message authResponse = new Message(sName, MessageType.AUTH_RESPONSE, msgColor, "false");

					if (server.authenticate(msg.content, this)) {
						authResponse.content = "true";

						String serverMsg = "New user connected: " + msg.user;
						this.server.sendMessage(new Message(sName, MessageType.MESSAGE, MessageColor.BLACK, serverMsg));
						this.server.addObserver(this);
						this.authenticated = true;
					}

					this.sendMessage(authResponse);
					break;
				case AUTH_RESPONSE:
					System.out.println("Server received unexpected AUTH_RESPONSE");
					break;
				case MESSAGE:
					System.out.println("Server received MESSAGE: " + msg.toString());
					break;
				}
			}
		}

		catch (IOException ex) {
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished...");
	}

	@Override
	public void update(Observable o, Object message) {
		this.sendMessage((Message) message);
	}

	void sendMessage(Message message) {
		String msg = message.serialize();
		this.writer.println(msg);
	}
}
