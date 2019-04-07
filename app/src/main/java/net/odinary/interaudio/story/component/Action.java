package net.odinary.interaudio.story.component;

import net.odinary.interaudio.story.adventure.odi.trigger.AdventureTriggerHandler;
import net.odinary.interaudio.story.odi.OdiHandler;
import net.odinary.interaudio.story.odi.trigger.Trigger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Action extends AbstractComponent
{
    protected Boolean alwaysAllowed;
    protected List<Trigger> triggers = new ArrayList<>();

    public Action(JSONObject componentJSON, OdiHandler triggerHandler) throws JSONException
    {
        super(componentJSON, actionType);

        alwaysAllowed = componentJSON.getBoolean("alwaysAllowed");
        triggers = triggerHandler.parse(componentJSON.getJSONArray("triggers"));
    }

    public Action(Action cloner, JSONObject actionOverrides, AdventureTriggerHandler triggerHandler) throws JSONException
    {
        super(cloner);

        alwaysAllowed = cloner.alwaysAllowed;

        if(actionOverrides.has("triggers")) triggers.addAll(triggerHandler.parse(actionOverrides.getJSONArray("triggers")));
    }

    public List<Trigger> getTriggers() { return triggers; }
}
