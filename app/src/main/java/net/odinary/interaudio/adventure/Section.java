package net.odinary.interaudio.adventure;

import net.odinary.interaudio.adventure.repositories.EntityRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Section extends AbstractEntity
{
    private List<Condition> conditions;
    private HashMap<String, String> directions = new HashMap<>();
    private List<Entity> entities = new ArrayList<>();
    private List<String> setVars = new ArrayList<>();

    public Section(JSONObject jsonSection) throws JSONException
    {
        super(jsonSection, "section");

        conditions = Conditions.parseConditions(jsonSection.getJSONArray("conditions"));

        JSONArray jsonDirections = jsonSection.getJSONArray("directions");

        for(int i = 0; i < jsonDirections.length(); i++)
        {
            directions.put(jsonDirections.getJSONObject(i).getString("direction"), jsonDirections.getJSONObject(i).getString("name"));
        }

        JSONArray entityArray = jsonSection.getJSONArray("entities");

        for(int i = 0; i < entityArray.length(); i++)
        {
            JSONObject entity = entityArray.getJSONObject(i);

            String entityType = entity.getString("type");
            String entityName = entity.getString("name");

            entities.add(new Entity(EntityRepository.getEntity(entityType, entityName)));
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