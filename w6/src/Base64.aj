import assignment_6.Message;

public aspect Base64 {
	
	String around() : execution(String Message.serialize()) {
		String result = proceed();
		return java.util.Base64.getEncoder().encodeToString(result.getBytes());
	}
	
	Message around(String text) : execution(Message Message.deserialize(String)) && args(text) {
		String result = new String(java.util.Base64.getDecoder().decode(text));
		return proceed(result);
	}
}