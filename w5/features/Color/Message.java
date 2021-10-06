import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {
	public MessageColor color;
	
	public Message(String user, MessageType type, String content) {
		this.color = MessageColor.BLACK;
	}

	public Message(String user, MessageType type, MessageColor color, String content) {
		this(user, type, content);
		this.color = color;
	}

	public String toString() {
		String s = original();
		return s + ", color: " + this.color.name();

	}
	
	protected JSONObject toJSONObject()
	{
		JSONObject o = original();
		o.put("color", this.color.name());
		return o;
	}
	
	protected static Message fromJSONObject(String data) throws ParseException
	{
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(data);
		Message m = original(data);
		m.color = MessageColor.valueOf((String) jsonObject.get("color"));
		return m;
	}
}
