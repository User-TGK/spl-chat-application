package spl.chat_application;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

	public static void main(String[] args) {

		// Store all client threads
		ArrayList<ServerThread> threadList = new ArrayList<>();

		// Default server port
		int port = 1234;

		// Override server port
		if (args.length == 2) {
			try {
				port = Integer.parseInt(args[1]);

			} catch (NumberFormatException e) {
				System.err.println("Argument" + args[1] + " must be an integer.");
				System.exit(1);
			}
		}

		ServerSocket serverSocket = null;
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				System.out.println("I/O error: " + e);
			}
			ServerThread serverThread = new ServerThread(socket);

			threadList.add(serverThread);
			serverThread.start();
		}
	}

}
