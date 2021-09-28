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
	// #if Authentication
	private final List<String> passwords = Arrays.asList("foo", "bar");
	private Set<UserThread> authenticated = new HashSet<>();
	// #endif

	private int port;
	private Set<UserThread> unauthenticated = new HashSet<>();

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
				this.unauthenticated.add(newConnection);
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

		Set<UserThread> clients = this.unauthenticated;
		// #if Authentication
		clients = this.authenticated;
		// #endif

		for (UserThread user : clients) {
			user.sendMessage(msg);
		}
	}

	void removeConnection(UserThread con) {
		// #if Authentication
		if (authenticated.contains(con)) {
			authenticated.remove(con);
		}
		// #endif

		if (unauthenticated.contains(con)) {
			unauthenticated.remove(con);
		}
	}

	// #if Authentication
	boolean authenticate(String password, UserThread authenticator) {
		if (this.passwords.contains(password)) {
			this.unauthenticated.remove(authenticator);
			this.authenticated.add(authenticator);

			return true;
		}

		return false;
	}
	// #endif
}

class UserThread extends Thread {
	// #if Authentication
	boolean authenticated;
	// #endif
	private Socket socket;
	private Server server;
	private PrintWriter writer;

	public UserThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;

		// #if Authentication
		this.authenticated = false;
		// #endif
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

				String decryptedMessage = clientMessage;
				// Decrypt iterating over the list backwards

				for (int i = server.pluginRegistry.encryptors.size(); i-- > 0;) {
					IEncryptionPlugin plugin = server.pluginRegistry.encryptors.get(i);
					decryptedMessage = plugin.decrypt(decryptedMessage);
				}

				Message msg = Message.deserialize(decryptedMessage);

				switch (msg.type) {
				// #if Authentication
				case AUTH:
					Message authResponse = new Message(sName, MessageType.AUTH_RESPONSE,
							// #if Color
							msgColor,
							// #endif
							"false");

					if (server.authenticate(msg.content, this)) {
						this.authenticated = true;
						authResponse.content = "true";

						String serverMsg = "New user connected: " + msg.user;
						server.broadcast(new Message(sName, MessageType.MESSAGE,
								// #if Color
								MessageColor.BLACK,
								// #endif
								serverMsg));
					}

					this.sendMessage(authResponse);
					break;
				case AUTH_RESPONSE:
					System.out.println("Server received unexpected AUTH_RESPONSE");

					break;
				// #endif
				case MESSAGE:
					// #if Authentication
					if (this.authenticated) {
						server.broadcast(msg);
					}
					// #else
//@					server.broadcast(msg);
					// #endif
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

	void sendMessage(Message message) {
		String msg = message.serialize();
		String encryptedMessage = msg;

		for (IEncryptionPlugin plugin : server.pluginRegistry.encryptors) {
			encryptedMessage = plugin.encrypt(encryptedMessage);
		}

		this.writer.println(encryptedMessage);
	}
}
