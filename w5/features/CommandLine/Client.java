import java.beans.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	protected void run() throws UnknownHostException, IOException
	{
		original();
		
		CLI ui = new CLI();

		connection.addPropertyChangeListener(ui);
		ui.addPropertyChangeListener(connection);
		ui.run();
	}
} 
