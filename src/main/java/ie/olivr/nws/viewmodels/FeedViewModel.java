package ie.olivr.nws.viewmodels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ie.olivr.nws.models.Story;
import ie.olivr.nws.services.PersonService;
import ie.olivr.nws.services.WebService;

public class FeedViewModel {
	private PersonService ps = PersonService.getInstance();
	private List<Story> stories;
	
	@Init
    public void init(){
		stories = new ArrayList<Story>();
		String jsonInString = WebService.getInstance().makeAPIGetRequest("/getArticles/5846a1a961d39700049a0429");
		Type listType = new TypeToken<List<Story>>() {}.getType();
		System.out.println(jsonInString);
		stories = new Gson().fromJson(jsonInString, listType);
    }
	public void navigate(String url)
	{
		Executions.getCurrent().sendRedirect(url, "_blank");
	}
	public List<Story> getStories() {
		return stories;
	}
	public void setStories(List<Story> stories) {
		this.stories = stories;
	}

}
