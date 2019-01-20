package net.odinary.interaudio.adventure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Section extends AbstractEntity
{
    private List<HashMap<String, String>> conditions;
    private HashMap<String, String> directions = new HashMap<>();
    private List<Entity> entities = new ArrayList<>();
    private List<String> setVars = new ArrayList<>();

    public Section(JSONObject jsonSection, HashMap<String, HashMap<String, Entity>> entitiesMap) throws JSONException
    {
        super(jsonSection, "section");

        conditions = Conditions.parseConditions(jsonSection.getJSONArray("conditions"));

        JSONObject jsonDirections = jsonSection.getJSONObject("directions");
        Iterator<String> keys = jsonDirections.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            String value = jsonDirections.get(key).toString();

            if(value != null && !value.isEmpty()) directions.put(key, value);
        }

        JSONArray entityArray = jsonSection.getJSONArray("entities");

        for(int i = 0; i < entityArray.length(); i++)
        {
            JSONObject entity = entityArray.getJSONObject(i);

            String entityType = entity.getString("type");
            String entityName = entity.getString("name");

            entities.add(entitiesMap.get(entityType).get(entityName));
        }

        JSONArray varArray = jsonSection.getJSONArray("setVariables");

        for(int i = 0; i < varArray.length(); i++)
        {
            setVars.add(varArray.getString(i));
        }
    }

    public Entity getEntity(String entityName)
    {
        for(int i = 0; i < entities.size(); i++)
        {
            if(entities.get(i).getName().equals(entityName)) return entities.get(i);
        }

        return null;
    }

    public String getFilename() { return filename; }

    public List<Entity> getEntities() { return entities; }
}