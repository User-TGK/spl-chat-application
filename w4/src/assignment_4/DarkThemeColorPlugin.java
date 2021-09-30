package assignment_4;

import java.util.Arrays;
import java.util.List;

public class DarkThemeColorPlugin implements IColorPlugin {
	
	public DarkThemeColorPlugin(PluginRegistry registry)
	{
		
	}

	@Override
	public List<MessageColor> getColors() {
		return Arrays.asList(
				MessageColor.BLACK,
				MessageColor.RED,
				MessageColor.BLUE
				);
	}

	@Override
	public MessageColor getDefaultColor() {
		return MessageColor.RED;
	}

}
