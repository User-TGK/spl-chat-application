public class ServerConnection extends Thread {
	protected Boolean processMessage(Message msg)
	{
		Boolean alreadyHandled = original(msg); 
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
}