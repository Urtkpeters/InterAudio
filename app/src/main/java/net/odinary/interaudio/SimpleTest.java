package net.odinary.interaudio;

import net.odinary.interaudio.adventure.component.entity.Action;
import net.odinary.interaudio.adventure.component.entity.variable.AdventureVariable;
import net.odinary.interaudio.adventure.component.entity.Entity;
import net.odinary.interaudio.adventure.Event;
import net.odinary.interaudio.adventure.component.entity.Section;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SimpleTest
{
    private static HashMap<String, HashMap<String, AdventureVariable>> adventureVars = new HashMap<>();
    private static HashMap<String, HashMap<String, Action>> actions = new HashMap<>();
    private static HashMap<String, HashMap<String, Entity>> entities = new HashMap<>();
    private static HashMap<String, Section> sections = new HashMap<>();
    private static List<String> playerActionList = new ArrayList<>();
    private static Section currentSection;

    public static void doSimpleTest(MainActivity mainActivity)
    {
        JSONObject packageJson = null;

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

            packageJson = new JSONObject(writer.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(packageJson != null)
        {
            try
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

//                    sections.put(section.getString("name"), new Section(section));
                }

                currentSection = sections.get(packageJson.getString("start"));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            Entity troll = entities.get("character").get("troll");

            sendCheckCondition(packageJson, troll);

            troll.setVariableValue("health", 0);

            sendCheckCondition(packageJson, troll);
        }
    }

    private static void sendCheckCondition(JSONObject packageJson, Entity target)
    {
        System.out.println("Checking value set to " + target.getVariable("health").getIValue());

        try
        {
//            List<HashMap<String, String>> conditions = ConditionHandler.parseConditions(packageJson.getJSONObject("sections").getJSONObject("attic").getJSONArray("conditions"));

            Event event = new Event(currentSection);

            event.setAction(actions.get("player").get("eat"));

            event.setTarget(currentSection.getEntity("sack"));

//            String failFilename = ConditionHandler.checkConditions(event);

//            if(failFilename != null) System.out.println("Condition was not met. Fail Filename: " + failFilename);
//            else System.out.println("Condition was met!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void parseVars(JSONObject jsonVars, String varType) throws JSONException
    {
        if(!varType.equals("player")) adventureVars.put(varType, new HashMap<String, AdventureVariable>());

        JSONArray variables = jsonVars.getJSONArray(varType);

        for(int i = 0; i < variables.length(); i++)
        {
            JSONObject variable = variables.getJSONObject(i);

            if(varType.equals("player"))
            {
//                PlayerRepository.addPlayerVariable(new PlayerVariable(variable));
            }
            else
            {
                adventureVars.get(varType).put(variable.getString("name"), new AdventureVariable(variable));
            }
        }
    }

    private static void parseActions(JSONObject jsonActions, String actionType) throws JSONException
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

    private static void parseEntities(JSONObject jsonEntities, String entityType) throws JSONException, PackageLoadException
    {
        entities.put(entityType, new HashMap<String, Entity>());

        JSONArray typeEntities = jsonEntities.getJSONArray(entityType);

        for(int i = 0; i < typeEntities.length(); i++)
        {
            JSONObject typeEntity = typeEntities.getJSONObject(i);

//            entities.get(entityType).put(typeEntity.getString("name"), new Entity(typeEntity, entityType, actions, adventureVars));
        }
    }
}
