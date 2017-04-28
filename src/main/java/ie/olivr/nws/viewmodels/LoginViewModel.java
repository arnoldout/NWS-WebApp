package ie.olivr.nws.viewmodels;

import java.io.IOException;

import org.zkoss.zk.ui.Executions;

import com.google.gson.Gson;

import ie.olivr.nws.models.AuthenticatedUser;
import ie.olivr.nws.models.Person;
import ie.olivr.nws.services.PersonService;
import ie.olivr.nws.services.WebService;

public class LoginViewModel {
	private String username = "";
	private String password = "";
	private PersonService ps = PersonService.getInstance();
	
	public void login() throws IOException
	{
		Person p = new Person(username, password);

		Gson gson = new Gson();
		String json = gson.toJson(p);
		String response = WebService.getInstance().makeAPIPostRequest("login", json);
		if(!response.toString().equals("false"))
		{
			PersonService.getInstance().setLoggedInUser(new AuthenticatedUser(username, password, response.toString()));
			Executions.sendRedirect("Feed.zul");
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
