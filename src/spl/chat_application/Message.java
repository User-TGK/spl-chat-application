package spl.chat_application;

public class Message {
	public String user;
	public MessageType type;
	public MessageColor color;
	public String content;

	public Message(String user, MessageType type, MessageColour color, String content)
	{
		this.user = user;
		this.type = type;
		this.color = color;
		this.content = content;
	}

	public String serialize() {
		JSONObject obj = new JSONObject();
		obj.put("user", this.user);
		obj.put("type", this.type.name());
		obj.put("color", this.color.name());
		obj.put("content", this.content);

		String enc = Base64.getEncoder().encodeToString(obj.toJSONString().getBytes());
		return new StringBuilder(enc).reverse().toString();
	}

	public static Message deserialize(String data) {
		String dec = new StringBuilder(data).reverse().toString()
		String json = new String(Base64.getDecoder().decode(dec));

		JSONObject jsonObject = new JSONParser().parse(json);

		String user = (String)jsonObject.get("user");
		MessageType type = MessageType.valueOf((String)jsonObject.get("type"));
		MessageColor color = MessageColor.valueOf((String)jsonObject.get("color"));
		String content = (String)jsonObject.get("content");

		return new Message(user, type, color, content);
	}
}
