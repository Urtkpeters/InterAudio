package net.odinary.interaudio.story.interactive;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.story.AbstractStory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Interactive extends AbstractStory
{
    Map<String, JSONObject> interactiveVariables;

    public Interactive(MainActivity mainActivity)
    {
        super(mainActivity);
    }

    public Interactive(String packageDir) throws Exception
    {
        super(packageDir);
    }

    protected void parseTypeJSON(JSONObject folioJson) throws Exception
    {
        // This whole section will need to be refactored with the task about making the AdventureVariables generic
        JSONArray variableArray = folioJson.getJSONArray("variables");
        interactiveVariables = new HashMap<>();

        for(int i = 0; i < variableArray.length(); i++)
        {
            JSONObject variable = variableArray.getJSONObject(i);

            variable.put("value",variable.getString("default"));

            interactiveVariables.put(variable.getString("name"), variable);
        }
    }
}
