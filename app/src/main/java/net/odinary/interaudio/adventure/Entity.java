package net.odinary.interaudio.adventure;

import net.odinary.interaudio.PackageLoadException;
import net.odinary.interaudio.adventure.repositories.EntityRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Entity extends AbstractEntity
{
    private HashMap<String, AdventureVariable> entityVars = new HashMap<>();
    private HashMap<String, Action> actions = new HashMap<>();
    private HashMap<String, Action> moveset = new HashMap<>();

    public Entity(JSONObject entityJson, String entityType) throws JSONException, PackageLoadException
    {
        super(entityJson, entityType);

        JSONObject jsonEntityVars = entityJson.getJSONObject("entityVars");
        Iterator<String> keys = jsonEntityVars.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            JSONObject jsonEntityVar = jsonEntityVars.getJSONObject(key);

            if(EntityRepository.checkEntityVariableExists(key))
            {
                entityVars.put(key, new AdventureVariable(EntityRepository.getEntityVariable(key)));

                switch(entityVars.get(key).getType())
                {
                    case "Boolean":
                        if(jsonEntityVar.has("value")) entityVars.get(key).setValue(jsonEntityVar.getBoolean("value"));
                        break;
                    case "Integer":
                        if(jsonEntityVar.has("max")) entityVars.get(key).setMax(jsonEntityVar.getInt("max"));
                        if(jsonEntityVar.has("value"))
                        {
                            int val = jsonEntityVar.getInt("value");

                            if(val == -2) val = entityVars.get(key).getMax();

                            entityVars.get(key).setValue(val);
                        }
                        break;
                    case "String":
                        if(jsonEntityVar.has("value")) entityVars.get(key).setValue(jsonEntityVar.getString("value"));
                        break;
                }
            }
        }

        JSONObject jsonActions = entityJson.getJSONObject("actions");
        keys = jsonActions.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            JSONObject jsonAction = jsonActions.getJSONObject(key);

            Action action = Player.getAction(key);

            if(action != null) actions.put(key, new Action(action, jsonAction));
            else throw new PackageLoadException("Could not load actions into entity. Entity: " + name + " Action: " + key);
        }

        JSONObject jsonMoveset = entityJson.getJSONObject("moveset");
        keys = jsonMoveset.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            String value = jsonMoveset.getString(key);

            // It is using a blank JSON object for now so that an error is not thrown but this needs to be templated with overrides from the JSON
            if(EntityRepository.checkEntityMoveExists(key)) moveset.put(key, new Action(EntityRepository.getEntityMove(key), new JSONObject()));
            else throw new PackageLoadException("Could not load actions into entity. Entity: " + name + " Action: " + key);
        }
    }

    public Entity(Entity cloner)
    {
        super(cloner);

        this.entityVars = cloner.entityVars;
        this.actions = cloner.actions;
        this.moveset = cloner.moveset;
    }

    public Action getActionOverride(String name) { return actions.get(name); }

    public Boolean checkActionOverride(String name) { return actions.containsKey(name); }

    public void setVariableValue(String variable, boolean value)
    {
        if(entityVars.containsKey(variable)) entityVars.get(variable).setValue(value);

        System.out.println("Could not find entity variable");
    }

    public void setVariableValue(String variable, int value)
    {
        if(entityVars.containsKey(variable)) entityVars.get(variable).setValue(value);

        System.out.println("Could not find entity variable");
    }

    public void setVariableValue(String variable, String value)
    {
        if(entityVars.containsKey(variable)) entityVars.get(variable).setValue(value);

        System.out.println("Could not find entity variable");
    }

    public Boolean checkVariableExists(String name) { return entityVars.containsKey(name); }

    public AdventureVariable getVariable(String variable) { return entityVars.get(variable); }
}