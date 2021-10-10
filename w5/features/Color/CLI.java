import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLI {
	private MessageColor color;

	private void handleInput(String username, String line) {
		if (line.startsWith("/color")) {
			String[] cArgs = line.trim().split("\\s+");
			if (cArgs.length != 2) {
				System.err.println("Color command format: /color <COLOR>");
				continue;
			}

			try {
				this.color = MessageColor.valueOf(cArgs[1].toUpperCase());
			} catch (Exception e) {
				System.err.println("Unknown color, use one of the following:");
				for (MessageColor c : MessageColor.values()) {
					System.out.println(c.name());
				}
			}

			continue;
		}
		else {
			original(username, line);
		}
	}
	
	private Message constructMessage(String username, MessageType type, String content) {
		return new Message(username, MessageType.MESSAGE, this.color, content);
	}

	private String formatMessage(Message msg) {
		if (msg.type != MessageType.MESSAGE) {
			throw new IllegalArgumentException("Only messages can be printed.");
		}

		// #if Color
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
}