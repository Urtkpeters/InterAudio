package net.odinary.interaudio.story.adventure.component;

import net.odinary.interaudio.story.adventure.odi.trigger.AdventureTriggerHandler;
import net.odinary.interaudio.story.adventure.odi.condition.ConditionHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class EntityAdventureAction extends AdventureAction
{
    private int useChance = 0;

    EntityAdventureAction(AdventureAction cloner, JSONObject actionOverrides, ConditionHandler conditionHandler, AdventureTriggerHandler triggerHandler) throws JSONException
    {
        super(cloner, actionOverrides, conditionHandler, triggerHandler);

        if(actionOverrides.has("useChance")) useChance = actionOverrides.getInt("useChance");
    }

    public int getUseChance() { return useChance; }
}
