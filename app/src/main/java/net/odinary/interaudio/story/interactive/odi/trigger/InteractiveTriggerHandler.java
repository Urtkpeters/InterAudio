package net.odinary.interaudio.story.interactive.odi.trigger;

import net.odinary.interaudio.story.component.Action;
import net.odinary.interaudio.story.interactive.InteractiveHandler;
import net.odinary.interaudio.story.odi.AbstractOdiHandler;
import net.odinary.interaudio.story.odi.OdiSegment;
import net.odinary.interaudio.story.odi.trigger.Trigger;
import net.odinary.interaudio.story.odi.trigger.TriggerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InteractiveTriggerHandler extends AbstractOdiHandler implements TriggerHandler
{
    private InteractiveHandler interactiveHandler;

    public InteractiveTriggerHandler(InteractiveHandler interactiveHandler) { this.interactiveHandler = interactiveHandler; }

    public List<Trigger> parse(JSONArray jsonArray) throws JSONException
    {
        List<Trigger> triggers = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            triggers.add(new Trigger(jsonObject.getString("type"), jsonObject.getString("trigger"), jsonObject.getString("operator")));
        }

        return triggers;
    }

    public void runTriggers(Action action)
    {
        List<Trigger> triggers = action.getTriggers();

        if(triggers.size() != 0)
        {
            for(Trigger trigger: triggers)
            {
                List<OdiSegment> leftSegments = trigger.getLeftSegments();

                if(leftSegments.size() > 0)
                {
                    List<String> scopes = leftSegments.get(0).getScopes();

                    if(scopes.size() > 0)
                    {
                        interactiveHandler.executeSystemCommand(scopes.get(0));
                    }
                }
            }
        }
    }
}
