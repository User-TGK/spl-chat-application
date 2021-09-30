package assignment_4;

public class ReverseEncryptionPlugin implements IEncryptionPlugin {
	
	public ReverseEncryptionPlugin(PluginRegistry registry)
	{
		
	}

	@Override
	public String encrypt(String data) {		
		StringBuilder b = new StringBuilder(data);
		String reverse = b.reverse().toString();
		
		return reverse;
	}

	@Override
	public String decrypt(String data) {

		StringBuilder b = new StringBuilder(data);
		String reverse = b.reverse().toString();
		
		return reverse;
	}

}
