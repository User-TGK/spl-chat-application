import java.io.*; 
import java.net.*; 
import java.beans.*; 

import org.json.simple.parser.ParseException; import java.awt.BorderLayout; 
import java.awt.Color; 
import java.awt.Dimension; 
import java.awt.FlowLayout; 
import java.awt.GridLayout; 
import java.awt.Insets; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.io.IOException; 
import java.net.Socket; 
import java.net.UnknownHostException; 

import javax.swing.BorderFactory; 
import javax.swing.JButton; 
import javax.swing.JComboBox; 
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JOptionPane; 
import javax.swing.JPanel; 
import javax.swing.JScrollPane; 
import javax.swing.JTextField; 
import javax.swing.JTextPane; 
import javax.swing.text.AttributeSet; 
import javax.swing.text.SimpleAttributeSet; 
import javax.swing.text.StyleConstants; 
import javax.swing.text.StyleContext; 

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
		
		GUI ui = new GUI();

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
