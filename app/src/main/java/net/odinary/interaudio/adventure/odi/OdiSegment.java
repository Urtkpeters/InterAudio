package net.odinary.interaudio.adventure.odi;

import java.util.List;

public class OdiSegment implements OdiSegmentInterface
{
    protected int type = empty;
    protected String scope;
    protected String entityVariable;
    protected String value;

    public OdiSegment(List<String> segment)
    {
        if(segment.size() > 0)
        {
            if(segment.get(0).equals("variable"))
            {
                type = variable;

                scope = segment.get(1);
                entityVariable = segment.get(2);
                value = segment.get(3);
            }
            else if(segment.get(0).contains("=") || segment.get(0).contains(">") || segment.get(0).contains("<") || segment.get(0).contains("{") || segment.get(0).contains("[") || segment.get(0).contains("+") || segment.get(0).contains("-"))
            {
                type = operator;

                value = segment.get(0);
            }
            else
            {
                type = raw;

                value = segment.get(0);
            }
        }
    }

    public Integer getType() { return type; }

    public String getScope() { return scope; }

    public String getVariable() { return entityVariable; }

    public String getValue() { return value; }
}
