package net.odinary.interaudio.adventure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Action extends AbstractEntity
{
    private String failFilename;
    private Boolean alwaysAllowed;
    private int time;
    private List<String> targets = new ArrayList<>();
    private List<String> secondaryKeys = new ArrayList<>();
    private List<String> secondaryTargets = new ArrayList<>();
    private List<HashMap<String, String>> conditions;
    private List<String> setVars = new ArrayList<>();
    private List<String> triggers = new ArrayList<>();

    public Action(JSONObject jsonAction) throws JSONException
    {
        super(jsonAction);

        failFilename = jsonAction.getString("failFilename");
        alwaysAllowed = jsonAction.getBoolean("alwaysAllowed");
        time = jsonAction.getInt("time");

        JSONArray targetsArray = jsonAction.getJSONArray("targets");

        for(int i = 0; i < targetsArray.length(); i++)
        {
            targets.add(targetsArray.getString(i));
        }

        JSONArray secondaryKeysArray = jsonAction.getJSONArray("secondaryKeys");

        for(int i = 0; i < secondaryKeysArray.length(); i++)
        {
            secondaryKeys.add(secondaryKeysArray.getString(i));
        }

        JSONArray secondaryTargetsArray = jsonAction.getJSONArray("secondaryTargets");

        for(int i = 0; i < secondaryTargetsArray.length(); i++)
        {
            secondaryTargets.add(secondaryTargetsArray.getString(i));
        }

        conditions = Conditions.parseConditions(jsonAction.getJSONArray("conditions"));

        JSONArray setVarsArray = jsonAction.getJSONArray("setVars");

        for(int i = 0; i < setVarsArray.length(); i++)
        {
            setVars.add(setVarsArray.getString(i));
        }

        JSONArray triggersArray = jsonAction.getJSONArray("triggers");

        for(int i = 0; i < triggersArray.length(); i++)
        {
            triggers.add(triggersArray.getString(i));
        }
    }

    public String getName() { return name; }

    public List<String> getTargets() { return targets; }

    public List<String> getSecondaryKeys() { return secondaryKeys; }

    public List<String> getSecondaryTargets() { return secondaryTargets; }

    public List<HashMap<String, String>> getConditions() { return conditions; }

    public int getTime() { return time; }
}