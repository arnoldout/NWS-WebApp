package ie.olivr.nws.viewmodels;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

import com.google.gson.Gson;

import ie.olivr.nws.models.AuthenticatedUser;
import ie.olivr.nws.models.Person;
import ie.olivr.nws.services.PersonService;
import ie.olivr.nws.services.WebService;

public class LoginViewModel {
	private String username = "";
	private String password = "";
	private String regUsername = "";
	private String regPassword = "";
	

	@Wire private Window regWindow;
	
	@Init
    public void init(){
		Cookie [] cookies = ((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getCookies();
		for (Cookie c : cookies) {
			if(c.getName().equals("UID"))
			{
				String response = WebService.getInstance().makeAPIGetRequest("getProfile"+c.getValue());
				if(!response.toString().equals("false"))
				{
					navigate(response);
				}
			}
		}
	}
	@AfterCompose
	public void afterComposed(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	public void login() throws IOException
	{
		Person p = new Person(username, password);

		Gson gson = new Gson();
		String json = gson.toJson(p);
		String response = WebService.getInstance().makeAPIPostRequest("login", json);
		if(!response.toString().equals("false"))
		{
			navigate(response);
		}
	}
	public void register()
	{
		Person p = new Person(regUsername, regPassword);
		Gson gson = new Gson();
		String json = gson.toJson(p);
		String response = WebService.getInstance().makeAPIPostRequest("addProfile", json);
		if(!response.toString().equals("false"))
		{
			navigate(response);
		}
		
	}
	public void navigate(String response)
	{
		PersonService.getInstance().setLoggedInUser(new AuthenticatedUser(username, password, response.toString()));
		Executions.sendRedirect("Feed.zul");
		HttpServletResponse res = (HttpServletResponse)Executions.getCurrent().getNativeResponse();
		Cookie userCookie = new Cookie("UID", response.toString());
		res.addCookie(userCookie);
	}
	
	public void showRegister()
	{
		regWindow.setVisible(true);
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
	public String getRegPassword() {
		return regPassword;
	}
	public void setRegPassword(String regPassword) {
		this.regPassword = regPassword;
	}
	public String getRegUsername() {
		return regUsername;
	}
	public void setRegUsername(String regUsername) {
		this.regUsername = regUsername;
	}

}
