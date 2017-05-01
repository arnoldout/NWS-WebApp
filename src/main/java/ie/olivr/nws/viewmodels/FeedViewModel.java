package ie.olivr.nws.viewmodels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.bson.types.ObjectId;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import ie.olivr.nws.models.Story;
import ie.olivr.nws.services.PersonService;
import ie.olivr.nws.services.WebService;

public class FeedViewModel {
	// private PersonService ps = PersonService.getInstance();
	private List<Story> stories;
	private Queue<Story> allStories;

	@Init
	public void init() {
		if (PersonService.getInstance().getLoggedInUser() != null) {
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
				int count = 0;
				while (!allStories.isEmpty() && count < 10) {
					stories.add(allStories.poll());
					count++;
				}
			}
		}
		else{
			Executions.sendRedirect("index.zul");
		}
	}

	public void navigate(Story st) {
		stories.remove(st);
		WebService.getInstance().makeAPIGetRequest("readArticle/"+PersonService.getInstance().getLoggedInUser().getId()+"/" + st.get_id());
		Executions.getCurrent().sendRedirect(st.getUri(), "_blank");
		for (int i = 0; i < st.getCategories().size(); i++) {
			WebService.getInstance()
					.makeAPIGetRequest("addLike/" + PersonService.getInstance().getLoggedInUser().getId() + "/" + st.getCategories().get(i));
		}
		stories.add(allStories.poll());
	}

	public List<Story> getStories() {
		return stories;
	}

	public void setStories(List<Story> stories) {
		this.stories = stories;
	}

}
