import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.io.OutputStream; 
import java.io.PrintWriter; 
import java.net.Socket; 

import org.json.simple.parser.ParseException; public   class  ServerConnection  extends Thread {
	
	private Socket socket;

	
	private Server server;

	
	private PrintWriter writer;

	

	public ServerConnection(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}

	
	
	 private Boolean  processMessage__wrappee__chat_application  (Message msg)
	{
		switch (msg.type) {
		case MESSAGE:
			server.broadcast(msg);
			return true;
		default:
			return false;
		}
	}

	
	protected Boolean processMessage(Message msg)
	{
		Boolean alreadyHandled = processMessage__wrappee__chat_application(msg); 
		if (!alreadyHandled) {
			switch (msg.type) {
			case AUTH:
				Message authResponse = new Message("Server", MessageType.AUTH_RESPONSE, "false");
				
				if (server.authenticate(msg.content, this)) {
					authResponse.content = "true";
				}

				this.sendMessage(authResponse);
				return true;
			case AUTH_RESPONSE:
				System.out.println("Server received unexpected AUTH_RESPONSE");
				return true;
			default:
				break;
			}
		}
		
		return alreadyHandled;
	}

	

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			String clientMessage;
			while ((clientMessage = reader.readLine()) != null) {
				Message msg = Message.deserialize(clientMessage);
				if (!processMessage(msg)) {
					System.err.println("Message was not processed, unknown message type");
				}
			}
		}

		catch (IOException ex) {
			System.out.println("Error in ServerConnection: " + ex.getMessage());
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
		this.writer.println(msg);
	}


}
