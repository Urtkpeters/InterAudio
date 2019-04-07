package net.odinary.interaudio.story.adventure.odi.condition;

import net.odinary.interaudio.story.odi.AbstractOdi;

public class Condition extends AbstractOdi
{
    private String failFilename;

    Condition(String type, String condition, String operator, String failFilename)
    {
        super(type, condition, operator);

        this.failFilename = failFilename;
    }

    public String getFailFilename() { return failFilename; }
}