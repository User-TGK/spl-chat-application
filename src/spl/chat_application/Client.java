package spl.chat_application;

import java.io.*;
import java.net.*;

public class Client {

	private static String formatMessage(Message msg) {
		if (msg.type != MessageType.MESSAGE) {
			throw new ArgumentedException("Only messages can be printed.");
		}

		String startColor = "";

		switch (msg.color) {
		case MessageColor.BLACK:
			startColor = "\u001b[30m";
			break;
		case MessageColor.RED:
			startColor = "\u001b[31m";
			break;
		case MessageColor.GREEN:
			startColor = "\u001b[32m";
			break;
		case MessageColor.YELLOW:
			startColor = "\u001b[33m";
			break;
		case MessageColor.BLUE:
			startColor = "\u001b[34m";
			break;
		case MessageColor.MAGENTA:
			startColor = "\u001b[35m";
			break;
		case MessageColor.CYAN:
			startColor = "\u001b[36m";
			break;
		case MessageColor.WHITE:
			startColor = "\u001b[36m";
			break;
		}

		return startColor + msg.content + "\u001b[0m";
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("usage: HOST PORT");
			System.exit(-1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		System.out.println("Please enter username");
		String username = stdIn.readLine();

		System.out.println("Please enter password");
		String password = stdIn.readLine();

		Socket socket = new Socket(host, port);
		ClientConnection connection = new ClientConnection(socket);
		connection.start();

		connection.sendData(new Message(username, MessageType.AUTH, Color.BLACK, password));

		String input;
		MessageColor color = Color.BLACK;
		while ((input = stdIn.readLine()) != null) {
			if (input.StartsWith("/color")) {
				String[] args = input.trim().split("\\s+");
				if (args.length != 2) {
					System.err.println("Color command format: /color <COLOR>");
					continue;
				}

				try {
					color = MessageColor.valueOf(args[1].to);	
				}
			} else {
				connection.sendData(new Message(username, MessageType.AUTH, color, input));
			}
		}
	}

}

class ClientConnection extends Thread
{
	private Socket socket = null;
	private Boolean auth = null;

	public ClientConnection(Socket socket)
	{
		this.socket = socket;
	}

	public void run()
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			Message message = Message.deserialize(in.readLine());
			if (message.type == MessageType.AUTH_RESPONSE) {
				this.auth = Boolean.valueOf(message.content);
				if (this.auth) {
					System.out.println("Authenticated you can now start messaging.");
				}
				else {
					throw new RuntimeException("Wrong username and/or password");
				}
			}
			else {
				System.out.println(formatMessage(message));
			}
		}
	}

	public void sendData(Message msg) {
		try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			out.println(msg.serialize());
		}
	}
}
