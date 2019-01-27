package net.odinary.interaudio.adventure.condition;

import java.util.ArrayList;
import java.util.List;

public class Condition
{
    public static final String booleanComparison = "Boolean";
    public static final String integerComparison = "Integer";
    public static final String stringComparison = "String";

    private String type;
    private String operator;
    private String failFilename;

    private List<ConditionSegment> leftSegments = new ArrayList<>();
    private List<ConditionSegment> rightSegments = new ArrayList<>();
    private ConditionSegment operatorSegment;

    Condition(String type, String condition, String operator, String failFilename)
    {
        this.type = type;
        this.operator = operator;
        this.failFilename = failFilename;

        boolean passedOperator = false;

        List<String> segments = parseNotation(condition, " ");

        for(int i = 0; i < segments.size(); i++)
        {
            ConditionSegment tmpSeg = new ConditionSegment(parseNotation(segments.get(i), "."));

            if(tmpSeg.getType() == ConditionSegment.operator)
            {
                operatorSegment = tmpSeg;
                passedOperator = true;
            }
            else if(!passedOperator)
            {
                leftSegments.add(tmpSeg);
            }
            else
            {
                rightSegments.add(tmpSeg);
            }
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

    public List<ConditionSegment> getLeftSegments() { return leftSegments; }

    public List<ConditionSegment> getRightSegments() { return rightSegments; }

    public ConditionSegment getOperatorSegment() { return operatorSegment; }

    public String getType() { return type; }

    public String getOperator() { return operator; }

    public String getFailFilename() { return failFilename; }
}