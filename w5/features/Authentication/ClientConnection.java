import java.beans.PropertyChangeListener;

public class ClientConnection extends Thread implements PropertyChangeListener {
	protected Boolean processMessage(Message msg)
	{
		Boolean alreadyHandled = original(msg); 
		if (!alreadyHandled) {
			switch (msg.type) {
			case AUTH_RESPONSE:
				Boolean auth = Boolean.valueOf(msg.content);
				if (auth) {
					this.support.firePropertyChange("ClientConnectionAuthorized", null, null);
				} else {
					this.support.firePropertyChange("ClientConnectionUnauthorized", null, null);
				}
				return true;
			default:
				break;
			}
		}
		
		return alreadyHandled;
	}
}