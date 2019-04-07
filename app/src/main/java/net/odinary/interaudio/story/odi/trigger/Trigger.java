package net.odinary.interaudio.story.odi.trigger;

import net.odinary.interaudio.story.odi.AbstractOdi;

public class Trigger extends AbstractOdi
{
    public static final String actionType = "Action";
    public static final String systemType = "System";

    public Trigger(String type, String trigger, String operator) { super(type, trigger, operator); }
}