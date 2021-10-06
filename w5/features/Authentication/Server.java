import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;

public class Server {
	private ServerConnection[] authenticated = new ServerConnection[MAX_CONNECTIONS];
	private final String[] passwords = { "foo", "bar" };

	public void broadcast(Message msg) {
		System.out.println(msg);

		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.authenticated[i] != null) {
				this.authenticated[i].sendMessage(msg);
			}
		}
	}

	public void removeConnection(ServerConnection con) {
		original(con);

		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (this.authenticated[i] != null && this.authenticated[i].equals(con)) {
				this.authenticated[i] = null;
				break;
			}
		}
	}

	public boolean authenticate(String password, ServerConnection authenticator) {
		for (int i = 0; i < this.passwords.length; i++) {
			if (this.passwords[i].equals(password)) {

				// Remove ServerConnection from unauthenticated
				for (int j = 0; j < MAX_CONNECTIONS; j++) {
					if (unauthenticated[j] != null && unauthenticated[j].equals(authenticator)) {
						unauthenticated[j] = null;
						break;
					}
				}

				// Add ServerConnection to authenticated
				for (int j = 0; i < MAX_CONNECTIONS; j++) {
					if (authenticated[j] == null) {
						authenticated[j] = authenticator;
						break;
					}
				}

				return true;
			}
		}

		return false;
	}
}
