package assignment_4;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class FooBarAuthenticationPlugin implements IAuthenticationPlugin {

	private final List<String> passwords = Arrays.asList("foo", "bar");
	private Set<String> authenticated = new HashSet<>();

	@Override
	public boolean authenticate(String id, String password) {
		if (passwords.contains(password)) {
			this.authenticated.add(id);
			return true;
		}
		return false;
	}

	@Override
	public boolean isAuthenticated(String id) {
		return authenticated.contains(id);
	}

	@Override
	public void remove(String id) {
		if (authenticated.contains(id)) {
			authenticated.remove(id);
		}
	}
}
