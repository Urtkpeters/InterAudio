package net.odinary.interaudio.adventure.odi.condition;

import net.odinary.interaudio.adventure.odi.AbstractOdi;

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