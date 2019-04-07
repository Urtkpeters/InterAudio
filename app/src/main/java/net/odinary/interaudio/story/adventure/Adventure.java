package net.odinary.interaudio.story.adventure;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.folio.FolioLoadException;
import net.odinary.interaudio.story.AbstractStory;
import net.odinary.interaudio.story.adventure.component.AdventureAction;
import net.odinary.interaudio.story.adventure.component.Entity;
import net.odinary.interaudio.story.adventure.component.Section;
import net.odinary.interaudio.story.adventure.odi.trigger.AdventureTriggerHandler;
import net.odinary.interaudio.story.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.story.adventure.repository.EntityRepository;
import net.odinary.interaudio.story.adventure.repository.PlayerRepository;
import net.odinary.interaudio.story.adventure.repository.WorldRepository;
import net.odinary.interaudio.story.adventure.component.variable.AdventureVariable;
import net.odinary.interaudio.story.adventure.component.variable.PlayerVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class Adventure extends AbstractStory
{
    private static final String worldComponent = "world";
    private static final String playerComponent = "player";
    private static final String entityComponent = "entity";

    private ConditionHandler conditionHandler;
    private AdventureTriggerHandler triggerHandler;

    private WorldRepository worldRepository = new WorldRepository();
    private PlayerRepository playerRepository = new PlayerRepository();
    private EntityRepository entityRepository = new EntityRepository();

    Adventure(MainActivity mainActivity, ConditionHandler conditionHandler, AdventureTriggerHandler triggerHandler)
    {
        super(mainActivity);

        this.conditionHandler = conditionHandler;
        this.triggerHandler = triggerHandler;
    }

    Adventure(String packageDir, ConditionHandler conditionHandler, AdventureTriggerHandler triggerHandler) throws Exception
    {
        super(packageDir);

        this.conditionHandler = conditionHandler;
        this.triggerHandler = triggerHandler;
    }

    protected void parseTypeJSON(JSONObject packageJson) throws Exception
    {
        // Parse Variables
        JSONObject jsonVariables = packageJson.getJSONObject("variables");
        Iterator<String> keys = jsonVariables.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            parseVars(jsonVariables, key);
        }

        // Parse Actions
        JSONObject jsonActions = packageJson.getJSONObject("actions");
        keys = jsonActions.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            parseActions(jsonActions, key);
        }

        // Parse Entities
        JSONObject jsonEntities = packageJson.getJSONObject("entities");
        keys = jsonEntities.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            parseEntities(jsonEntities, key);
        }

        // Parse Sections
        JSONArray jsonSections = packageJson.getJSONArray("sections");

        for(int i = 0; i < jsonSections.length(); i++)
        {
            JSONObject section = jsonSections.getJSONObject(i);

            worldRepository.addSection(new Section(section, entityRepository, conditionHandler));
        }

        worldRepository.setCurrentSection(start);
        worldRepository.addVariable(new AdventureVariable("time", "", AdventureVariable.numberType));

        // Associate Entities to Vars

        // Associate Entities to Actions

        // Associate Entities to Moveset

        // Associate Sections to Sections

        // Associate Sections to Entities
    }

    private void parseVars(JSONObject jsonVars, String varType) throws JSONException, FolioLoadException
    {
        JSONArray variables = jsonVars.getJSONArray(varType);

        for(int i = 0; i < variables.length(); i++)
        {
            JSONObject variable = variables.getJSONObject(i);

            switch(varType)
            {
                case worldComponent: worldRepository.addVariable(new AdventureVariable(variable)); break;
                case playerComponent: playerRepository.addVariable(new PlayerVariable(variable)); break;
                case entityComponent: entityRepository.addVariable(new AdventureVariable(variable)); break;
                default: throw new FolioLoadException("Undefined variable type");
            }
        }

        playerRepository.addVariable(new PlayerVariable("Alive", "", PlayerVariable.booleanType));
    }

    private void parseActions(JSONObject jsonActions, String actionType) throws JSONException
    {
        JSONArray typeActions = jsonActions.getJSONArray(actionType);

        for(int i = 0; i < typeActions.length(); i++)
        {
            JSONObject typeAction = typeActions.getJSONObject(i);

            switch(actionType)
            {
                case worldComponent: worldRepository.addAction(new AdventureAction(typeAction, conditionHandler, triggerHandler));
                case playerComponent: playerRepository.addAction(new AdventureAction(typeAction, conditionHandler, triggerHandler));
                case entityComponent: entityRepository.addAction(new AdventureAction(typeAction, conditionHandler, triggerHandler));
            }
        }
    }

    private void parseEntities(JSONObject jsonEntities, String entityType) throws JSONException, FolioLoadException
    {
        JSONArray typeEntities = jsonEntities.getJSONArray(entityType);

        for(int i = 0; i < typeEntities.length(); i++)
        {
            JSONObject typeEntity = typeEntities.getJSONObject(i);

            entityRepository.addEntity(entityType, typeEntity.getString("name"), new Entity(typeEntity, entityType, entityRepository, playerRepository, conditionHandler, triggerHandler));
        }
    }

    public String checkSecondaryActions(String resultPhrase, List<String> secondaryActions)
    {
        for(String action: secondaryActions)
        {
            if(resultPhrase.contains(action)) return action;
        }

        return null;
    }

    public Entity checkTarget(String resultPhrase)
    {
        return checkTarget(resultPhrase, null);
    }

    public Entity checkTarget(String resultPhrase, String primaryTarget)
    {
        // Check section entities
        Entity target = checkTarget(resultPhrase, primaryTarget, worldRepository.getCurrentSection().getEntities());

        if(target != null) return target;

        // Check playerRepository inventory
        target = checkTarget(resultPhrase, primaryTarget, playerRepository.getInventory());

        if(target != null) return target;
        else return playerRepository.checkInventory(resultPhrase);
    }

    private Entity checkTarget(String resultPhrase, String primaryTarget, List<Entity> entities)
    {
        for(Entity entity: entities)
        {
            List<String> entityKeywords = entity.getKeywords();

            for(String keyword: entityKeywords)
            {
                if(!keyword.equals(primaryTarget) && resultPhrase.contains(keyword)) return entity;
            }
        }

        return null;
    }

    public String checkDirection(String resultPhrase)
    {
        HashMap<String, String> directions = worldRepository.getCurrentSection().getDirections();

        for (Map.Entry<String, String> direction: directions.entrySet())
        {
            String key = direction.getKey();
            String value = direction.getValue();

            if(resultPhrase.contains(key) && worldRepository.getSections().containsKey(value)) return value;
        }

        return null;
    }

    public WorldRepository getWorldRepository() { return worldRepository; }

    public PlayerRepository getPlayerRepository() { return playerRepository; }

    public EntityRepository getEntityRepository() { return entityRepository; }
}