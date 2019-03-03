package net.odinary.interaudio.adventure.component.entity;

import net.odinary.interaudio.adventure.odi.condition.Condition;
import net.odinary.interaudio.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.adventure.odi.trigger.Trigger;
import net.odinary.interaudio.adventure.odi.trigger.TriggerHandler;

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
    private List<Trigger> triggers = new ArrayList<>();

    public Action(JSONObject jsonAction, ConditionHandler conditionHandler, TriggerHandler triggerHandler) throws JSONException
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

        conditions = conditionHandler.parse(jsonAction.getJSONArray("conditions"));

        triggers = triggerHandler.parse(jsonAction.getJSONArray("triggers"));
    }

    public Action(Action cloner, JSONObject actionOverrides, ConditionHandler conditionHandler, TriggerHandler triggerHandler) throws JSONException
    {
        super(cloner);

        if(actionOverrides.has("filename")) filename = actionOverrides.getString("filename");

        if(actionOverrides.has("failFilename")) failFilename = actionOverrides.getString("failFilename");

        if(actionOverrides.has("conditions")) conditions.addAll(conditionHandler.parse(actionOverrides.getJSONArray("conditions")));

        if(actionOverrides.has("triggers")) triggers.addAll(triggerHandler.parse(actionOverrides.getJSONArray("triggers")));
    }

    public String getName() { return name; }

    public String getFailFilename() { return failFilename; }

    public List<String> getTargetTypes() { return targetTypes; }

    public List<String> getSecondaryKeys() { return secondaryKeys; }

    public List<String> getSecondaryTargets() { return secondaryTargets; }

    public List<Condition> getConditions() { return conditions; }

    public List<Trigger> getTriggers() { return triggers; }

    public int getTime() { return time; }
}