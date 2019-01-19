package net.odinary.interaudio;

import net.odinary.interaudio.adventure.Action;
import net.odinary.interaudio.adventure.AdventureVariable;
import net.odinary.interaudio.adventure.Conditions;
import net.odinary.interaudio.adventure.Entity;
import net.odinary.interaudio.adventure.PlayerVariable;
import net.odinary.interaudio.adventure.Section;

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
                JSONObject jsonSections = packageJson.getJSONObject("sections");
                keys = jsonSections.keys();

                while(keys.hasNext())
                {
                    String key = keys.next();
                    sections.put(key, new Section(key, jsonSections.getJSONObject(key), entities));
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
            List<HashMap<String, String>> conditions = Conditions.parseConditions(packageJson.getJSONObject("sections").getJSONObject("attic").getJSONArray("conditions"));
            String failFilename = Conditions.checkConditions(conditions, adventureVars, currentSection, target, null);

            if(failFilename != null) System.out.println("Condition was not met. Fail Filename: " + failFilename);
            else System.out.println("Condition was met!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void parseVars(JSONObject jsonVars, String varType) throws JSONException
    {
        adventureVars.put(varType, new HashMap<String, AdventureVariable>());

        JSONObject typeEntities = jsonVars.getJSONObject(varType);
        Iterator<String> keys = typeEntities.keys();

        while(keys.hasNext())
        {
            String key = keys.next();

            if(varType.equals("player"))
            {
                adventureVars.get(varType).put(key, new PlayerVariable(key, typeEntities.getJSONObject(key)));
            }
            else
            {
                adventureVars.get(varType).put(key, new AdventureVariable(key, typeEntities.getJSONObject(key)));
            }
        }
    }

    private static void parseActions(JSONObject jsonActions, String actionType) throws JSONException
    {
        actions.put(actionType, new HashMap<String, Action>());

        JSONObject typeActions = jsonActions.getJSONObject(actionType);
        Iterator<String> keys = typeActions.keys();

        while(keys.hasNext())
        {
            String key = keys.next();

            actions.get(actionType).put(key, new Action(key, typeActions.getJSONObject(key)));
            if(actionType.equals("player")) playerActionList.add(key);
        }
    }

    private static void parseEntities(JSONObject jsonEntities, String entityType) throws JSONException, PackageLoadException
    {
        entities.put(entityType, new HashMap<String, Entity>());

        JSONObject typeEntities = jsonEntities.getJSONObject(entityType);
        Iterator<String> keys = typeEntities.keys();

        while(keys.hasNext())
        {
            String key = keys.next();

            entities.get(entityType).put(key, new Entity(key, typeEntities.getJSONObject(key), actions, adventureVars));
        }
    }
}
