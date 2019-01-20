package net.odinary.interaudio.adventure;

import net.odinary.interaudio.PackageLoadException;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class Adventure
{
    private String packageName;
    private String packageType;
    private String author;
    private String audioFileExt;
    private HashMap<String, Section> sections = new HashMap<>();
    private HashMap<String, HashMap<String, AdventureVariable>> adventureVars = new HashMap<>();
    private HashMap<String, HashMap<String, Action>> actions = new HashMap<>();
    private List<String> playerActionList = new ArrayList<>();
    private HashMap<String, HashMap<String, Entity>> entities = new HashMap<>();
    private Section currentSection;

    Adventure(String packageDir) throws Exception
    {
        JSONObject packageJson;
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

            packageJson = new JSONObject(writer.toString());
        }
        catch(IOException | JSONException e)
        {
            e.printStackTrace();

            throw new Exception("Failure to read package json");
        }

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

                sections.put(section.getString("name"), new Section(section, entities));
            }

            currentSection = sections.get(packageJson.getString("start"));

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

    private void parseVars(JSONObject jsonVars, String varType) throws JSONException
    {
        adventureVars.put(varType, new HashMap<String, AdventureVariable>());

        JSONArray variables = jsonVars.getJSONArray(varType);

        for(int i = 0; i < variables.length(); i++)
        {
            JSONObject variable = variables.getJSONObject(i);

            if(varType.equals("player"))
            {
                Player.addPlayerVariable(new PlayerVariable(variable));
            }
            else
            {
                adventureVars.get(varType).put(variable.getString("name"), new AdventureVariable(variable));
            }
        }
    }

    private void parseActions(JSONObject jsonActions, String actionType) throws JSONException
    {
        actions.put(actionType, new HashMap<String, Action>());

        JSONArray typeActions = jsonActions.getJSONArray(actionType);

        for(int i = 0; i < typeActions.length(); i++)
        {
            JSONObject typeAction = typeActions.getJSONObject(i);
            String name = typeAction.getString("name");

            actions.get(actionType).put(name, new Action(typeAction));
            if(actionType.equals("player")) playerActionList.add(name);
        }
    }

    private void parseEntities(JSONObject jsonEntities, String entityType) throws JSONException, PackageLoadException
    {
        entities.put(entityType, new HashMap<String, Entity>());

        JSONArray typeEntities = jsonEntities.getJSONArray(entityType);

        for(int i = 0; i < typeEntities.length(); i++)
        {
            JSONObject typeEntity = typeEntities.getJSONObject(i);

            entities.get(entityType).put(typeEntity.getString("name"), new Entity(typeEntity, actions, adventureVars));
        }
    }

    public Action getPlayerAction(String resultPhrase)
    {
        HashMap<String, Action> playerActions = actions.get("player");

        for(String key: playerActions.keySet())
        {
            Action action = playerActions.get(key);

            if(resultPhrase.contains(playerActions.get(key).getName())) return action;
        }

        return null;
    }

    public String checkActions(String resultPhrase)
    {
        for(String action: playerActionList)
        {
            if(resultPhrase.contains(action)) return action;
        }

        return null;
    }

    public String checkSecondaryActions(String resultPhrase, List<String> secondaryActions)
    {
        for(String action: secondaryActions)
        {
            if(resultPhrase.contains(action)) return action;
        }

        return null;
    }

    public Entity checkTarget(String resultPhrase, List<String> targets)
    {
        return checkTarget(resultPhrase, targets, null);
    }

    public Entity checkTarget(String resultPhrase, List<String> targets, String primaryTarget)
    {
        List<Entity> entities = currentSection.getEntities();

        for(Entity entity: entities)
        {
            String entityName = entity.getName();

            if(!entityName.equals(primaryTarget) && resultPhrase.contains(entityName))
            {
                for(String target: targets)
                {
                    if(entityName.equals(target))
                    {
                        return entity;
                    }
                }

                return null;
            }
        }

        return null;
    }

    public Section getCurrentSection() { return currentSection; }

    public String getPackageName() { return packageName; }

    public String getPackageType() { return packageType; }

    public String getAuthor() { return author; }

    public String getAudioFileExt() { return audioFileExt; }

    public String getCurrentSectionFilename() { return currentSection.getFilename(); }

    public List<String> getPlayerActionList() { return playerActionList; }

    public HashMap<String, HashMap<String, AdventureVariable>> getAdventureVars() { return adventureVars; }
}