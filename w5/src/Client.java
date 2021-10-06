import java.io.*; 
import java.net.*; import java.beans.*; 

import org.json.simple.parser.ParseException; 
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.net.Socket; 
import java.net.UnknownHostException; 

public   class  Client {
	
	protected String host;

	
	protected int port;

	
	protected ClientConnection connection;

	
	
	public Client(String host, int port)
	{
		this.host = host;
		this.port = port;
		this.connection = null;
	}

	
	
	 private void  run__wrappee__chat_application  () throws UnknownHostException, IOException
	{
		Socket socket = new Socket(host, port);
		connection = new ClientConnection(socket);

		connection.start();
	}

	
	protected void run() throws UnknownHostException, IOException
	{
		run__wrappee__chat_application();
		
		CLI ui = new CLI();

		connection.addPropertyChangeListener(ui);
		ui.addPropertyChangeListener(connection);
		ui.run();
	}

	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length != 2) {
			System.err.println("usage: HOST PORT");
			System.exit(-1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		Client c = new Client(host, port);
		c.run();
	}


}
