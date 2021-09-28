package assignment_4;

import java.lang.reflect.Constructor;
import java.util.List;

public class PluginLoader {
	public PluginRegistry registry;

	public PluginLoader() {
		registry = new PluginRegistry();
	}

	public void load(List<String> plugins) {
		for (String pluginName : plugins) {

			try {
				Class<?> pluginClass = Class.forName(pluginName);
				Constructor<?> constructor = pluginClass.getConstructor();
				Object plugin = constructor.newInstance();

				if (plugin instanceof IEncryptionPlugin) {
					this.registry.encryptors.add((IEncryptionPlugin) plugin);
				}

			} catch (Exception e) {
				System.out.println("Cannot load plugin " + pluginName + ", reason: " + e);
			}
		}
	}
}
