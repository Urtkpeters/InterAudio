package net.odinary.interaudio.adventure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class PlayerVariable extends AdventureVariable
{
    private String maxFailFilename;
    private HashMap<Boolean, String> bFilemap;
    private HashMap<Integer, String> iFilemap;
    private HashMap<String, String> sFilemap;

    public PlayerVariable(JSONObject variable) throws JSONException
    {
        super(variable);

        maxFailFilename = variable.getString("maxFailFilename");

        switch(type)
        {
            case "Boolean": parseBooleanMap(variable); break;
            case "Integer": parseIntMap(variable); break;
            case "String": parseStringMap(variable); break;
        }
    }

    PlayerVariable(PlayerVariable cloner)
    {
        super(cloner);

        this.maxFailFilename = cloner.maxFailFilename;
        this.bFilemap = cloner.bFilemap;
        this.iFilemap = cloner.iFilemap;
        this.sFilemap = cloner.sFilemap;
    }

    private void parseBooleanMap(JSONObject variable) throws JSONException
    {
        bFilemap = new HashMap<>();
        JSONObject jsonFilenames = variable.getJSONObject("filenames");
        Iterator<String> keys = jsonFilenames.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            bFilemap.put(Boolean.valueOf(key), jsonFilenames.getString(key));
        }
    }

    private void parseIntMap(JSONObject variable) throws JSONException
    {
        iFilemap = new HashMap<>();
        JSONObject jsonFilenames = variable.getJSONObject("filenames");
        Iterator<String> keys = jsonFilenames.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            String filename = jsonFilenames.getString(key);

            if(key.contains("-"))
            {
                int numberOne = Integer.parseInt(key.substring(0, key.indexOf("-")));
                int numberTwo = Integer.parseInt(key.substring(key.indexOf("-") + 1, key.length()));

                for(int i = numberOne; i < numberTwo - numberOne; i++)
                {
                    iFilemap.put(i, filename);
                }
            }
            else
            {
                iFilemap.put(Integer.parseInt(key), filename);
            }
        }
    }

    private void parseStringMap(JSONObject variable) throws JSONException
    {
        sFilemap = new HashMap<>();
        sValue = variable.getString("value");

        JSONObject jsonFilenames = variable.getJSONObject("filenames");
        Iterator<String> keys = jsonFilenames.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            sFilemap.put(key, jsonFilenames.getString(key));
        }
    }
}