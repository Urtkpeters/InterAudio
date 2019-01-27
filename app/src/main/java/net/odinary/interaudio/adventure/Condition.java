package net.odinary.interaudio.adventure;

import java.util.ArrayList;
import java.util.List;

class Condition
{
    private List<ConditionSegment> conditionSegments = new ArrayList<>();
    private String operator = "";
    private String failFilename = "";

    Condition(String condition, String operator, String failFilename)
    {
        this.operator = operator;
        this.failFilename = failFilename;

        List<String> segments = parseNotation(condition, " ");

        for(int i = 0; i < segments.size(); i++)
        {
            conditionSegments.add(new ConditionSegment(parseNotation(segments.get(i), ".")));
        }
    }

    private static List<String> parseNotation(String input, String character)
    {
        List<String> list = new ArrayList<>();

        // The plus one is to account for the additional segment after the end of the last character
        int countPath = (input.length() - input.replace(character, "").length()) + 1;

        for(int i = 0; i < countPath; i++)
        {
            int dotIndex = input.indexOf(character);

            if(dotIndex != -1)
            {
                list.add(input.substring(0, dotIndex));
                input = input.substring(dotIndex + 1, input.length());
            }
            else
            {
                list.add(input);
            }
        }

        return list;
    }

    public List<ConditionSegment> getSegments() { return conditionSegments; }

    public String getOperator() { return operator; }

    public String getFailFilename() { return failFilename; }
}