package assignment_4;

import java.util.Base64;

public class Base64EncryptionPlugin implements IEncryptionPlugin {
	
	public Base64EncryptionPlugin(PluginRegistry registry)
	{
		
	}

	@Override
	public String encrypt(String data) {
		return Base64.getEncoder().encodeToString(data.getBytes());
	}

	@Override
	public String decrypt(String data) {
		return new String(Base64.getDecoder().decode(data));
	}

}
