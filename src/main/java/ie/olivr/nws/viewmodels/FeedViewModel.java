package ie.olivr.nws.viewmodels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;

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
		String jsonInString = WebService.getInstance().makeAPIGetRequest("/getArticles/"+ps.getLoggedInUser().getId());
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Story>>() {}.getType();
		List<Story> stori = new Gson().fromJson(jsonInString, listType);
		System.out.println(stori);
		for (int i = 0; i < 10; i++) {
			stories.add(new Story("BRAPP"+i));
			
		}
    }
	public List<Story> getStories() {
		return stories;
	}
	public void setStories(List<Story> stories) {
		this.stories = stories;
	}

}
