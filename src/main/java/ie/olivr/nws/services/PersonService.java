package ie.olivr.nws.services;

import ie.olivr.nws.models.AuthenticatedUser;

public class PersonService {

	private AuthenticatedUser loggedInUser;
	private static PersonService ps = new PersonService();
	private PersonService(){
		
	}
	public static PersonService getInstance()
	{
		return ps;
	}
	public AuthenticatedUser getLoggedInUser() {
		return loggedInUser;
	}
	public void setLoggedInUser(AuthenticatedUser loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
}
