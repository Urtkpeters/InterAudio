package net.odinary.interaudio.adventure.component.entity.variable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class PlayerVariable extends AdventureVariable implements VariableInterface
{
    private String maxFailFilename;
    private HashMap<Boolean, String> bFilemap;
    private HashMap<Double, String> nFilemap;
    private HashMap<String, String> sFilemap;

    public PlayerVariable(JSONObject variable) throws JSONException
    {
        super(variable);

        maxFailFilename = variable.getString("maxFailFilename");

        switch(varType)
        {
            case "Boolean": parseBooleanMap(variable); break;
            case "Number": parseNumberMap(variable); break;
            case "String": parseStringMap(variable); break;
        }
    }

    public PlayerVariable(String name, String filename, String varType)
    {
        super(name, filename, varType);
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

    private void parseNumberMap(JSONObject variable) throws JSONException
    {
        nFilemap = new HashMap<>();
        JSONObject jsonFilenames = variable.getJSONObject("filenames");
        Iterator<String> keys = jsonFilenames.keys();

        while(keys.hasNext())
        {
            String key = keys.next();
            String filename = jsonFilenames.getString(key);

            if(key.contains("-"))
            {
                double numberOne = Double.parseDouble(key.substring(0, key.indexOf("-")));
                double numberTwo = Double.parseDouble(key.substring(key.indexOf("-") + 1, key.length()));

                for(double i = numberOne; i < numberTwo - numberOne; i++)
                {
                    nFilemap.put(i, filename);
                }
            }
            else
            {
                nFilemap.put(Double.parseDouble(key), filename);
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