package net.odinary.interaudio.adventure.component.entity;

import net.odinary.interaudio.adventure.condition.Condition;
import net.odinary.interaudio.adventure.condition.ConditionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Action extends AbstractEntity
{
    private String failFilename;
    private Boolean alwaysAllowed;
    private int time;
    private List<String> targetTypes = new ArrayList<>();
    private List<String> secondaryKeys = new ArrayList<>();
    private List<String> secondaryTargets = new ArrayList<>();
    private List<Condition> conditions = new ArrayList<>();
    private List<String> setVars = new ArrayList<>();
    private List<String> triggers = new ArrayList<>();

    public Action(JSONObject jsonAction) throws JSONException
    {
        super(jsonAction, "action");

        failFilename = jsonAction.getString("failFilename");
        alwaysAllowed = jsonAction.getBoolean("alwaysAllowed");
        time = jsonAction.getInt("time");

        JSONArray targetTypesArray = jsonAction.getJSONArray("targets");

        for(int i = 0; i < targetTypesArray.length(); i++)
        {
            targetTypes.add(targetTypesArray.getString(i));
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

        conditions = ConditionHandler.parseConditions(jsonAction.getJSONArray("conditions"));

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

    public Action(Action cloner, JSONObject actionOverrides) throws JSONException
    {
        super(cloner);

        if(actionOverrides.has("filename")) filename = actionOverrides.getString("filename");

        if(actionOverrides.has("failFilename")) failFilename = actionOverrides.getString("failFilename");

        if(actionOverrides.has("conditions")) conditions.addAll(ConditionHandler.parseConditions(actionOverrides.getJSONArray("conditions")));

        if(actionOverrides.has("setVars"))
        {
            JSONArray newSetVars = actionOverrides.getJSONArray("setVars");

            for(int i = 0; i < newSetVars.length(); i++)
            {
                setVars.add(newSetVars.getString(i));
            }
        }
    }

    public String getName() { return name; }

    public String getFailFilename() { return failFilename; }

    public List<String> getTargetTypes() { return targetTypes; }

    public List<String> getSecondaryKeys() { return secondaryKeys; }

    public List<String> getSecondaryTargets() { return secondaryTargets; }

    public List<Condition> getConditions() { return conditions; }

    public int getTime() { return time; }
}