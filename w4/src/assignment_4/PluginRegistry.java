package assignment_4;

import java.util.ArrayList;
import java.util.List;

public class PluginRegistry {
	public List<IEncryptionPlugin> encryptors;
	
	public PluginRegistry() {
		encryptors = new ArrayList<IEncryptionPlugin>();
	}
}
