package ie.olivr.nws.models;

public class AuthenticatedUser extends Person{

	private String id;
	public AuthenticatedUser(String username, String password, String id) {
		super(username, password);
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	
}
