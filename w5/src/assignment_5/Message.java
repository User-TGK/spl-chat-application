package assignment_5;

import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {
	public String user;
	public MessageType type;
	//#if Color
	public MessageColor color;
	//#endif
	public String content;

	public Message(
		String user,
		MessageType type,
		//#if Color 
		MessageColor color,
		//#endif
		String content) 
	{
		this.user = user;
		this.type = type;
		//#if Color
		this.color = color;
		//#endif
		this.content = content;
	}

	public String toString() {
		return "user: " + this.user 
				+ ", type: " + this.type.name()
				//#if Color 
				+ ", color: " + this.color.name()
				//#endif
				+ ", content: " + this.content;

	}

	public String serialize() {
		JSONObject obj = new JSONObject();
		obj.put("user", this.user);
		obj.put("type", this.type.name());
		//#if Color
		obj.put("color", this.color.name());
		//#endif
		obj.put("content", this.content);

		String enc = obj.toJSONString();
		//#if Base64
		enc = Base64.getEncoder().encodeToString(obj.toJSONString().getBytes());
		//#endif

		//#if Reverse
		enc = new StringBuilder(enc).reverse().toString();
		//#endif

		return enc;
	}

	public static Message deserialize(String data) throws ParseException {		
		String message = data;

		//#if Reverse
		message = new StringBuilder(message).reverse().toString();
		//#endif

		//#if Base64
		message = new String(Base64.getDecoder().decode(message));
		//#endif

		JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);

		String user = (String) jsonObject.get("user");
		MessageType type = MessageType.valueOf((String) jsonObject.get("type"));
		//#if Color
		MessageColor color = MessageColor.valueOf((String) jsonObject.get("color"));
		//#endif
		String content = (String) jsonObject.get("content");

		return new Message(
					user, 
					type,
					//#if Color 
					color,
					//#endif
					content);
	}
}
