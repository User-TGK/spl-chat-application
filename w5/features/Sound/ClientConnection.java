import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.parser.ParseException;

public class ClientConnection extends Thread implements PropertyChangeListener {
	public void sendData(Message msg) throws IOException {
		original(msg);
		Toolkit.getDefaultToolkit().beep();
	}
	
	protected Boolean processMessage(Message msg)
	{
		Boolean b = original(msg); 
		if (b) {
			Toolkit.getDefaultToolkit().beep();
		}
		
		return b;
	}
}