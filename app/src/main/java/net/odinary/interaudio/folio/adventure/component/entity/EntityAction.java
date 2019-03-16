package net.odinary.interaudio.folio.adventure.component.entity;

import net.odinary.interaudio.folio.adventure.odi.condition.ConditionHandler;
import net.odinary.interaudio.folio.adventure.odi.trigger.TriggerHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class EntityAction extends Action
{
    private int useChance = 0;

    EntityAction(Action cloner, JSONObject actionOverrides, ConditionHandler conditionHandler, TriggerHandler triggerHandler) throws JSONException
    {
        super(cloner, actionOverrides, conditionHandler, triggerHandler);

        if(actionOverrides.has("useChance")) useChance = actionOverrides.getInt("useChance");
    }

    public int getUseChance() { return useChance; }
}
