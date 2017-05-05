package ie.olivr.nws.viewmodels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import ie.olivr.nws.models.AuthenticatedUser;
import ie.olivr.nws.models.Story;
import ie.olivr.nws.services.PersonService;
import ie.olivr.nws.services.WebService;

public class FeedViewModel {
	// private PersonService ps = PersonService.getInstance();
	private List<Story> stories;
	private Queue<Story> allStories;

	@Init
	public void init() {
		// check for identification cookie
		if (PersonService.getInstance().getLoggedInUser() == null) {
			Cookie[] cookies = ((HttpServletRequest) Executions.getCurrent().getNativeRequest()).getCookies();
			if (cookies != null) {
				for (Cookie c : cookies) {
					if (c.getName().equals("UID")) {
						// store user in ps
						PersonService.getInstance().setLoggedInUser(new AuthenticatedUser("", "", c.getValue()));
						break;
					}
				}
				if (PersonService.getInstance().getLoggedInUser() == null) {
					Executions.sendRedirect("index.zul");
				}
			}
			// user now logged it, so get stories
			generateStories();

		}
	}

	public void generateStories() {
		stories = new ArrayList<Story>();
		String jsonInString = WebService.getInstance()
				.makeAPIGetRequest("getArticles/" + PersonService.getInstance().getLoggedInUser().getId());
		Type listType = new TypeToken<Queue<Story>>() {
		}.getType();
		// Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class,
		// new ObjectIdTypeAdapter()).create();

		JsonDeserializer<ObjectId> des = new JsonDeserializer<ObjectId>() {

			public ObjectId deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
					throws JsonParseException {
				return new ObjectId(je.getAsJsonObject().get("$oid").getAsString());
			}
		};
		if (!jsonInString.equals("false")) {
			Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, des).create();
			allStories = gson.fromJson(jsonInString, listType);
			pollNewStories();
		}

	}

	public void pollNewStories() {
		setStories(new ArrayList<Story>());
		int count = 0;
		while (!allStories.isEmpty() && count < 10) {
			addStory(allStories.poll());
			count++;
		}
	}

	public void navigate(Story st) {
		Executions.getCurrent().sendRedirect(st.getUri(), "_blank");
		//remove story from view
		removeStory(st);
		WebService.getInstance().makeAPIGetRequest(
				"readArticle/" + PersonService.getInstance().getLoggedInUser().getId() + "/" + st.get_id());
		
		
		
		for (int i = 0; i < st.getCategories().size(); i++) {
			WebService.getInstance().makeAPIGetRequest("addLike/"
					+ PersonService.getInstance().getLoggedInUser().getId() + "/" + st.getCategories().get(i));
		}
		if (!allStories.isEmpty())
			stories.add(allStories.poll());
	}

	public void dislikeStory(Story st) {
		pollStory();
		removeStory(st);
		for (int i = 0; i < st.getCategories().size(); i++) {
			WebService.getInstance().makeAPIGetRequest("remLike/"
					+ PersonService.getInstance().getLoggedInUser().getId() + "/" + st.getCategories().get(i));
		}
	}

	public List<Story> getStories() {
		return stories;
	}

	public void setStories(List<Story> stories) {
		this.stories = stories;
		BindUtils.postNotifyChange(null, null, FeedViewModel.this, "stories");
	}

	public void pollStory() {
		if (!allStories.isEmpty()) {
			stories.add(allStories.poll());
			BindUtils.postNotifyChange(null, null, FeedViewModel.this, "stories");
		}
	}

	public void addStory(Story arg0) {
		stories.add(arg0);
		BindUtils.postNotifyChange(null, null, FeedViewModel.this, "stories");
	}

	public void removeStory(Story arg0) {
		stories.remove(arg0);
		BindUtils.postNotifyChange(null, null, FeedViewModel.this, "stories");
	}

}
