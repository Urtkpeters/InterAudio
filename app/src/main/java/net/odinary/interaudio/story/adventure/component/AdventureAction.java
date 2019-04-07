package net.odinary.interaudio.story.adventure.component;

import net.odinary.interaudio.story.adventure.odi.trigger.AdventureTriggerHandler;
import net.odinary.interaudio.story.adventure.odi.condition.Condition;
import net.odinary.interaudio.story.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.story.odi.trigger.Trigger;
import net.odinary.interaudio.story.component.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdventureAction extends Action
{
    private String failFilename;
    private int time;
    private List<String> targetTypes = new ArrayList<>();
    private List<String> secondaryKeys = new ArrayList<>();
    private List<String> secondaryTargets = new ArrayList<>();
    private List<Condition> conditions = new ArrayList<>();

    public AdventureAction(JSONObject jsonAction, ConditionHandler conditionHandler, AdventureTriggerHandler triggerHandler) throws JSONException
    {
        super(jsonAction, triggerHandler);

        failFilename = jsonAction.getString("failFilename");
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
    }

    public AdventureAction(AdventureAction cloner, JSONObject actionOverrides, ConditionHandler conditionHandler, AdventureTriggerHandler triggerHandler) throws JSONException
    {
        super(cloner, actionOverrides, triggerHandler);

        if(actionOverrides.has("filename")) filename = actionOverrides.getString("filename");

        if(actionOverrides.has("failFilename")) failFilename = actionOverrides.getString("failFilename");

        if(actionOverrides.has("conditions")) conditions.addAll(conditionHandler.parse(actionOverrides.getJSONArray("conditions")));
    }

    public String getName() { return name; }

    public String getFailFilename() { return failFilename; }

    public List<String> getTargetTypes() { return targetTypes; }

    public List<String> getSecondaryKeys() { return secondaryKeys; }

    public List<String> getSecondaryTargets() { return secondaryTargets; }

    public List<Condition> getConditions() { return conditions; }

    public int getTime() { return time; }
}