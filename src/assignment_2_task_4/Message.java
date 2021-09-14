package assignment_2_task_4;

import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Message {
	public String user;
	public MessageType type;
	public MessageColor color;
	public String content;

	public Message(String user, MessageType type, MessageColor color, String content) {
		this.user = user;
		this.type = type;
		this.color = color;
		this.content = content;
	}

	public String toString() {
		return "user: " + this.user + ", type: " + this.type.name() + ", color: " + this.color.name() + ", content: "
				+ this.content;
	}

	public String serialize() {
		JSONObject obj = new JSONObject();
		obj.put("user", this.user);
		obj.put("type", this.type.name());
		obj.put("color", this.color.name());
		obj.put("content", this.content);

		String enc = obj.toJSONString();
		switch (Config.Encryption) {
			case BASE64:
				enc = Base64.getEncoder().encodeToString(enc.getBytes());
				break;
			case REVERSE:
				enc = (new StringBuilder(enc)).reverse().toString();
				break;
			case BASE64REVERSE:
				enc = Base64.getEncoder().encodeToString(enc.getBytes());
				enc = (new StringBuilder(enc)).reverse().toString();
				break;
		}

		return enc;
	}

	public static Message deserialize(String data) throws ParseException {
		switch (Config.Encryption) {
			case BASE64:
				data = new String(Base64.getDecoder().decode(data));
				break;
			case REVERSE:
				data = new StringBuilder(data).reverse().toString();
				break;
			case BASE64REVERSE:
				data = new StringBuilder(data).reverse().toString();
				data = new String(Base64.getDecoder().decode(data));
				break;
		}

		JSONObject jsonObject = (JSONObject) new JSONParser().parse(data);

		String user = (String) jsonObject.get("user");
		MessageType type = MessageType.valueOf((String) jsonObject.get("type"));
		MessageColor color = MessageColor.valueOf((String) jsonObject.get("color"));
		String content = (String) jsonObject.get("content");

		return new Message(user, type, color, content);
	}
}
