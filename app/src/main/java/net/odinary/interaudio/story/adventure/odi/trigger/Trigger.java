package net.odinary.interaudio.story.adventure.odi.trigger;

import net.odinary.interaudio.story.adventure.odi.AbstractOdi;

public class Trigger extends AbstractOdi
{
    public static final String actionType = "Action";
    public static final String systemType = "System";

    Trigger(String type, String trigger, String operator) { super(type, trigger, operator); }
}