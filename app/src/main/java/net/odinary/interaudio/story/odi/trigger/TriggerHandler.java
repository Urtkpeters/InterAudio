package net.odinary.interaudio.story.odi.trigger;

import net.odinary.interaudio.story.adventure.Event;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public interface TriggerHandler
{
    List<Trigger> parse(JSONArray jsonArray) throws JSONException;

//    void runTriggers(Event event);
}
