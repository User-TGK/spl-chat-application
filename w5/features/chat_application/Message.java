import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {
	public String user;
	public MessageType type;
	public String content;

	public Message(String user, MessageType type, String content) {
		this.user = user;
		this.type = type;
		this.content = content;
	}

	public String toString() {
		return "user: " + this.user + ", type: " + this.type.name() + ", content: " + this.content;

	}
	
	protected JSONObject toJSONObject()
	{
		JSONObject obj = new JSONObject();
		obj.put("user", this.user);
		obj.put("type", this.type.name());
		obj.put("content", this.content);
		return obj;
	}
	
	protected static Message fromJSONObject(String data) throws ParseException
	{
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(data);

		String user = (String) jsonObject.get("user");
		MessageType type = MessageType.valueOf((String) jsonObject.get("type"));
		String content = (String) jsonObject.get("content");

		return new Message(user, type, content);
	}

	public String serialize() {
		return toJSONObject().toJSONString();
	}

	public static Message deserialize(String data) throws ParseException {
		return fromJSONObject(data);
	}
}
