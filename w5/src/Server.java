import java.io.*; 
import java.net.*; 
import java.util.Arrays; 
import java.util.HashSet; 
import java.util.List; 
import java.util.Set; 

import org.json.simple.parser.ParseException; 

public  class  Server {
	
	private int port;

	
	private final int MAX_CONNECTIONS = 100;

	

	private final UserThread[] unauthenticated;

	

	public Server(int port) {
		this.port = port;
		this.unauthenticated = new UserThread[MAX_CONNECTIONS];
	}

	

	public void execute() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);

			// #if Logging
			System.out.println("Server is listening on port " + port);
			// #endif

			while (true) {
				Socket socket = serverSocket.accept();

				// #if Logging
				System.out.println("New client connected");
				// #endif

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
			// #if Logging
			System.out.println("Error in the server: " + ex.getMessage());
			ex.printStackTrace();
			// #endif
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
		// #if Logging
		System.out.println(msg);
		// #endif

		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.unauthenticated[i] != null) {
				this.unauthenticated[i].sendMessage(msg);
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
	}


} 

 

class  UserThread  extends Thread {
	
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
							"true");

					this.sendMessage(authResponse);
					break;
				case AUTH_RESPONSE:
					// #if Logging
					System.out.println("Server received unexpected AUTH_RESPONSE");
					// #endif

					break;
				case MESSAGE:

					server.broadcast(msg);
					break;
				}
			}
		}

		catch (IOException ex) {
			// #if Logging
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
			// #endif
		} catch (ParseException e) {
			// #if Logging
			e.printStackTrace();
			// #endif
		} finally {
			server.removeConnection(this);
		}

		try {
			this.socket.close();
		} catch (IOException e) {
			// #if Logging
			e.printStackTrace();
			// #endif
		}
	}

	

	void sendMessage(Message message) {
		String msg = message.serialize();
		this.writer.println(msg);
	}


}
