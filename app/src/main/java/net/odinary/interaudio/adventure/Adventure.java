package net.odinary.interaudio.adventure;

import net.odinary.interaudio.MainActivity;
import net.odinary.interaudio.PackageLoadException;
import net.odinary.interaudio.R;
import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.Entity;
import net.odinary.interaudio.adventure.component.entity.Section;
import net.odinary.interaudio.adventure.component.entity.variable.VariableInterface;
import net.odinary.interaudio.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.adventure.odi.trigger.TriggerHandler;
import net.odinary.interaudio.adventure.repository.EntityRepository;
import net.odinary.interaudio.adventure.repository.PlayerRepository;
import net.odinary.interaudio.adventure.repository.WorldRepository;
import net.odinary.interaudio.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.adventure.component.entity.variable.PlayerVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class Adventure
{
    private static final String worldComponent = "world";
    private static final String timeComponent = "time";
    private static final String playerComponent = "player";
    private static final String entityComponent = "entity";

    private String packageName;
    private String packageType;
    private String author;
    private String audioFileExt;

    private ConditionHandler conditionHandler;
    private TriggerHandler triggerHandler;

    private WorldRepository worldRepository = new WorldRepository();
    private PlayerRepository playerRepository = new PlayerRepository();
    private EntityRepository entityRepository = new EntityRepository();

    Adventure(MainActivity mainActivity, ConditionHandler conditionHandler, TriggerHandler triggerHandler)
    {
        this.conditionHandler = conditionHandler;
        this.triggerHandler = triggerHandler;

        try(InputStream inputStream = mainActivity.getResources().openRawResource(R.raw.audioadventure))
        {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;

            while((n = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, n);
            }

            parseJSON(new JSONObject(writer.toString()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    Adventure(String packageDir, ConditionHandler conditionHandler, TriggerHandler triggerHandler) throws Exception
    {
        this.conditionHandler = conditionHandler;
        this.triggerHandler = triggerHandler;

        File storyFile = new File(packageDir);

        if(!storyFile.exists()) throw new Exception("Could not find json file");

        try(InputStream inputStream = new FileInputStream(storyFile))
        {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;

            while((n = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, n);
            }

            parseJSON(new JSONObject(writer.toString()));
        }
        catch(IOException | JSONException e)
        {
            e.printStackTrace();

            throw new Exception("Failure to read package json");
        }
    }

    private void parseJSON(JSONObject packageJson) throws Exception
    {
        try
        {
            packageName = packageJson.getString("packageName");
            packageType = packageJson.getString("packageType");
            author = packageJson.getString("author");
            audioFileExt = packageJson.getString("audioFileExt");

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

            worldRepository.setCurrentSection(packageJson.getString("start"));
            worldRepository.addVariable(new AdventureVariable("time", "", AdventureVariable.integerType));

            // Associate Entities to Vars

            // Associate Entities to Actions

            // Associate Entities to Moveset

            // Associate Sections to Sections

            // Associate Sections to Entities
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            throw new Exception("Error getting data from package JSON");
        }
    }

    private void parseVars(JSONObject jsonVars, String varType) throws JSONException, PackageLoadException
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
                default: throw new PackageLoadException("Undefined variable type");
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
                case timeComponent: worldRepository.addAction(new Action(typeAction, conditionHandler, triggerHandler));
                case playerComponent: playerRepository.addAction(new Action(typeAction, conditionHandler, triggerHandler));
                case entityComponent: entityRepository.addAction(new Action(typeAction, conditionHandler, triggerHandler));
            }
        }
    }

    private void parseEntities(JSONObject jsonEntities, String entityType) throws JSONException, PackageLoadException
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
            String entityName = entity.getName();

            if(!entityName.equals(primaryTarget) && resultPhrase.contains(entityName))
            {
                return entity;
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

    public String getPackageName() { return packageName; }

    public String getPackageType() { return packageType; }

    public String getAuthor() { return author; }

    public String getAudioFileExt() { return audioFileExt; }
}