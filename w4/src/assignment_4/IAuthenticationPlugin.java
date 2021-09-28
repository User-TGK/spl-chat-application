package assignment_4;

public interface IAuthenticationPlugin {
	public boolean authenticate(String id, String password);

	public boolean isAuthenticated(String id);
	
	public void remove(String id);
}
