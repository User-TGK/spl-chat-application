package assignment_2_task_4;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Server {
	private final List<String> passwords = Arrays.asList("foo", "bar");

	private Set<UserThread> authenticated = new HashSet<>();
	private Set<UserThread> unauthenticated = new HashSet<>();

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(Config.Port)) {

			if (Config.EnableLogging) {
				System.out.println("Server is listening on port " + Config.Port);
			}

			while (true) {
				Socket socket = serverSocket.accept();

				if (Config.EnableLogging) {
					System.out.println("New client connected");
				}

				UserThread newConnection = new UserThread(socket, this);
				this.unauthenticated.add(newConnection);
				newConnection.start();
			}

		} catch (IOException ex) {
			if (Config.EnableLogging) {
				System.out.println("Error in the server: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			Config.parseArgs(args);
		} catch (IllegalArgumentException ie) {
			System.err.println(ie.getMessage());
			Config.usage();
			System.exit(-1);
		}

		Server server = new Server();
		server.execute();
	}

	void broadcast(Message msg, UserThread exclude) {
		if (Config.EnableLogging) {
			System.out.println(msg);
		}

		if (!Config.EnableAuthentication) {
			for (UserThread user : this.unauthenticated) {
				if (user != exclude) {
					user.sendMessage(msg);
				}
			}
		}
		else {
			for (UserThread user : this.authenticated) {
				if (user != exclude) {
					user.sendMessage(msg);
				}
			}
		}
	}

	boolean authenticate(String password, UserThread authenticator) {
		// Authentication is disabled, no need to move sockets from
		// unauthenticated to authenticated
		if (!Config.EnableAuthentication) {
			return true;
		}

		if (this.passwords.contains(password)) {
			this.unauthenticated.remove(authenticator);
			this.authenticated.add(authenticator);

			return true;
		}

		return false;
	}
}

class UserThread extends Thread {
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
				
				Message msg = Message.deserialize(clientMessage);

				switch (msg.type) {
				case AUTH:
					if (Config.EnableAuthentication) {					
						Message authResponse = new Message(sName, MessageType.AUTH_RESPONSE, msgColor, "false");
						
						if (server.authenticate(msg.content, this)) {
							this.authenticated = true;
							authResponse.content = "true";
							
							String serverMsg = "New user connected: " + msg.user;
							server.broadcast(new Message(sName, MessageType.MESSAGE, MessageColor.BLACK, serverMsg), this);
						}
						
						this.sendMessage(authResponse);
					}
					break;
				case AUTH_RESPONSE:
					if (Config.EnableLogging) {
						System.out.println("Server received unexpected AUTH_RESPONSE");
					}
					break;
				case MESSAGE:
					if (!Config.EnableAuthentication) {
						server.broadcast(msg, this);
					}
					else if (this.authenticated) {
						server.broadcast(msg, this);
					}

					break;
				}
			}
		}

		catch (IOException ex) {
			if (Config.EnableLogging) {
				System.out.println("Error in UserThread: " + ex.getMessage());
				ex.printStackTrace();
			}
		} catch (ParseException e) {
			if (Config.EnableLogging) {
				e.printStackTrace();
			}
		}

		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Config.EnableLogging) {
			System.out.println("Finished...");
		}
	}

	void sendMessage(Message message) {
		String msg = message.serialize();
		this.writer.println(msg);
	}
}
