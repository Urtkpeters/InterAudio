package net.odinary.interaudio.story.adventure.component;

import net.odinary.interaudio.folio.FolioLoadException;
import net.odinary.interaudio.story.adventure.Event;
import net.odinary.interaudio.story.adventure.odi.trigger.AdventureTriggerHandler;
import net.odinary.interaudio.story.component.AbstractComponent;
import net.odinary.interaudio.story.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.story.adventure.repository.EntityRepository;
import net.odinary.interaudio.story.adventure.repository.PlayerRepository;
import net.odinary.interaudio.story.adventure.component.variable.AdventureVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Entity extends AbstractComponent
{
    private HashMap<String, AdventureVariable> entityVars = new HashMap<>();
    private HashMap<String, AdventureAction> actions = new HashMap<>();
    private HashMap<String, EntityAdventureAction> moveset = new HashMap<>();

    public Entity(JSONObject entityJson, String entityType, EntityRepository entityRepository, PlayerRepository playerRepository, ConditionHandler conditionHandler, AdventureTriggerHandler triggerHandler) throws JSONException, FolioLoadException
    {
        super(entityJson, entityType);

        JSONObject jsonEntityVars = entityJson.getJSONObject("entityVars");
        Iterator<String> keys = jsonEntityVars.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            JSONObject jsonEntityVar = jsonEntityVars.getJSONObject(key);

            if(entityRepository.getVariables().containsKey(key)) entityVars.put(key, new AdventureVariable((AdventureVariable) entityRepository.getVariable(key), jsonEntityVar));
        }

        JSONObject jsonActions = entityJson.getJSONObject("actions");
        keys = jsonActions.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            JSONObject jsonAction = jsonActions.getJSONObject(key);

            AdventureAction adventureAction = playerRepository.getAction(key);

            if(adventureAction != null) actions.put(key, new AdventureAction(adventureAction, jsonAction, conditionHandler, triggerHandler));
            else throw new FolioLoadException("Could not load actions into entity. Entity: " + name + " AdventureAction: " + key);
        }

        JSONObject jsonMoveset = entityJson.getJSONObject("moveset");
        keys = jsonMoveset.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            JSONObject value = jsonMoveset.getJSONObject(key);

            if(entityRepository.getActions().containsKey(key)) moveset.put(key, new EntityAdventureAction(entityRepository.getAction(key), value, conditionHandler, triggerHandler));
            else throw new FolioLoadException("Could not load actions into entity. Entity: " + name + " - AdventureAction: " + key);
        }
    }

    public Entity(Entity cloner)
    {
        super(cloner);

        this.entityVars = cloner.entityVars;
        this.actions = cloner.actions;
        this.moveset = cloner.moveset;
    }

    public AdventureAction getActionOverride(String name) { return actions.get(name); }

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

    public void decideAction(Event event)
    {
        Random rng = new Random();
        int number = rng.nextInt(99) + 1;

        for(EntityAdventureAction entityAction: moveset.values())
        {
            int useChance = entityAction.getUseChance();

            if(useChance > number)
            {
                event.setAdventureAction(entityAction);

                List<String> targetTypes = entityAction.getTargetTypes();

                // I have the target set to "this" as a placeholder. I will need to set the target to the player once the player entity has been created.
                // Also need to set the target to other things once the functionality behind that has been created as well and do additional ifs below for those.
                if(targetTypes.get(0).equals(Entity.playerType)) event.setTarget(this);

                break;
            }

            number -= useChance;
        }
    }
}