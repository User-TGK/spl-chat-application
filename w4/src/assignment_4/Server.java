package assignment_4;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Server {
	private int port;
	private Set<UserThread> connections = new HashSet<>();

	public PluginRegistry pluginRegistry;

	public Server(int port, PluginRegistry registry) {
		this.port = port;
		this.pluginRegistry = registry;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("New client connected");

				UserThread newConnection = new UserThread(socket, this);
				this.connections.add(newConnection);
				newConnection.start();
			}

		} catch (IOException ex) {
			System.out.println("Error in the server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java server PORT [plugin_1 plugin_2 ... plugin_n]");
			System.exit(-1);
		}

		PluginLoader loader = new PluginLoader();
		List<String> plugins = new ArrayList<String>();

		if (args.length > 1) {
			plugins = Arrays.asList(args).subList(1, args.length);
		}

		loader.load(plugins);

		int port = Integer.parseInt(args[0]);

		Server server = new Server(port, loader.registry);
		server.execute();
	}

	void broadcast(Message msg) {
		System.out.println(msg);

		Set<UserThread> clients = this.connections;

		if (pluginRegistry.authenticator != null) {

			for (UserThread user : clients) {
				if (pluginRegistry.authenticator.isAuthenticated(user.getConnectionId())) {
					user.sendMessage(msg);
				}
			}
		}

		else {
			for (UserThread user : clients) {
				user.sendMessage(msg);
			}
		}
	}

	void removeConnection(UserThread con) {
		if (connections.contains(con)) {
			connections.remove(con);
		}

		if (pluginRegistry.authenticator != null) {
			pluginRegistry.authenticator.remove(con.getConnectionId());
		}
	}
}

class UserThread extends Thread {
	private Socket socket;
	private Server server;
	private PrintWriter writer;

	public UserThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			MessageColor msgColor = MessageColor.BLACK;
			if (server.pluginRegistry.colorer != null) {
				msgColor = server.pluginRegistry.colorer.getDefaultColor();
			}
			String sName = "Server";
			String clientMessage;

			while ((clientMessage = reader.readLine()) != null) {

				String decryptedMessage = clientMessage;
				// Decrypt iterating over the list backwards

				for (int i = server.pluginRegistry.encryptors.size(); i-- > 0;) {
					IEncryptionPlugin plugin = server.pluginRegistry.encryptors.get(i);
					decryptedMessage = plugin.decrypt(decryptedMessage);
				}

				Message msg = Message.deserialize(decryptedMessage);

				switch (msg.type) {
				case AUTH:
					Message authResponse = new Message(sName, MessageType.AUTH_RESPONSE, msgColor, "true");

					if (server.pluginRegistry.authenticator != null) {
						if (!server.pluginRegistry.authenticator.authenticate(this.getConnectionId(), msg.content)) {
							authResponse.content = "false";
							this.sendMessage(authResponse);
							break;
						}
					}

					String serverMsg = "New user connected: " + msg.user;
					server.broadcast(new Message(sName, MessageType.MESSAGE, msgColor, serverMsg));

					this.sendMessage(authResponse);
					break;
				case AUTH_RESPONSE:
					System.out.println("Server received unexpected AUTH_RESPONSE");

					break;
				case MESSAGE:
					server.broadcast(msg);
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

	public String getConnectionId() {
		return this.socket.getRemoteSocketAddress().toString();
	}

	void sendMessage(Message message) {
		String msg = message.serialize();
		String encryptedMessage = msg;

		for (IEncryptionPlugin plugin : server.pluginRegistry.encryptors) {
			encryptedMessage = plugin.encrypt(encryptedMessage);
		}

		this.writer.println(encryptedMessage);
	}
}
