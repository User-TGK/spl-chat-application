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
				Object plugin = null;
				for (Constructor<?> c : pluginClass.getConstructors()) {
					if (c.getParameterCount() == 1 && c.getParameterTypes()[0] == PluginRegistry.class) {
						plugin = c.newInstance(this.registry);
						break;
					}
				}
				if (plugin == null) {
					throw new RuntimeException("Plugin is invalid");
				}

				if (plugin instanceof IEncryptionPlugin) {
					this.registry.encryptors.add((IEncryptionPlugin) plugin);
				}

				else if (plugin instanceof IUIPlugin) {
					this.registry.uis.add((IUIPlugin) plugin);
				}

				else if (plugin instanceof IAuthenticationPlugin) {
					this.registry.authenticator = (IAuthenticationPlugin) plugin;
				}

				else if (plugin instanceof IColorPlugin) {
					this.registry.colorer = (IColorPlugin) plugin;
				}

			} catch (Exception e) {
				System.out.println("Cannot load plugin " + pluginName + ", reason: " + e);
			}
		}
	}
}
