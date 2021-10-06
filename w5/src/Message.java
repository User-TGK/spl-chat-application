import java.util.Base64; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 
import org.json.simple.parser.ParseException; 

public   class  Message {
	
	public String user;

	
	public MessageType type;

	
	public String content;

	
	
	public Message  (String user, MessageType type, String content) {
		this.user = user;
		this.type = type;
		this.content = content;
	
		this.color = MessageColor.BLACK;
	}

	

	 private String  toString__wrappee__chat_application  () {
		return "user: " + this.user + ", type: " + this.type.name() + ", content: " + this.content;

	}

	

	public String toString() {
		String s = toString__wrappee__chat_application();
		return s + ", color: " + this.color.name();

	}

	
	
	 private JSONObject  toJSONObject__wrappee__chat_application  ()
	{
		JSONObject obj = new JSONObject();
		obj.put("user", this.user);
		obj.put("type", this.type.name());
		obj.put("content", this.content);
		return obj;
	}

	
	
	protected JSONObject toJSONObject()
	{
		JSONObject o = toJSONObject__wrappee__chat_application();
		o.put("color", this.color.name());
		return o;
	}

	
	
	 private static Message  fromJSONObject__wrappee__chat_application  (String data) throws ParseException
	{
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(data);

		String user = (String) jsonObject.get("user");
		MessageType type = MessageType.valueOf((String) jsonObject.get("type"));
		String content = (String) jsonObject.get("content");

		return new Message(user, type, content);
	}

	
	
	protected static Message fromJSONObject(String data) throws ParseException
	{
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(data);
		Message m = fromJSONObject__wrappee__chat_application(data);
		m.color = MessageColor.valueOf((String) jsonObject.get("color"));
		return m;
	}

	

	 private String  serialize__wrappee__chat_application  () {
		return toJSONObject().toJSONString();
	}

	

	 private String  serialize__wrappee__Base64  () {
		return Base64.getEncoder().encodeToString(serialize__wrappee__chat_application().getBytes());
	}

	

	public String serialize() {
		return new StringBuilder(serialize__wrappee__Base64()).reverse().toString();
	}

	

	 private static Message  deserialize__wrappee__chat_application  (String data) throws ParseException {
		return fromJSONObject(data);
	}

	

	 private static Message  deserialize__wrappee__Base64  (String data) throws ParseException {
		String enc = new String(Base64.getDecoder().decode(data));
		return deserialize__wrappee__chat_application(enc);
	}

	

	public static Message deserialize(String data) throws ParseException {
		String dec = new StringBuilder(data).reverse().toString();
		return deserialize__wrappee__Base64(dec);
	}

	
	public MessageColor color;

	

	public Message(String user, MessageType type, MessageColor color, String content) {
		this(user, type, content);
		this.color = color;
	}


}
