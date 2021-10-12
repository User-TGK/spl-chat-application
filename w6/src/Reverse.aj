import assignment_6.Message;

public aspect Reverse {
	declare precedence : Base64, Reverse;
	
	String around() : execution(String Message.serialize()) {
		String result = proceed();
		return new StringBuilder(result).reverse().toString();
	}
	
	Message around(String text) : execution(Message Message.deserialize(String)) && args(text) {
		String result = new StringBuilder(text).reverse().toString();
		return proceed(result);
	}
}