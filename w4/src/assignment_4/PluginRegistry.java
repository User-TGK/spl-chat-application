package assignment_4;

import java.util.ArrayList;
import java.util.List;

public class PluginRegistry {
	public List<IEncryptionPlugin> encryptors;
	public List<IUIPlugin> uis;
	public IAuthenticationPlugin authenticator;
	public IColorPlugin colorer;

	public PluginRegistry() {
		encryptors = new ArrayList<IEncryptionPlugin>();
		uis = new ArrayList<IUIPlugin>();
		authenticator = null;
		colorer = null;
	}
}
