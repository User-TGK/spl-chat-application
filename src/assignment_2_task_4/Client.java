package assignment_2_task_4;

import java.io.*;
import java.net.*;

import org.json.simple.parser.ParseException;

public class Client {

	public static String formatMessage(Message msg) {
		if (msg.type != MessageType.MESSAGE) {
			throw new IllegalArgumentException("Only messages can be printed.");
		}

		String startColor = "";

		switch (msg.color) {
		case BLACK:
			startColor = "\u001b[30m";
			break;
		case RED:
			startColor = "\u001b[31m";
			break;
		case GREEN:
			startColor = "\u001b[32m";
			break;
		case YELLOW:
			startColor = "\u001b[33m";
			break;
		case BLUE:
			startColor = "\u001b[34m";
			break;
		case MAGENTA:
			startColor = "\u001b[35m";
			break;
		case CYAN:
			startColor = "\u001b[36m";
			break;
		case WHITE:
			startColor = "\u001b[36m";
			break;
		}

		return startColor + msg.content + "\u001b[0m";
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
			Config.parseArgs(args);
		} catch (IllegalArgumentException ie) {
			System.err.println(ie.getMessage());
			Config.usage();
			System.exit(-1);
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter username");
		String username = stdIn.readLine();

		Socket socket = new Socket(Config.Host, Config.Port);
		ClientConnection connection = new ClientConnection(socket);
		connection.start();

		if (Config.EnableAuthentication) {
			System.out.println("Please enter password");
			String password = stdIn.readLine();

			try {
				connection.sendData(new Message(username, MessageType.AUTH, MessageColor.BLACK, password));
			} catch (IOException e1) {
				if (Config.EnableLogging) {
					e1.printStackTrace();
				}
			}
		}

		String input;
		MessageColor color = MessageColor.BLACK;
		while ((input = stdIn.readLine()) != null) {
			if (input.startsWith("/color")) {
				String[] cArgs = input.trim().split("\\s+");
				if (cArgs.length != 2) {
					System.err.println("Color command format: /color <COLOR>");
					continue;
				}

				try {
					color = MessageColor.valueOf(cArgs[1].toUpperCase());
				} catch (Exception e) {
					System.err.println("Unknown color, use one of the following:");
					for (MessageColor c : MessageColor.values()) {
						System.out.println(c.name());
					}
				}
			} else {
				try {
					connection.sendData(new Message(username, MessageType.MESSAGE, color, input));
				} catch (IOException e) {
					if (Config.EnableLogging) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}

class ClientConnection extends Thread {
	private Socket socket = null;
	private Boolean auth = null;
	private PrintWriter writer;

	public ClientConnection(Socket socket) throws IOException {
		this.socket = socket;

		OutputStream output = socket.getOutputStream();
		this.writer = new PrintWriter(output, true);
	}

	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			while (true) {
				Message message = Message.deserialize(in.readLine());
				if (message.type == MessageType.AUTH_RESPONSE) {
					if (Config.EnableAuthentication) {
						this.auth = Boolean.valueOf(message.content);
						if (this.auth) {
							System.out.println("Authenticated you can now start messaging.");
						} else {
							throw new RuntimeException("Wrong username and/or password");
						}
					}
				} else if (message.type == MessageType.MESSAGE) {
					System.out.println(Client.formatMessage(message));
				} else {
					if (Config.EnableLogging) {
						System.err.println("Received unknown message type");
					}
				}
			}
		} catch (IOException e) {
			if (Config.EnableLogging) {
				e.printStackTrace();
			}
			System.exit(-1);
		} catch (ParseException e) {
			if (Config.EnableLogging) {
				e.printStackTrace();
			}
			System.exit(-1);
		}
	}

	public void sendData(Message msg) throws IOException {
		if (Config.EnableLogging) {
			System.out.println("Sending out msg: " + msg);
		}

		this.writer.println(msg.serialize());
	}
}
