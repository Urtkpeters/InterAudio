package net.odinary.interaudio.adventure;

import net.odinary.interaudio.PackageLoadException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Entity extends AbstractEntity
{
    private HashMap<String, AdventureVariable> entityVars = new HashMap<>();
    private HashMap<String, Action> actions = new HashMap<>();
    private HashMap<String, Action> moveset = new HashMap<>();

    public Entity(JSONObject entityJson, HashMap<String, HashMap<String, Action>> actionMap, HashMap<String, HashMap<String, AdventureVariable>> adventureVars) throws JSONException, PackageLoadException
    {
        super(entityJson);

        HashMap<String, AdventureVariable> globalEntityVars = adventureVars.get("entity");

        JSONObject jsonEntityVars = entityJson.getJSONObject("entityVars");
        Iterator<String> keys = jsonEntityVars.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            JSONObject jsonEntityVar = jsonEntityVars.getJSONObject(key);

            if(globalEntityVars.containsKey(key))
            {
                entityVars.put(key, new AdventureVariable(globalEntityVars.get(key)));

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

        // THIS WHOLE ACTION SECTION IS BEING DONE INCORRECTLY. THE ACTIONS ARE SUPPOSED TO JUST BE OVERRIDES FOR THE FILE NAMES

        JSONObject jsonActions = entityJson.getJSONObject("actions");
        keys = jsonActions.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            String value = jsonActions.getString(key);

            Action action = actionMap.get("player").get(key);

            if(action != null) actions.put(key, action);
            else throw new PackageLoadException("Could not load actions into entity. Entity: " + name + " Action: " + key);
        }

        JSONObject jsonMoveset = entityJson.getJSONObject("moveset");
        keys = jsonMoveset.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            String value = jsonMoveset.getString(key);

            // I NEED TO SET THE VALUE INTO EITHER A NEW CLASS FOR ENTITY ACTIONS OR SOME METHOD OF STORAGE OF THE DIFFERENT TYPES OF ACTIONS CHANCE PERCENTAGES

            Action action = actionMap.get("moveset").get(key);

            if(action != null) moveset.put(key, action);
            else throw new PackageLoadException("Could not load actions into entity. Entity: " + name + " Action: " + key);
        }
    }

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

    public AdventureVariable getVariable(String variable)
    {
        if(entityVars.containsKey(variable)) return entityVars.get(variable);

        System.out.println("Could not find entity variable");
        return null;
    }
}