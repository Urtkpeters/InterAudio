package net.odinary.interaudio.adventure;

import java.util.List;

class ConditionSegment
{
    public static final int empty = 0;
    public static final int variable = 1;
    public static final int operator = 2;
    public static final int raw = 3;

    public static final String player = "player";
    public static final String global = "global";
    public static final String sections = "sections";
    public static final String target = "target";
    public static final String secondaryTarget = "secondaryTarget";

    private int type = empty;

    private String scope;
    private String entityVariable;
    private String value;

    ConditionSegment(List<String> segment)
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
            else if(segment.get(0).contains("=") || segment.get(0).contains(">") || segment.get(0).contains("<") || segment.get(0).contains("{") || segment.get(0).contains("["))
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