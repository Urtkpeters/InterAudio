package net.odinary.interaudio.story.component;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public abstract class AbstractComponent implements Component
{
    public static final String actionType = "action";
    public static final String sectionType = "section";
    public static final String variableType = "variable";
    public static final String itemType = "item";
    public static final String objectType = "object";
    public static final String characterType = "character";
    public static final String playerType = "player";

    protected String name;
    protected String type;
    protected String filename;
    protected List<String> keywords;

    protected AbstractComponent(JSONObject entityJson, String type) throws JSONException
    {
        this.name = entityJson.getString("name");
        this.filename = entityJson.getString("filename");
        this.type = type;
        JSONArray keywords = entityJson.getJSONArray("keywords");

        for(int i = 0; i < keywords.length(); i++)
        {
            // Remove all spaces and special characters. Makes it easier for comparison.
            this.keywords.add(keywords.getString(i).replaceAll("\\s+", "").replaceAll("[^\\w\\s]","").toLowerCase());
        }
    }

    protected AbstractComponent(String name, String filename, String type)
    {
        this.name = name;
        this.filename = filename;
        this.type = type;
    }

    protected AbstractComponent(AbstractComponent cloner)
    {
        this.name = cloner.name;
        this.filename = cloner.filename;
        this.type = cloner.type;
    }

    public String getName() { return name; }

    public String getFilename() { return filename; }

    public String getType() { return type; }

    public List<String> getKeywords() { return keywords; }
}