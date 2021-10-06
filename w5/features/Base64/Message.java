import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {

	public String serialize() {
		return Base64.getEncoder().encodeToString(original().getBytes());
	}

	public static Message deserialize(String data) throws ParseException {
		String enc = new String(Base64.getDecoder().decode(data));
		return original(enc);
	}
}
