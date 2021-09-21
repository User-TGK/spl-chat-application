package assignment_3;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Server {
	//#if Authentication
//@	private final List<String> passwords = Arrays.asList("foo", "bar");
//@	private Set<UserThread> authenticated = new HashSet<>();
	//#endif

	private int port;
	private Set<UserThread> unauthenticated = new HashSet<>();

	public Server(int port) {
		this.port = port;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			//#if Logging
			System.out.println("Server is listening on port " + port);
			//#endif

			while (true) {
				Socket socket = serverSocket.accept();
				//#if Logging
				System.out.println("New client connected");
				//#endif

				UserThread newConnection = new UserThread(socket, this);
				this.unauthenticated.add(newConnection);
				newConnection.start();
			}

		} catch (IOException ex) {
			//#if Logging
			System.out.println("Error in the server: " + ex.getMessage());
			ex.printStackTrace();
			//#endif
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

	void broadcast(Message msg, UserThread exclude) {
		//#if Logging
		System.out.println(msg);
		//#endif

		Set<UserThread> clients = this.unauthenticated;
		//#if Authentication
//@		clients = this.authenticated;
		//#endif

		for (UserThread user : clients) {
			if (user != exclude) {
				user.sendMessage(msg);
			}
		}
	}

	//#if Authentication
//@	boolean authenticate(String password, UserThread authenticator) {
//@		if (this.passwords.contains(password)) {
//@			this.unauthenticated.remove(authenticator);
//@			this.authenticated.add(authenticator);
//@
//@			return true;
//@		}
//@
//@		return false;
//@	}
	//#endif
}

class UserThread extends Thread {
	//#if Authentication
//@	boolean authenticated;
	//#endif
	private Socket socket;
	private Server server;
	private PrintWriter writer;

	public UserThread(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;

		//#if Authentication
//@		this.authenticated = false;
		//#endif
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			//#if Color
//@			MessageColor msgColor = MessageColor.BLACK;
			//#endif
			String sName = "Server";
			String clientMessage;

			while (true) {

				clientMessage = reader.readLine();
				Message msg = Message.deserialize(clientMessage);

				switch (msg.type) {
				//#if Authentication
//@				case AUTH:
//@					Message authResponse = new Message(
//@						sName,
//@						MessageType.AUTH_RESPONSE,
						//#if Color
//@						msgColor,
						//#endif
//@						"false");
//@
//@					if (server.authenticate(msg.content, this)) {
//@						this.authenticated = true;
//@						authResponse.content = "true";
//@
//@						String serverMsg = "New user connected: " + msg.user;
//@						server.broadcast(new Message(
//@							sName,
//@							MessageType.MESSAGE,
							//#if Color 
//@							MessageColor.BLACK,
							//#endif
//@							serverMsg), this);
//@					}
//@
//@					this.sendMessage(authResponse);
//@					break;
//@				case AUTH_RESPONSE:
					//#if Logging
//@					System.out.println("Server received unexpected AUTH_RESPONSE");
					//#endif
//@
//@					break;
				//#endif
				case MESSAGE:
					//#if Authentication
//@					if (this.authenticated) {
//@						server.broadcast(msg, this);
//@					}
					//#else
					server.broadcast(msg, this);
					//#endif
					break;
				}
			}
		}

		catch (IOException ex) {
			//#if Logging
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
			//#endif
		} catch (ParseException e) {
			//#if Logging
			e.printStackTrace();
			//#endif
		}

		try {
			this.socket.close();
		} catch (IOException e) {
			//#if Logging
			e.printStackTrace();
			//#endif
		}
	}

	void sendMessage(Message message) {
		String msg = message.serialize();
		this.writer.println(msg);
	}
}
