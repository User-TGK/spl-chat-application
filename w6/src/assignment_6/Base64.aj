package assignment_6;

public aspect Base64 {
	declare precedence: World, Beautiful; 

	after() returning(String plaintext): execution(String Message.serialize()) {
		System.out.println(plaintext);
	}
	
	before() returning(String plaintext): execution(String Message.deserialize(String data)) {
		System.out.println(plaintext);
	}
}
