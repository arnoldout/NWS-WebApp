package ie.olivr.nws.viewmodels;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.bson.Document;
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
import ie.olivr.nws.services.WebService;

public class FeedViewModel {
	//private PersonService ps = PersonService.getInstance();
	private List<Story> stories;
	private Queue<Story> allStories;
	
	@Init
    public void init(){
		stories = new ArrayList<Story>();
		String jsonInString = WebService.getInstance().makeAPIGetRequest("getArticles/59078dd05ccca70004e91f99");
		Type listType = new TypeToken<Queue<Story>>() {}.getType();
		//Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
		
		JsonDeserializer<ObjectId> des = new JsonDeserializer<ObjectId>() {

            public ObjectId deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
                return new ObjectId(je.getAsJsonObject().get("$oid").getAsString());
            }
        };
        if(!jsonInString.equals("false"))
        {
	        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, des).create();
	        allStories = gson.fromJson(jsonInString, listType);
			for (int i = 0; i < 10; i++) {
				stories.add(allStories.poll());
			}
        }
    }
	public void navigate(Story st)
	{
		WebService.getInstance().makeAPIGetRequest("readArticle/59078dd05ccca70004e91f99/"+st.get_id());
		Executions.getCurrent().sendRedirect(st.getUri(), "_blank");
		for (int i = 0; i < st.getCategories().size(); i++) {
			WebService.getInstance().makeAPIGetRequest("addLike/" + "59078dd05ccca70004e91f99"+ "/"+st.getCategories().get(i));
		}
	}
	public List<Story> getStories() {
		return stories;
	}
	public void setStories(List<Story> stories) {
		this.stories = stories;
	}

}
