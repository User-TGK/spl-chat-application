import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {

	public String serialize() {
		return new StringBuilder(original()).reverse().toString();
	}

	public static Message deserialize(String data) throws ParseException {
		String dec = new StringBuilder(data).reverse().toString();
		return original(dec);
	}
}
